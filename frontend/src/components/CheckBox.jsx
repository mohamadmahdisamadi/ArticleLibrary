export function CheckBox({checked, onChange, text}) {
    return (
        <label style={{ color: 'black', display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer', padding: '5px', borderRadius: '4px' }}>
            <input
                type="checkbox"
                checked={checked}
                onChange={onChange}
                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
            />
            <span style={{ fontSize: '15px' }}>{text}</span>
        </label>
    )
}