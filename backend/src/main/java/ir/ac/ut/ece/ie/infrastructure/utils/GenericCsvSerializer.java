package ir.ac.ut.ece.ie.infrastructure.utils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GenericCsvSerializer<T> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Class<T> type;
    private final String delimiter;

    public GenericCsvSerializer(Class<T> type, String delimiter) {
        this.type = type;
        this.delimiter = delimiter;
    }

    public String serialize(T obj) {
        try {
            StringBuilder sb = new StringBuilder();
            List<Field> fields = getAllFields(type);

            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                f.setAccessible(true);
                Object value = f.get(obj);

                String encoded = encodeValue(value);
                sb.append(encoded);

                if (i < fields.size() - 1) sb.append(delimiter);
            }

            return sb.toString();


        } catch (Exception e) {
            throw new RuntimeException("Serialization failed for " + type.getName(), e);
        }
    }

    public T deserialize(String line) {
        try {
            if (line == null || line.trim().isEmpty()) return null;

            String[] parts = line.split(delimiter, -1);
            List<Field> fields = getAllFields(type);

            if (parts.length < fields.size()) return null;

            T instance = type.getDeclaredConstructor().newInstance();

            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                f.setAccessible(true);
                Object value = decodeValue(parts[i], f.getType());
                f.set(instance, value);
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed for " + type.getName(), e);
        }
    }

    private String encodeValue(Object value) {
        if (value == null) return "";

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return EncoderUtil.encode(String.join(";", (List<String>) list));
        }

        if (value instanceof LocalDateTime) {
            return EncoderUtil.encode(((LocalDateTime) value).format(FORMATTER));
        }

        return EncoderUtil.encode(value.toString());
    }

    private Object decodeValue(String part, Class<?> targetType) {
        String decoded = EncoderUtil.decode(part);

        if (targetType == String.class)
            return decoded;

        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(decoded.isEmpty() ? "0" : decoded);

        if (targetType == LocalDateTime.class)
            return decoded.isEmpty() ? null : LocalDateTime.parse(decoded, FORMATTER);

        if (List.class.isAssignableFrom(targetType))
            return decoded.isEmpty() ? new ArrayList<>() : Arrays.asList(decoded.split(";"));

        return decoded;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;

        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }

        return fields;
    }
}
