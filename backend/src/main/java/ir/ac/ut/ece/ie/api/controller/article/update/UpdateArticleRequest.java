package ir.ac.ut.ece.ie.api.controller.article.update;

import java.util.List;

public record UpdateArticleRequest(String title, String summary, String body, List<Long> citedArticleIds) {}