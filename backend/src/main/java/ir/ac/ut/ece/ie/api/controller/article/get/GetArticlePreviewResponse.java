package ir.ac.ut.ece.ie.api.controller.article.get;

import java.time.LocalDateTime;

public record GetArticlePreviewResponse(Long id, String title, String summary,
                                        long citedCount, long citingCount,
                                        String createdAt) {}
