package ir.ac.ut.ece.ie.domain.entity.article;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;

public class ArticleReferenceEntity extends BaseEntity {
    private String citedArticleId;
    private String citingArticleId;

    public ArticleReferenceEntity() {}

    public ArticleReferenceEntity setCitedArticleId(String citedArticleId) { this.citedArticleId = citedArticleId; return this; }
    public String getCitedArticleId() { return citedArticleId; }

    public ArticleReferenceEntity setCitingArticleId(String citingArticleId) { this.citingArticleId = citingArticleId; return this; }
    public String getCitingArticleId() { return citingArticleId; }
}
