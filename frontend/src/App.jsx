import { Outlet } from 'react-router-dom';

function App() {
    return (
        <div>
            <main style={{ marginTop: '20px' }}>
                <Outlet />
            </main>
        </div>
    );
}

export default App;