import {Link} from "react-router-dom";

export function ArticlePreview({article}) {
    return (
        <Link
            to={`/article-details/${article.id}`}
            className="article-card-link"
            style={{ textDecoration: 'none' }}
        >
            <div key={article.id} className="article-card">
                <h3>
                    {article.title.length > 64
                        ? `${article.title.substring(0, 64)}...`
                        : article.title}
                </h3>
                <p className="summary">
                    {article.summary.length > 100
                        ? `${article.summary.substring(0, 100)}...`
                        : article.summary}
                </p>
                <div className="meta">
                    <span>
                        Published: {article.createdAt}
                    </span>
                    <span>
                        Citations: <strong>{article.citedCount}</strong> |
                        Citing: <strong>{article.citingCount}</strong>
                    </span>
                </div>
            </div>
        </Link>
    )
}