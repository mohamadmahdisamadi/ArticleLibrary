package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;
import ir.ac.ut.ece.ie.domain.entity.common.SoftDeleteEntity;
import ir.ac.ut.ece.ie.infrastructure.utils.GenericCsvSerializer;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public abstract class BaseRepository<T extends BaseEntity> {

    private final String filePath;
    protected final GenericCsvSerializer<T> serializer;

    protected BaseRepository(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.serializer = new GenericCsvSerializer<>(type, ",");
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + filePath, e);
            }
        }
    }

    protected List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) lines.add(line);
        br.close();
        return lines;
    }

    protected void writeAllLines(List<String> lines) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }
        bw.close();
    }

    public void add(T entity) throws Exception {
        if (entity == null)
            return;
        entity.setId(UUID.randomUUID().toString());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setLastModifiedAt(entity.getCreatedAt());
        List<String> lines = readAllLines();
        lines.add(serializer.serialize(entity));
        writeAllLines(lines);
    }

    public void addMany(List<T> entities) throws Exception {
        if (entities.isEmpty())
            return;
        List<String> lines = readAllLines();
        for (T entity : entities) {
            entity.setId(UUID.randomUUID().toString());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setLastModifiedAt(entity.getCreatedAt());
            lines.add(serializer.serialize(entity));
        }
        writeAllLines(lines);
    }

    public List<T> getAll() throws Exception {
        List<String> lines = readAllLines();
        List<T> result = new ArrayList<>();

        for (String l : lines) {
            T e = serializer.deserialize(l);
            if (e != null && e.passesQueryFilter())
                result.add(e);
        }

        return result;
    }

    public T getById(String id) throws Exception {
        return getAll().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void delete(String id) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();

        boolean found = false;

        for (String l : lines) {
            T e = serializer.deserialize(l);

            if (e != null && e.getId().equals(id)) {
                found = true;
                if (e instanceof SoftDeleteEntity) {
                    ((SoftDeleteEntity) e).setIsDeleted(1);
                    e.setLastModifiedAt(LocalDateTime.now());
                    newLines.add(serializer.serialize(e));
                }
            } else newLines.add(l);
        }

        if (!found)
            throw new IllegalArgumentException("Not found");

        writeAllLines(newLines);
    }

    public void deleteMany(List<String> ids) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();

        for (String l : lines) {
            T e = serializer.deserialize(l);

            if (ids.contains(e.getId())) {
                if (e instanceof SoftDeleteEntity) {
                    ((SoftDeleteEntity) e).setIsDeleted(1);
                    e.setLastModifiedAt(LocalDateTime.now());
                    newLines.add(serializer.serialize(e));
                }
            } else {
                newLines.add(l);
            }
        }

        writeAllLines(newLines);
    }

    public void deleteAllHaving(String fieldName, String value) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();

        for (String l : lines) {
            T e = serializer.deserialize(l);

            if (getFieldValue(e, fieldName).equals(value)) {
                if (e instanceof SoftDeleteEntity) {
                    ((SoftDeleteEntity) e).setIsDeleted(1);
                    e.setLastModifiedAt(LocalDateTime.now());
                    newLines.add(serializer.serialize(e));
                }
            } else {
                newLines.add(l);
            }
        }

        writeAllLines(newLines);
    }


    public void deleteAll() throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();

        for (String l : lines) {
            T e = serializer.deserialize(l);

            if (e instanceof SoftDeleteEntity) {
                ((SoftDeleteEntity) e).setIsDeleted(1);
                e.setLastModifiedAt(LocalDateTime.now());
                newLines.add(serializer.serialize(e));
            }
        }

        writeAllLines(newLines);
    }


    public void edit(T updatedEntity) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();

        boolean found = false;

        for (String l : lines) {
            T e = serializer.deserialize(l);

            if (e != null  && e.getId().equals(updatedEntity.getId())) {
                if (!e.passesQueryFilter())
                    throw new IllegalArgumentException("Entity does not exist");

                updatedEntity.setCreatedAt(e.getCreatedAt());
                updatedEntity.setLastModifiedAt(LocalDateTime.now());
                newLines.add(serializer.serialize(updatedEntity));
                found = true;

            } else newLines.add(l);
        }

        if (!found)
            throw new IllegalArgumentException("Not found");

        writeAllLines(newLines);
    }

    protected Object getFieldValue(T entity, String fieldName) {
        Class<?> current = entity.getClass();

        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(entity);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
