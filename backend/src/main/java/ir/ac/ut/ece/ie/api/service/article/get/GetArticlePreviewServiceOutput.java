package ir.ac.ut.ece.ie.api.service.article.get;

import java.time.LocalDateTime;

public record GetArticlePreviewServiceOutput(String id, String title, String summary,
                                             int citedCount, int citingCount,
                                             LocalDateTime createdAt) {}
