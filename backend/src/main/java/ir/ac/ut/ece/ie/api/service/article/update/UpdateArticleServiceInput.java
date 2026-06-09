package ir.ac.ut.ece.ie.api.service.article.update;

import java.util.List;

public record UpdateArticleServiceInput(Long id, String title, String summary, String body, List<Long> citedArticleIds) {}