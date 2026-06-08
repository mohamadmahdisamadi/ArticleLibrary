package ir.ac.ut.ece.ie.server.endpoints.put;

import ir.ac.ut.ece.ie.dto.EditArticleRequest;
import ir.ac.ut.ece.ie.server.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.service.ArticleService;
import ir.ac.ut.ece.ie.utils.JsonParser;

import java.util.Map;

public class EditArticleHandler {
    private final ArticleService service;

    public EditArticleHandler() { service = new ArticleService(); }

    public byte[] handle(Map<String ,String> params, String requestBodyAsString) throws Exception {
        if (params.containsKey("id") == false)
            return ApiResult.Failure("Invalid Input").toString().getBytes();

        EditArticleRequest request = JsonParser.parse(requestBodyAsString, EditArticleRequest.class);
        String id = params.get("id");

        ApiResult serviceResponse = service.editArticle(id, request.title, request.summary, request.body, request.citations);
        return serviceResponse.toString().getBytes();
    }

}
