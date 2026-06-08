package ir.ac.ut.ece.ie.infrastructure.utils;

public class FieldComparator {

    @FunctionalInterface
    public interface CompareFunction {
        boolean match(Object actual, Object expected);
    }

    public static final CompareFunction EXACT = (actual, expected) -> {
        if (actual == null && expected == null) return true;
        if (actual == null || expected == null) return false;
        return actual.toString().equals(expected.toString());
    };

    public static final CompareFunction EXACT_IGNORE_CASE = (actual, expected) -> {
        if (actual == null || expected == null) return false;
        return actual.toString().equalsIgnoreCase(expected.toString());
    };

    public static final CompareFunction CONTAINS = (actual, expected) -> {
        if (actual == null || expected == null) return false;
        return actual.toString().contains(expected.toString());
    };

    public static final CompareFunction STARTS_WITH = (actual, expected) -> {
        if (actual == null || expected == null) return false;
        return actual.toString().startsWith(expected.toString());
    };

    public static final CompareFunction ENDS_WITH = (actual, expected) -> {
        if (actual == null || expected == null) return false;
        return actual.toString().endsWith(expected.toString());
    };

}
