package ir.ac.ut.ece.ie.api.controller.article.get;

import java.time.LocalDateTime;

public record GetArticlePreviewResponse(String id, String title, String summary,
                                        int citedCount, int citingCount,
                                        String createdAt) {}
