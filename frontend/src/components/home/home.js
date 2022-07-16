import React from 'react';

import Branch from '../branch/branch';

import paitingImage from './painting.jpg';
import sculptureImage from './sculpture.jpg';
import architectureImage from './architecture.jpg';

const Home = () => {
    return (
        <div className='row'>
            <div className='col-4'>
                <Branch title='Painting' imgSrc={paitingImage}></Branch>
            </div>
            <div className='col-4'>
                <Branch title='Sculpture' imgSrc={sculptureImage}></Branch>
            </div>
            <div className='col-4'>
                <Branch title='Architecture' imgSrc={architectureImage}></Branch>
            </div>
        </div>
    );
};

export default Home;