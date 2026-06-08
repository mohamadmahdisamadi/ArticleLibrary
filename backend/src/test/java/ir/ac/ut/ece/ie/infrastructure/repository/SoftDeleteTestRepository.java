package ir.ac.ut.ece.ie.infrastructure.repository;

public class SoftDeleteTestRepository extends BaseRepository<SoftDeleteTestEntity> {
    public SoftDeleteTestRepository(String filePath) {
        super(filePath, SoftDeleteTestEntity.class);
    }
}

