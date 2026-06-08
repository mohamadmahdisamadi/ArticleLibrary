package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.dto.ArticleEntity;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ArticleRepository implements ArticleRepositoryInterface {
    private static final String TEXT_FILE = "articles.txt";
    private static final String DELIMITER = ",";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ArticleRepository() {
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        File file = new File(TEXT_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encode(String value) {
        if (value == null) return "";
        return value.replace("%", "%25")
                .replace(DELIMITER, "%2C")
                .replace("\n", "%0A")
                .replace("\r", "%0D")
                .replace("\t", "%09");
    }

    private String decode(String value) {
        if (value == null) return "";
        return value.replace("%09", "\t")
                .replace("%0D", "\r")
                .replace("%0A", "\n")
                .replace("%2C", DELIMITER)
                .replace("%25", "%");
    }

    private String encodeCitedIds(List<String> citedIds) {
        if (citedIds == null || citedIds.isEmpty()) {
            return "";
        }
        return String.join(";", citedIds);
    }

    private List<String> decodeCitedIds(String encoded) {
        List<String> result = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) {
            return result;
        }
        String[] parts = encoded.split(";");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.add(part);
            }
        }
        return result;
    }

    private String articleToLine(ArticleEntity article) {
        StringBuilder sb = new StringBuilder();
        sb.append(encode(article.getId())).append(DELIMITER);
        sb.append(encode(article.getTitle())).append(DELIMITER);
        sb.append(encode(article.getSummary())).append(DELIMITER);
        sb.append(encode(article.getBody())).append(DELIMITER);
        sb.append(encode(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : "")).append(DELIMITER);
        sb.append(encode(article.getLastModifiedAt() != null ? article.getLastModifiedAt().format(FORMATTER) : "")).append(DELIMITER);
        sb.append(encodeCitedIds(article.getCitedArticleIds())).append(DELIMITER);
        sb.append(article.getIsDeleted());
        return sb.toString();
    }

    private ArticleEntity lineToArticle(String line) {
        if (line == null || line.trim().isEmpty())
            return null;

        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 8)
            return null;

        ArticleEntity article = new ArticleEntity();
        article.setId(decode(parts[0]));
        article.setTitle(decode(parts[1]));
        article.setSummary(decode(parts[2]));
        article.setBody(decode(parts[3]));

        String createdAt = decode(parts[4]);
        String lastModifiedAt = decode(parts[5]);

        article.setCreatedAt(createdAt.isEmpty() ? null : LocalDateTime.parse(createdAt, FORMATTER));
        article.setLastModifiedAt(lastModifiedAt.isEmpty() ? null : LocalDateTime.parse(lastModifiedAt, FORMATTER));
        article.setCitedArticleIds(decodeCitedIds(decode(parts[6])));
        article.setIsDeleted(Integer.parseInt(decode(parts[7])));
        return article;
    }

    private List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    private void writeAllLines(List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    @Override
    public void add(ArticleEntity article) throws Exception {
        List<String> lines = readAllLines();

        article.setId(UUID.randomUUID().toString());
        LocalDateTime currentTime = LocalDateTime.now();
        article
            .setCreatedAt(currentTime)
            .setLastModifiedAt(currentTime)
            .setCitedArticleIds(article.getCitedArticleIds());

//        List<String> validCitedIds = new ArrayList<>();
//        if (article.getCitedArticleIds() != null)
//            for (String cid : article.getCitedArticleIds())
//                if (getById(cid).isPresent())
//                    validCitedIds.add(cid);


        lines.add(articleToLine(article));
        writeAllLines(lines);
    }

    @Override
    public List<ArticleEntity> getAll() throws Exception {
        List<ArticleEntity> articles = new ArrayList<>();
        List<String> lines = readAllLines();
        for (String line : lines) {
            ArticleEntity article = lineToArticle(line);
            if (article != null && article.getIsDeleted() == 0)
                articles.add(article);
        }
        return articles;
    }

    @Override
    public Optional<ArticleEntity> getById(String id) throws Exception {
        return getAll().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ArticleEntity> getByTitle(String query) throws Exception {
        return getAll().stream()
                .filter(a -> a.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleEntity> getBySummary(String query) throws Exception {
        return getAll().stream()
                .filter(a -> a.getSummary().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();
        boolean removed = false;

        for (String line : lines) {
            ArticleEntity article = lineToArticle(line);
            if (article != null && article.getId().equals(id)) {
                removed = true;
                article.setIsDeleted(1);
                article.setLastModifiedAt(LocalDateTime.now());
                newLines.add(articleToLine(article));
            } else {
                newLines.add(line);
            }
        }

        if (!removed) {
            throw new IllegalArgumentException("Article with id " + id + " not found");
        }

        writeAllLines(newLines);
    }

    @Override
    public void edit(ArticleEntity updatedArticle) throws Exception {
        List<String> lines = readAllLines();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (String line : lines) {
            ArticleEntity article = lineToArticle(line);
            if (article != null && article.getId().equals(updatedArticle.getId())) {
                if (article.getIsDeleted() == 1)
                    throw new IllegalArgumentException("Article with id " + updatedArticle.getId() + " not found");

//                List<String> validCitedIds = new ArrayList<>();
//                if (updatedArticle.getCitedArticleIds() != null) {
//                    for (String cid : updatedArticle.getCitedArticleIds()) {
//                        if (getById(cid).isPresent()) {
//                            validCitedIds.add(cid);
//                        }
//                    }
//                }

                updatedArticle
                    .setCitedArticleIds(updatedArticle.getCitedArticleIds())
                    .setCreatedAt(article.getCreatedAt())
                    .setLastModifiedAt(LocalDateTime.now());

                newLines.add(articleToLine(updatedArticle));
                found = true;
            } else {
                newLines.add(line);
            }
        }

        if (!found)
            throw new IllegalArgumentException("Article with id " + updatedArticle.getId() + " not found");

        writeAllLines(newLines);
        return;
    }

    @Override
    public boolean checkIfTitleExists(String title) throws Exception {
        return getAll().stream().anyMatch(a -> a.getTitle().equals(title));
    }
}