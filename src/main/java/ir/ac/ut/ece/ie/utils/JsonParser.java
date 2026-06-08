package ir.ac.ut.ece.ie.utils;

import java.lang.reflect.Field;
import java.util.*;

public class JsonParser {

    public static <T> T parse(String json, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            Map<String, Object> map = parseJsonToMap(json);

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (map.containsKey(fieldName)) {
                    Object value = map.get(fieldName);
                    Object converted = convert(value, field.getType());
                    field.set(instance, converted);
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Parse error: " + e.getMessage(), e);
        }
    }

    // پارس JSON به Map ساده
    private static Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> result = new HashMap<>();

        json = json.trim();
        if (!json.startsWith("{")) return result;

        json = json.substring(1, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            if (json.charAt(i) != '"') { i++; continue; }
            i++;
            int keyStart = i;
            while (i < json.length() && json.charAt(i) != '"') i++;
            String key = json.substring(keyStart, i);
            i++;

            while (i < json.length() && json.charAt(i) != ':') i++;
            i++;

            while (i < json.length() && json.charAt(i) == ' ') i++;

            if (i < json.length()) {
                if (json.charAt(i) == '"') {
                    // String
                    i++;
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != '"') i++;
                    result.put(key, json.substring(valStart, i));
                    i++;
                } else if (json.charAt(i) == '[') {
                    result.put(key, parseArray(json, i));
                    while (i < json.length() && json.charAt(i) != ']') i++;
                    i++;
                } else if (json.charAt(i) == '{') {
                    int braceCount = 1;
                    int objStart = i + 1;
                    i++;
                    while (i < json.length() && braceCount > 0) {
                        if (json.charAt(i) == '{') braceCount++;
                        else if (json.charAt(i) == '}') braceCount--;
                        i++;
                    }
                    result.put(key, json.substring(objStart, i - 1));
                } else {
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                    String val = json.substring(valStart, i).trim();
                    if (val.equals("null")) result.put(key, null);
                    else if (val.equals("true")) result.put(key, true);
                    else if (val.equals("false")) result.put(key, false);
                    else {
                        try { result.put(key, Integer.parseInt(val)); }
                        catch (Exception e) {
                            try { result.put(key, Double.parseDouble(val)); }
                            catch (Exception e2) { result.put(key, val); }
                        }
                    }
                }
            }

            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ')) i++;
        }

        return result;
    }

    // پارس آرایه
    private static List<Object> parseArray(String json, int start) {
        List<Object> result = new ArrayList<>();
        int i = start + 1; // رد کردن [

        while (i < json.length() && json.charAt(i) != ']') {
            while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == ',')) i++;

            if (i < json.length()) {
                if (json.charAt(i) == '"') {
                    i++;
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != '"') i++;
                    result.add(json.substring(valStart, i));
                    i++;
                } else if (json.charAt(i) == '[') {
                    result.add(parseArray(json, i));
                    while (i < json.length() && json.charAt(i) != ']') i++;
                    i++;
                } else {
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != ']') i++;
                    result.add(json.substring(valStart, i).trim());
                }
            }
        }

        return result;
    }

    // تبدیل نوع
    private static Object convert(Object value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class) {
            return value.toString();
        }

        if (targetType == List.class || targetType == ArrayList.class) {
            List<String> list = new ArrayList<>();
            if (value instanceof List) {
                for (Object item : (List<?>) value) {
                    list.add(item != null ? item.toString() : "");
                }
            }
            return list;
        }

        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number) return ((Number) value).intValue();
            return Integer.parseInt(value.toString());
        }

        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) return ((Number) value).longValue();
            return Long.parseLong(value.toString());
        }

        if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(value.toString());
        }

        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value.toString());
        }

        return value.toString();
    }
}