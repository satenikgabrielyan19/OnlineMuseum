import React from 'react';

import { Link } from 'react-router-dom';

const style = {
    width: '18rem',
};

const Branch = ({ title, imgSrc }) => {
    return (
        <div className='card' style={style}>
            <img src={imgSrc} className='card-img-top' alt='...' />
            <div className='card-body'>
                <Link to={`/branch/${title.toLowerCase()}`} className='btn btn-primary h-100 d-flex align-items-center justify-content-center'>
                    <h6 className='card-title'>{title}</h6>
                </Link>
            </div>
        </div>
    );
};

export default Branch;
