package hunt.tyler.core;

public class TimeFormatter {

    public static String cairnToISO8601(String cairnTime) {
        String newTime = cairnTime.replace(" ", "T") + "Z";
        return newTime;
    }
}
