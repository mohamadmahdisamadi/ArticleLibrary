package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.api.service.article.ArticleReferenceService;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleReferenceRepository;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleReferenceServiceImpl implements ArticleReferenceService {

    @Autowired private ArticleRepository articleRepository;
    @Autowired private ArticleReferenceRepository repository;

    public void addReferences(Long articleId, List<Long> citedArticleIds) throws Exception {
        ArticleEntity citingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new Exception("Article not found: " + articleId));

        List<ArticleEntity> citedArticles = new ArrayList<>();
        for (Long citedArticleId : citedArticleIds)
            citedArticles.add(articleRepository.findById(citedArticleId)
                    .orElseThrow(() -> new Exception("Article not found: " + citedArticleId)));

        repository.saveAll(citedArticles.stream().map(
                a -> new ArticleReferenceEntity().setCited(a).setCiting(citingArticle))
                .toList());
    }

    public void updateReferences(Long articleId, List<Long> newCitedArticleIds) throws Exception {
        repository.deleteByCiting_Id(articleId);
        this.addReferences(articleId, newCitedArticleIds);
    }

    public List<ArticleEntity> getReferencesTo(Long articleId) throws Exception {
        return repository.findByCited_Id(articleId).stream().map(ArticleReferenceEntity::getCiting).toList();
    }

    public List<ArticleEntity> getReferencesFrom(Long articleId) throws Exception {
        return repository.findByCiting_Id(articleId).stream().map(ArticleReferenceEntity::getCited).toList();
    }

    public long countReferencesFrom(Long articleId) throws Exception {
        return this.getReferencesFrom(articleId).size();
    }

    public long countReferencesTo(Long articleId) throws Exception {
        return this.getReferencesTo(articleId).size();
    }
}
