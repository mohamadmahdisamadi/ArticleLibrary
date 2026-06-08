export function ActionButton({ text, type, onClick }) {
    const className = 'btn ' + type;
    return (
        <button className={className} onClick={onClick}> {text} </button>
    )
}