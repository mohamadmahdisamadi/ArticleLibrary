package ir.ac.ut.ece.ie.infrastructure.utils;

public class EncoderUtil {
    private static final String DELIMITER = ",";

    public static String encode(String value) {
        if (value == null) return "";
        return value.replace("%", "%25")
                .replace(DELIMITER, "%2C")
                .replace("\n", "%0A")
                .replace("\r", "%0D")
                .replace("\t", "%09");
    }

    public static String decode(String value) {
        if (value == null) return "";
        return value.replace("%09", "\t")
                .replace("%0D", "\r")
                .replace("%0A", "\n")
                .replace("%2C", DELIMITER)
                .replace("%25", "%");
    }
}
