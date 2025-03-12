import axios from "axios";
import { getSession } from "./session";

// Create an Axios instance with a base URL
const axiosClient = axios.create({
  baseURL: "http://localhost:8080/api/v1", // Removed the /api/v1 prefix
  headers: {
    "Content-Type": "application/json",
  },
});

// Add a request interceptor to add the auth token to requests
axiosClient.interceptors.request.use(
  async (config) => {
    // Get the session to retrieve the token
    const session = await getSession();

    // If there's a session with a token, add it to the request headers
    if (session?.token) {
      config.headers.Authorization = `Bearer ${session.token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle token expiration
axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If the error is 401 (Unauthorized) and we haven't already tried to refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // Redirect to login page
      if (typeof window !== "undefined") {
        window.location.href = "/";
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
