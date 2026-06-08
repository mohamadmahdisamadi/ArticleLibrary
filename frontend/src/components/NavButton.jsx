import { Link } from 'react-router-dom';

export function NavButton ({type, to, text}) {
    const className = 'btn ' + type;
    return (
        <Link to={to} className={className} > {text} </Link>
    )
}