import {apiCaller, handleVoidResponse, handleDataResponse} from "./apiCaller.js";

export const articleService = {

    getAllArticles: async () => {
        const response = await apiCaller.get('/articles');
        return handleDataResponse(response);
    },

    getArticlesPreview: async () => {
        const response = await apiCaller.get('/articles/preview');
        return handleDataResponse(response);
    },

    getArticlesTitle: async () => {
        const response = await apiCaller.get('/articles/title');
        return handleDataResponse(response);
    },

    getOtherArticlesTitle: async (articleId) => {
        const response = await apiCaller.get('/articles/title');
        const data = handleDataResponse(response);
        return data.filter(article => article.id !== Number(articleId));
    },


    getArticleById: async (id) => {
        const response = await apiCaller.get(`/articles/${id}`);
        return handleDataResponse(response);
    },

    getArticlePreviewById: async (id) => {
        const response = await apiCaller.get(`/articles/${id}/preview`);
        return handleDataResponse(response);
    },

    searchArticles: async (query) => {
        const response = await apiCaller.get('/articles/search', {
            params: { query: query }
        });
        return handleDataResponse(response);
    },

    createArticle: async (title, summary, body, citedArticleIds = []) => {
        const requestBody = {
            title: title,
            summary: summary,
            body: body,
            citedArticleIds: citedArticleIds
        };
        const response = await apiCaller.post('/articles', requestBody);
        return handleVoidResponse(response);
    },

    updateArticle: async (id, title, summary, body, citedArticleIds = []) => {
        const requestBody = {
            title: title,
            summary: summary,
            body: body,
            citedArticleIds: citedArticleIds
        };
        const response = await apiCaller.put(`/articles/${id}`, requestBody);
        return handleVoidResponse(response);
    },

    deleteArticleById: async (id) => {
        const response = await apiCaller.delete(`/articles/${id}`);
        return handleVoidResponse(response);
    },

    deleteAllArticles: async () => {
        const response = await apiCaller.delete('/articles');
        return handleVoidResponse(response);
    }
};
