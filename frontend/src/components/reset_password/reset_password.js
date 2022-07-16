import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { resetPassword, getCurrentUserToken } from '../../services/auth';

const ResetPassword = () => {
	const navigate = useNavigate();
	const [searchParams, _] = useSearchParams();

  const [isError, setIsError] = useState(false);
  const [successMessage, setSuccessMessage] = useState(null);

  const handleSubmit = async (event) => {
		event.preventDefault();

		const { password } = document.forms[0];

		const token = searchParams.get('token');

		const passwordResetResponse = await resetPassword(password.value, token);

		if (passwordResetResponse && passwordResetResponse.status == 200) {
			const { text } = passwordResetResponse;

			setSuccessMessage(text);
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
        <div className='title'>Reset Password</div>
		{
			successMessage ? (
				<p>{successMessage}</p>
			) : (
				<div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label>New Password </label>
							<input type='password' name='password' required />
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

export default ResetPassword;