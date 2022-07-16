import axios from 'axios';

import { getCurrentUserToken } from './auth';

const API_URL = 'http://localhost:8080/api/v1/artworks';

const createArtwork = async(formData) => {
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

export {
    createArtwork,
};