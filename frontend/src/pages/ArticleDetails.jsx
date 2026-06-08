import {TopMenu} from "../components/TopMenu.jsx";
import {NavButton} from "../components/NavButton.jsx";
import {ActionButton} from "../components/ActionButton.jsx";
import {articleService} from "../services/articleService.js";
import {Link, useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";

export function ArticleDetails() {
    const { articleId } = useParams();
    const navigate = useNavigate();

    const [article, setArticle] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchArticleDetails = async () => {
            try {
                const data = await articleService.getArticleById(articleId);
                console.log(data);
                setArticle(data);
            } catch (e) {
                console.error("Error fetching article details.", e.message);
                alert("Server Error while fetching article details.");
                navigate("/home-page");
            } finally {
                setLoading(false);
            }
        }

        fetchArticleDetails();
    }, [articleId])

    const handleDelete = async () => {
        if (window.confirm("Are you sure you want to delete this article?")) {
            try {
                await articleService.deleteArticleById(articleId);
                navigate('/home-page');
            } catch (error) {
                console.error("Delete failed:", error);
                alert("Failed to delete the article.");
            }
        }
    };

    if (loading) return <div className="container"><p>Loading article content...</p></div>;
    if (!article) return null;


    return (
        <>
            <TopMenu title="Article Details">
                <ActionButton text="Delete" type="btn-danger" onClick={handleDelete} />
                <NavButton text="Edit" to={`/edit-article/${articleId}`} type="btn-warning" />
                <NavButton text="Home Page" to="/home-page" type="btn-primary" />
            </TopMenu>

            <div className="container" style={{ marginTop: '30px', maxWidth: '850px' }}>
                <div className="article-detail-wrapper">

                    <h1 style={{ wordBreak: 'break-word', color: 'darkmagenta' }}>{article.title}</h1>

                    <p className="summary-box" style={{ wordBreak: 'break-word', fontStyle: 'italic', color: 'black', marginTop: '10px', marginBottom: '10px' }}>
                        <strong>Abstract:</strong> {article.summary}
                    </p>

                    <hr />

                    <div className="article-body" style={{
                        color: 'black', fontSize: '17px', marginTop: '15px', marginBottom: '15px',
                        whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                    }}>
                        {article.body}
                    </div>

                    <div className="citations-inline" style={{ marginTop: '50px', fontSize: '16px', color: '#334155' }}>
                        {article.citedArticles.length > 0 && <strong>Cited articles: </strong>}
                        {!article.citedArticles || article.citedArticles.length === 0 ? (
                            <span style={{ color: '#94a3b8', fontStyle: 'italic' }}>This article hasn't cited any other papers.</span>
                        ) : (
                            article.citedArticles.map((citedArticle, index) => {
                                const isLast = index === article.citedArticles.length - 1;
                                const isSecondToLast = index === article.citedArticles.length - 2;

                                return (
                                    <span key={citedArticle.id}>
                                        <Link
                                            to={`/article/${citedArticle.id}`}
                                            style={{ fontStyle: 'italic', color: 'rebeccapurple', textDecoration: 'none', fontWeight: '500' }}
                                        >
                                            {citedArticle.title}
                                        </Link>

                                        {isLast ? '.' : isSecondToLast ? ' and ' : ', '}
                                    </span>
                                );
                            })
                        )}
                    </div>

                    <p className="date" style={{ color: '#334155', marginTop: '10px', marginBottom: '10px' }}>
                        {`Published at ${article.createdAt}` +
                        (article.createdAt === article.lastModifiedAt ? '.' : ` and was last modified at ${article.lastModifiedAt}`)}
                    </p>

                </div>
            </div>
        </>
    )
}
