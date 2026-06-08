package ir.ac.ut.ece.ie.server.endpoints.delete;

import ir.ac.ut.ece.ie.server.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.service.ArticleService;
import java.util.Map;

public class DeleteArticleHandler {
    private final ArticleService service;

    public DeleteArticleHandler() { service = new ArticleService(); }

    public byte[] handle(Map<String, String> queryParams) {
        if (queryParams.containsKey("id") == false)
            return ApiResult.Failure("Invalid Input").toString().getBytes();

        String id = queryParams.get("id");
        ApiResult serviceResponse = service.deleteArticle(id);
        return serviceResponse.toString().getBytes();
    }
}
