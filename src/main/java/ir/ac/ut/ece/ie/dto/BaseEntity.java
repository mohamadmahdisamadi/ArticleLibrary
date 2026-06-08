package ir.ac.ut.ece.ie.dto;

import ir.ac.ut.ece.ie.utils.DateTimeDisplayer;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    protected String id;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastModifiedAt;

    public String getId() { return id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public String displayShortCreationTime() { return DateTimeDisplayer.getShortDateTime(createdAt); }
    public String displayDetailedCreationTime() { return DateTimeDisplayer.getDetailedDateTime(createdAt); }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public String displayShortModificationTime() { return DateTimeDisplayer.getShortDateTime(lastModifiedAt); }
    public String displayDetailedModificationTime() { return DateTimeDisplayer.getDetailedDateTime(lastModifiedAt); }

}
