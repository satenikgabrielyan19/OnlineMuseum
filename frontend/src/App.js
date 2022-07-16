import React, { useState, useEffect } from 'react';
import { Routes, Route, Link } from 'react-router-dom';

import jwt from 'jwt-decode'

import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import SignIn from './components/signin/sign_in';
import SignUp from './components/signup/sign_up';
import PasswordRecovery from './components/password_recovery/password_recovery';
import ResetPassword from './components/reset_password/reset_password';
import Home from './components/home/home';
import BranchPage from './components/branch_page/branch_page';
import StyleCreator from './components/style_creator/style_creator';
import ArtistCreator from './components/artists_creator/artist_creator';
import ArtworkCreator from './components/artwork_creator/artwork_creator';
import StylePage from './components/style_page/style_page';
import ArtistPage from './components/artist_page/artist_page';

import { getCurrentUserToken, logout } from './services/auth';
import { isAdmin } from './services/user';

function App() {
  const [currentUser, setCurrentUser] = useState(undefined);
  const [currentUserToken, setCurrentUserToken] = useState(undefined);

  useEffect(() => {
    const userToken = getCurrentUserToken();

    setCurrentUserToken(userToken)

    if (currentUserToken) {
      const { jwtToken } = currentUserToken;

      setCurrentUser(jwt(jwtToken));
    }
  }, []);

  useEffect(() => {
    if (currentUserToken) {
      const { jwtToken } = currentUserToken;

      setCurrentUser(jwt(jwtToken));
    }
  }, [currentUserToken]);

  const handleLogout = () => logout();

  return (
    <div>
      <nav className='navbar navbar-expand-lg navbar-dark bg-dark'>
        <div className='container-fluid'>
            <Link to={'/'} className='navbar-brand'>
              Online Museum
            </Link>
            <button className='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarContent' aria-controls='navbarSupportedContent' aria-expanded='false' aria-label='Toggle navigation'>
                <span className='navbar-toggler-icon'></span>
            </button>
            <div className='collapse navbar-collapse' id='navbarContent'>
                <ul className='navbar-nav me-auto mb-2 mb-lg-0'>
                    <li className='nav-item'>
                      <Link to={'/home'} className='nav-link'>
                        Home
                      </Link>
                    </li>
                    {
                      !currentUser && (
                        <>
                          <li className='nav-item'>
                            <Link to={'/login'} className='nav-link'>
                              Login
                            </Link>
                          </li>
                          <li className='nav-item'>
                            <Link to={'/register'} className='nav-link'>
                              Sign Up
                            </Link>
                          </li>
                        </>
                      )
                    }
                    {
                      currentUser && isAdmin(currentUser) && (
                        <li className='nav-item dropdown'>
                          <a className='nav-link dropdown-toggle' href='#' id='navbarDropdownMenuLink' role='button' data-bs-toggle='dropdown' aria-expanded='false'>
                            Creators
                          </a>
                          <ul className='dropdown-menu' aria-labelledby='navbarDropdownMenuLink'>
                            <li>
                              <Link to={'/styles/create'} className='dropdown-item'>
                                Create Style
                              </Link>
                            </li>
                            <li>
                              <Link to={'/artists/create'} className='dropdown-item'>
                                Create Artist
                              </Link>
                            </li>
                            <li>
                              <Link to={'/artworks/create'} className='dropdown-item'>
                                Create Artwork
                              </Link>
                            </li>
                          </ul>
                        </li>
                      )
                    }
                </ul>
                <ul className='navbar-nav'>
                    {
                      currentUser && (
                        <>
                          <li className='nav-item'>
                            <a href='/' className='nav-link' onClick={handleLogout}>
                              Logout
                            </a>
                          </li>
                        </>
                      )
                    }
                </ul>
            </div>
        </div>
    </nav>
      <div className='container mt-3'>
        <Routes>
          <Route path='/' element={<Home />} />
          <Route path='/home' element={<Home />} />
          <Route path='/login' element={<SignIn setCurrentUserToken={setCurrentUserToken} />} />
          <Route path='/register' element={<SignUp />} />
          <Route path='/forgot_password' element={<PasswordRecovery />} />
          <Route path='/reset_password' element={<ResetPassword />} />
          <Route path='/branch/:branch' element={<BranchPage />} />
          <Route path='/styles/create' element={<StyleCreator />} />
          <Route path='/styles/:styleId' element={<StylePage />} />
          <Route path='/artists/create' element={<ArtistCreator />} />
          <Route path='/artists/:artistId' element={<ArtistPage />} />
          <Route path='/artworks/create' element={<ArtworkCreator />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
