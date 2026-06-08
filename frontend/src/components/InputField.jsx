export function InputField(
    {label, placeHolder, value, onChange, isTextArea = false, rows = 5}) {
    return (
        <div className="form-group" style={{ width: '100%' }}>
            {label && <label>{label}</label>}

            {isTextArea ? (
                // Multi-line field variant (for the main Article Body)
                <textarea
                    className="form-control"
                    placeholder={placeHolder}
                    value={value}
                    onChange={onChange}
                    rows={rows}
                    style={{ resize: 'vertical' }} // Allows users to scale up/down safely
                />
            ) : (
                // Standard single-line field variant (for Search, Title, Summary)
                <input
                    type="text"
                    className="form-control"
                    placeholder={placeHolder}
                    value={value}
                    onChange={onChange}
                />
            )}
        </div>
    );
}