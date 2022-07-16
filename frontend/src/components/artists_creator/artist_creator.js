import React, { useState } from 'react';

import { createArtist } from '../../services/artist';

const ArtistCreator = () => {
  const [isError, setIsError] = useState(false);
  const [hasCreated, setHasCreated] = useState(false);

  const handleSubmit = async (event) => {
		event.preventDefault();

		const {
			fullName,
			branch,
			birthday,
			deathday,
			photo,
			bio,
		} = document.forms[0];

		const formData = new FormData();

		const artistJSON = JSON.stringify({
			fullName: fullName.value,
			sphere: branch.value,
			birthYear: birthday.value,
			deathYear: deathday.value,
		});
		const artistBlob = new Blob([artistJSON], {
			type: 'application/json'
		});

		formData.append( 'artist', artistBlob);

		formData.append( 
			'files', 
			photo.files[0], 
			photo.files[0].name 
		);
		formData.append( 
			'files', 
			bio.files[0], 
			bio.files[0].name 
		);

		const artistResponse = await createArtist(formData);

		if (artistResponse && artistResponse.status == 200) {
			setHasCreated(true);
		} else {
			setIsError(true);
		}
	};

  return (
    <div className='app'>
      <div className='form'>
        <div className='title'>Create Artist</div>
		{
			hasCreated ? (
				<p>The artist has successfully been created.</p>
			) : (
				<div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label>Full Name </label>
							<input className='form-control' type='text' name='fullName' required />
						</div>
						<div className='input-container'>
							<label>Branch </label>
							<select className='form-select' aria-label='Select a branch' name='branch'>
-								<option value='painting'>Painting</option>
								<option value='sculpture'>Sculpture</option>
								<option value='architecture'>Architecture</option>
							</select>
						</div>
						<div className='input-container'>
							<label>Birthday </label>
							<input type='date' name='birthday' required />
						</div>
						<div className='input-container'>
							<label>Deathday </label>
							<input type='date' name='deathday' required />
						</div>
						<div className='input-container'>
							<label>Photo </label>
							<input className='form-control' type='file' name='photo' required />
						</div>
						<div className='input-container'>
							<label>Biography </label>
							<input className='form-control' type='file' name='bio' required />
						</div>
						{isError && <div className='error'>An error occurred</div>}
						<div className='button-container'>
							<input type='submit' />
						</div>
					</form>
				</div>
			)
		}
      </div>
    </div>
  );
}

export default ArtistCreator;