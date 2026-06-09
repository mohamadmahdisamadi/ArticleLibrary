import { useState, useEffect } from 'react';
import {articleService} from "../services/articleService.js";
import { TopMenu } from "../components/TopMenu.jsx";
import {InputField} from "../components/InputField.jsx";
import {ArticlePreview} from "../components/ArticlePreview.jsx";
import {NavButton} from "../components/NavButton.jsx";

export function Home() {
    const [articles, setArticles] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [loading, setLoading] = useState(true);

    const loadArticles = async (query = '') => {
        try {
            let data;
            if (query) {
                data = await articleService.searchArticles(query);
            } else {
                data = await articleService.getArticlesPreview();
            }
            setArticles(data);
        } catch (error) {
            console.error("Error fetching articles:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const fetchInitialData = async () => {
            setLoading(true);
            await loadArticles();
        };

        fetchInitialData();
    }, []);

    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearchQuery(value);
        setLoading(true);
        loadArticles(value);
    };

    return (
        <>
            <TopMenu title="Article Library">
                <NavButton text="Add Article" to="/add-article" type="btn-primary" />
            </TopMenu>

            <div className="container" style={{ marginTop: '10px' }}>
                <InputField
                    placeHolder="Search articles by title or summary..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                />

                {loading ? (
                    <p style={{ color: 'var(--accent-color)' }}>Loading articles...</p>
                ) : articles.length === 0 ? (
                        searchQuery.length === 0 ? (
                            <p style={{ color: "darkred" }}>No articles yet, publish the first one!</p>
                        ) : (
                            <p style={{ color: "darkred" }}>No articles found, try a different query.</p>
                        )
                ) : (
                    <div>
                        {articles.map((article) => (
                            <ArticlePreview article={article}> </ArticlePreview>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}