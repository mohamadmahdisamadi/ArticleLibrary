package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.api.service.article.ArticleReferenceService;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleReferenceRepository;
import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleReferenceServiceImpl implements ArticleReferenceService {

    @Autowired
    private ArticleReferenceRepository repository;

    public void addReferences(String articleId, List<String> citedArticleIds) throws Exception {
        repository.addMany(citedArticleIds.stream()
                .map(i -> new ArticleReferenceEntity()
                        .setCitingArticleId(articleId)
                        .setCitedArticleId(i))
                .toList());
    }

    public void updateReferences(String articleId, List<String> newCitedArticleIds) throws Exception {
        repository.deleteAllHaving("citingArticleId", articleId);
        repository.addMany(newCitedArticleIds.stream()
                .map(i -> new ArticleReferenceEntity()
                        .setCitingArticleId(articleId)
                        .setCitedArticleId(i))
                .toList());
    }

    public List<String> getReferences(String articleId, boolean ingoing) throws Exception {
        String fieldName = ingoing ? "citedArticleId" : "citingArticleId";
        return repository.searchField(fieldName, articleId, FieldComparator.EXACT).stream()
                        .map(a -> ingoing ? a.getCitingArticleId() : a.getCitedArticleId()).toList();
    }

    public int getReferencesCount(String articleId, boolean ingoing) throws Exception {
        String fieldName = ingoing ? "citedArticleId" : "citingArticleId";
        return repository.searchField(fieldName, articleId, FieldComparator.EXACT).size();
    }
}
