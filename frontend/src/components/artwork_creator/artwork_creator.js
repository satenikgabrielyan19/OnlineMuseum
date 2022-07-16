import React, { useState, useEffect } from 'react';

import { createArtwork } from '../../services/artwork';
import { getArtistsByBranch, getStylesByBranch } from '../../services/branch';

const ArtworkCreator = () => {
  const [isError, setIsError] = useState(false);
  const [hasCreated, setHasCreated] = useState(false);

  const [styles, setStyles] = useState({ data: [] });
  const [artists, setArtists] = useState({ data: [] });

  const [branch, setBranch] = useState('painting');

  const handleSubmit = async (event) => {
		event.preventDefault();

		const {
			title,
			style,
			artist,
			photo,
			description,
		} = document.forms[0];

		const formData = new FormData();

		const artworkJSON = JSON.stringify({
			fullName: title.value,
			style: style.value,
			artistId: artist.value,
		});
		const artworkBlob = new Blob([artworkJSON], {
			type: 'application/json'
		});

		formData.append('artwork', artworkBlob);

		formData.append( 
			'files', 
			photo.files[0], 
			photo.files[0].name 
		);
		formData.append( 
			'files', 
			description.files[0], 
			description.files[0].name 
		);

		const artworkResponse = await createArtwork(formData);

		if (artworkResponse && artworkResponse.status == 200) {
			setHasCreated(true);
		} else {
			setIsError(true);
		}
	};

  const handleBranchChange = async (event) => {
		setBranch(event.target.value);
  };

  useEffect(() => {
		const fetchStyles = async () => {
			const stylesResponse = await getStylesByBranch(branch);

			setStyles(stylesResponse);
		};

		const fetchArtists = async () => {
			const artistsResponse = await getArtistsByBranch(branch);

			setArtists(artistsResponse);
		};

		fetchStyles();
		fetchArtists();
  }, [branch]);

  return (
    <div className='app'>
      <div className='form'>
        <div className='title'>Create Artwork</div>
		{
			hasCreated ? (
				<p>The artwork has successfully been created.</p>
			) : (
				<div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label>Title </label>
							<input className='form-control' type='text' name='title' required />
						</div>
						<div className='input-container'>
							<label>Branch </label>
							<select className='form-select' aria-label='Select a branch' name='branch' onChange={handleBranchChange}>
-								<option value='painting'>Painting</option>
								<option value='sculpture'>Sculpture</option>
								<option value='architecture'>Architecture</option>
							</select>
						</div>
						<div className='input-container'>
							<label>Style </label>
							<select className='form-select' aria-label='Select a style' name='style'>
								{
									styles.data.map(({ id, name }) => (
										<option key={id} value={name}>{name}</option>
									))
								}
							</select>
						</div>
						<div className='input-container'>
							<label>Artist </label>
							<select className='form-select' aria-label='Select an artist' name='artist'>
								{
									artists.data.map(({ id, fullName }) => (
										<option key={id} value={id}>{fullName}</option>
									))
								}
							</select>
						</div>
						<div className='input-container'>
							<label>Photo </label>
							<input className='form-control' type='file' name='photo' required />
						</div>
						<div className='input-container'>
							<label>Description </label>
							<input className='form-control' type='file' name='description' required />
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

export default ArtworkCreator;