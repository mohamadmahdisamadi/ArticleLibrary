import axios from 'axios';

export const apiCaller = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
});

export const handleDataResponse = (response) => {
    const { success, message, data } = response.data;
    if (success)
        return data;
    else
        throw new Error(message || 'Server Error');
};

export const handleVoidResponse = (response) => {
    const { success, message } = response.data;
    if (!success)
        throw new Error(message || 'Server Error');
};

