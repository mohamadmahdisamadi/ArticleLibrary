import {TopMenu} from "../components/TopMenu.jsx";
import {useEffect, useState} from "react";
import {articleService} from "../services/articleService.js";
import {useNavigate, useParams} from "react-router-dom";
import {InputField} from "../components/InputField.jsx";
import {ActionButton} from "../components/ActionButton.jsx";
import {CheckBox} from "../components/CheckBox.jsx";
import {NavButton} from "../components/NavButton.jsx";

export function EditArticle() {
    const { articleId } = useParams();
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [summary, setSummary] = useState('');
    const [body, setBody] = useState('');

    const [existingArticles, setExistingArticles] = useState([]);
    const [selectedCitations, setSelectedCitations] = useState([]);
    const [loadingArticle, setLoadingArticle] = useState(true);
    const [loadingArticles, setLoadingArticles] = useState(true);

    useEffect(() => {
        const fetchArticleTitles = async () => {
            try {
                const response = await articleService.getOtherArticlesTitle(articleId);
                setExistingArticles(response);
            } catch (e) {
                console.error("Failed to load article titles.", e);
            } finally {
                setLoadingArticles(false);
            }
        }
        fetchArticleTitles();
    }, [articleId]);

    useEffect(() => {
        const fetchArticleDetails = async () => {
            try {
                const data = await articleService.getArticleById(articleId);
                setTitle(data.title);
                setSummary(data.summary);
                setBody(data.body);
                console.log(data.citedArticles.map(citedArticle => citedArticle.id));
                setSelectedCitations(data.citedArticles.map(citedArticle => citedArticle.id));
            } catch (e) {
                console.error("Error fetching article details.", e.message);
                alert("Server Error while fetching article details.");
                navigate("/home-page");
            } finally {
                setLoadingArticle(false);
            }
        }
        fetchArticleDetails();
    }, [articleId])


    const handleCitationToggle = (articleId) => {
        if (selectedCitations.includes(articleId)) {
            setSelectedCitations(selectedCitations.filter(citedArticleId => citedArticleId !== articleId));
        } else {
            setSelectedCitations([...selectedCitations, articleId]);
        }
    }

    const handleUpdateArticle = async () => {
        if (!title.trim()) {
            alert("Please fill in title"); return;
        }
        if (!summary.trim()) {
            alert("Please fill in summary"); return;
        }
        if (!body.trim()) {
            alert("Please fill in body"); return;
        }

        try {
            await articleService.updateArticle(articleId, title, summary, body, selectedCitations);
            navigate(`/article-details/${articleId}`);
        } catch (e) {
            console.error("Error updating article:", e);
            alert("Failed to update the article. Please try again.");
        }
    }

    return (
        <>
            <TopMenu title="Update Article">
                <NavButton text="Home Page" to="/home-page" type="btn-primary" />
            </TopMenu>

            <div className="container" style={{ marginTop: '30px', maxWidth: '800px' }}>

                {loadingArticle ? <p>Loading article...</p> :
                <>
                    <InputField
                        label="Title"
                        placeHolder="Enter title of the article..."
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />

                    <InputField
                        label="Summary"
                        placeHolder="Provide an abstract of the article ..."
                        value={summary}
                        onChange={(e) => setSummary(e.target.value)}
                    />

                    <InputField
                        label="Body"
                        placeHolder="Write the main body of the article..."
                        value={body}
                        isTextArea={true}
                        rows={10}
                        onChange={(e) => setBody(e.target.value)}
                    />
                </>
                }

                <div style={{ marginTop: '35px', padding: '20px', background: 'royalblue', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
                    <p style={{ fontSize: '17px', color: 'black', marginBottom: '15px' }}>
                        Check the boxes next to the articles that this article references:
                    </p>

                    {loadingArticles ? (
                        <p>Loading available papers...</p>
                    ) : existingArticles.length === 0 ? (
                        <p style={{ color: 'black', fontStyle: 'italic' }}>No other articles available in the library to cite. Be the first to get citation by others.</p>
                    ) : (
                        <div style={{ maxHeight: '180px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '10px', paddingRight: '5px' }}>
                            {existingArticles.map((article) => (
                                <CheckBox
                                    text={article.title}
                                    key={article.id}
                                    checked={selectedCitations.includes(article.id)}
                                    onChange={() => handleCitationToggle(article.id)}
                                />
                            ))}
                        </div>
                    )}
                </div>

                <div style={{ marginTop: '35px', display: 'flex', justifyContent: 'flex-end' }}>
                    <ActionButton
                        text="Save Changes"
                        onClick={handleUpdateArticle}
                        type="btn-primary"
                    />
                </div>
            </div>
        </>
    )
}