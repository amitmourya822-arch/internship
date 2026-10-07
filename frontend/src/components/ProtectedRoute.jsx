import { Navigate } from "react-router-dom";
import { isTokenValid, getRole } from "../services/api";

function ProtectedRoute({ children, role }) {

    const token =
        localStorage.getItem("token");

    if (!isTokenValid(token)) {

        localStorage.removeItem("token");
        localStorage.removeItem("role");

        return <Navigate to="/login" />;
    }

    if (role && getRole() !== role) {

        return <Navigate to="/" />;
    }

    return children;
}

export default ProtectedRoute;