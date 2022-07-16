import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';

import { capitalize } from 'lodash';

import CardList from './card_list';

import { getArtistsByBranch, getStylesByBranch } from '../../services/branch';

const BranchPage = () => {
    const { branch } = useParams();
    const [artists, setArtists] = useState({ data: [] });
    const [styles, setStyles] = useState({ data: [] });

    useEffect(() => {
        const fetchArtists = async () => {
            const artistsResponse = await getArtistsByBranch(branch);

            setArtists(artistsResponse);
        };

        const fetchStyles = async () => {
            const stylessResponse = await getStylesByBranch(branch);

            setStyles(stylessResponse);
        };

        fetchArtists();
        fetchStyles();
	}, []);

    return (
        <div  className='container'>
            <div className='row'>
                <h1 className='text-center'>{capitalize(branch)}</h1>
            </div>
            {styles.data.length > 0 && <CardList title='Styles' data={styles.data} />}
            {artists.data.length > 0 && <CardList title='Artists' data={artists.data.map(({ id, fullName }) => ({ id, name: fullName }))} />}
        </div>
    );
};

export default BranchPage;
