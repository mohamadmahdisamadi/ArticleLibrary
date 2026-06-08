package ir.ac.ut.ece.ie.api.service.article.get;

import java.time.LocalDateTime;
import java.util.List;

public record GetArticleDetailsServiceOutput(String id, String title, String summary, String body,
                                             List<GetArticleTitleServiceOutput> citedArticles,
                                             LocalDateTime createdAt, LocalDateTime lastModifiedAt) {}
