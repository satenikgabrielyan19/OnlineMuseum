import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/spheres';

const getStylesByBranch = async(branch) => {
    try {
        const response = await axios.get(`${API_URL}/${branch}/styles`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

const getArtistsByBranch = async(branch) => {
    try {
        const response = await axios.get(`${API_URL}/${branch}/artists`);

        return response.data;
    } catch {
        return { data: [] };
    }
};

export {
    getStylesByBranch,
    getArtistsByBranch,
};