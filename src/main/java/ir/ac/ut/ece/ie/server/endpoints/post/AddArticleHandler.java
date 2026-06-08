package ir.ac.ut.ece.ie.server.endpoints.post;

import ir.ac.ut.ece.ie.dto.AddArticleRequest;
import ir.ac.ut.ece.ie.server.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.service.ArticleService;
import ir.ac.ut.ece.ie.utils.JsonParser;

public class AddArticleHandler {
    private final ArticleService service;

    public AddArticleHandler() { service = new ArticleService(); }

    public byte[] handle(String requestAsString) throws Exception {
        AddArticleRequest request = new AddArticleRequest();
        try {
            request = JsonParser.parse(requestAsString, AddArticleRequest.class);
        } catch (Exception e) {
            return ApiResult.Failure("Invalid Input").toString().getBytes();
        }

        ApiResult serviceResponse = service.addArticle(request.title, request.summary, request.body, request.citations);
        return serviceResponse.toString().getBytes();
    }
}
