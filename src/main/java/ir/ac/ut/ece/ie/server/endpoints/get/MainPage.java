package ir.ac.ut.ece.ie.server.endpoints.get;

import ir.ac.ut.ece.ie.server.common.endpointnames.HttpEndpoints;
import ir.ac.ut.ece.ie.service.ArticleService;
import ir.ac.ut.ece.ie.dto.ArticleEntity;
import ir.ac.ut.ece.ie.utils.HtmlUtil;

import java.util.List;
import java.util.Map;

public class MainPage {
    private ArticleService service;

    public MainPage() {
        service = new ArticleService();
    }

    public byte[] getPage(Map<String, String> args) {
        //        htmlBuilder.append("    <link rel=\"stylesheet\" href=\"styles.css\">\n");

        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("<title>Article Repository</title>\n")
                .append("<style>\n")
                .append(" * { margin: 0; padding: 0; box-sizing: border-box; }\n")
                .append("body {font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\nmin-height: 100vh; padding: 40px 20px;\n}\n")
                .append(".container {\nmax-width: 900px;\nmargin: 0 auto;\n}\n")
                .append(".header {\ndisplay: flex;\njustify-content: space-between;\nalign-items: center;\nmargin-bottom: 40px;\n}\n")
                .append(".header h1 {\ncolor: white;\nfont-size: 2.5rem;\ntext-shadow: 2px 2px 4px rgba(0,0,0,0.2);\n}\n")
                .append(".add-btn {\nbackground: #ff6b6b;\ncolor: white;\npadding: 12px 28px;\nborder: none;\nborder-radius: 25px;\nfont-size: 1rem;\nfont-weight: 600;\ncursor: pointer;\ntext-decoration: none;\ntransition: all 0.3s ease;\nbox-shadow: 0 4px 15px rgba(255,107,107,0.4);\n}\n")
                .append(".add-btn:hover {\ntransform: translateY(-2px);\nbox-shadow: 0 6px 20px rgba(255,107,107,0.6);\n}\n")
                .append(".search-form {\ndisplay: flex;\ngap: 10px;\nmargin-bottom: 30px;\n}\n")
                .append(".search-form input {\nflex: 1;\npadding: 12px 20px;\nborder: none;\nborder-radius: 25px;\nfont-size: 1rem;\nbox-shadow: 0 4px 15px rgba(0,0,0,0.1);\n}\n")
                .append(".search-form input:focus {\noutline: none;\nbox-shadow: 0 4px 20px rgba(0,0,0,0.2);\n}\n")
                .append(".search-btn {\nbackground: #20c997;\ncolor: white;\npadding: 12px 28px;\nborder: none;\nborder-radius: 25px;\nfont-size: 1rem;\nfont-weight: 600;\ncursor: pointer;\ntransition: all 0.3s ease;\nbox-shadow: 0 4px 15px rgba(32,201,151,0.4);\n}\n")
                .append(".search-btn:hover {\ntransform: translateY(-2px);\nbox-shadow: 0 6px 20px rgba(32,201,151,0.6);\n}\n")
                .append(".clear-search {\nbackground: #6c757d;\ncolor: white;\npadding: 12px 20px;\nborder: none;\nborder-radius: 25px;\nfont-size: 1rem;\nfont-weight: 600;\ncursor: pointer;\ntext-decoration: none;\ntransition: all 0.3s ease;\n}\n")
                .append(".clear-search:hover {\nbackground: #5a6268;\n}\n")
                .append(".article-card {\nbackground: white;\nborder-radius: 16px;\npadding: 30px;\nmargin-bottom: 25px;\nbox-shadow: 0 10px 40px rgba(0,0,0,0.1);\ntransition: transform 0.3s ease;\n}\n")
                .append(".article-card:hover {\ntransform: translateY(-5px);\n}\n")
                .append(".article-link {text-decoration: none;\ncolor: inherit;\ndisplay: block;\n}\n")
                .append(".article-title {\ncolor: #2d3436;\nfont-size: 1.5rem;\nmargin-bottom: 15px;\nfont-weight: 700;\n}\n")
                .append(".article-abstract {\ncolor: #636e72;\nline-height: 1.8;\nmargin-bottom: 20px;\n}\n")
                .append(".article-footer {\ndisplay: flex;\njustify-content: space-between;\nalign-items: center;\npadding-top: 20px;\nborder-top: 1px solid #eee;\n}\n")
                .append(".stats {\ndisplay: flex;\ngap: 20px;\n}\n")
                .append(".stat-badge {\ndisplay: flex;\nalign-items: center;\ngap: 6px;\nbackground: #f8f9fa;\npadding: 8px 16px;\nborder-radius: 20px;\nfont-size: 0.9rem;\ncolor: #495057;\n}\n")
                .append(".stat-badge.citations {\nbackground: #e3f2fd;\ncolor: #1976d2;\n}\n")
                .append(".stat-badge.cited-by {\nbackground: #fff3e0;\ncolor: #f57c00;\n}\n")
                .append(".date {\ncolor: #95a5a6;\nfont-size: 0.9rem;\n}\n")
                .append(".empty-state {\ntext-align: center;\ncolor: white;\npadding: 60px 20px;\n}\n")
                .append(".empty-state h2 {\nfont-size: 1.8rem;\nmargin-bottom: 10px;\n}\n")
                .append(".search-results-info {\ncolor: white;\nmargin-bottom: 20px;\nfont-size: 1.1rem;\n}\n")
                .append("</style>\n</head>\n<body>\n");

        htmlBuilder.append("<div class=\"container\">\n");

        String query = args.getOrDefault("query", "");

        htmlBuilder
                .append("<div class=\"header\">\n")
                .append("<h1>Articles</h1>\n")
                .append("<a href=\"/").append(HttpEndpoints.AddArticlePage).append("\" class=\"add-btn\">Add Article ➕</a>\n")
                .append("</div>\n");

        htmlBuilder.append("    <form class=\"search-form\" method=\"GET\" action=\"/").append(HttpEndpoints.MainPage).append("\">\n");
        htmlBuilder.append("        <input type=\"text\" name=\"query\" placeholder=\"Search articles...\" value=\"").append(HtmlUtil.escapeHtml(query)).append("\" />\n");
        htmlBuilder.append("        <button class=\"search-btn\" type=\"submit\">Search</button>\n");

        if (query != null && query.isEmpty() == false) {
            htmlBuilder.append("        <a href=\"/").append(HttpEndpoints.MainPage).append("\" class=\"clear-search\">Clear ✕</a>\n");
        }
        htmlBuilder.append("    </form>\n");

        List<ArticleEntity> articles;
        if (query != null && query.isEmpty() == false) {
            articles = service.searchArticles(query);
            htmlBuilder.append("    <p class=\"search-results-info\">Showing results for \"").append(HtmlUtil.escapeHtml(query)).append("\" (").append(articles.size()).append(" found)</p>\n");
        } else {
            articles = service.getAllArticles();
        }

        if (articles.isEmpty()) {
            if (query != null && query.isEmpty() == false) {
                htmlBuilder
                        .append("    <div class=\"empty-state\">\n")
                        .append("        <h2>No results found.</h2>\n")
                        .append("    </div>\n");
            } else {
                htmlBuilder
                        .append("    <div class=\"empty-state\">\n")
                        .append("        <h2>No articles yet.</h2>\n")
                        .append("        <p>Add the first one.</p>\n")
                        .append("    </div>\n");
            }
        } else {
            for (ArticleEntity article : articles) {
                int citedCount = article.getCitedArticleIds() != null ? article.getCitedArticleIds().size() : 0;
                long citedByCount = service.getCitedByOthersCount(article.getId());
                htmlBuilder
                        .append(String.format("<a href=\"/%s/%s\" class=\"article-link\"><div class=\"article-card\">\n", HttpEndpoints.ArticleDetailsPage, article.getId()))
                        .append("<h2 class=\"article-title\">").append(HtmlUtil.escapeHtml(article.getTitle())).append("</h2>\n")
                        .append("<p class=\"article-abstract\">").append(HtmlUtil.escapeHtml(article.getSummary())).append("</p>\n")
                        .append("<div class=\"article-footer\">\n")
                        .append("<div class=\"stats\">\n")
                        .append("<div class=\"stat-badge citations\">\n<span>📖</span>\n<span>").append(String.format("cited %s articles", String.valueOf(citedCount))).append("</span>\n</div>\n")
                        .append("<div class=\"stat-badge cited-by\">\n<span>📑</span>\n<span>").append(String.format("cited by %s articles", String.valueOf(citedByCount))).append("</span>\n</div>\n")
                        .append("</div>\n<div class=\"date\">\n📅 ").append(HtmlUtil.escapeHtml(article.displayShortCreationTime())).append("\n</div>\n</div>\n")
                        .append("</div></a>\n");
            }
        }
        htmlBuilder.append("</div>\n</body>\n</html>\n");
        return htmlBuilder.toString().getBytes();
    }
}