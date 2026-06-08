package ir.ac.ut.ece.ie.api.controller.article;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ece.ie.api.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.api.controller.article.create.CreateArticleRequest;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticlePreviewResponse;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticleResponse;
import ir.ac.ut.ece.ie.api.controller.article.get.GetArticleTitleResponse;
import ir.ac.ut.ece.ie.api.controller.article.update.UpdateArticleRequest;
import ir.ac.ut.ece.ie.api.service.article.ArticleService;
import ir.ac.ut.ece.ie.api.service.article.create.CreateArticleServiceInput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticleDetailsServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.get.GetArticlePreviewServiceOutput;
import ir.ac.ut.ece.ie.api.service.article.update.UpdateArticleServiceInput;
import ir.ac.ut.ece.ie.api.utils.DateTimeDisplayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticlesController.class)
class ArticlesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService service;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime fixedDateTime = LocalDateTime.now();



    @Nested @DisplayName("createArticle")
    class CreateArticle {
        @Test void createArticle_shouldCallService_whenRequestIsProvided() throws Exception {
            CreateArticleRequest request = new CreateArticleRequest(
                    "Test Article", "Summary", "Body", List.of());
            String body = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/articles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk());
            Mockito.verify(service).createArticle(any());
        }

        @Test
        void createArticle_shouldReturnSuccess_whenNoException() throws Exception {
            CreateArticleRequest request = new CreateArticleRequest(
                    "Test Article", "Summary", "Body", List.of());

            String body = objectMapper.writeValueAsString(request);

            MvcResult result = mockMvc.perform(post("/articles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertTrue(response.getSuccess());
        }

        @Test
        void createArticle_shouldReturnFailure_whenCatchesException() throws Exception {
            CreateArticleRequest request = new CreateArticleRequest(
                    "", "", "", List.of());

            String body = objectMapper.writeValueAsString(request);

            doThrow(new Exception("Some error message"))
                    .when(service).createArticle(any(CreateArticleServiceInput.class));

            MvcResult result = mockMvc.perform(post("/articles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("updateArticle")
    class UpdateArticle {
        @Test
        void updateArticle_shouldCallService_whenRequestIsProvided() throws Exception {
            UpdateArticleRequest request = new UpdateArticleRequest(
                    "Test Article", "Summary", "Body", List.of());
            String body = objectMapper.writeValueAsString(request);
            mockMvc.perform(put("/articles/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk());
            Mockito.verify(service).updateArticle(any());
        }

        @Test
        void updateArticle_shouldReturnSuccess_whenNoException() throws Exception {
            UpdateArticleRequest request = new UpdateArticleRequest(
                    "Test Article", "Summary", "Body", List.of());

            String body = objectMapper.writeValueAsString(request);

            String articleId = "1";
            MvcResult result = mockMvc.perform(put("/articles/" + articleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertTrue(response.getSuccess());
        }

        @Test
        void updateArticle_shouldReturnFailure_whenCatchesException() throws Exception {
            UpdateArticleRequest request = new UpdateArticleRequest(
                    "", "", "", List.of());

            String body = objectMapper.writeValueAsString(request);

            doThrow(new Exception("Some error message"))
                    .when(service).updateArticle(any(UpdateArticleServiceInput.class));

            MvcResult result = mockMvc.perform(put("/articles/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("getArticle")
    class GetArticle {
        @Test
        void getArticle_shouldCallService_whenIdIsProvided() throws Exception {
            mockMvc.perform(get("/articles/1")).andExpect(status().isOk());
            Mockito.verify(service).getArticleDetails(any());
        }

        @Test
        void getArticle_shouldReturnArticle_whenArticleIsFound() throws Exception {
            String articleId = "1";
            GetArticleDetailsServiceOutput output =
                    new GetArticleDetailsServiceOutput(
                            articleId, "Title", "Summary", "Body",
                            List.of(), fixedDateTime, fixedDateTime
                    );

            Mockito.when(service.getArticleDetails("1")).thenReturn(output);

            MvcResult result = mockMvc.perform(get("/articles/" + articleId)).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<GetArticleResponse> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<GetArticleResponse>>() {
            });

            assertTrue(response.getSuccess());
            assertEquals(response.getData(),
                    new GetArticleResponse(
                            output.id(), output.title(), output.summary(), output.body(),
                            output.citedArticles().stream().map(a -> new GetArticleTitleResponse(a.id(), a.title())).toList(),
                            DateTimeDisplayer.getDetailedDateTime(output.createdAt()), DateTimeDisplayer.getDetailedDateTime(output.lastModifiedAt())));
        }

        @Test
        void getArticle_shouldReturnFailure_whenCatchesException() throws Exception {
            Mockito.when(service.getArticleDetails(any(String.class))).thenThrow(new Exception("Some server error"));

            MvcResult result = mockMvc.perform(get("/articles/1")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("getArticles")
    class GetArticles {
        @Test
        void getArticles_shouldCallService_atAllTime() throws Exception {
            mockMvc.perform(get("/articles")).andExpect(status().isOk());
            Mockito.verify(service).getArticlesDetails(any());
        }

        @Test
        void getArticles_shouldReturnArticles_whenNoException() throws Exception {
            GetArticleDetailsServiceOutput output =
                    new GetArticleDetailsServiceOutput(
                            "1", "Title", "Summary", "Body",
                            List.of(), fixedDateTime, fixedDateTime);
            Mockito.when(service.getArticlesDetails(null)).thenReturn(Collections.nCopies(3, output));

            MvcResult result = mockMvc.perform(get("/articles")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<List<GetArticleResponse>> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<List<GetArticleResponse>>>() {
            });

            assertTrue(response.getSuccess());
            assertEquals(3, response.getData().size());
            assertEquals(
                    new GetArticleResponse(
                            output.id(), output.title(), output.summary(), output.body(),
                            output.citedArticles().stream().map(a -> new GetArticleTitleResponse(a.id(), a.title())).toList(),
                            DateTimeDisplayer.getDetailedDateTime(output.createdAt()), DateTimeDisplayer.getDetailedDateTime(output.lastModifiedAt())),
                    response.getData().get(0));
        }

        @Test
        void getArticles_shouldReturnFailure_whenCatchesException() throws Exception {
            Mockito.when(service.getArticlesDetails(null)).thenThrow(new Exception("Some server error"));

            MvcResult result = mockMvc.perform(get("/articles")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("getArticlePreview")
    class GetArticlePreview {
        @Test
        void getArticlePreview_shouldCallService_whenIdIsProvided() throws Exception {
            mockMvc.perform(get("/articles/1/preview")).andExpect(status().isOk());
            Mockito.verify(service).getArticlePreview(any());
        }

        @Test
        void getArticlePreview_shouldReturnArticle_whenArticleIsFound() throws Exception {
            String articleId = "1";
            GetArticlePreviewServiceOutput output =
                    new GetArticlePreviewServiceOutput(
                            articleId, "Title", "Summary", 1, 1, fixedDateTime
                    );

            Mockito.when(service.getArticlePreview("1")).thenReturn(output);

            MvcResult result = mockMvc.perform(get("/articles/" + articleId + "/preview")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<GetArticlePreviewResponse> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<GetArticlePreviewResponse>>() {
            });

            assertTrue(response.getSuccess());
            assertEquals(response.getData(),
                    new GetArticlePreviewResponse(
                            output.id(), output.title(), output.summary(), output.citedCount(), output.citingCount(),
                            DateTimeDisplayer.getShortDateTime(output.createdAt())));
        }

        @Test
        void getArticlePreview_shouldReturnFailure_whenCatchesException() throws Exception {
            Mockito.when(service.getArticlePreview(any(String.class))).thenThrow(new Exception("Some server error"));

            MvcResult result = mockMvc.perform(get("/articles/1/preview")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
            assertNull(response.getData());
        }
    }



    @Nested @DisplayName("getArticlesPreview")
    class GetArticlesPreview {
        @Test
        void getArticlesPreview_shouldCallService_atAllTime() throws Exception {
            mockMvc.perform(get("/articles/preview")).andExpect(status().isOk());
            Mockito.verify(service).getArticlesPreview(null);
        }

        @Test
        void getArticlesPreview_shouldReturnArticles_whenNoException() throws Exception {
            GetArticlePreviewServiceOutput output =
                    new GetArticlePreviewServiceOutput(
                            "1", "Title", "Summary", 1, 1, fixedDateTime);

            Mockito.when(service.getArticlesPreview(null)).thenReturn(Collections.nCopies(3, output));

            MvcResult result = mockMvc.perform(get("/articles/preview")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<List<GetArticlePreviewResponse>> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<List<GetArticlePreviewResponse>>>() {
            });

            assertTrue(response.getSuccess());
            assertEquals(3, response.getData().size());
            assertEquals(
                    new GetArticlePreviewResponse(
                            output.id(), output.title(), output.summary(), output.citedCount(), output.citingCount(),
                            DateTimeDisplayer.getShortDateTime(output.createdAt())),
                    response.getData().get(0));
        }

        @Test
        void getArticlesPreview_shouldReturnFailure_whenCatchesException() throws Exception {
            Mockito.when(service.getArticlesPreview(null)).thenThrow(new Exception("Some server error"));

            MvcResult result = mockMvc.perform(get("/articles/preview")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("searchArticles")
    class SearchArticles {
        @Test
        void searchArticles_shouldNotCallService_whenQueryParamIsNotProvided() throws Exception {
            mockMvc.perform(get("/articles/search")).andExpect(status().is4xxClientError());
            Mockito.verify(service, never()).getArticlesPreview(any());
        }

        @Test
        void searchArticles_shouldCallService_whenQueryParamIsProvided() throws Exception {
            mockMvc.perform(get("/articles/search?query=BlueDemon")).andExpect(status().isOk());
            Mockito.verify(service).getArticlesPreview("BlueDemon");
        }

        @Test
        void searchArticles_shouldReturnArticles_whenNoException() throws Exception {
            GetArticlePreviewServiceOutput output =
                    new GetArticlePreviewServiceOutput(
                            "1", "Title", "Summary", 1, 1, fixedDateTime);
            Mockito.when(service.getArticlesPreview("RussianConeyIsland")).thenReturn(Collections.nCopies(3, output));

            MvcResult result = mockMvc.perform(get("/articles/search?query=RussianConeyIsland")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<List<GetArticlePreviewResponse>> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<List<GetArticlePreviewResponse>>>() {
            });

            assertTrue(response.getSuccess());
            assertEquals(3, response.getData().size());
            assertEquals(
                    new GetArticlePreviewResponse(
                            output.id(), output.title(), output.summary(), output.citedCount(), output.citingCount(),
                            DateTimeDisplayer.getShortDateTime(output.createdAt())),
                    response.getData().get(0));
        }

        @Test
        void searchArticles_shouldReturnFailure_whenCatchesException() throws Exception {
            Mockito.when(service.getArticlesPreview("UnholyCover")).thenThrow(new Exception("Some server error"));

            MvcResult result = mockMvc.perform(get("/articles/search?query=UnholyCover")).andExpect(status().isOk()).andReturn();

            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }



    @Nested @DisplayName("deleteArticle")
    class DeleteArticle {
        @Test
        void deleteArticle_shouldCallService_whenIdIsProvided() throws Exception {
            String articleId = "1";
            mockMvc.perform(delete("/articles/" + articleId)).andExpect(status().isOk());
            verify(service).deleteArticle(articleId);
        }

        @Test
        void deleteArticle_shouldReturnFailure_whenCatchesException() throws Exception {
            doThrow(new Exception("Some server error")).when(service).deleteArticle(any(String.class));
            MvcResult result = mockMvc.perform(delete("/articles/1")).andExpect(status().isOk()).andReturn();
            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }

        @Test
        void deleteArticle_shouldReturnSuccess_whenIdIsProvided() throws Exception {
            String articleId = "1";
            MvcResult result = mockMvc.perform(delete("/articles/" + articleId)).andExpect(status().isOk()).andReturn();
            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertTrue(response.getSuccess());
        }
    }



    @Nested @DisplayName("deleteArticles")
    class DeleteArticles {
        @Test
        void deleteArticles_shouldCallService_atAllTime() throws Exception {
            mockMvc.perform(delete("/articles")).andExpect(status().isOk());
            verify(service).deleteArticles();
        }

        @Test
        void deleteArticles_shouldReturnFailure_whenCatchesException() throws Exception {
            doThrow(new Exception("Some server error")).when(service).deleteArticles();
            MvcResult result = mockMvc.perform(delete("/articles")).andExpect(status().isOk()).andReturn();
            String responseJson = result.getResponse().getContentAsString();
            ApiResult<Void> response = objectMapper.readValue(responseJson, new TypeReference<ApiResult<Void>>() {
            });

            assertFalse(response.getSuccess());
        }
    }
}