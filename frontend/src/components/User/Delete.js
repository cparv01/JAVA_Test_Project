import React, { useState } from 'react';
import axios from "../utils/axiosInstance";
import { Dialog } from '@headlessui/react';
import '../../App.css';

const DeleteUser = ({ userId, onDeleteSuccess, onClose }) => {
    const [message, setMessage] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(true);  // Modal is open by default

    const handleDelete = async () => {
        const token = localStorage.getItem('access_token');

        console.log(token); 
        
        try {
            const response = await axios.delete(`/users/deleteUser/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 200) {
                setMessage('User deleted successfully.');
                onDeleteSuccess(userId); // Pass the userId to the callback to remove from the list
                onClose(); // Close the modal
            } else {
                setMessage(`Failed to delete user. Status code: ${response.status}`);
            }
        } catch (error) {
            setMessage(`Error deleting user: ${error.message}`);
        }
    };

    return (
        <Dialog open={isModalOpen} onClose={onClose}>
            <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity">
                <div className="flex justify-center items-center min-h-screen">
                    <Dialog.Panel className="bg-white rounded-lg p-6 w-96">
                        <Dialog.Title className="text-lg font-semibold">Confirm Deletion</Dialog.Title>
                        <Dialog.Description className="mt-2">
                            Are you sure you want to delete this user? This action cannot be undone.
                        </Dialog.Description>

                        <div className="mt-4 flex justify-end space-x-4">
                            <button
                                onClick={onClose}
                                className="text-gray-500 hover:text-gray-700 px-4 py-2"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
                                className="bg-red-600 text-white hover:bg-red-700 px-4 py-2 rounded"
                            >
                                Confirm Delete
                            </button>
                        </div>
                        {message && <div className="mt-4 text-center text-red-600">{message}</div>}
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    );
};

export default DeleteUser;
