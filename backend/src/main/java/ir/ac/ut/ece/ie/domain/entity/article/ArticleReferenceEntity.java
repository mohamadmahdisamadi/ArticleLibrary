package ir.ac.ut.ece.ie.domain.entity.article;

import ir.ac.ut.ece.ie.domain.entity.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ArticleReferences")
public class ArticleReferenceEntity extends BaseEntity {

    @ManyToOne @JoinColumn(nullable = false)
    private ArticleEntity cited;

    @ManyToOne @JoinColumn(nullable = false)
    private ArticleEntity citing;

    public ArticleReferenceEntity() {}

    public ArticleReferenceEntity setCited(ArticleEntity cited) { this.cited = cited; return this; }
    public ArticleEntity getCited() { return cited; }

    public ArticleReferenceEntity setCiting(ArticleEntity citing) { this.citing = citing; return this; }
    public ArticleEntity getCiting() { return citing; }
}
