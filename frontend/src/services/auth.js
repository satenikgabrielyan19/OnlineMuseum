import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/auth';

const login = async(email, password) => {
    try {
        const response = await axios.post(`${API_URL}/login`, {
            email,
            password
        });

        if (response.data.jwtToken) {
            localStorage.setItem('user', JSON.stringify(response.data));

            return response.data;
        }
    } catch {
        return null;
    }
};

const signup = async(payload) => {
    try {
        const response = await axios.post(`${API_URL}/signup`, payload);

        return response;
    } catch {
        return null;
    }
};

const forgotPassword = async(email) => {
    try {
        const response = await axios.post(`${API_URL}/forgot_password`, { email });

        return response.data;
    } catch {
        return null;
    }
};

const resetPassword = async(password, token) => {
    try {
        const response = await axios.post(`${API_URL}/reset_password`, { password, token });

        return response.data;
    } catch {
        return null;
    }
};

const logout = () => {
    localStorage.removeItem('user');
};

const getCurrentUserToken = () => {
    return JSON.parse(localStorage.getItem('user'));
};

export {
    login,
    logout,
    signup,
    forgotPassword,
    resetPassword,
    getCurrentUserToken,
};