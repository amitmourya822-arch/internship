import { Navigate } from "react-router-dom";
import { isTokenValid } from "../services/api";

function ProtectedRoute({ children }) {

    const token =
        localStorage.getItem("token");

    if (!isTokenValid(token)) {

        localStorage.removeItem("token");
        localStorage.removeItem("role");

        return <Navigate to="/login" />;
    }

    return children;
}

export default ProtectedRoute;