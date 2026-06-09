package ir.ac.ut.ece.ie.domain.entity.article;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Articles")
public class ArticleEntity extends BaseEntity {

    @Column(nullable = false, length = 64, unique = true)
    private String title;

    @Column(nullable = false, length = 256)
    private String summary;

    @Column(nullable = false, length = 1024)
    private String body;

    public ArticleEntity() {}

    public String getTitle() { return title; }
    public ArticleEntity setTitle(String title) { this.title = title; return this; }

    public String getSummary() { return summary; }
    public ArticleEntity setSummary(String summary) { this.summary = summary; return this; }

    public String getBody() { return body; }
    public ArticleEntity setBody(String body) { this.body = body; return this; }
}