import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { getCurrentUserToken, forgotPassword } from '../../services/auth';

const PasswordRecovery = () => {
	const navigate = useNavigate();

  	const [isError, setIsError] = useState(false);
  	const [passwordResetInstructions, setPasswordResetInstructions] = useState(null);

  	const handleSubmit = async (event) => {
    	event.preventDefault();

    	const { email } = document.forms[0];

    	const forgotPasswordResponse = await forgotPassword(email.value);

		if (forgotPasswordResponse && forgotPasswordResponse.status == 200) {
			const { text } = forgotPasswordResponse;

			setPasswordResetInstructions(text);
		} else {
			setIsError(true);
		}
 	};

	useEffect(() => {
		const user = getCurrentUserToken();

		if (user) {
			navigate('/home');
		}
	}, []);

  return (
    <div className='app'>
      <div className='form'>
        <div className='title'>Password recovery</div>
			{
				passwordResetInstructions ? 
				(
					<p>{passwordResetInstructions}</p>
				) : (
					<>
						<div>
							<form onSubmit={handleSubmit}>
								<div className='input-container'>
									<label>Email </label>
									<input type='text' name='email' required />
								</div>
								{isError && <div className='error'>Invalid email or password</div>}
								<div className='button-container'>
									<input type='submit' />
								</div>
							</form>
						</div>
					</>		
				)
			}
      </div>
    </div>
  );
}

export default PasswordRecovery;