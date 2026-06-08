export function TopMenu({ title, children }) {
    return (
        <nav className="navbar">
            <div className="container navbar-content">
                <span className="logo">{title}</span>

                <div className="navbar-links" style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                    {children}
                </div>
            </div>
        </nav>
    );
}