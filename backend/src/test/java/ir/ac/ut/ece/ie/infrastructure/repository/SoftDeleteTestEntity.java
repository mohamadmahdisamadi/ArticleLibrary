package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.common.SoftDeleteEntity;

public class SoftDeleteTestEntity extends SoftDeleteEntity {
    private String title;

    public SoftDeleteTestEntity() {}

    public SoftDeleteTestEntity(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
