package ir.ac.ut.ece.ie.service;

import ir.ac.ut.ece.ie.dto.ArticleEntity;
import ir.ac.ut.ece.ie.repository.*;
import ir.ac.ut.ece.ie.server.common.resultpattern.ApiResult;


import java.util.*;

public class ArticleService {
    private final ArticleRepository repository;

    public ArticleService() {
        this.repository = new ArticleRepository();
    }

    public List<ArticleEntity> getAllArticles() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            System.err.println("Error reading articles: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<ArticleEntity> getArticleById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            System.err.println("Error finding article: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getArticleTitleById(String id) {
        try {
            Optional<ArticleEntity> article = repository.getById(id);

            if (article.isPresent())
                return Optional.of(article.get().getTitle());
            else {
                System.err.println("Error finding article: article with id " + id + " doesn't exist.");
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Error finding article: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<ArticleEntity> searchArticles(String query) {
        try {
            List<ArticleEntity> searchResult = new ArrayList<>();
            searchResult.addAll(repository.getByTitle(query));
            searchResult.addAll(repository.getBySummary(query));

            List<ArticleEntity> distinctSearchResult = new ArrayList<>();
            for (ArticleEntity article : searchResult)
                if (distinctSearchResult.stream().anyMatch(a -> a.getId().equals(article.getId())) == false)
                    distinctSearchResult.add(article);

            return distinctSearchResult;

        } catch (Exception e) {
            System.err.println("Error searching articles: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public ApiResult addArticle(String title, String summary, String body, List<String> citedArticleIds) throws Exception {
        boolean isValid = validateArticle(title, summary, body, true);
        if (isValid == false)
            return ApiResult.Failure("Invalid Input");

        try {

            ArticleEntity article = new ArticleEntity()
                    .setTitle(title)
                    .setSummary(summary)
                    .setBody(body)
                    .setCitedArticleIds(citedArticleIds);

            repository.add(article);
            return ApiResult.Succeed();
        } catch (Exception e) {
            System.err.println("Error adding article: " + e.getMessage());
            return ApiResult.Failure("Server Error");
        }
    }

    public ApiResult editArticle(String id, String title, String summary, String body, List<String> citedArticleIds) throws Exception {
        Optional<String> titleOptional = getArticleTitleById(id);
        boolean isValid = validateArticle(title, summary, body,
                titleOptional.isPresent() && titleOptional.get().equals(title) == false);

        if (isValid == false)
            return ApiResult.Failure("Invalid Input");

        try {
            ArticleEntity article = new ArticleEntity()
                    .setId(id)
                    .setTitle(title)
                    .setSummary(summary)
                    .setBody(body)
                    .setCitedArticleIds(citedArticleIds);

            repository.edit(article);
            return ApiResult.Succeed();
        } catch (Exception e) {
            System.err.println("Error editing article: " + e.getMessage());
            return ApiResult.Failure("Server Error");
        }
    }

    public ApiResult deleteArticle(String id) {
        try {
            repository.delete(id);
            return ApiResult.Succeed();
        } catch (Exception e) {
            return ApiResult.Failure(e.getMessage());
        }
    }

    public long getCitedByOthersCount(String articleId) {
        try {
            return repository.getAll().stream()
                    .filter(a ->
                            a.getCitedArticleIds().contains(articleId)
                            && a.getId().equals(articleId) == false)
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }


    private boolean validateArticle(String title, String summary, String body, boolean checkTitleExistence) {
        try {
            if (title == null || title.isEmpty()) {
                System.err.println("Error adding article: title can't be empty!");
                return false;
            }

            if (summary == null || summary.isEmpty()) {
                System.err.println("Error adding article: summary can't be empty!");
                return false;
            }

            if (body == null || body.isEmpty()) {
                System.err.println("Error adding article: body can't be empty!");
                return false;
            }

            if (checkTitleExistence && repository.checkIfTitleExists(title)) {
                System.err.println("Error adding article: title already exists.");
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }

    }
}