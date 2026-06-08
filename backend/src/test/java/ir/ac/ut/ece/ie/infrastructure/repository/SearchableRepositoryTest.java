package ir.ac.ut.ece.ie.infrastructure.repository;

import ir.ac.ut.ece.ie.infrastructure.utils.FieldComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class SearchableRepositoryTest {

    @TempDir
    Path tempDir;

    private SearchableTestRepository repository;
    private TestEntity entity1;
    private TestEntity entity2;
    private TestEntity entity3;
    private final LocalDateTime fixedTime = LocalDateTime.of(2004, Month.SEPTEMBER, 14, 0, 0);
    private final LocalDateTime theDayAfterFixedTime = fixedTime.plusDays(1);

    @BeforeEach
    void setUp() throws Exception {
        repository = new SearchableTestRepository(tempDir.toString() + "/searchable-repository-for-test.txt");

        entity1 = new TestEntity("It hurts to remember", 7, List.of(), fixedTime);
        entity2 = new TestEntity("It hurts to hold on", 17, List.of(), fixedTime);
        entity3 = new TestEntity("Turn my head", 27, List.of(), theDayAfterFixedTime);

        repository.add(entity1);
        repository.add(entity2);
        repository.add(entity3);
    }



    @Nested @DisplayName("searchField")
    class SearchFieldTests {
        @Test void givenExistingSingleMatch_shouldReturnOneEntity_whenSearchFieldIsCalled() throws Exception {
            List<TestEntity> results = repository.searchField("name", "Turn my head", FieldComparator.EXACT);

            assertEquals(1, results.size());
            assertThat(entity3).usingRecursiveComparison().isEqualTo(results.get(0));
        }

        @Test void givenExistingMultipleMatches_shouldReturnAllMatchingEntities_whenSearchFieldIsCalled() throws Exception {
            List<TestEntity> results = repository.searchField("name", "It hurts to", FieldComparator.CONTAINS);

            assertEquals(2, results.size());
            assertThat(results)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(entity1, entity2));
        }

        @Test void givenNonMatchingValue_shouldReturnEmptyList_whenSearchFieldIsCalled() throws Exception {
            List<TestEntity> results = repository.searchField("age", 21, FieldComparator.EXACT);
            assertTrue(results.isEmpty());
        }

        @Test void givenCaseInsensitiveMatch_shouldReturnEntities_whenSearchFieldIsCalled() throws Exception {
            List<TestEntity> results = repository.searchField("name", "turn my head", FieldComparator.EXACT_IGNORE_CASE);
            assertThat(entity3).usingRecursiveComparison().isEqualTo(results.get(0));
        }

        @Test void givenNonExistentField_shouldReturnEmptyList_whenSearchFieldIsCalled() throws Exception {
            List<TestEntity> results = repository.searchField("notLights", "The hurt's relentless", FieldComparator.EXACT);
            assertTrue(results.isEmpty());
        }
    }
}
