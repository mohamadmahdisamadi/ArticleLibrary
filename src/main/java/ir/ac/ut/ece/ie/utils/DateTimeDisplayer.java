package ir.ac.ut.ece.ie.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDisplayer {

    public static String getAmPm(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a");
        return dateTime.format(formatter);
    }

    public static String getShortDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return dateTime.format(formatter);
    }

    public static String getDetailedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        return dateTime.format(formatter);
    }

    public static String getTimeIn24HoursFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    public static String getTimeIn12HoursFormat(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return dateTime.format(formatter);
    }
    
    public static String getShortDateTime(LocalDateTime dateTime) {
        return String.format("%s - %s", getShortDate(dateTime), getTimeIn12HoursFormat(dateTime));
    }
    
    public static String getDetailedDateTime(LocalDateTime dateTime) {
        return String.format("%s - %s", getDetailedDate(dateTime), getTimeIn12HoursFormat(dateTime));
    }
}