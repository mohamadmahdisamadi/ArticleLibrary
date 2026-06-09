package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.api.service.article.ArticleReferenceService;
import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleTitleServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository repository;

    @Mock
    private ArticleReferenceService articleReferenceService;

    @InjectMocks
    private ArticleServiceImpl service;

    private ArticleEntity article1;
    private ArticleEntity article2;
    private final LocalDateTime fixedTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        article1 = new ArticleEntity()
                .setTitle("title1")
                .setSummary("summary1")
                .setBody("body1");
        article1.setId(1L);
        article1.setCreatedAt(fixedTime);
        article1.setLastModifiedAt(fixedTime);

        article2 = new ArticleEntity()
                .setTitle("title2")
                .setSummary("summary2")
                .setBody("body2");
        article2.setId(2L);
        article2.setCreatedAt(fixedTime);
        article2.setLastModifiedAt(fixedTime);
    }

    @Nested
    @DisplayName("getArticleDetails")
    class GetArticleDetails {
        @Test
        void getArticleDetails_shouldReturnDetails_whenArticleExists() throws Exception {
            when(repository.findById(2L)).thenReturn(Optional.of(article2));
            when(articleReferenceService.getReferencesFrom(2L)).thenReturn(List.of(article1));

            GetArticleDetailsServiceOutput result = service.getArticleDetails(2L);

            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals("title2", result.title());
            assertEquals(1, result.citedArticles().size());
            assertEquals(1L, result.citedArticles().get(0).id());
            verify(repository, times(1)).findById(2L);
        }

        @Test
        void getArticleDetails_shouldThrowException_whenArticleNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> service.getArticleDetails(99L));
            assertEquals("Not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getArticlesDetails")
    class GetArticlesDetails {
        @Test
        void getArticlesDetails_shouldReturnAllArticles_whenQueryIsNull() throws Exception {
            when(repository.findAll()).thenReturn(List.of(article1, article2));
            when(articleReferenceService.getReferencesFrom(anyLong())).thenReturn(Collections.emptyList());

            List<GetArticleDetailsServiceOutput> results = service.getArticlesDetails(null);

            assertNotNull(results);
            assertEquals(2, results.size());
            verify(repository, times(1)).findAll();
            verify(repository, never()).findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCase(anyString(), anyString());
        }

        @Test
        void getArticlesDetails_shouldReturnSearchedArticles_whenQueryIsProvided() throws Exception {
            String query = "search-term";
            when(repository.findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCase(query, query))
                    .thenReturn(List.of(article1));

            List<GetArticleDetailsServiceOutput> results = service.getArticlesDetails(query);

            assertEquals(1, results.size());
            assertEquals(1L, results.get(0).id());
            verify(repository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("getArticlesTitle")
    class GetArticlesTitle {
        @Test
        void getArticlesTitle_shouldReturnTitlesAndIds() throws Exception {
            when(repository.findAll()).thenReturn(List.of(article1, article2));

            List<GetArticleTitleServiceOutput> result = service.getArticlesTitle();

            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).id());
            assertEquals("title1", result.get(0).title());
        }
    }

    @Nested
    @DisplayName("getArticlePreview")
    class GetArticlePreview {
        @Test
        void getArticlePreview_shouldReturnPreview_whenArticleExists() throws Exception {
            when(repository.findById(1L)).thenReturn(Optional.of(article1));
            when(articleReferenceService.countReferencesTo(1L)).thenReturn(5L);
            when(articleReferenceService.countReferencesFrom(1L)).thenReturn(3L);

            GetArticlePreviewServiceOutput result = service.getArticlePreview(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals(5L, result.citedCount());
            assertEquals(3L, result.citingCount());
        }

        @Test
        void getArticlePreview_shouldThrowException_whenArticleNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> service.getArticlePreview(99L));
            assertEquals("Not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getArticlesPreview")
    class GetArticlesPreviewList {
        @Test
        void getArticlesPreview_shouldReturnAllArticlePreviews_whenQueryIsNull() throws Exception {
            when(repository.findAll()).thenReturn(List.of(article1, article2));
            when(articleReferenceService.countReferencesTo(anyLong())).thenReturn(0L);
            when(articleReferenceService.countReferencesFrom(anyLong())).thenReturn(0L);

            List<GetArticlePreviewServiceOutput> results = service.getArticlesPreview(null);

            assertEquals(2, results.size());
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("validateArticle & Create Paths")
    class ValidateAndCreateArticle {
        @Test
        void createArticle_shouldSucceed_withValidInput() throws Exception {
            CreateArticleServiceInput input = new CreateArticleServiceInput("New Title", "New Summary", "New Body", List.of(2L));
            when(repository.existsByTitle("New Title")).thenReturn(false);

            service.createArticle(input);

            verify(repository, times(1)).save(any(ArticleEntity.class));
            verify(articleReferenceService, times(1)).addReferences(any(), eq(List.of(2L)));
        }

        @Test
        void validateArticle_shouldReturnRelatedErrorMessage_whenTitleExists() {
            CreateArticleServiceInput input = new CreateArticleServiceInput("title1", "Summary", "Body", List.of());
            when(repository.existsByTitle("title1")).thenReturn(true);

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertEquals("title already exists", exception.getMessage());
        }

        @Test
        void validateArticle_shouldReturnRelatedErrorMessage_whenFieldsAreEmpty() {
            CreateArticleServiceInput input = new CreateArticleServiceInput("", "", "", List.of());

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertTrue(exception.getMessage().contains("title can't be empty"));
            assertTrue(exception.getMessage().contains("summary can't be empty"));
            assertTrue(exception.getMessage().contains("body can't be empty"));
        }
    }

    @Nested
    @DisplayName("updateArticle")
    class UpdateArticle {
        @Test
        void updateArticle_shouldSucceed_withValidNewInput() throws Exception {
            UpdateArticleServiceInput input = new UpdateArticleServiceInput(1L, "New Unique Title", "Updated Summary", "Updated Body", List.of());
            when(repository.findById(1L)).thenReturn(Optional.of(article1));
            when(repository.existsByTitle("New Unique Title")).thenReturn(false);

            service.updateArticle(input);

            ArgumentCaptor<ArticleEntity> articleCaptor = ArgumentCaptor.forClass(ArticleEntity.class);
            verify(repository, times(1)).save(articleCaptor.capture());
            ArticleEntity capturedArticle = articleCaptor.getValue();

            assertEquals("New Unique Title", capturedArticle.getTitle());
            assertEquals("Updated Summary", capturedArticle.getSummary());
            verify(articleReferenceService, times(1)).updateReferences(1L, List.of());
        }

        @Test
        void updateArticle_withUnchangedTitle_shouldNotCheckTitleExistence() throws Exception {
            UpdateArticleServiceInput input = new UpdateArticleServiceInput(1L, "title1", "Updated Summary", "Updated Body", List.of());
            when(repository.findById(1L)).thenReturn(Optional.of(article1));

            service.updateArticle(input);

            verify(repository, never()).existsByTitle(anyString());
            verify(repository, times(1)).save(any(ArticleEntity.class));
        }
    }

    @Nested
    @DisplayName("deleteOperations")
    class DeleteOperations {
        @Test
        void deleteArticle_shouldCallRepository() throws Exception {
            service.deleteArticle(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        void deleteArticles_shouldCallRepository() throws Exception {
            service.deleteArticles();
            verify(repository, times(1)).deleteAll();
        }
    }
}