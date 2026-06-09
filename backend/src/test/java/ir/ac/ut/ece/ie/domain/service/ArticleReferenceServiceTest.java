package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleReferenceRepository;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleReferenceServiceTest {

    @Mock
    private ArticleReferenceRepository repository;
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleReferenceServiceImpl service;

    @Captor
    private ArgumentCaptor<List<ArticleReferenceEntity>> entityListCaptor;

    private ArticleEntity article1;
    private ArticleEntity article2;

    @BeforeEach
    void setup() {
        article1 = new ArticleEntity();
        article1.setId(1L);
        article2 = new ArticleEntity();
        article2.setId(2L);
    }

    @Nested
    @DisplayName("addReferences")
    class AddReferences {
        @Test
        @DisplayName("Should successfully add references when all articles exist")
        void addReferences_shouldAddReferencesCorrectly() throws Exception {
            List<Long> citedIds = List.of(2L);

            when(articleRepository.findById(article1.getId())).thenReturn(Optional.of(article1));
            when(articleRepository.findById(article2.getId())).thenReturn(Optional.of(article2));

            service.addReferences(article1.getId(), citedIds);

            verify(repository).saveAll(entityListCaptor.capture());
            List<ArticleReferenceEntity> savedEntities = entityListCaptor.getValue();

            assertEquals(1, savedEntities.size());
            assertEquals(article1.getId(), savedEntities.get(0).getCiting().getId());
            assertEquals(article2.getId(), savedEntities.get(0).getCited().getId());
        }

        @Test
        @DisplayName("Should throw exception when the citing article is not found")
        void addReferences_shouldThrowException_whenCitingArticleNotFound() {
            when(articleRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> {
                service.addReferences(1L, List.of(2L));
            });

            assertEquals("Article not found: 1", exception.getMessage());
            verify(repository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw exception when a cited article is not found")
        void addReferences_shouldThrowException_whenCitedArticleNotFound() {
            when(articleRepository.findById(article1.getId())).thenReturn(Optional.of(article1));
            when(articleRepository.findById(2L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> {
                service.addReferences(article1.getId(), List.of(2L));
            });

            assertEquals("Article not found: 2", exception.getMessage());
            verify(repository, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("updateReferences")
    class UpdateReferences {
        @Test
        @DisplayName("Should delete old references and save new references")
        void updateReferences_shouldDeleteOldAndAddNewReferences() throws Exception {
            Long articleId = 1L;
            List<Long> newCitedIds = List.of(2L);

            // Stubbing required for internal call to addReferences
            when(articleRepository.findById(article1.getId())).thenReturn(Optional.of(article1));
            when(articleRepository.findById(article2.getId())).thenReturn(Optional.of(article2));

            service.updateReferences(articleId, newCitedIds);

            verify(repository).deleteByCiting_Id(articleId);
            verify(repository).saveAll(entityListCaptor.capture());

            List<ArticleReferenceEntity> savedEntities = entityListCaptor.getValue();
            assertEquals(1, savedEntities.size());
            assertEquals(articleId, savedEntities.get(0).getCiting().getId());
            assertEquals(2L, savedEntities.get(0).getCited().getId());
        }
    }

    @Nested
    @DisplayName("getReferences")
    class GetReferences {
        @Test
        void getReferencesTo_shouldReturnReferencesTo_whenCalled() throws Exception {
            ArticleReferenceEntity entity = new ArticleReferenceEntity()
                    .setCiting(article2)
                    .setCited(article1);

            when(repository.findByCited_Id(article1.getId())).thenReturn(List.of(entity));

            List<ArticleEntity> result = service.getReferencesTo(article1.getId());

            assertEquals(1, result.size());
            assertEquals(article2.getId(), result.get(0).getId());
            verify(repository).findByCited_Id(article1.getId());
        }

        @Test
        void getReferencesFrom_shouldReturnReferencesFrom_whenCalled() throws Exception {
            ArticleReferenceEntity entity = new ArticleReferenceEntity()
                    .setCiting(article1)
                    .setCited(article2);

            when(repository.findByCiting_Id(article1.getId())).thenReturn(List.of(entity));

            List<ArticleEntity> result = service.getReferencesFrom(article1.getId());

            assertEquals(1, result.size());
            assertEquals(article2.getId(), result.get(0).getId());
            verify(repository).findByCiting_Id(article1.getId());
        }
    }

    @Nested
    @DisplayName("countReferences")
    class CountReferences {
        @Test
        @DisplayName("Should correctly count references originating from an article")
        void countReferencesFrom_shouldReturnCorrectSize() throws Exception {
            ArticleReferenceEntity entity = new ArticleReferenceEntity().setCiting(article1).setCited(article2);
            when(repository.findByCiting_Id(article1.getId())).thenReturn(List.of(entity));

            long count = service.countReferencesFrom(article1.getId());

            assertEquals(1, count);
        }

        @Test
        @DisplayName("Should correctly count references pointing to an article")
        void countReferencesTo_shouldReturnCorrectSize() throws Exception {
            ArticleReferenceEntity entity = new ArticleReferenceEntity().setCiting(article2).setCited(article1);
            when(repository.findByCited_Id(article1.getId())).thenReturn(List.of(entity));

            long count = service.countReferencesTo(article1.getId());

            assertEquals(1, count);
        }
    }
}