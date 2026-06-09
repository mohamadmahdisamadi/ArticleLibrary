package ir.ac.ut.ece.ie.api.controller.article.get;

import java.util.List;

public record GetArticleResponse(Long id, String title, String summary, String body,
                                 List<GetArticleTitleResponse> citedArticles,
                                 String createdAt, String lastModifiedAt) {}
