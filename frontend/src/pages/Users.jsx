import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../services/api";

function Users() {

    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState("");
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        role: "MENTEE"
    });

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            const response = await api.get("/users");
            setUsers(response.data);
        } catch (error) {
            console.error(error);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const createUser = async (e) => {
        e.preventDefault();
        setMessage("");
        try {
            const response = await api.post("/users", formData);
            setMessage(response.data?.message || "User created");
            setFormData({ name: "", email: "", password: "", role: "MENTEE" });
            loadUsers();
        } catch (error) {
            setMessage(error.response?.data?.message || "Failed to create user");
        }
    };

    const deleteUser = async (id) => {
        setMessage("");
        try {
            await api.delete(`/users/${id}`);
            loadUsers();
        } catch (error) {
            setMessage(error.response?.data?.message || "Failed to delete user");
        }
    };

    return (
        <>
            <Navbar />

            <div style={{ padding: "20px" }}>
                <h1>Users Management (Admin)</h1>

                {message && <p style={{ fontWeight: "bold" }}>{message}</p>}

                <form onSubmit={createUser}>

                    <input
                        type="text"
                        name="name"
                        placeholder="Name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />

                    {" "}

                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    {" "}

                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        minLength={6}
                    />

                    {" "}

                    <select
                        name="role"
                        value={formData.role}
                        onChange={handleChange}
                    >
                        <option value="ADMIN">ADMIN</option>
                        <option value="MENTOR">MENTOR</option>
                        <option value="MENTEE">MENTEE</option>
                    </select>

                    <button type="submit">Create User</button>
                </form>

                <hr />

                {users.length > 0 ? (
                    users.map((user) => (
                        <div
                            key={user.id}
                            style={{
                                background: "#f8fafc",
                                padding: "10px",
                                marginBottom: "10px",
                                borderRadius: "8px"
                            }}
                        >
                            <strong>{user.name}</strong>
                            {" "}- {user.email} -{" "}
                            <span style={{ fontWeight: "bold" }}>
                                {user.role}
                            </span>

                            <button
                                onClick={() => deleteUser(user.id)}
                                style={{ float: "right" }}
                            >
                                Delete
                            </button>
                        </div>
                    ))
                ) : (
                    <p>No users found.</p>
                )}
            </div>
        </>
    );
}

export default Users;