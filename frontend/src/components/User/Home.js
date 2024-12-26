import React, { useState, useEffect } from 'react';
import axios from "../utils/axiosInstance";
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../App.css';
import Swal from 'sweetalert2';

const Home = () => {
    const [users, setUsers] = useState([]);
    const [userIdToDelete, setUserIdToDelete] = useState("");
    const navigate = useNavigate();

    // Fetch all users when the component mounts
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await axios.get('/users/getAll');
                if (response.status === 200) {
                    setUsers(response.data.data);
                } else {
                    toast.error(`Failed to fetch users. Status code: ${response.status}`);
                }
            } catch (error) {
                console.error('Error fetching users:', error);
                toast.error(`Error fetching users: ${error.message}`);
            }
        };
        fetchUsers();
    }, []);

    // Log userIdToDelete when it changes
    useEffect(() => {
        console.log("Selected user ID to delete:", userIdToDelete);
    }, [userIdToDelete]);

    const handleUpdate = (userId) => {
        navigate(`/update/${userId}`);
    };

    // Handle user deletion
    const handleDelete = async (userId) => {
        try {
            const token = localStorage.getItem('access_token');
            if (!token) {
                toast.error('You need to login first. Please provide a valid access token.');
                return;
            }

            const response = await axios.delete(`/users/deleteUser/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 200) {
                toast.success('User deleted successfully.');
                setUsers(users.filter(user => user.id !== userId));
            } else {
                toast.error(`Failed to delete user. Status code: ${response.status}`);
            }
        } catch (error) {
            toast.error('Error deleting user: ' + error.message);
            console.error('Error deleting user:', error);
        }
    };

    // Open SweetAlert2 confirmation dialog
    const openDeleteConfirmation = (userId) => {
        setUserIdToDelete(userId); // Set the ID for deletion
        Swal.fire({
            title: 'Are you sure?',
            text: 'Do you want to delete this user?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'Cancel',
        }).then((result) => {
            if (result.isConfirmed) {
                handleDelete(userId);  // Pass the ID directly to handleDelete
            }
        });
    };

    return (
        <div className="home-container" style={{ marginTop: "60px" }}>
            <h2>All Users</h2>
            {users.length > 0 ? (
                <table className="users-table">
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Mobile Number</th>
                            <th>Country</th>
                            <th>State</th>
                            <th>City</th>
                            <th>Pincode</th>
                            <th>Created At</th>
                            <th>Age</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <tr key={user.id}>
                                <td>{user.username}</td>
                                <td>{user.email}</td>
                                <td>{user.mobileNumber}</td>
                                <td>{user.countryName}</td>
                                <td>{user.stateName}</td>
                                <td>{user.cityName}</td>
                                <td>{user.pincode}</td>
                                <td>{new Date(user.createdAt).toLocaleString()}</td>
                                <td>{user.age}</td>
                                <td>
                                    <div className="action-buttons-container">
                                        <button
                                            onClick={() => handleUpdate(user.id)}
                                            className="action-button update-button"
                                        >
                                            Update
                                        </button>
                                        <button
                                            onClick={() => openDeleteConfirmation(user.id)}
                                            className="action-button delete-button"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <div>No users available</div>
            )}
            <ToastContainer />
        </div>
    );
};

export default Home;
