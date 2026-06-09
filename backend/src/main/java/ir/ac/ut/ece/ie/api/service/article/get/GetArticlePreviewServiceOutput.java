package ir.ac.ut.ece.ie.api.service.article.get;

import java.time.LocalDateTime;

public record GetArticlePreviewServiceOutput(Long id, String title, String summary,
                                             long citedCount, long citingCount,
                                             LocalDateTime createdAt) {}
