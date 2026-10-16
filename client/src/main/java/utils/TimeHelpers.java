package utils;

import dto.EventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeHelpers {
    public static final ZoneId ZONE = ZoneId.of("Europe/Moscow");

    public static String beautify(Duration duration){
        if(duration.equals(Duration.ZERO)) {
            return "0";
        }

        long d = duration.toDays();
        duration = duration.minusDays(d);
        long h = duration.toHours();
        duration = duration.minusHours(h);
        long m = duration.toMinutes();

        List<String> parts = new ArrayList<>();
        if (d > 0) {
            parts.add(d + " day" + (d > 1 ? "s" : ""));
        }
        if (h > 0) {
            parts.add(h + " hour" + (h > 1 ? "s" : ""));
        }
        if (m > 0) {
            parts.add(m + " minute" + (m > 1 ? "s" : ""));
        }

        return String.join(" ", parts);
    }

    private static final Pattern SHORT_DURATION = Pattern.compile("(\\d+)\\s*([dhm])", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_DURATION_FULL = Pattern.compile("(\\s*\\d+\\s*[dhm]s?\\s*)+", Pattern.CASE_INSENSITIVE);

    /**
     * Parse a duration in ISO-8601 (PT10H, P1DT2H) or short form (10h, 2h30m, 1d 2h).
     */
    public static Duration parse(String input) {
        try {
            return Duration.parse(input);
        } catch (DateTimeParseException ignored) {
        }

        if (!SHORT_DURATION_FULL.matcher(input).matches())
            throw new DateTimeParseException("Duration isn't in the right format: ", input, 0);

        Matcher matcher = SHORT_DURATION.matcher(input);
        Duration duration = Duration.ZERO;
        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            duration = switch (matcher.group(2).toLowerCase()) {
                case "d" -> duration.plusDays(value);
                case "h" -> duration.plusHours(value);
                default -> duration.plusMinutes(value);
            };
        }
        return duration;
    }

    private static LocalDateTime monthlyOccurrence(LocalDateTime start, YearMonth month) {
        // Events on the 29th-31st fall on the last day of shorter months.
        return month.atDay(Math.min(start.getDayOfMonth(), month.lengthOfMonth())).atTime(start.toLocalTime());
    }

    /**
     * Get the first occurrence of the event at or after now, or null if the event is over.
     */
    public static LocalDateTime getNextRecurrenceTime(EventDto event, LocalDateTime now){
        LocalDateTime start = event.getStart();
        if (!start.isBefore(now))
            return start;

        return switch (event.getRepeat()){
            case NONE -> null;
            case HOURLY -> {
                var next = now.truncatedTo(ChronoUnit.HOURS).withMinute(start.getMinute());
                yield next.isBefore(now) ? next.plusHours(1) : next;
            }
            case DAILY -> {
                var next = now.toLocalDate().atTime(start.toLocalTime());
                yield next.isBefore(now) ? next.plusDays(1) : next;
            }
            case WEEKLY -> {
                var next = now.toLocalDate().with(TemporalAdjusters.nextOrSame(start.getDayOfWeek())).atTime(start.toLocalTime());
                yield next.isBefore(now) ? next.plusWeeks(1) : next;
            }
            case MONTHLY -> {
                var month = YearMonth.from(now);
                var next = monthlyOccurrence(start, month);
                yield next.isBefore(now) ? monthlyOccurrence(start, month.plusMonths(1)) : next;
            }
        };
    }
}
