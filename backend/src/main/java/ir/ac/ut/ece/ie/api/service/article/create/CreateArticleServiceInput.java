package ir.ac.ut.ece.ie.api.service.article.create;

import java.util.List;

public record CreateArticleServiceInput(String title, String summary, String body, List<String> citedArticleIds) {}