package ir.ac.ut.ece.ie.api.service.article;

import java.util.List;


public interface ArticleReferenceService {
    public void addReferences(String articleId, List<String> citedArticleIds) throws Exception;
    public void updateReferences(String articleId, List<String> newCitedArticleIds) throws Exception;

    public List<String> getReferences(String articleId, boolean ingoing) throws Exception;
    public int getReferencesCount(String articleId, boolean ingoing) throws Exception;

//    public void deleteReferences(String articleId, boolean ingoing) throws Exception;
}