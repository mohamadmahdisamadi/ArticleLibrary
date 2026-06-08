package ir.ac.ut.ece.ie.infrastructure.repository;

public class TestRepository extends BaseRepository<TestEntity> {
    public TestRepository(String filePath) {
        super(filePath, TestEntity.class);
    }
}
