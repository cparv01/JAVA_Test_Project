import React, { useState, useEffect } from 'react';
import axios from "../utils/axiosInstance"; // Import the configured axios instance
import { useParams, useNavigate } from 'react-router-dom'; // Import useParams and useNavigate
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import default styles
import Select from 'react-select'; // Import react-select

const UpdateUser = () => {
    const { userId } = useParams(); // Get the userId from the URL
    const navigate = useNavigate();  // Initialize navigate hook
    const [userData, setUserData] = useState({
        username: '',
        password: '',
        email: '',
        mobileNumber: '',
        dob: '',
        cityId: '',
        stateId: '',
        countryId: '',
        pincode: ''
    });

    const [loading, setLoading] = useState(false);

    const [countries, setCountries] = useState([]);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);

    const [selectedCountry, setSelectedCountry] = useState(null);
    const [selectedState, setSelectedState] = useState(null);
    const [selectedCity, setSelectedCity] = useState(null);

    const [filteredStates, setFilteredStates] = useState([]);
    const [filteredCities, setFilteredCities] = useState([]);

    // Fetch the user data based on the userId
    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem('access_token');

            try {
                const response = await axios.get(`/users/getUser/${userId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.status === 200) {
                    const user = response.data.data;

                    // Set the user data, extracting country, state, and city
                    setUserData({
                        ...user,
                        countryId: user.country?.id || '',
                        stateId: user.state?.id || '',
                        cityId: user.city?.id || '',
                    });

                    // Set the selected country, state, and city based on the response
                    setSelectedCountry({
                        value: user.country?.id || '',
                        label: user.country?.name || '',
                    });
                    setSelectedState({
                        value: user.state?.id || '',
                        label: user.state?.name || '',
                    });
                    setSelectedCity({
                        value: user.city?.id || '',
                        label: user.city?.name || '',
                    });

                    // Filter states and cities based on the user's country and state
                    setFilteredStates(states.filter(state => state.countryId === user.country?.id));
                    setFilteredCities(cities.filter(city => city.stateId === user.state?.id));
                } else {
                    toast.error(`Failed to fetch user data. Status code: ${response.status}`);
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
                toast.error('You need to login first.');
            }
        };

        fetchUserData();
    }, [userId, states, cities]); // Run only once and when `states` and `cities` are available

    // Fetch country, state, and city data
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
    }, []); // Fetch places data once when the component mounts

    // Handle country change
    const handleCountryChange = (selectedOption) => {
        const selectedCountryId = selectedOption?.value || '';
        setSelectedCountry(selectedOption);
        setUserData(prevUserData => ({
            ...prevUserData,
            countryId: selectedCountryId,
        }));

        // Filter states based on selected country
        setFilteredStates(states.filter(state => state.countryId === Number(selectedCountryId)));

        // Reset city when country changes
        setFilteredCities([]);
        setSelectedState(null);
        setSelectedCity(null);
    };

    // Handle state change (load cities for the selected state)
    const handleStateChange = (selectedOption) => {
        const selectedStateId = selectedOption?.value || '';
        setSelectedState(selectedOption);
        setUserData(prevUserData => ({
            ...prevUserData,
            stateId: selectedStateId,
        }));

        // Filter cities based on selected state
        setFilteredCities(cities.filter(city => city.stateId === Number(selectedStateId)));
        setSelectedCity(null); // Reset city when state changes
    };

    // Handle city change
    const handleCityChange = (selectedOption) => {
        setSelectedCity(selectedOption);
        setUserData(prevUserData => ({
            ...prevUserData,
            cityId: selectedOption?.value || '',
        }));
    };

    // Handle form submission
    const handleUpdateSubmit = (e) => {
        e.preventDefault();
        setLoading(true);

        // Dynamically create updatedData object
        const updatedData = { ...userData };

        const token = localStorage.getItem('access_token');

        if (!token) {
            setLoading(false);
            toast.error('You need to login first. Please provide a valid access token.');
            return;
        }

        // Make the API request to update user
        axios
            .put(`/users/updateUser/${userId}`, updatedData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((response) => {
                toast.success('User details updated successfully.');
                setLoading(false);
                setTimeout(() => {
                    navigate('/'); //3 seconds
                }, 3000);
                // navigate('/'); // Redirect to home after successful update
            })
            .catch((error) => {
                setLoading(false);
                toast.error((error.response?.data?.message));
            });
    };

    return (
        <div className="update-container">
            <h2>Update User Details</h2>

            <form onSubmit={handleUpdateSubmit}>
                {/* Other form fields */}
                <div className="input-group">
                    <label>Username:</label>
                    <input
                        type="text"
                        value={userData.username}
                        onChange={(e) => setUserData({...userData, username: e.target.value})}
                    />
                </div>

                <div className="input-group">
                    <label>Password:</label>
                    <input
                        type="password"
                        value={userData.password}
                        onChange={(e) => setUserData({...userData, password: e.target.value})}
                    />
                </div>

                <div className="input-group">
                    <label>Email:</label>
                    <input
                        type="email"
                        value={userData.email}
                        onChange={(e) => setUserData({...userData, email: e.target.value})}
                    />
                </div>

                <div className="input-group">
                    <label>Mobile Number:</label>
                    <input
                        type="text"
                        value={userData.mobileNumber}
                        onChange={(e) => setUserData({...userData, mobileNumber: e.target.value})}
                    />
                </div>

                <div className="input-group">
                    <label>Date of Birth:</label>
                    <input
                        type="date"
                        value={userData.dob}
                        onChange={(e) => setUserData({...userData, dob: e.target.value})}
                    />
                </div>

                <div className="input-group-row">
                    <div className="input-group">
                        <label>Country:</label>
                        <Select
                            value={selectedCountry}
                            onChange={handleCountryChange}
                            options={countries.map(country => ({
                                value: country.id,
                                label: country.name
                            }))}
                        />
                    </div>

                    <div className="input-group">
                        <label>State:</label>
                        <Select
                            value={selectedState}
                            onChange={handleStateChange}
                            options={filteredStates.map(state => ({
                                value: state.id,
                                label: state.name
                            }))}
                            isDisabled={filteredStates.length === 0}
                        />
                    </div>

                    <div className="input-group">
                        <label>City:</label>
                        <Select
                            value={selectedCity}
                            onChange={handleCityChange}
                            options={filteredCities.map(city => ({
                                value: city.id,
                                label: city.name
                            }))}
                            isDisabled={filteredCities.length === 0}
                        />
                    </div>
                </div>

                <div className="input-group">
                    <label>Pincode:</label>
                    <input
                        type="text"
                        value={userData.pincode}
                        onChange={(e) => setUserData({...userData, pincode: e.target.value})}
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? 'Updating...' : 'Update'}
                </button>
            </form>

            <ToastContainer />
        </div>
    );
};

export default UpdateUser;
