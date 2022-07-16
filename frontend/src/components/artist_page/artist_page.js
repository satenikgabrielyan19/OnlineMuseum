import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';

import CardList from './card_list';

import { getArtist, getArtworksByArtist } from '../../services/artist';

const ArtistPage = () => {
    const { artistId } = useParams();

    const [artist, setArtist] = useState({});

    const [artworks, setArtworks] = useState({ data: [] });

    useEffect(() => {
        const fetchArtist = async () => {
            const artistResponse = await getArtist(artistId);

            setArtist(artistResponse);
        };

        const fetchArtworks = async () => {
            const artworksResponse = await getArtworksByArtist(artistId);

            setArtworks(artworksResponse);
        };

        fetchArtist();
        fetchArtworks();
	}, [artistId]);

    return (
        <div  className='container'>
            <div className='row'>
                <h1 className='text-center'>{artist.fullName}</h1>
            </div>
            {artworks.data.length > 0 && <CardList title='Artworks' data={artworks.data.map(({ id, fullName, pictureUrl }) => ({ id, fullName, photoUrl: pictureUrl }))} />}
        </div>
    );
};

export default ArtistPage;
