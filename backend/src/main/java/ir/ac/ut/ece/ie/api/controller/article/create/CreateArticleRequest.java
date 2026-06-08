package ir.ac.ut.ece.ie.api.controller.article.create;

import java.util.List;

public record CreateArticleRequest(String title, String summary, String body, List<String> citedArticleIds) {}