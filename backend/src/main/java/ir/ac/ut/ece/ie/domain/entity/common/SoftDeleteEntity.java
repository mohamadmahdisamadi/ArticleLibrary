package ir.ac.ut.ece.ie.domain.entity.common;

public abstract class SoftDeleteEntity extends BaseEntity {
    private int isDeleted;
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
    public int getIsDeleted() { return this.isDeleted; }

    public SoftDeleteEntity() { isDeleted = 0; }
    @Override
    public boolean passesQueryFilter() { return isDeleted == 0; }
}
