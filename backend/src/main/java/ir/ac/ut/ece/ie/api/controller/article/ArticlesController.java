package ir.ac.ut.ece.ie.api.controller.article;

import ir.ac.ut.ece.ie.api.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.api.controller.article.create.CreateArticleRequest;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticlePreviewResponse;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticleResponse;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticleTitleResponse;
import ir.ac.ut.ece.ie.api.controller.article.update.UpdateArticleRequest;
import ir.ac.ut.ece.ie.api.service.article.ArticleService;
import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;


import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;
import ir.ac.ut.ece.ie.api.utils.DateTimeDisplayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = {"*"})
public class ArticlesController {

	@Autowired
	private ArticleService service;

	@PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<Void> createArticle(@RequestBody CreateArticleRequest request) {
		try {
			CreateArticleServiceInput serviceInput = new CreateArticleServiceInput(
					request.title(), request.summary(),	request.body(),	request.citedArticleIds());
			service.createArticle(serviceInput);
			return ApiResult.success();
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<Void> updateArticle(@PathVariable("id") Long id, @RequestBody UpdateArticleRequest request) {
		try {
			UpdateArticleServiceInput serviceInput = new UpdateArticleServiceInput(
					id, request.title(), request.summary(), request.body(), request.citedArticleIds());

			service.updateArticle(serviceInput);
			return ApiResult.success();
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}



	@GetMapping(value = "",	produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<List<GetArticleResponse>> getArticles() {
		try {
			return ApiResult.success(service.getArticlesDetails(null).stream()
					.map(a -> new GetArticleResponse(
							a.id(),	a.title(), a.summary(),	a.body(),
							a.citedArticles().stream().map(citedArticle -> new GetArticleTitleResponse(citedArticle.id(), citedArticle.title())).toList(),
							DateTimeDisplayer.getDetailedDateTime(a.createdAt()),
							DateTimeDisplayer.getDetailedDateTime(a.lastModifiedAt())))
					.toList());
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<GetArticleResponse> getArticle(@PathVariable("id") Long id) {
		try {
			GetArticleDetailsServiceOutput article = service.getArticleDetails(id);

			return ApiResult.success(new GetArticleResponse(
					article.id(), article.title(), article.summary(), article.body(),
					article.citedArticles().stream().map(citedArticle -> new GetArticleTitleResponse(citedArticle.id(), citedArticle.title())).toList(),
					DateTimeDisplayer.getDetailedDateTime(article.createdAt()),
					DateTimeDisplayer.getDetailedDateTime(article.lastModifiedAt())));
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@GetMapping(value = "/preview",	produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<List<GetArticlePreviewResponse>> getArticlesPreview() {
		try {
			return ApiResult.success(service.getArticlesPreview(null).stream()
					.map(a -> new GetArticlePreviewResponse(
							a.id(),	a.title(), a.summary(), a.citedCount(), a.citingCount(),
							DateTimeDisplayer.getShortDateTime(a.createdAt())))
					.toList());

		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@GetMapping(value = "/{id}/preview", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<GetArticlePreviewResponse> getArticlePreview(@PathVariable("id") Long id) {
		try {
			GetArticlePreviewServiceOutput article = service.getArticlePreview(id);

			return ApiResult.success(new GetArticlePreviewResponse(
					article.id(), article.title(), article.summary(), article.citedCount(), article.citingCount(),
					DateTimeDisplayer.getShortDateTime(article.createdAt())));
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@GetMapping(value = "/title",	produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<List<GetArticleTitleResponse>> getArticlesTitle() {
		try {
			return ApiResult.success(service.getArticlesTitle().stream()
					.map(a -> new GetArticleTitleResponse(
							a.id(),	a.title()))
					.toList());

		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<List<GetArticlePreviewResponse>> searchArticles(@RequestParam String query) {
		try {
			return ApiResult.success(service.getArticlesPreview(query).stream()
					.map(a -> new GetArticlePreviewResponse(
							a.id(),	a.title(), a.summary(), a.citedCount(), a.citingCount(),
							DateTimeDisplayer.getShortDateTime(a.createdAt())))
					.toList());

		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<Void> deleteAllArticles() {
		try {
			service.deleteArticles();
			return ApiResult.success();
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}

	@DeleteMapping(value = "/{id}",	produces = MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<Void> deleteArticle(@PathVariable("id") Long id) {
		try {
			service.deleteArticle(id);
			return ApiResult.success();
		} catch (Exception e) {
			return ApiResult.failure(e.getMessage());
		}
	}
}