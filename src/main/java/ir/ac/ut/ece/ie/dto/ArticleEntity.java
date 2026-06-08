package ir.ac.ut.ece.ie.dto;

import ir.ac.ut.ece.ie.utils.DateTimeDisplayer;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleEntity extends SoftDeleteEntity {
    private String title;
    private String summary;
    private String body;
    private List<String> citedArticleIds;

    public ArticleEntity() { this.setIsDeleted(0); }

    public ArticleEntity setId(String id) { this.id = id; return this; }

//    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public ArticleEntity setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

    public ArticleEntity setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; return this; }

    public String getTitle() { return title; }
    public ArticleEntity setTitle(String title) { this.title = title; return this; }

    public String getSummary() { return summary; }
    public ArticleEntity setSummary(String summary) { this.summary = summary; return this; }

    public String getBody() { return body; }
    public ArticleEntity setBody(String body) { this.body = body; return this; }

    public List<String> getCitedArticleIds() { return citedArticleIds; }
    public ArticleEntity setCitedArticleIds(List<String> citedArticleIds) { this.citedArticleIds = citedArticleIds; return this; }
    public void addCitedArticleId(String citedArticleId) { this.citedArticleIds.add(citedArticleId); }
}