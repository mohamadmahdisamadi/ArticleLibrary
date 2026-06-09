package ir.ac.ut.ece.ie.api.service.article;

import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleTitleServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;

import java.util.List;

public interface ArticleService {
    public List<GetArticleDetailsServiceOutput> getArticlesDetails(String query) throws Exception;
    public List<GetArticlePreviewServiceOutput> getArticlesPreview(String query) throws Exception;
    public List<GetArticleTitleServiceOutput> getArticlesTitle() throws Exception;

    public GetArticleDetailsServiceOutput getArticleDetails(Long id) throws Exception;
    public GetArticlePreviewServiceOutput getArticlePreview(Long id) throws Exception;

    public void createArticle(CreateArticleServiceInput request) throws Exception;
    public void updateArticle(UpdateArticleServiceInput request) throws Exception;

    public void deleteArticle(Long id) throws Exception;
    public void deleteArticles() throws Exception;
}
