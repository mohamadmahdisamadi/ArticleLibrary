package ir.ac.ut.ece.ie.domain.service;

import ir.ac.ut.ece.ie.domain.entity.article.ArticleReferenceEntity;
import ir.ac.ut.ece.ie.infrastructure.repository.ArticleReferenceRepository;
import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleReferenceServiceTest {

    @Mock
    private ArticleReferenceRepository repository;

    @InjectMocks
    private ArticleReferenceServiceImpl service;

    @Captor
    private ArgumentCaptor<List<ArticleReferenceEntity>> entityListCaptor;



    @Nested @DisplayName("addReferences")
    class AddReferences {
        @Test void addReferences_shouldAddReferencesCorrectly_attAllTimes() throws Exception {
            String articleId = "1";
            List<String> citedIds = List.of("2", "3");

            service.addReferences(articleId, citedIds);

            verify(repository).addMany(entityListCaptor.capture());
            List<ArticleReferenceEntity> savedEntities = entityListCaptor.getValue();

            assertEquals(2, savedEntities.size());
            assertEquals(articleId, savedEntities.get(0).getCitingArticleId());
            assertEquals("2", savedEntities.get(0).getCitedArticleId());
            assertEquals("3", savedEntities.get(1).getCitedArticleId());
        }
    }



    @Nested @DisplayName("updateReferences")
    class UpdateReferences {
        @Test void updateReferences_shouldDeleteOldAndAddNewReferences_atAllTime() throws Exception {
            String articleId = "1";
            List<String> newCitedIds = List.of("2");

            service.updateReferences(articleId, newCitedIds);

            verify(repository).deleteAllHaving("citingArticleId", articleId);
            verify(repository).addMany(entityListCaptor.capture());
            List<ArticleReferenceEntity> savedEntities = entityListCaptor.getValue();
            assertEquals(1, savedEntities.size());
            assertEquals(articleId, savedEntities.get(0).getCitingArticleId());
            assertEquals("2", savedEntities.get(0).getCitedArticleId());
        }
    }



    @Nested @DisplayName("getReferences")
    class GetReferences {
        @Test void getReferences_shouldReturnIngoingReferences_whenAskedForIngoing() throws Exception {
            String articleId = "1";
            ArticleReferenceEntity entity = new ArticleReferenceEntity()
                    .setCitingArticleId("2")
                    .setCitedArticleId(articleId);

            when(repository.searchField("citedArticleId", articleId, FieldComparator.EXACT))
                    .thenReturn(List.of(entity));

            List<String> result = service.getReferences(articleId, true);

            assertEquals(1, result.size());
            assertEquals("2", result.get(0));
            verify(repository).searchField("citedArticleId", articleId, FieldComparator.EXACT);
        }

        @Test void getReferences_shouldReturnOutgoingReferences_whenAskedForOutgoing() throws Exception {
            String articleId = "1";
            ArticleReferenceEntity entity = new ArticleReferenceEntity()
                    .setCitingArticleId(articleId)
                    .setCitedArticleId("2");

            when(repository.searchField("citingArticleId", articleId, FieldComparator.EXACT))
                    .thenReturn(List.of(entity));

            List<String> result = service.getReferences(articleId, false);

            assertEquals(1, result.size());
            assertEquals("2", result.get(0));
            verify(repository).searchField("citingArticleId", articleId, FieldComparator.EXACT);
        }
    }



    @Nested @DisplayName("getReferencesCount")
    class GetReferencesCount {
        @Test void getReferenceCount_shouldCallRepository_atAllTime() throws Exception {
            when(repository.searchField("citingArticleId", "1", FieldComparator.EXACT))
                    .thenReturn(List.of(new ArticleReferenceEntity()));
            service.getReferencesCount("1", false);
            verify(repository, times(1)).searchField(anyString(), eq("1"), any());
        }

        @Test void getReferencesCount_shouldReturnIngoingReferencesCount_whenAskedForIngoing() throws Exception {
            String articleId = "1";

            when(repository.searchField("citedArticleId", articleId, FieldComparator.EXACT))
                    .thenReturn(List.of(new ArticleReferenceEntity(), new ArticleReferenceEntity()));

            int count = service.getReferencesCount(articleId, true);
            assertEquals(2, count);
        }

        @Test void getReferencesCount_shouldReturnOutgoingReferencesCount_whenAskedForOutgoing() throws Exception {
            String articleId = "1";

            when(repository.searchField("citingArticleId", articleId, FieldComparator.EXACT))
                    .thenReturn(List.of(new ArticleReferenceEntity()));

            int count = service.getReferencesCount(articleId, false);
            assertEquals(1, count);
        }
    }
}
