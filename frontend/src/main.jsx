import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import App from './App.jsx';
import { Home } from './pages/Home.jsx';
import './index.css';
import {AddArticle} from "./pages/AddArticle.jsx";
import {ArticleDetails} from "./pages/ArticleDetails.jsx";
import {EditArticle} from "./pages/EditArticle.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                path: '',
                element: <Home />
            },
            {
                path: 'home-page',
                element: <Home />
            },
            {
                path: 'add-article',
                element: <AddArticle />
            },
            {
                path: 'edit-article/:articleId',
                element: <EditArticle />
            },
            {
                path: 'article-details/:articleId',
                element: <ArticleDetails />
            },

        ]
    }
]);

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>
);