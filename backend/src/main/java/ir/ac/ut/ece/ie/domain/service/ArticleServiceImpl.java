package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.api.service.article.ArticleReferenceService;
import ir.ac.ut.ece.ie.api.service.article.ArticleService;
import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleTitleServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleRepository;
import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository repository;

    @Autowired
    private ArticleReferenceService articleReferenceService;

    public List<GetArticleDetailsServiceOutput> getArticlesDetails(String query) throws Exception {
        List<ArticleEntity> articles = (query == null) ? repository.getAll() : searchArticles(query);
        List<GetArticleDetailsServiceOutput> output = new ArrayList<>();
        for (ArticleEntity article : articles)
            output.add(getArticleDetails(article));
        return output;
    }

    public List<GetArticlePreviewServiceOutput> getArticlesPreview(String query) throws Exception {
        List<ArticleEntity> articles = (query == null) ? repository.getAll() : searchArticles(query);
        List<GetArticlePreviewServiceOutput> output = new ArrayList<>();
        for (ArticleEntity article : articles)
            output.add(getArticlePreview(article));
        return output;
    }

    @Override
    public List<GetArticleTitleServiceOutput> getArticlesTitle() throws Exception {
        return repository.getAll().stream()
                .map(a -> new GetArticleTitleServiceOutput(a.getId(), a.getTitle())).toList();
    }

    public GetArticleDetailsServiceOutput getArticleDetails(String id) throws Exception {
        ArticleEntity article = repository.getById(id);
        if (article == null)
            throw new Exception("Not found");
        return getArticleDetails(article);
    }

    public GetArticlePreviewServiceOutput getArticlePreview(String id) throws Exception {
        ArticleEntity article = repository.getById(id);
        if (article == null)
            throw new Exception("Not found");
        return getArticlePreview(article);
    }

    public void createArticle(CreateArticleServiceInput request) throws Exception {
        String errorMessage = validateArticle(request.title(), request.summary(), request.body(), true);
        if (!errorMessage.isEmpty())
            throw new Exception(errorMessage);

        ArticleEntity article = new ArticleEntity()
                .setTitle(request.title())
                .setSummary(request.summary())
                .setBody(request.body());

        repository.add(article);
        articleReferenceService.addReferences(article.getId(), request.citedArticleIds());
    }

    public void updateArticle(UpdateArticleServiceInput request) throws Exception {
         ArticleEntity article = repository.getById(request.id());
        if (article == null)
            throw new Exception("Not found");

        String oldTitle = article.getTitle();

        String errorMessage = validateArticle(request.title(), request.summary(), request.body(),
                (oldTitle != null && !oldTitle.equals(request.title())));

        if (!errorMessage.isEmpty())
            throw new Exception(errorMessage);

        article.setTitle(request.title())
                .setSummary(request.summary())
                .setBody(request.body());

        repository.edit(article);
        articleReferenceService.updateReferences(article.getId(), request.citedArticleIds());
    }

    public void deleteArticle(String id) throws Exception { repository.delete(id); }

    public void deleteArticles() throws Exception { repository.deleteAll(); }

    private List<ArticleEntity> searchArticles(String query) throws Exception {
        List<ArticleEntity> searchResult = new ArrayList<>();
        searchResult.addAll(repository.searchField("title", query, FieldComparator.CONTAINS));
        searchResult.addAll(repository.searchField("summary", query, FieldComparator.CONTAINS));

        List<ArticleEntity> distinctSearchResult = new ArrayList<>();
        for (ArticleEntity article : searchResult)
            if (distinctSearchResult.stream().noneMatch(a -> a.getId().equals(article.getId())))
                distinctSearchResult.add(article);

        return distinctSearchResult;
    }


    private GetArticleDetailsServiceOutput getArticleDetails(ArticleEntity article) throws Exception {
        List<String> referenceArticleIds = articleReferenceService.getReferences(article.getId(), false);
        List<GetArticleTitleServiceOutput> citedArticles = new ArrayList<>();

        for (String referenceArticleId : referenceArticleIds) {
            citedArticles.add(new GetArticleTitleServiceOutput(
                    referenceArticleId, repository.getById(referenceArticleId).getTitle()));
        }

        return new GetArticleDetailsServiceOutput(
                article.getId(), article.getTitle(), article.getSummary(), article.getBody(),
                citedArticles, article.getCreatedAt(), article.getLastModifiedAt());
    }

    private GetArticlePreviewServiceOutput getArticlePreview(ArticleEntity article) throws Exception {
        return new GetArticlePreviewServiceOutput(
                article.getId(), article.getTitle(), article.getSummary(),
                articleReferenceService.getReferencesCount(article.getId(), true),
                articleReferenceService.getReferencesCount(article.getId(), false),
                article.getCreatedAt()
        );
    }

    private String validateArticle(String title, String summary, String body, boolean checkTitleExistence) {
        try {
            String message = "";
            if (title == null || title.isEmpty())
                message += "title can't be empty";

            if (summary == null || summary.isEmpty())
                message += ((!message.isEmpty()) ? " | " : "") + "summary can't be empty";

            if (body == null || body.isEmpty())
                message += ((!message.isEmpty()) ? " | " : "") + "body can't be empty";

            if (checkTitleExistence && repository.valueExists("title", title))
                message += ((!message.isEmpty()) ? " | " : "") + "title already exists";


            return message;
        } catch (Exception e) {
            return "server error";
        }
    }
}