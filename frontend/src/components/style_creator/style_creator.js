import React, { useState, useEffect } from 'react';

import { createStyle } from '../../services/style';

const StyleCreator = () => {
  const [isError, setIsError] = useState(false);
  const [hasCreated, setHasCreated] = useState(false);

  const handleSubmit = async (event) => {
		event.preventDefault();

		const {
			name,
			branch,
			description,
		} = document.forms[0];

		const styleResponse = await createStyle({
			name: name.value,
			branch: branch.value,
			description: description.value,
		});

		if (styleResponse && styleResponse.status == 200) {
			setHasCreated(true);
		} else {
			setIsError(true);
		}
	};

  return (
    <div className='app'>
      <div className='form'>
        <div className='title'>Create Style</div>
		{
			hasCreated ? (
				<p>The style has successfully been created.</p>
			) : (
				<div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label>Name </label>
							<input className='form-control' type='text' name='name' required />
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
							<label for='description' className='form-label'>Description</label>
  							<textarea className='form-control' id='description' rows='3' name='description'></textarea>
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

export default StyleCreator;