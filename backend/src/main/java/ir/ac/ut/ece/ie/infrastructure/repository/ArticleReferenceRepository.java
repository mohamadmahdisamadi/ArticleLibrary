package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArticleReferenceRepository extends JpaRepository<ArticleReferenceEntity, Long> {
    List<ArticleReferenceEntity> findByCiting_Id(Long citingId);
    List<ArticleReferenceEntity> findByCited_Id(Long citedId);
    @Transactional void deleteByCiting_Id(Long citingId);
}
