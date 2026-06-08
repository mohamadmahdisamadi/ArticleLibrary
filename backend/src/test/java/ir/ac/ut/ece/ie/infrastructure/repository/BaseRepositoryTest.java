package ir.ac.ut.ece.ie.infrastructure.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BaseRepositoryTest {

    @TempDir
    Path tempDir;

    private TestRepository repository;
    private SoftDeleteTestRepository softDeleteRepository;
    private Path repositoryFilePath;
    private Path softDeleteRepositoryFilePath;
    private TestEntity entity;
    private SoftDeleteTestEntity softDeleteEntity;
    private LocalDateTime fixedTime = LocalDateTime.of(2026, 5, 26, 0, 0);

    @BeforeEach
    void setUp() {
        repositoryFilePath = tempDir.resolve("test_repository.txt");
        softDeleteRepositoryFilePath = tempDir.resolve("soft_delete_test_repository.txt");

        repository = new TestRepository(repositoryFilePath.toString());
        softDeleteRepository = new SoftDeleteTestRepository(softDeleteRepositoryFilePath.toString());

        entity = new TestEntity(
                "The worse reality gets, the less you wanna hear about it",
                47, List.of("indie", "alternative"), fixedTime);
        softDeleteEntity = new SoftDeleteTestEntity("Solidarity can be difficult when you got cool stuff to lose");
    }

    @Nested @DisplayName("add")
    class CreateAndReadTests {
        @Test void givenNewEntity_shouldSetBaseEntityFields_whenAdded() throws Exception {
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();
            assertAll(
                    () -> assertNull(entity.getId()),
                    () -> assertNull(entity.getCreatedAt()),
                    () -> assertNull(entity.getLastModifiedAt())
            );

            repository.add(entity);
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();
            assertAll(
                    () -> assertNotNull(entity.getId()),
                    () -> assertNotNull(entity.getCreatedAt()),
                    () -> assertNotNull(entity.getLastModifiedAt()),
                    () -> assertEquals(numberOfLinesBeforeOperation + 1, numberOfLinesAfterOperation)
            );
        }

        @Test void givenNewEntity_shouldSaveEntityToRepository_whenAdded() throws Exception {
            repository.add(entity);
            assertNotNull(entity.getId());
            TestEntity retrieved = repository.getById(entity.getId());
            assertNotNull(retrieved);
            assertThat(retrieved).usingRecursiveComparison().isEqualTo(entity);
        }

        @Test void givenListOfEntities_shouldSaveAll_whenAddManyIsCalled() throws Exception {
            List<TestEntity> entities = Collections.nCopies(7, entity);
            repository.addMany(entities);
            for (TestEntity e : entities)
                assertNotNull(repository.getById(e.getId()));
        }
    }



    @Nested
    @DisplayName("update")
    class UpdateTests {
        @Test void givenExistingEntity_shouldUpdateLastModifiedAt_whenUpdated() throws Exception {
            repository.add(entity);
            TestEntity retrievedEntity = repository.getById(entity.getId());
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();

            String newName = "I wanna be a seven-foot zombie";
            retrievedEntity.setName(newName);
            Thread.sleep(10); // wait enough time to get a new lastModifiedTime
            repository.edit(retrievedEntity);
            TestEntity updatedEntity = repository.getById(entity.getId());
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();

            assertAll(
                () -> assertEquals(newName, updatedEntity.getName()),
                () -> assertNotNull(updatedEntity.getLastModifiedAt()),
                () -> assertTrue(updatedEntity.getLastModifiedAt().isAfter(updatedEntity.getCreatedAt())),
                () -> assertEquals(numberOfLinesBeforeOperation, numberOfLinesAfterOperation)
            );
        }

        @Test void givenAlreadyDeletedEntity_shouldThrowException_whenUpdated() throws Exception {
            softDeleteRepository.add(softDeleteEntity);
            SoftDeleteTestEntity retrievedEntity = softDeleteRepository.getById(softDeleteEntity.getId());
            softDeleteRepository.delete(softDeleteEntity.getId());

            Exception e = assertThrows(Exception.class, () -> softDeleteRepository.edit(retrievedEntity));
            assertEquals("Entity does not exist", e.getMessage());
        }

        @Test void givenUnavailableEntityId_shouldThrowException_whenUpdated() throws Exception {
            repository.add(entity);
            TestEntity retrievedEntity = repository.getById(entity.getId());

            retrievedEntity.setId("Animals on TV singing (definitely not an existing id)");

            Exception e = assertThrows(Exception.class, () -> repository.edit(retrievedEntity));
            assertEquals("Not found", e.getMessage());
        }
    }



    @Nested @DisplayName("delete")
    class DeleteTests {
        @Test void givenNotExistingEntityId_shouldThrowException_whenDeleted() throws Exception {
            String entityId = "There's no one I disapprove of more or root from more than myself (definitely not an existing id)";
            Exception e = assertThrows(Exception.class, () -> repository.delete(entityId));
            assertEquals("Not found", e.getMessage());
        }

        @Test void givenHardDeleteEntity_shouldBeHardDeleted_whenDeleted() throws Exception {
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();
            repository.add(entity);
            repository.delete(entity.getId());
            assertNull(repository.getById(entity.getId()));
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();
            assertEquals(numberOfLinesBeforeOperation, numberOfLinesAfterOperation);
        }

        @Test void givenSoftDeleteEntity_shouldBeSoftDeleted_whenDeleted() throws Exception {
            softDeleteRepository.add(softDeleteEntity);
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();
            softDeleteRepository.delete(softDeleteEntity.getId());
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();

            assertEquals(numberOfLinesBeforeOperation, numberOfLinesAfterOperation);
            assertNull(softDeleteRepository.getById(softDeleteEntity.getId()));
            assertTrue(Files.readString(softDeleteRepositoryFilePath).contains(softDeleteEntity.getId()));
        }

        @Test void givenHardDeleteEntities_shouldAllBeHardDeleted_whenDeletedMany() throws Exception {
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();
            repository.add(entity);
            repository.delete(entity.getId());
            assertNull(repository.getById(entity.getId()));
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();
            assertEquals(numberOfLinesBeforeOperation, numberOfLinesAfterOperation);
        }

        @Test void givenSoftDeleteEntities_shouldAllBeSoftDeleted_whenDeletedMany() throws Exception {
            softDeleteRepository.add(softDeleteEntity);
            long numberOfLinesBeforeOperation = Files.lines(repositoryFilePath).count();
            softDeleteRepository.delete(softDeleteEntity.getId());
            long numberOfLinesAfterOperation = Files.lines(repositoryFilePath).count();

            assertEquals(numberOfLinesBeforeOperation, numberOfLinesAfterOperation);
            assertNull(softDeleteRepository.getById(softDeleteEntity.getId()));
            assertTrue(Files.readString(softDeleteRepositoryFilePath).contains(softDeleteEntity.getId()));
        }

        @Test void givenFieldAndValue_shouldDeleteAllMatching_whenDeleteAllHaving() throws Exception {
            repository.add(entity);
            TestEntity entity2 = new TestEntity("If you're better than me, you don't have to judge me", 17, List.of(), fixedTime);
            repository.add(entity2);

            assertNotNull(repository.getById(entity.getId()));
            repository.deleteAllHaving("name", entity.getName());
            assertNull(repository.getById(entity.getId()));
            assertNotNull(repository.getById(entity2.getId()));
        }

        @Test void givenRepositoryWithEntities_shouldReturnNothing_whenDeleteAll() throws Exception {
            repository.add(entity);
            TestEntity entity2 = new TestEntity("Throwing all my plans out the window", 17, List.of(), fixedTime);
            repository.add(entity2);

            assertFalse(repository.getAll().isEmpty());
            repository.deleteAll();
            assertTrue(repository.getAll().isEmpty());
        }
    }



    @Nested @DisplayName("get")
    class GetTests {
        @Test void givenEntityId_shouldReturnEntity_whenExists() throws Exception {
            repository.add(entity);
            assertNotNull(repository.getById(entity.getId()));
        }

        @Test void givenNonExistingEntityId_shouldReturnNull_whenGetByIdIsCalled() throws Exception {
            String nonExistingId = "If i walk away from you and leave my love, could I laugh again?";
            assertNull(repository.getById(nonExistingId));
        }

        @Test void givenEmptyRepository_shouldReturnEmptyList_whenGetAllIsCalled() throws Exception {
            assertTrue(repository.getAll().isEmpty());
        }

        @Test void givenFilledRepository_shouldReturnNonEmptyList_whenGetAllIsCalled() throws Exception {
            repository.add(entity);
            assertFalse(repository.getAll().isEmpty());
        }

        @Test void givenEntityThatDoesNotPassQueryFilter_shouldNotReturnIt_whenGetAllIsCalled() throws Exception {
            softDeleteRepository.add(softDeleteEntity);
            softDeleteRepository.delete(softDeleteEntity.getId());
            assertFalse(softDeleteRepository.getAll().stream().anyMatch(e -> e.getId().equals(softDeleteEntity.getId())));
        }

    }
}
