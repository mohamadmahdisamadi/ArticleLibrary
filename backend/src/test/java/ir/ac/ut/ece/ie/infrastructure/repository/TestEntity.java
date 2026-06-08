package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

public class TestEntity extends BaseEntity {
    private String name;
    private int age;
    private List<String> tags;
    private LocalDateTime eventDate;

    public TestEntity() {}

    public TestEntity(String name, int age, List<String> tags, LocalDateTime eventDate) {
        this.name = name;
        this.age = age;
        this.tags = tags;
        this.eventDate = eventDate;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setAge(int age) { this.age = age; }
    public int getAge() { return age; }

    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getTags() { return tags; }

    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public LocalDateTime getEventDate() { return eventDate; }
}
