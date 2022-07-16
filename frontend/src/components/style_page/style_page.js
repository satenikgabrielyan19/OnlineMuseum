import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';

import CardList from './card_list';

import { getArtistsByStyle, getArtworksByStyle, getStyle } from '../../services/style';

const StylePage = () => {
    const { styleId } = useParams();

    const [style, setStyle] = useState({});

    const [artists, setArtists] = useState({ data: [] });
    const [artworks, setArtworks] = useState({ data: [] });

    useEffect(() => {
        const fetchStyle = async () => {
            const styleResponse = await getStyle(styleId);

            setStyle(styleResponse);
        };

        const fetchArtists = async () => {
            const artistsResponse = await getArtistsByStyle(styleId);

            setArtists(artistsResponse);
        };

        const fetchArtworks = async () => {
            const artworksResponse = await getArtworksByStyle(styleId);

            setArtworks(artworksResponse);
        };

        fetchStyle();
        fetchArtists();
        fetchArtworks();
	}, [styleId]);

    return (
        <div  className='container'>
            <div className='row'>
                <h1 className='text-center'>{style.name}</h1>
            </div>
            <div className='row  mt-5'>
                <p>{style.descriptionUrl}</p>
            </div>
            {artists.data.length > 0 && <CardList title='Artists' data={artists.data} />}
            {artworks.data.length > 0 && <CardList title='Artworks' data={artworks.data.map(({ id, fullName, pictureUrl }) => ({ id, fullName, photoUrl: pictureUrl }))} />}
        </div>
    );
};

export default StylePage;
