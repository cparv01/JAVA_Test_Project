import React, { useState, useEffect } from 'react';
import './../../App.css';
import axios from "../utils/axiosInstance";
import { ToastContainer, toast } from 'react-toastify'; // Import Toastify
import 'react-toastify/dist/ReactToastify.css';  // Import default styles

import Select from 'react-select'; // Import react-select
import { useNavigate } from 'react-router-dom';

const Register = () => {



    const navigate = useNavigate();  // Initialize navigate hook

    const [isLogin, setIsLogin] = useState(true); // Toggle between login and register
    const [username, setUsername] = useState(''); // Store email or mobile for login
    const [otp, setOtp] = useState(''); // Store OTP input
    const [isOtpSent, setIsOtpSent] = useState(false); // OTP sent status
    const [timer, setTimer] = useState(30); // Countdown for OTP expiry
    const [isVerified, setIsVerified] = useState(false); // OTP verification status
    const [accessToken, setAccessToken] = useState(''); // Store access token after successful login

    const [registerUsername, setRegisterUsername] = useState('');
    const [registerPassword, setRegisterPassword] = useState('');
    const [registerEmail, setRegisterEmail] = useState('');
    const [registerMobile, setRegisterMobile] = useState('');
    const [registerDob, setRegisterDob] = useState('');
    const [registerPincode, setRegisterPincode] = useState('');

    const [countryId, setCountryId] = useState('');
    const [stateId, setStateId] = useState('');
    const [cityId, setCityId] = useState('');

    const [countries, setCountries] = useState([]);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);

    const [filteredStates, setFilteredStates] = useState([]);
    const [filteredCities, setFilteredCities] = useState([]);

    // Handle OTP Request (Step 1: Send OTP)
    const handleRequestOtp = () => {
        if (!username) {
            toast.error('Please enter your email or mobile number.');
            return;
        }

        axios
            .post('/users/login', { username })  // Login API to get access token
            .then((response) => {
                const token = response.data.data.access_token;
                localStorage.setItem('access_token', token);  // Store token in localStorage
                setAccessToken(token);  // Also update the state with token
                setIsOtpSent(true);  // OTP sent successfully
                toast.success('OTP has been sent to your email/mobile.');
                startTimer();  // Start OTP timer
                console.log(localStorage.setItem('access_token', token))
            })
            .catch((error) => {
                toast.error('Error sending OTP: ' + (error.response?.data?.message || error.message));
            });
    };

    // Start the countdown for OTP expiry
    const startTimer = () => {
        const interval = setInterval(() => {
            setTimer((prevTime) => {
                if (prevTime <= 1) {
                    clearInterval(interval); // Clear the interval when timer reaches 0
                    return 0; // Stop the timer at 0
                }
                return prevTime - 1; // Decrease the timer value by 1 each second
            });
        }, 1000);

        // Cleanup the interval when the component is unmounted
        return () => clearInterval(interval);
    };

    // Handle OTP Verification (Step 2: Verify OTP)
    const handleVerifyOtp = () => {
        if (!otp) {
            toast.error('Please enter the OTP.');
            return;
        }

        const token = localStorage.getItem('access_token');

        if (!token) {
            toast.error('No access token found. Please log in again.');
            return;
        }

        const emailOrMobile = username;

        // Pass access token in Authorization header for OTP verification
        axios
            .post(
                '/users/verify-otp', // Replace with your actual endpoint
                { otp, emailOrMobile }, // Send OTP and username
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`, // Attach the token in Authorization header
                    },
                }
            )
            .then((response) => {
                setIsVerified(true);
                toast.success('OTP verified successfully!');
                setTimeout(() => {
                    navigate('/'); //3 seconds
                    window.location.reload();
                }, 3000);
            })
            .catch((error) => {
                toast.error('Invalid OTP or OTP expired: ' + (error.response?.data?.message || error.message));
            });
    };

    useEffect(() => {
        axios.get('http://localhost:8080/api/users/getPlace')
            .then((response) => {
                const [stateData, cityData, countryData] = response.data.data;
                setStates(stateData);
                setCities(cityData);
                setCountries(countryData);
            })
            .catch((error) => {
                console.error("There was an error fetching the place data:", error);
            });
    }, []);

    // Function to handle registration form submission
    const handleRegisterSubmit = (e) => {
        e.preventDefault();
        const registerDto = {
            username: registerUsername,
            password: registerPassword,
            email: registerEmail,
            mobileNumber: registerMobile,
            dob: registerDob,
            pincode: registerPincode,
            countryId: countryId,  // Send countryId
            stateId: stateId,      // Send stateId
            cityId: cityId         // Send cityId
        };
        axios.post('http://localhost:8080/api/users/register', registerDto)
            .then((response) => {
                toast.success('Registration successful');

                setRegisterUsername('');
                setRegisterPassword('');
                setRegisterEmail('');
                setRegisterMobile('');
                setRegisterDob('');
                setRegisterPincode('');
                setCountryId('');
                setStateId('');
                setCityId('');
                setFilteredStates([]);
                setFilteredCities([]);

                // Optionally, reset other flags (if needed)
                setIsLogin(true);

            })
            .catch((error) => {
                toast.error('Registration failed: ' + error.response?.data?.message || error.message);
            });
    };

    // Resend OTP after timer expires
    const handleResendOtp = () => {
        setTimer(30); // Reset the timer
        handleRequestOtp(); // Send OTP request again
    };

    // Handle country change (load states for the selected country)
    const handleCountryChange = (e) => {
        const selectedCountryId = e.target.value;
        setCountryId(selectedCountryId);

        // Reset state and city when country changes
        setStateId('');
        setCityId('');

        // Filter states based on selected countryId
        const filteredStates = states.filter(state => state.countryId === Number(selectedCountryId));
        setFilteredStates(filteredStates);

        // Optionally reset filtered cities if the country changes
        setFilteredCities([]);
    };

    // Handle state change (load cities for the selected state)
    const handleStateChange = (e) => {
        const selectedStateId = e.target.value;
        setStateId(selectedStateId);

        // Reset city when state changes
        setCityId('');

        // Filter cities based on selected stateId
        const filteredCities = cities.filter(city => city.stateId === Number(selectedStateId));
        setFilteredCities(filteredCities);
    };

    // Handle city change (set the cityId)
    const handleCityChange = (e) => {
        setCityId(e.target.value);
    };

    return (
        <div className="form-container">
            <div className="form-tabs">
                <button
                    className={`tab-button ${isLogin ? 'active' : ''}`}
                    onClick={() => setIsLogin(true)}
                >
                    Login
                </button>
                <button
                    className={`tab-button ${!isLogin ? 'active' : ''}`}
                    onClick={() => setIsLogin(false)}
                >
                    Register
                </button>
            </div>

            {/* Login Form */}
            {isLogin && (
                <div className="login-container">
                    <h2>Login with OTP</h2>
                    {!isOtpSent && !isVerified && (
                        <div className="input-container">
                            <label>Email or Mobile:</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Enter your email or mobile"
                            />
                            <button onClick={handleRequestOtp}>Send OTP</button>
                        </div>
                    )}

                    {/* Step 2: Enter OTP */}
                    {isOtpSent && !isVerified && (
                        <div className="otp-container">
                            <label>OTP:</label>
                            <input
                                type="text"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                placeholder="Enter OTP"
                            />
                            <div className="timer">Time left: {timer}
                                {timer === 0 && !isVerified && (
                                    <button
                                        className="resend-otp-button"
                                        style={{ marginLeft: "30%" }}
                                        onClick={handleResendOtp}
                                    >
                                        Resend OTP
                                    </button>
                                )}
                            </div>
                            <button onClick={handleVerifyOtp}>Verify OTP</button>
                        </div>
                    )}

                    {/* Step 3: Success Message */}
                    {isVerified && (
                        <div className="success-message">
                            <p>Login successful!</p>
                        </div>
                    )}
                </div>
            )}

            {/* Register Form */}
            {!isLogin && (
                <div className="form-box">
                    <h2>Register</h2>
                    <form onSubmit={handleRegisterSubmit}>
                        <div className="input-group">
                            <label>Username:</label>
                            <input
                                type="text"
                                value={registerUsername}
                                onChange={(e) => setRegisterUsername(e.target.value)}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Password:</label>
                            <input
                                type="password"
                                value={registerPassword}
                                onChange={(e) => setRegisterPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Email:</label>
                            <input
                                type="email"
                                value={registerEmail}
                                onChange={(e) => setRegisterEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Mobile Number:</label>
                            <input
                                type="text"
                                value={registerMobile}
                                onChange={(e) => setRegisterMobile(e.target.value)}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Date of Birth:</label>
                            <input
                                type="date"
                                value={registerDob}
                                onChange={(e) => setRegisterDob(e.target.value)}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Pincode:</label>
                            <input
                                type="text"
                                value={registerPincode}
                                onChange={(e) => setRegisterPincode(e.target.value)}
                                required
                            />
                        </div>

                        {/* Country, State, and City Dropdowns in a Row */}
                        <div className="input-group-row">
                            {/* Country Dropdown */}
                            <div className="input-group">
                                <label>Country:</label>
                                <select
                                    value={countryId}
                                    onChange={handleCountryChange}>
                                    <option value="">Select Country</option>
                                    {countries.map((country) => (
                                        <option key={country.id} value={country.id}>{country.name}</option>
                                    ))}
                                </select>

                                {/*<Select*/}
                                {/*    value={countryId}*/}
                                {/*    onChange={handleCountryChange}*/}
                                {/*    options={countries.map(country => ({*/}
                                {/*        value: country.id,*/}
                                {/*        label: country.name*/}
                                {/*    }))}*/}
                                {/*/>*/}
                            </div>

                            {/* State Dropdown */}
                            <div className="input-group">
                                <label>State:</label>
                                <select value={stateId} onChange={handleStateChange}
                                        disabled={filteredStates.length === 0}>
                                    <option value="">Select State</option>
                                    {filteredStates.map((state) => (
                                        <option key={state.id} value={state.id}>{state.name}</option>
                                    ))}
                                </select>

                                {/*<Select*/}
                                {/*    value={selectedState}*/}
                                {/*    onChange={handleStateChange}*/}
                                {/*    options={filteredStates.map(state => ({*/}
                                {/*        value: state.id,*/}
                                {/*        label: state.name*/}
                                {/*    }))}*/}
                                {/*    isDisabled={filteredStates.length === 0}*/}
                                {/*/>*/}
                            </div>

                            {/* City Dropdown */}
                            <div className="input-group">
                                <label>City:</label>
                                <select value={cityId} onChange={handleCityChange} disabled={filteredCities.length === 0}>
                                    <option value="">Select City</option>
                                    {filteredCities.map((city) => (
                                        <option key={city.id} value={city.id}>{city.name}</option>
                                    ))}
                                </select>
                            </div>
                        </div>


                        <button type="submit" className="submit-button">Register</button>
                    </form>
                    {/*/!*Display message*!/*/}
                    {/*{message && <div className="message">{message}</div>}*/}
                </div>
            )}

            <ToastContainer position="top-right" autoClose={5000} hideProgressBar={false} />
        </div>
    );
};

export default Register;
