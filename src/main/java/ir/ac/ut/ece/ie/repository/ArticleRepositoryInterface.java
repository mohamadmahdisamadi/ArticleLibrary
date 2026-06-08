package ir.ac.ut.ece.ie.repository;

import ir.ac.ut.ece.ie.dto.ArticleEntity;
import java.util.List;
import java.util.Optional;

public interface ArticleRepositoryInterface {
    void add(ArticleEntity article) throws Exception;
    List<ArticleEntity> getAll() throws Exception;
    Optional<ArticleEntity> getById(String id) throws Exception;
    List<ArticleEntity> getByTitle(String query) throws Exception;
    List<ArticleEntity> getBySummary(String query) throws Exception;
    void delete(String id) throws Exception;
    void edit(ArticleEntity updatedArticle) throws Exception;
    boolean checkIfTitleExists(String title) throws Exception;
}
