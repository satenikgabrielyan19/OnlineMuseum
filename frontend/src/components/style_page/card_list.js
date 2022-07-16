import React from 'react';
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
                                    dataChunk.map(({ id, fullName, photoUrl }) => (
                                        <div className='col-2' style={style} key={id}>
                                            <div className='card border-success'>
                                                <img src={'http://127.0.0.1:8887/' + photoUrl.split('\\').slice(4).join('/')} className="card-img-top" alt={fullName} />
                                                <div className='card-body text-success'>
                                                    <h5 className='card-title text-center'>{fullName}</h5>
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
