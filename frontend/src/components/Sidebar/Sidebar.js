// Sidebar.js
import React from 'react';
import { useNavigate } from 'react-router-dom';  // Import useNavigate
import '../../App.css'; // Add styles for sidebar here

const Sidebar = () => {
    const navigate = useNavigate();  // Initialize navigate hook

    const handleUpdate = () => {
        console.log('Update clicked');
        navigate('/update');  // Programmatically navigate to the update page
    };

    const handleDelete = () => {
        console.log('Delete clicked');
        navigate('/Delete')
        // Add your delete logic here
    };

    return (
        <div className="sidebar">
            <h3>Actions</h3>
            <button className="sidebar-button" onClick={handleUpdate}>Update</button>
            <button className="sidebar-button" onClick={handleDelete}>Delete</button>
        </div>
    );
};

export default Sidebar;
