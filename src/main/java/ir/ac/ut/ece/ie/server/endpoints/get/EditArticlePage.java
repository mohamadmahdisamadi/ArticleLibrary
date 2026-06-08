package ir.ac.ut.ece.ie.server.endpoints.get;

import ir.ac.ut.ece.ie.server.common.endpointnames.HttpEndpoints;
import ir.ac.ut.ece.ie.service.ArticleService;
import ir.ac.ut.ece.ie.dto.ArticleEntity;
import ir.ac.ut.ece.ie.utils.HtmlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditArticlePage {
    private ArticleService service;

    public EditArticlePage() { service = new ArticleService(); }

    public byte[] getPage(Map<String, String> args) {
        String articleId = args.get("id");

        ArticleEntity article = null;
        try {
            article = service.getArticleById(articleId).orElse(null);
        } catch (Exception e) {
            article = null;
        }

        if (article == null) {
            return ("Article not found").getBytes();
        }

        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html lang=\"en\">\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("    <meta charset=\"UTF-8\">\n");
        htmlBuilder.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        htmlBuilder.append("    <title>Edit Article</title>\n");
        htmlBuilder.append("    <style>\n");
        htmlBuilder.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        htmlBuilder.append("        body {\n");
        htmlBuilder.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
        htmlBuilder.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n");
        htmlBuilder.append("            min-height: 100vh;\n");
        htmlBuilder.append("            padding: 40px 20px;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .container {\n");
        htmlBuilder.append("            max-width: 900px;\n");
        htmlBuilder.append("            margin: 0 auto;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        h1 {\n");
        htmlBuilder.append("            font-size: 2.3rem;\n");
        htmlBuilder.append("            color: white;\n");
        htmlBuilder.append("            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);\n");
        htmlBuilder.append("            margin-bottom: 35px;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .form-card {\n");
        htmlBuilder.append("            background: white;\n");
        htmlBuilder.append("            border-radius: 16px;\n");
        htmlBuilder.append("            padding: 30px;\n");
        htmlBuilder.append("            box-shadow: 0 10px 40px rgba(0,0,0,0.1);\n");
        htmlBuilder.append("            margin-bottom: 25px;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        label {\n");
        htmlBuilder.append("            font-weight: 600;\n");
        htmlBuilder.append("            display: block;\n");
        htmlBuilder.append("            margin-bottom: 8px;\n");
        htmlBuilder.append("            color: #2d3436;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        input[type='text'], textarea {\n");
        htmlBuilder.append("            width: 100%;\n");
        htmlBuilder.append("            padding: 12px;\n");
        htmlBuilder.append("            border-radius: 10px;\n");
        htmlBuilder.append("            border: 1px solid #ccc;\n");
        htmlBuilder.append("            margin-bottom: 20px;\n");
        htmlBuilder.append("            font-size: 1rem;\n");
        htmlBuilder.append("            font-family: inherit;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        textarea { height: 140px; resize: vertical; }\n");
        htmlBuilder.append("        .checkbox-list {\n");
        htmlBuilder.append("            background: #f8f9fa;\n");
        htmlBuilder.append("            padding: 20px;\n");
        htmlBuilder.append("            border-radius: 12px;\n");
        htmlBuilder.append("            max-height: 200px;\n");
        htmlBuilder.append("            overflow-y: auto;\n");
        htmlBuilder.append("            margin-bottom: 25px;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .checkbox-item { margin-bottom: 10px; }\n");
        htmlBuilder.append("        .submit-btn {\n");
        htmlBuilder.append("            background: orange;\n");
        htmlBuilder.append("            color: white;\n");
        htmlBuilder.append("            padding: 14px 32px;\n");
        htmlBuilder.append("            border: none;\n");
        htmlBuilder.append("            border-radius: 25px;\n");
        htmlBuilder.append("            font-size: 1.1rem;\n");
        htmlBuilder.append("            font-weight: 600;\n");
        htmlBuilder.append("            cursor: pointer;\n");
        htmlBuilder.append("            transition: all 0.3s ease;\n");
        htmlBuilder.append("            box-shadow: 0 4px 15px rgba(255,107,107,0.4);\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .submit-btn:hover {\n");
        htmlBuilder.append("            transform: translateY(-2px);\n");
        htmlBuilder.append("            box-shadow: 0 6px 20px rgba(255,107,107,0.6);\n");
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
        htmlBuilder.append("	        text-decoration: none;\n");
        htmlBuilder.append("            z-index: 1000;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .back-link:hover {\n");
        htmlBuilder.append("            transform: translateY(-2px);\n");
        htmlBuilder.append("	        text-decoration: none;\n");
        htmlBuilder.append("            box-shadow: 0 6px 20px rgba(255,107,107,0.6);\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .message {\n");
        htmlBuilder.append("            padding: 15px;\n");
        htmlBuilder.append("            border-radius: 10px;\n");
        htmlBuilder.append("            margin-bottom: 20px;\n");
        htmlBuilder.append("            font-weight: 500;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .message.success {\n");
        htmlBuilder.append("            background: #d4edda;\n");
        htmlBuilder.append("            color: #155724;\n");
        htmlBuilder.append("            border: 1px solid #c3e6cb;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .message.error {\n");
        htmlBuilder.append("            background: #f8d7da;\n");
        htmlBuilder.append("            color: #721c24;\n");
        htmlBuilder.append("            border: 1px solid #f5c6cb;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("    </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("<div class=\"container\">\n");
        htmlBuilder.append("<h1>Edit Article</h1>\n");
        htmlBuilder.append("<div class=\"form-card\">\n");
        htmlBuilder.append("    <div id=\"message\" class=\"message\" style=\"display: none;\"></div>\n");

        htmlBuilder.append("    <form id=\"articleForm\">\n");
        htmlBuilder.append("        <input type=\"hidden\" name=\"id\" value=\"").append(HtmlUtil.escapeHtml(article.getId())).append("\" />\n");

        htmlBuilder.append("        <label>Title</label>\n");
        htmlBuilder.append("        <input type=\"text\" name=\"title\" required value=\"").append(HtmlUtil.escapeHtml(article.getTitle())).append("\" />\n");

        htmlBuilder.append("        <label>Summary</label>\n");
        htmlBuilder.append("        <textarea name=\"summary\" required>").append(HtmlUtil.escapeHtml(article.getSummary())).append("</textarea>\n");

        htmlBuilder.append("        <label>Body</label>\n");
        htmlBuilder.append("        <textarea name=\"body\" required>").append(HtmlUtil.escapeHtml(article.getBody())).append("</textarea>\n");

        htmlBuilder.append("        <label>Citations (select related articles):</label>\n");
        htmlBuilder.append("        <div class=\"checkbox-list\">\n");

        List<ArticleEntity> allArticles = new ArrayList<>();
        try {
            allArticles = service.getAllArticles();
        } catch (Exception e) {}

        List<String> selectedCitations = article.getCitedArticleIds();
        if (selectedCitations == null)
            selectedCitations = new ArrayList<>();


        for (ArticleEntity art : allArticles) {
            if (art.getId().equals(article.getId())) {
                continue;
            }

            String id = HtmlUtil.escapeHtml(art.getId());
            String title = HtmlUtil.escapeHtml(art.getTitle());
            boolean isChecked = selectedCitations.contains(art.getId());

            htmlBuilder.append("            <div class=\"checkbox-item\">\n");
            htmlBuilder.append("                <input type=\"checkbox\" name=\"citations\" value=\"").append(id).append("\"");
            if (isChecked) {
                htmlBuilder.append(" checked");
            }
            htmlBuilder.append("> ").append(title).append("\n");
            htmlBuilder.append("            </div>\n");
        }

        htmlBuilder.append("        </div>\n");
        htmlBuilder.append("        <button class=\"submit-btn\" type=\"submit\">Edit Article</button>\n");
        htmlBuilder.append("    </form>\n");

        htmlBuilder.append("</div>\n");

        htmlBuilder.append(String.format("<a href=\"/%s/%s\" class=\"back-link\">Back to Article</a>\n", HttpEndpoints.ArticleDetailsPage, article.getId()));

        htmlBuilder.append("</div>\n");

        htmlBuilder.append("    <script>\n");
        htmlBuilder.append("        document.getElementById('articleForm').addEventListener('submit', function(e) {\n");
        htmlBuilder.append("            e.preventDefault();\n");
        htmlBuilder.append("            \n");
        htmlBuilder.append("            var form = e.target;\n");
        htmlBuilder.append("            var formData = new FormData(form);\n");
        htmlBuilder.append("            var data = {\n");
        htmlBuilder.append("                title: formData.get('title'),\n");
        htmlBuilder.append("                summary: formData.get('summary'),\n");
        htmlBuilder.append("                body: formData.get('body'),\n");
        htmlBuilder.append("                citations: []\n");
        htmlBuilder.append("            };\n");
        htmlBuilder.append("            \n");
        htmlBuilder.append("            var checkboxes = form.querySelectorAll('input[name=\"citations\"]:checked');\n");
        htmlBuilder.append("            checkboxes.forEach(function(checkbox) {\n");
        htmlBuilder.append("                data.citations.push(checkbox.value);\n");
        htmlBuilder.append("            });\n");
        htmlBuilder.append("            \n");
        htmlBuilder.append(String.format("fetch('/%s/%s', {\n", HttpEndpoints.EditArticleHandler, articleId));
        htmlBuilder.append("                method: 'PUT',\n");
        htmlBuilder.append("                headers: {\n");
        htmlBuilder.append("                    'Content-Type': 'application/json'\n");
        htmlBuilder.append("                },\n");
        htmlBuilder.append("                body: JSON.stringify(data)\n");
        htmlBuilder.append("            })\n");
        htmlBuilder.append("            .then(function(response) {\n");
        htmlBuilder.append("                return response.json();\n");
        htmlBuilder.append("            })\n");
        htmlBuilder.append("            .then(function(result) {\n");
        htmlBuilder.append("                if (result.isSuccess) {\n");
        htmlBuilder.append(String.format("      window.location.href = '/%s/%s';\n", HttpEndpoints.ArticleDetailsPage, articleId));
        htmlBuilder.append("                } else {\n");
        htmlBuilder.append("                    messageEl.className = 'message error';\n");
        htmlBuilder.append("                    messageEl.textContent = 'Error: ' + result.message;\n");
        htmlBuilder.append("                }\n");
        htmlBuilder.append("                messageEl.style.display = 'block';\n");
        htmlBuilder.append("            })\n");
        htmlBuilder.append("            .catch(function(error) {\n");
        htmlBuilder.append("                var messageEl = document.getElementById('message');\n");
        htmlBuilder.append("                messageEl.className = 'message error';\n");
        htmlBuilder.append("                messageEl.textContent = 'Error updating article: ' + error.message;\n");
        htmlBuilder.append("                messageEl.style.display = 'block';\n");
        htmlBuilder.append("            });\n");
        htmlBuilder.append("        });\n");
        htmlBuilder.append("    </script>\n");





        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>\n");
        return htmlBuilder.toString().getBytes();
    }
}