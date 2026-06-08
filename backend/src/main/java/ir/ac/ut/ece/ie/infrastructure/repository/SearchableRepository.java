package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;
import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public abstract class SearchableRepository<T extends BaseEntity> extends BaseRepository<T> {

    public SearchableRepository(String fileName, Class<T> type) {
        super(fileName, type);
    }

    public boolean valueExists(String fieldName, Object expectedValue) throws Exception {
        return getAll().stream().anyMatch(e -> FieldComparator.EXACT.match(getFieldValue(e, fieldName), expectedValue));
    }

    public List<T> searchField(String fieldName, Object expectedValue, FieldComparator.CompareFunction comparator) throws Exception {
        return getAll().stream().filter(e -> comparator.match(getFieldValue(e, fieldName), expectedValue)).toList();
    }

    public List<T> searchAND(Map<String, Object> conditions, FieldComparator.CompareFunction comparator) throws Exception {
        return getAll().stream().filter(e -> matchesAllConditions(e, conditions, comparator)).toList();
    }

    public List<T> searchOR(Map<String, Object> conditions, FieldComparator.CompareFunction comparator) throws Exception {
        return getAll().stream().filter(e -> matchesAnyCondition(e, conditions, comparator)).toList();
    }

    private boolean matchesAllConditions(T entity, Map<String, Object> conditions, FieldComparator.CompareFunction comparator) {
        return conditions.entrySet().stream()
                .allMatch(e -> comparator.match(getFieldValue(entity, e.getKey()), e.getValue()));
    }

    private boolean matchesAnyCondition(T entity, Map<String, Object> conditions, FieldComparator.CompareFunction comparator) {
        return conditions.entrySet().stream()
                .anyMatch(e -> comparator.match(getFieldValue(entity, e.getKey()), e.getValue()));
    }
}
