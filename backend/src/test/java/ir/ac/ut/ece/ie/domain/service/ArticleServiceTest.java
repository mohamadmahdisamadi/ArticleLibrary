package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.api.service.article.ArticleReferenceService;
import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleTitleServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;
import ir.ac.ut.ece.ie.domain.entity.article.ArticleEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleRepository;
import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    private ArticleEntity article3;
    private final LocalDateTime fixedTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {

        article1 = new ArticleEntity()
                .setTitle("title1")
                .setSummary("summary1")
                .setBody("body1");
        article1.setId("1");
        article1.setCreatedAt(fixedTime);
        article1.setLastModifiedAt(fixedTime);

        article2 = new ArticleEntity()
                .setTitle("title2")
                .setSummary("summary2")
                .setBody("body2");
        article2.setId("2");
        article2.setCreatedAt(fixedTime);
        article2.setLastModifiedAt(fixedTime);

        article3 = new ArticleEntity()
                .setTitle("title3")
                .setSummary("summary3")
                .setBody("body3");
        article3.setId("3");
        article3.setCreatedAt(fixedTime);
        article3.setLastModifiedAt(fixedTime);
    }

    @Nested
    @DisplayName("getArticleDetails")
    class GetArticleDetails {
        @Test void getArticleDetails_shouldReturnDetails_whenArticleExists() throws Exception {
            when(repository.getById("1")).thenReturn(article1);
            when(repository.getById("2")).thenReturn(article2);
            when(articleReferenceService.getReferences("2", false)).thenReturn(List.of("1"));

            GetArticleDetailsServiceOutput result = service.getArticleDetails("2");

            assertNotNull(result);
            assertEquals(
                    new GetArticleDetailsServiceOutput(
                        "2", "title2", "summary2", "body2",
                        List.of(new GetArticleTitleServiceOutput("1", "title1")), fixedTime, fixedTime),
                    result);
            verify(repository, times(1)).getById("1");
            verify(repository, times(1)).getById("2");
            verify(articleReferenceService, times(1)).getReferences("2", false);
        }

        @Test void getArticleDetails_shouldThrowException_whenArticleNotFound() throws Exception {
            when(repository.getById("nonexistent")).thenReturn(null);

            Exception exception = assertThrows(Exception.class, () -> service.getArticleDetails("nonexistent"));
            assertEquals("Not found", exception.getMessage());
        }
    }



    @Nested
    @DisplayName("getArticlesDetails")
    class GetArticlesDetails {
        @Test void getArticlesDetails_shouldReturnAllArticles_whenQueryIsNull() throws Exception {
            when(repository.getAll()).thenReturn(List.of(article1, article2));
            when(articleReferenceService.getReferences("2", false)).thenReturn(List.of("1"));
            when(articleReferenceService.getReferences("1", false)).thenReturn(Collections.emptyList());
            when(repository.getById("1")).thenReturn(article1);

            List<GetArticleDetailsServiceOutput> results = service.getArticlesDetails(null);

            assertNotNull(results);
            assertEquals(2, results.size());
            verify(repository, times(1)).getAll();
            verify(repository, never()).searchField(anyString(), anyString(), any());
        }

        @Test
        void getArticlesDetails_shouldReturnSearchedArticles_whenQueryIsProvided() throws Exception {
            String queryPhrase = "In the bar with the beasts";
            when(repository.searchField("title", queryPhrase, FieldComparator.CONTAINS)).thenReturn(List.of(article1));
            when(repository.searchField("summary", queryPhrase, FieldComparator.CONTAINS)).thenReturn(List.of());
            when(articleReferenceService.getReferences(anyString(), anyBoolean())).thenReturn(Collections.emptyList());

            List<GetArticleDetailsServiceOutput> results = service.getArticlesDetails(queryPhrase);

            assertEquals(1, results.size());
            assertEquals("1", results.get(0).id());
            verify(repository, never()).getAll();
            verify(repository, times(1)).searchField("title", queryPhrase, FieldComparator.CONTAINS);
            verify(repository, times(1)).searchField("summary", queryPhrase, FieldComparator.CONTAINS);
        }

        @Test void getArticlesDetails_shouldReturnDistinctArticles_whenQueryMatchesMultipleFields() throws Exception {
            String queryPhrase = "Lost in the ocean beyond the sea";
            when(repository.searchField("title", queryPhrase, FieldComparator.CONTAINS)).thenReturn(List.of(article1));
            when(repository.searchField("summary", queryPhrase, FieldComparator.CONTAINS)).thenReturn(List.of(article1));
            when(articleReferenceService.getReferences(anyString(), anyBoolean())).thenReturn(Collections.emptyList());

            List<GetArticleDetailsServiceOutput> results = service.getArticlesDetails(queryPhrase);

            assertEquals(1, results.size());
            assertEquals("1", results.get(0).id());
        }
    }



    @Nested
    @DisplayName("getArticlePreview")
    class GetArticlePreview {
        @Test void getArticlePreview_callsRepository_atAllTimes() throws Exception {
            String articleId = "1";
            when(repository.getById(articleId)).thenReturn(article1);
            service.getArticlePreview(articleId);
            verify(repository, times(1)).getById(articleId);
        }

        @Test void getArticlePreview_shouldReturnPreview_whenArticleExists() throws Exception {
            when(repository.getById("1")).thenReturn(article1);
            when(articleReferenceService.getReferencesCount("1", true)).thenReturn(1);
            when(articleReferenceService.getReferencesCount("1", false)).thenReturn(1);

            GetArticlePreviewServiceOutput result = service.getArticlePreview("1");

            assertNotNull(result);
            assertEquals(
                    new GetArticlePreviewServiceOutput(
                            "1", "title1", "summary1", 1, 1, fixedTime),
                    result);
        }

        @Test void getArticlePreview_shouldThrowException_whenArticleNotFound() throws Exception {
            when(repository.getById("nonexistent")).thenReturn(null);

            Exception exception = assertThrows(Exception.class, () -> service.getArticlePreview("nonexistent"));
            assertEquals("Not found", exception.getMessage());
        }
    }



    @Nested
    @DisplayName("getArticlesPreview")
    class GetArticlesPreviewList {
        @Test void getArticlesPreview_shouldReturnAllArticlePreviews_whenQueryIsNull() throws Exception {
            when(repository.getAll()).thenReturn(List.of(article1, article2));
            when(articleReferenceService.getReferencesCount("1", true)).thenReturn(1);
            when(articleReferenceService.getReferencesCount("1", false)).thenReturn(0);
            when(articleReferenceService.getReferencesCount("2", true)).thenReturn(0);
            when(articleReferenceService.getReferencesCount("2", false)).thenReturn(1);

            List<GetArticlePreviewServiceOutput> results = service.getArticlesPreview(null);

            assertEquals(2, results.size());
            verify(repository, times(1)).getAll();
        }

        @Test void getArticlesPreview_shouldReturnEmptyList_whenNoArticlesFound() throws Exception {
            when(repository.getAll()).thenReturn(Collections.emptyList());
            List<GetArticlePreviewServiceOutput> results = service.getArticlesPreview(null);
            assertTrue(results.isEmpty());
        }
    }



    @Nested @DisplayName("validateArticle")
    class ValidateArticle {
        @Test void validateArticle_shouldNotReturnErrorMessage_whenNothingIsWrong() throws Exception {
            CreateArticleServiceInput input = new CreateArticleServiceInput("New Title", "New Summary", "New Body", List.of());
            when(repository.valueExists("title", "New Title")).thenReturn(false);
            service.createArticle(input);
            verify(repository, times(1)).add(any(ArticleEntity.class));
            verify(articleReferenceService, times(1)).addReferences(any(), any());
        }

        @Test void validateArticle_shouldReturnRelatedErrorMessage_whenTitleExists() throws Exception {
            String articleTitle = "Maharishi";
            CreateArticleServiceInput input = new CreateArticleServiceInput(articleTitle, "New Summary", "New Body", List.of());
            when(repository.valueExists("title", articleTitle)).thenReturn(true);

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertEquals("title already exists", exception.getMessage());
        }

        @Test void validateArticle_shouldReturnRelatedErrorMessage_whenTitleIsEmpty() throws Exception {
            CreateArticleServiceInput input = new CreateArticleServiceInput("", "New Summary", "New Body", List.of());
            when(repository.valueExists("title", "")).thenReturn(false);

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertEquals("title can't be empty", exception.getMessage());
        }

        @Test void validateArticle_shouldReturnRelatedErrorMessage_whenSummaryAndBodyAreEmpty() throws Exception {
            CreateArticleServiceInput input = new CreateArticleServiceInput("she demon", "", "", List.of());
            when(repository.valueExists("title", "she demon")).thenReturn(false);

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertEquals("summary can't be empty | body can't be empty", exception.getMessage());
        }

        @Test void validateArticle_shouldReturnServerErrorMessage_whenRepositoryThrowsException() throws Exception {
            CreateArticleServiceInput input = new CreateArticleServiceInput("she demon", "", "", List.of());
            when(repository.valueExists("title", "she demon")).thenThrow(new Exception());

            Exception exception = assertThrows(Exception.class, () -> service.createArticle(input));
            assertEquals("server error", exception.getMessage());
        }

    }



    @Nested
    @DisplayName("createArticle")
    class CreateArticle {
        @Test void createArticle_shouldSucceed_withValidInput() throws Exception {
            String articleTitle = "Maharishi";
            String articleId = "randomId";
            List<String> articleCitedIds = List.of("2", "3");
            CreateArticleServiceInput input = new CreateArticleServiceInput(articleTitle, "New Summary", "New Body", articleCitedIds);
            ArticleEntity createdArticle = new ArticleEntity()
                    .setTitle(input.title())
                    .setSummary(input.summary())
                    .setBody(input.body());

            when(repository.valueExists("title", articleTitle)).thenReturn(false);
            service.createArticle(input);
            verify(repository, times(1)).add(any(ArticleEntity.class));
            verify(articleReferenceService, times(1)).addReferences(any(), any());
        }
    }

    @Nested
    @DisplayName("updateArticle")
    class UpdateArticle {

        @Test void updateArticle_shouldSucceed_withValidInput() throws Exception {
            UpdateArticleServiceInput input = new UpdateArticleServiceInput("1", "title11", "summary11", "body", List.of());
            when(repository.getById("1")).thenReturn(article1);
            when(repository.valueExists("title", "title11")).thenReturn(false);

            service.updateArticle(input);

            ArgumentCaptor<ArticleEntity> articleCaptor = ArgumentCaptor.forClass(ArticleEntity.class);
            verify(repository, times(1)).edit(articleCaptor.capture());
            ArticleEntity capturedArticle = articleCaptor.getValue();
            assertEquals("1", capturedArticle.getId());
            assertEquals("title11", capturedArticle.getTitle());
            assertEquals("summary11", capturedArticle.getSummary());
            verify(articleReferenceService, times(1)).updateReferences("1", List.of());
        }

        @Test void updateArticle_withUnchangedTitle_shouldSucceed() throws Exception {
            UpdateArticleServiceInput input = new UpdateArticleServiceInput("1", article1.getTitle(), "summary11", "body11", List.of());
            when(repository.getById("1")).thenReturn(article1);

            service.updateArticle(input);

            verify(repository, never()).valueExists(anyString(), anyString());
            verify(repository, times(1)).edit(any(ArticleEntity.class));
            verify(articleReferenceService, times(1)).updateReferences("1", List.of());
        }


        @Test
        void updateArticle_whenArticleNotFound_shouldThrowException() throws Exception {
            UpdateArticleServiceInput input = new UpdateArticleServiceInput("nonexistent", "T", "S", "B", Collections.emptyList());
            when(repository.getById("nonexistent")).thenReturn(null);

            Exception exception = assertThrows(Exception.class, () -> service.updateArticle(input));
            assertEquals("Not found", exception.getMessage());
        }
    }



    @Nested
    @DisplayName("deleteArticle")
    class DeleteArticle {
        @Test void deleteArticle_shouldCallRepository_atAllTimes() throws Exception {
            service.deleteArticle("1");
            verify(repository, times(1)).delete("1");
        }
    }



    @Nested
    @DisplayName("deleteArticles")
    class DeleteArticles {
        @Test
        void deleteArticles_shouldCallRepository_atAllTimes() throws Exception {
            service.deleteArticles();
            verify(repository, times(1)).deleteAll();
        }
    }
}
