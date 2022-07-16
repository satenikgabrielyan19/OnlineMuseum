import React from 'react';
import { Link } from 'react-router-dom';

import { chunk } from 'lodash';

const style = {
    width: '15rem',
};

const CARDS_PER_ROW = 5;

const CardList = ({ title, data }) => {
    return (
        <>
            <div className='row  mt-5'>
                <h3>{title}</h3>
            </div>
            <div>
                {
                    chunk(data, CARDS_PER_ROW).map(dataChunk => (
                        <>
                            <div className='row mt-3' key={dataChunk.map(({ id }) => id).join('-')}>
                                {
                                    dataChunk.map(({ id, name }) => (
                                        <div className='col-2' style={style} key={id}>
                                            <div className='card border-success'>
                                                <div className='card-body text-success'>
                                                    <Link to={`/${title === 'Styles' ? 'styles' : 'artists'}/${id}`}>
                                                        <h5 className='card-title text-center'>{name}</h5>
                                                    </Link>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                }
                            </div>
                        </>
                    ))
                }
            </div>
        </>
    );
};

export default CardList;
