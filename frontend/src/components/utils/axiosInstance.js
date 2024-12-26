
import axios from 'axios';

// Create an axios instance with the base URL set to the backend API
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',  // Adjust to your backend's base URL
    timeout: 5000,  // Optional timeout
    headers: {
        'Content-Type': 'application/json',  // Ensure Content-Type is set to JSON
    },
});

export default axiosInstance;



// import axios from 'axios';
//
// // Create an Axios instance with base URL
// const axiosInstance = axios.create({
//     baseURL: 'http://localhost:8080/api', // Your API base URL
// });
//
// // Add a request interceptor to include the Bearer token if available
// axiosInstance.interceptors.request.use(
//     (config) => {
//         const token = localStorage.getItem('access_token'); // Retrieve token from localStorage
//
//         if (token) {
//             config.headers['Authorization'] = `Bearer ${token}`; // Attach token to request header
//         }
//
//         return config;
//     },
//     (error) => {
//         return Promise.reject(error);
//     }
// );
//
// export default axiosInstance;
