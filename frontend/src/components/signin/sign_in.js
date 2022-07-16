import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { login, getCurrentUserToken } from '../../services/auth';

const SignIn = ({ setCurrentUserToken }) => {
	const navigate = useNavigate();

  const [isError, setIsError] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const { email, password } = document.forms[0];

    const authResponse = await login(email.value, password.value);

		if (authResponse) {
			setCurrentUserToken(authResponse);

			navigate('/home');
		} else {
			setIsError(true);
		}
 	};

	useEffect(() => {
		const user = getCurrentUserToken();

		console.log(user);

		if (user) {
			navigate('/home');
		}
	}, []);

  return (
    <div className='app'>
      <div className='form'>
        <div className='title'>Sign In</div>
        <div>
					<form onSubmit={handleSubmit}>
						<div className='input-container'>
							<label for="email" className="form-label">Email </label>
  							<input type="email" className="form-control" id="email" name='email' placeholder="name@example.com" required />
						</div>
						<div className='input-container'>
							<label for="password" className="form-label">Password </label>
							<input type='password' className="form-control" id='password' name='password' required />
						</div>
						<div className='input-container'>
							<Link to={'/forgot_password'}>
								<a>Forgot Password?</a>
							</Link>
						</div>
						{isError && <div className='error'>Invalid email or password</div>}
						<div className='button-container'>
							<input type='submit' />
						</div>
					</form>
				</div>
      </div>
    </div>
  );
}

export default SignIn;