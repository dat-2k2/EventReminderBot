package utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
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
}
