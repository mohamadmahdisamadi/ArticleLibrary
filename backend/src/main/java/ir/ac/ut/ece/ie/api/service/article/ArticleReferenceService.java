package ir.ac.ut.ece.ie.api.service.article;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;

import java.util.List;


public interface ArticleReferenceService {
    public void addReferences(Long articleId, List<Long> citedArticleIds) throws Exception;
    public void updateReferences(Long articleId, List<Long> newCitedArticleIds) throws Exception;

    public List<ArticleEntity> getReferencesTo(Long articleId) throws Exception;
    public List<ArticleEntity> getReferencesFrom(Long articleId) throws Exception;
    public long countReferencesTo(Long articleId) throws Exception;
    public long countReferencesFrom(Long articleId) throws Exception;
}