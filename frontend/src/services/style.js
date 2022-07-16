import axios from 'axios';

import { getCurrentUserToken } from './auth';

const API_URL = 'http://localhost:8080/api/v1/style';

const getStyle = async(styleId) => {
    try {
        const response = await axios.get(`${API_URL}/${styleId}`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

const createStyle = async({ name, branch, description }) => {
    try {
        const { jwtToken } = getCurrentUserToken();
        const response = await axios.post(
            API_URL, {
                name,
                sphere: branch,
                descriptionUrl: description,
            }, {
                headers: { 'Authorization': `Bearer ${jwtToken}` }
            }
        );

        return response;
    } catch {
        return null;
    }
};

const getArtistsByStyle = async(styleId) => {
    try {
        const response = await axios.get(`${API_URL}/${styleId}/artists`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

const getArtworksByStyle = async(styleId) => {
    try {
        const response = await axios.get(`${API_URL}/${styleId}/artworks`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

export {
    getStyle,
    createStyle,
    getArtistsByStyle,
    getArtworksByStyle,
};