package ir.ac.ut.ece.ie.infrastructure.repository;

public class SearchableTestRepository extends SearchableRepository<TestEntity> {
    public SearchableTestRepository(String fileName) {
        super(fileName, TestEntity.class);
    }
}
