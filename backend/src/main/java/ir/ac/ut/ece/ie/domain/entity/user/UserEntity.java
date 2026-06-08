package ir.ac.ut.ece.ie.domain.entity.user;

import ir.ac.ut.ece.ie.domain.entity.common.SoftDeleteEntity;

import java.util.List;

public class UserEntity extends SoftDeleteEntity {
    private String username;
    private String password;
    private List<String> writtenArticles;

    public UserEntity() {}

    public String getPassword() { return password; }
    public UserEntity setPassword(String password) { this.password = password; return this; }

    public String getUsername() { return username; }
    public UserEntity setUsername(String username) { this.username = username; return this; }

    public List<String> getWrittenArticles() { return writtenArticles; }
    public UserEntity setWrittenArticles(List<String> writtenArticles) { this.writtenArticles = writtenArticles; return this; }
}


