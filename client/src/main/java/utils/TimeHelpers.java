package utils;

import dto.EventDto;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeHelpers {

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

    public static Duration parse(String input) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(d|h|m)(s?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            duration = switch (unit) {
                case "d" -> duration.plusDays(value);
                case "h" -> duration.plusHours(value);
                case "m" -> duration.plusMinutes(value);
                default -> duration;
            };
        }

        if (!matcher.matches())
            throw new DateTimeParseException("Duration isn't in the right format: ", input, input.length() - matcher.end());

        return duration;
    }

    /**
     * Get recurred time near this moment
     * @param e
     * @param now
     * @return
     */
    public static LocalDateTime getRecurrenceTimeNearThisDay(EventDto e, LocalDateTime now){
        return switch (e.getRepeat()){
            case NONE -> e.getStart();
            case HOURLY ->
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            now.getHour(),
                            e.getStart().getMinute()
                    );
            case DAILY ->
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            e.getStart().getHour(),
                            e.getStart().getMinute()
                    );
            case WEEKLY->
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.with(TemporalAdjusters.previousOrSame(e.getStart().getDayOfWeek())).getDayOfMonth(),
                            e.getStart().getHour(),
                            e.getStart().getMinute()
                    );
            case MONTHLY ->
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            e.getStart().getDayOfMonth(),
                            e.getStart().getHour(),
                            e.getStart().getMinute()
                    );
        };
    }


    public static LocalDateTime getNextRecurrenceTime(EventDto event, LocalDateTime now){
        return switch (event.getRepeat()){
            case NONE -> event.getStart().isBefore(now) ? null : event.getStart();
            case HOURLY -> (event.getStart().isBefore(now) && getRecurrenceTimeNearThisDay(event,now).isBefore(now))?(getRecurrenceTimeNearThisDay(event, now.plusHours(1))):getRecurrenceTimeNearThisDay(event,now);
            case DAILY -> (event.getStart().isBefore(now) && getRecurrenceTimeNearThisDay(event,now).isBefore(now))?(getRecurrenceTimeNearThisDay(event, now.plusDays(1))):getRecurrenceTimeNearThisDay(event,now);
            case WEEKLY -> (event.getStart().isBefore(now) && getRecurrenceTimeNearThisDay(event,now).isBefore(now))?(getRecurrenceTimeNearThisDay(event, now.plusWeeks(1))):getRecurrenceTimeNearThisDay(event,now);
            case MONTHLY -> (event.getStart().isBefore(now) && getRecurrenceTimeNearThisDay(event,now).isBefore(now))?(getRecurrenceTimeNearThisDay(event, now.plusMonths(1))):getRecurrenceTimeNearThisDay(event,now);
        };
    }
}
