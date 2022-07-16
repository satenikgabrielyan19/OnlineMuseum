import axios from 'axios';

import { getCurrentUserToken } from './auth';

const API_URL = 'http://localhost:8080/api/v1/artists';

const getArtist = async(artistId) => {
    try {
        const response = await axios.get(`${API_URL}/${artistId}`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

const createArtist = async(formData) => {
    try {
        const { jwtToken } = getCurrentUserToken();
        const response = await axios.post(
            API_URL, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${jwtToken}`,
                }
            }
        );

        return response;
    } catch {
        return null;
    }
};

const getArtworksByArtist = async(artistId) => {
    try {
        const response = await axios.get(`${API_URL}/${artistId}/artworks`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

export {
    getArtist,
    createArtist,
    getArtworksByArtist,
};