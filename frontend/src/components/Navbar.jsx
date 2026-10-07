import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import api, { getRole, isAdmin } from "../services/api";

function Navbar() {

    const logout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("role");

        window.location.href = "/login";
    };

    const [notificationCount, setNotificationCount] = useState(0);

    useEffect(() => {
        loadNotifications();
    }, []);

    const loadNotifications = async () => {
        try {
            const response = await api.get("/notifications");

            setNotificationCount(
                response.data.filter(
                    n => !n.read
                ).length
            );
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <nav
            style={{
                background: "#2563eb",
                padding: "15px",
                display: "flex",
                gap: "15px",
                alignItems: "center"
            }}
        >
            <Link to="/" style={{ color: "white" }}>
                Dashboard
            </Link>

            <Link to="/students" style={{ color: "white" }}>
                Students
            </Link>

            <Link to="/mentors" style={{ color: "white" }}>
                Mentors
            </Link>

            <Link to="/meetings" style={{ color: "white" }}>
                Meetings
            </Link>

            <Link to="/tasks" style={{ color: "white" }}>
                Tasks
            </Link>

            <Link to="/goals" style={{ color: "white" }}>
                Goals
            </Link>

            {isAdmin() && (
                <Link to="/users" style={{ color: "white" }}>
                    Users
                </Link>
            )}

            <Link
                to="/notifications"
                style={{
                    color: "white",
                    textDecoration: "none"
                }}
            >
                🔔 Notifications ({notificationCount})
            </Link>

            <span
                style={{
                    marginLeft: "auto",
                    color: "white",
                    background: "rgba(255,255,255,0.2)",
                    padding: "5px 10px",
                    borderRadius: "999px",
                    fontSize: "12px",
                    fontWeight: "bold"
                }}
            >
                🔑 {getRole()}
            </span>

            <button
                onClick={logout}
                style={{
                    cursor: "pointer"
                }}
            >
                Logout
            </button>
        </nav>
    );
}

export default Navbar;