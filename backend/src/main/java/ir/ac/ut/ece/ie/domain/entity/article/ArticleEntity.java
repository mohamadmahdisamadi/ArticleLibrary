package ir.ac.ut.ece.ie.domain.entity.article;

import ir.ac.ut.ece.ie.domain.entity.common.SoftDeleteEntity;

public class ArticleEntity extends SoftDeleteEntity {
    private String title;
    private String summary;
    private String body;

    public ArticleEntity() {}

    public String getTitle() { return title; }
    public ArticleEntity setTitle(String title) { this.title = title; return this; }

    public String getSummary() { return summary; }
    public ArticleEntity setSummary(String summary) { this.summary = summary; return this; }

    public String getBody() { return body; }
    public ArticleEntity setBody(String body) { this.body = body; return this; }
}