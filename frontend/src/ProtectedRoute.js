import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ element: Element, ...rest }) => {
    const token = localStorage.getItem('access_token');

    // If not logged in, redirect to the login page
    if (!token) {
        return <Navigate to="/register" replace />;
    }

    // If logged in, render the passed component (e.g. Update, Delete)
    return <Element {...rest} />;
};

export default ProtectedRoute;
