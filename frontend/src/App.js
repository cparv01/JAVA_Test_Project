import { Route, Routes, Link, useNavigate  } from 'react-router-dom';
import './App.css';
import Register from './components/Register/Register';
import Update from './components/User/Update';
import Delete from './components/User/Delete';
import Home from './components/User/Home'; // Import Home Component
import ProtectedRoute from './ProtectedRoute';
import { useEffect, useState } from 'react';

import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import default styles


function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false); // State for login status
    const navigate = useNavigate();


    // useEffect to continuously check login status
    useEffect(() => {
        const token = localStorage.getItem('access_token');

         if (token) {
             setIsLoggedIn(!!token);
         } else {
             setIsLoggedIn(false);
             navigate('/register'); // Redirect to login if not logged in
         }


//        setIsLoggedIn(!!token); // Update the login status based on presence of the token
        
    }, [navigate]); // This will run once when the component is mounted

    // Handle Logout
    const handleLogout = () => {
        localStorage.removeItem('access_token');  // Remove token on logout
        setIsLoggedIn(false);  // Update the login state

        toast.success('User logged out successfully.');

        // After 3 seconds, navigate to the Home page
        setTimeout(() => {
            navigate('/register'); // Redirect to home page after 3 seconds
        }, 2000);

    };

    return (
        <div className="app-container">
            {/* Taskbar with Home Button on the Right */}
            <div
                style={{
                    backgroundColor: '#fff',
                    color: '#fff',
                    padding: '10px 20px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    position: 'fixed',
                    top: '0',
                    width: '100%',
                    zIndex: '1000',
                }}
            >

                <div style={{ marginLeft: '86%', marginBottom: '-100px' }}>
                    <Link
                        to="/"
                        style={{
                            marginRight: '20px',
                            color: '#fff',
                            textDecoration: 'none',
                            fontSize: '18px',
                            fontWeight: 'bold',
                            padding: '8px 16px',
                            backgroundColor: '#007bff',
                            borderRadius: '5px',
                            transition: 'background-color 0.3s',
                        }}
                    >
                        Home
                    </Link>

                    {isLoggedIn ? (
                        <Link
                            onClick={handleLogout}
                            style={{
                                textDecoration: 'none',
                                transition: 'background-color 0.3s',
                                color: '#fff',
                                fontSize: '18px',
                                fontWeight: 'bold',
                                padding: '8px 16px',
                                backgroundColor: '#dc3545',
                                borderRadius: '5px',
                            }}
                        >
                            Log Out
                        </Link>
                    ) : (
                        <Link
                            to="/register"
                            style={{
                                color: '#fff',
                                textDecoration: 'none',
                                fontSize: '18px',
                                fontWeight: 'bold',
                                padding: '8px 16px',
                                backgroundColor: '#28a745',
                                borderRadius: '5px',
                                transition: 'background-color 0.3s',
                            }}
                        >
                            Login
                        </Link>
                    )}
                </div>
            </div>

            <div className="main-content">
                <Routes>
                    {/* Public Routes */}
                    <Route path="/" element={<Home />} />
                    <Route path="/register" element={<Register />} />

                    {/* Protected Routes */}
                    <Route
                        path="/update/:userId"
                        element={<ProtectedRoute element={Update} />}
                    />
                    <Route
                        path="/delete"
                        element={<ProtectedRoute element={Delete} />}
                    />

                    <Route
                        path="/"
                        element={
                            isLoggedIn ? <Home /> : <navigate to="/register" />
                        }
                    />
                </Routes>
            </div>
        </div>
    );
}

export default App;
