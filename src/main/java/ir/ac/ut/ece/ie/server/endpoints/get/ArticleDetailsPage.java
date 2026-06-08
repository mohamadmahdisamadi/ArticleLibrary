package ir.ac.ut.ece.ie.server.endpoints.get;

import ir.ac.ut.ece.ie.server.common.endpointnames.HttpEndpoints;
import ir.ac.ut.ece.ie.service.ArticleService;
import ir.ac.ut.ece.ie.dto.ArticleEntity;
import ir.ac.ut.ece.ie.utils.HtmlUtil;
import ir.ac.ut.ece.ie.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ArticleDetailsPage {

	private ArticleService service;

	public ArticleDetailsPage() {
		service = new ArticleService();

	}

	public byte[] getPage(Map<String, String> args) throws Exception {
		validateArgs(args);
        String articleId = args.get("id");

		StringBuilder htmlBuilder = new StringBuilder();

		htmlBuilder.append("<!DOCTYPE html>\n");
		htmlBuilder.append("<html lang=\"en\">\n");
		htmlBuilder.append("<head>\n");
		htmlBuilder.append("    <meta charset=\"UTF-8\">\n");
		htmlBuilder.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
		htmlBuilder.append("    <title>Article Details</title>\n");
		htmlBuilder.append("    <style>\n");
		htmlBuilder.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
		htmlBuilder.append("        body {\n");
		htmlBuilder.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
		htmlBuilder.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n");
		htmlBuilder.append("            min-height: 100vh;\n");
		htmlBuilder.append("            padding: 40px 20px;\n");
		htmlBuilder.append("            color: #2d3436;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .container {\n");
		htmlBuilder.append("            max-width: 900px;\n");
		htmlBuilder.append("            margin: 0 auto;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        h1 {\n");
		htmlBuilder.append("            font-size: 2.2rem;\n");
		htmlBuilder.append("            color: white;\n");
		htmlBuilder.append("            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);\n");
		htmlBuilder.append("            margin-bottom: 25px;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .article-detail {\n");
		htmlBuilder.append("            background: white;\n");
		htmlBuilder.append("            border-radius: 16px;\n");
		htmlBuilder.append("            padding: 30px;\n");
		htmlBuilder.append("            box-shadow: 0 10px 40px rgba(0,0,0,0.1);\n");
		htmlBuilder.append("            margin-bottom: 25px;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .article-detail h1 {\n");
		htmlBuilder.append("            color: purple;\n");
		htmlBuilder.append("            font-size: 1.6rem;\n");
		htmlBuilder.append("            margin-bottom: 15px;\n");
		htmlBuilder.append("            font-weight: 700;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .article-meta {\n");
		htmlBuilder.append("            color: #636e72;\n");
		htmlBuilder.append("            font-size: 0.95rem;\n");
		htmlBuilder.append("            margin-bottom: 20px;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        p {\n");
		htmlBuilder.append("            color: #495057;\n");
		htmlBuilder.append("            line-height: 1.7;\n");
		htmlBuilder.append("            margin-bottom: 20px;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        ul { margin-left: 20px; }\n");
		htmlBuilder.append("        a {\n");
		htmlBuilder.append("            color: #764ba2;\n");
		htmlBuilder.append("            font-weight: 600;\n");
		htmlBuilder.append("            text-decoration: none;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .back-link {\n");
		htmlBuilder.append("            position: fixed;\n");
		htmlBuilder.append("            top: 20px;\n");
		htmlBuilder.append("            right: 20px;\n");
		htmlBuilder.append("            display: inline-block;\n");
		htmlBuilder.append("            font-size: 0.9rem;\n");
		htmlBuilder.append("            background: #ff6b6b;\n");
		htmlBuilder.append("            padding: 8px 20px;\n");
		htmlBuilder.append("            color: white;\n");
		htmlBuilder.append("            border-radius: 20px;\n");
		htmlBuilder.append("            box-shadow: 0 4px 15px rgba(255,107,107,0.4);\n");
		htmlBuilder.append("            transition: all 0.3s ease;\n");
		htmlBuilder.append("            z-index: 1000;\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .edit-link {\n");
		htmlBuilder.append("            display: inline-block;\n");
		htmlBuilder.append("            background: orange;\n");
		htmlBuilder.append("            color: white;\n");
		htmlBuilder.append("            padding: 12px 28px;\n");
		htmlBuilder.append("		    border: none;\n");
		htmlBuilder.append("            border-radius: 25px;\n");
		htmlBuilder.append("		    font-size: 1rem;\n");
		htmlBuilder.append("		    font-weight: 600;\n");
		htmlBuilder.append("		    cursor: pointer;\n");
		htmlBuilder.append("            transition: all 0.3s ease;\n");
		htmlBuilder.append("            box-shadow: 0 4px 15px rgba(220,53,69,0.4);\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .delete-btn {\n");
		htmlBuilder.append("            background: red;\n");
		htmlBuilder.append("            color: white;\n");
		htmlBuilder.append("            padding: 6px 16px;\n");
		htmlBuilder.append("            border: none;\n");
		htmlBuilder.append("            border-radius: 20px;\n");
		htmlBuilder.append("            font-size: 1rem;\n");
		htmlBuilder.append("            font-weight: 600;\n");
		htmlBuilder.append("            cursor: pointer;\n");
		htmlBuilder.append("            transition: all 0.3s ease;\n");
		htmlBuilder.append("            box-shadow: 0 4px 10px rgba(220,53,69,0.3);\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("		.delete-btn:hover {\n");
		htmlBuilder.append("		    transform: translateY(-2px);\n");
		htmlBuilder.append("		    box-shadow: 0 6px 20px rgba(220,53,69,0.6);\n");
		htmlBuilder.append("		}\n");
		htmlBuilder.append("        .back-link:hover {\n");
		htmlBuilder.append("            transform: translateY(-2px);\n");
		htmlBuilder.append("	        text-decoration: none;\n");
		htmlBuilder.append("            box-shadow: 0 6px 20px rgba(255,107,107,0.6);\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("        .edit-link:hover {\n");
		htmlBuilder.append("            transform: translateY(-2px);\n");
		htmlBuilder.append("	        text-decoration: none;\n");
		htmlBuilder.append("            box-shadow: 0 6px 20px rgba(255,107,107,0.6);\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("    </style>\n");
		htmlBuilder.append("</head>\n");
		htmlBuilder.append("<body>\n");

		Optional<ArticleEntity> articleOptional = service.getArticleById(articleId);

		if (articleOptional.isPresent()) {
			ArticleEntity article = articleOptional.get();
			String title = article.getTitle();

			htmlBuilder.append("<div class=\"article-detail\">\n");
			htmlBuilder.append("<h1>").append(HtmlUtil.escapeHtml(title)).append("</h1>\n");

			htmlBuilder
                    .append("<p class=\"article-meta\">Created at ").append(HtmlUtil.escapeHtml(article.displayDetailedCreationTime()))
                    .append(article.getCreatedAt().isEqual(article.getLastModifiedAt())
                            ? " and has not been modified since then."
                            : String.format(" and was last modified at %s", article.displayDetailedModificationTime()))
                    .append("</p>\n");

			htmlBuilder.append("<p><strong>Abstract:</strong> ").append(HtmlUtil.escapeHtml(article.getSummary())).append("</p>\n");
			htmlBuilder.append("<p>").append(HtmlUtil.escapeHtml(article.getBody())).append("</p>\n");

			if (article.getCitedArticleIds() != null && !article.getCitedArticleIds().isEmpty()) {
                htmlBuilder.append("<p class=\"article-meta\">Cited following articles: ");
                List<String> citedArticleIds = article.getCitedArticleIds();
                for (int i=0; i< citedArticleIds.size(); i++) {
                    String citedId = citedArticleIds.get(i);
                    Optional<String> citedTitleOptional = service.getArticleTitleById(citedId);

					if (citedTitleOptional.isPresent()) {
						htmlBuilder.append(String.format("<a href=\"/%s/%s\">%s</a>", HttpEndpoints.ArticleDetailsPage, HtmlUtil.escapeHtml(citedId), HtmlUtil.escapeHtml(citedTitleOptional.get())));
					} else {
						htmlBuilder.append(String.format("%s", HtmlUtil.escapeHtml("Deleted Article")));
					}
					htmlBuilder.append((i < citedArticleIds.size() - 2) ? ", " : (i == citedArticleIds.size() - 2) ? " and " : ".");
                }
			}

			htmlBuilder.append("</div>\n");
		} else {
			htmlBuilder.append("<p>Article not found.</p>\n");
		}

		htmlBuilder.append(String.format("<a href=\"/%s\" class=\"back-link\">Back to Main Page</a>\n", HttpEndpoints.MainPage));
		htmlBuilder.append(String.format("<a href=\"/%s/%s\" class=\"edit-link\">Edit Article</a>\n", HttpEndpoints.EditArticlePage, articleId));
		htmlBuilder.append("<button class=\"delete-btn\" onclick=\"deleteArticle()\">\uD83D\uDDD1\uFE0F</button>\n");

		htmlBuilder.append("    <script>\n");
		htmlBuilder.append("        function deleteArticle() {\n");
		htmlBuilder.append(String.format("fetch('/%s/%s', {\n", HttpEndpoints.DeleteArticleHandler, articleId));
		htmlBuilder.append("                method: 'DELETE'\n");
		htmlBuilder.append("            })\n");
		htmlBuilder.append("            .then(function(response) {\n");
		htmlBuilder.append("                return response.json();\n");
		htmlBuilder.append("            })\n");
		htmlBuilder.append("            .then(function(result) {\n");
		htmlBuilder.append("                if (result.isSuccess) {\n");
		htmlBuilder.append(String.format("      window.location.href = '/%s';\n", HttpEndpoints.MainPage));
		htmlBuilder.append("                } else {\n");
		htmlBuilder.append("                    alert('Error: ' + result.message);\n");
		htmlBuilder.append("                }\n");
		htmlBuilder.append("            })\n");
		htmlBuilder.append("            .catch(function(error) {\n");
		htmlBuilder.append("                alert('Error deleting article: ' + error.message);\n");
		htmlBuilder.append("            });\n");
		htmlBuilder.append("        }\n");
		htmlBuilder.append("    </script>\n");

		htmlBuilder.append("</body>\n");
		htmlBuilder.append("</html>\n");

		return htmlBuilder.toString().getBytes();
	}

    private void validateArgs(Map<String, String> args) throws Exception {
        if (args.containsKey("id") == false)
            throw new Exception("Error finding argument 'id'");
    }
}
