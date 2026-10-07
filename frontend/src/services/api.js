import axios from "axios";

const API_URL = (
    import.meta.env.VITE_API_URL || "http://localhost:8081"
).replace(/\/+$/, "");

const api = axios.create({
    baseURL: `${API_URL}/api`
});

api.interceptors.request.use(
    (config) => {

        const token =
            localStorage.getItem("token");

        if (token) {
            config.headers.Authorization =
                `Bearer ${token}`;
        }

        return config;
    }
);

api.interceptors.response.use(
    (response) => response,
    (error) => {

        if (
            error.response &&
            error.response.status === 401
        ) {

            localStorage.removeItem("token");
            localStorage.removeItem("role");

            if (
                window.location.pathname !== "/login"
            ) {
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
);

export function isTokenValid(token) {

    if (!token) {
        return false;
    }

    try {

        const payload = JSON.parse(
            atob(token.split(".")[1])
        );

        if (!payload || !payload.exp) {
            return false;
        }

        return payload.exp * 1000 > Date.now();

    } catch {

        return false;
    }
}

export default api;