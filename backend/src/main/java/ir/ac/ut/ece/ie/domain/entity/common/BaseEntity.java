package ir.ac.ut.ece.ie.domain.entity.common;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public BaseEntity() {}

    public boolean passesQueryFilter() { return true; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
}
