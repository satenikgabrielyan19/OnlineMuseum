import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { signup, getCurrentUserToken } from '../../services/auth';

const SignUp = () => {
	const navigate = useNavigate();

  const [isError, setIsError] = useState(false);
  const [hasSignedUp, setHasSignedUp] = useState(false);

  const handleSubmit = async (event) => {
		event.preventDefault();

		const {
			firstName,
			lastName,
			birthday,
			username,
			email,
			password,
		} = document.forms[0];

		const userResponse = await signup({
			firstName: firstName.value,
			lastName: lastName.value,
			birthday: birthday.value,
			username: username.value,
			email: email.value,
			password: password.value,
		});

		if (userResponse && userResponse.status == 200) {
			setHasSignedUp(true);
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
        <div className='title'>Sign Up</div>
		{
			hasSignedUp ? (
				<p>We've sent you an email to activate your account.</p>
			) : (
				<div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label>First name </label>
							<input type='text' name='firstName' required />
						</div>
						<div className='input-container'>
							<label>Last name </label>
							<input type='text' name='lastName' required />
						</div>
						<div className='input-container'>
							<label>Birthday </label>
							<input type='date' name='birthday' required />
						</div>
						<div className='input-container'>
							<label>Username </label>
							<input type='text' name='username' required />
						</div>
						<div className='input-container'>
							<label>Email </label>
							<input type='text' name='email' required />
						</div>
						<div className='input-container'>
							<label>Password </label>
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

export default SignUp;