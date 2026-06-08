package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepository extends SearchableRepository<ArticleEntity> {
    public ArticleRepository() {
        super("articles.txt", ArticleEntity.class);
    }

    public ArticleRepository(String fileName) {
        super(fileName, ArticleEntity.class);
    }
}
