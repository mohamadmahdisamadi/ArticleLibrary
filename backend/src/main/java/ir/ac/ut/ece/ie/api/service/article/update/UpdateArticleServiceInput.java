package ir.ac.ut.ece.ie.api.service.article.update;

import java.util.List;

public record UpdateArticleServiceInput(String id, String title, String summary, String body, List<String> citedArticleIds) {}