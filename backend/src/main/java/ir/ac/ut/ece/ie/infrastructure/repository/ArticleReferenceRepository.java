package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleReferenceRepository extends SearchableRepository<ArticleReferenceEntity> {
    public ArticleReferenceRepository() {
        super("article-references.txt", ArticleReferenceEntity.class);
    }

    public ArticleReferenceRepository(String fileName) {
        super(fileName, ArticleReferenceEntity.class);
    }
}
