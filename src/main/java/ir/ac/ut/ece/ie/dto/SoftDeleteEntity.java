package ir.ac.ut.ece.ie.dto;

public abstract class SoftDeleteEntity extends BaseEntity {
    private int isDeleted;
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
    public int getIsDeleted() { return this.isDeleted; }
}
