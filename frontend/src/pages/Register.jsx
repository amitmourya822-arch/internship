import { useState } from "react";
import api from "../services/api";

function Register() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    const handleRegister = async (e) => {

        e.preventDefault();

        setMessage("");
        setIsError(false);

        try {

            const response = await api.post(
                "/auth/register",
                {
                    username,
                    password
                }
            );

            setMessage(response.data.message);
            setIsError(false);

            setTimeout(() => {
                window.location.href = "/login";
            }, 1200);

        } catch (err) {

            console.error(err);

            setMessage(
                err.response?.data?.message
                || "Registration failed. Try again."
            );
            setIsError(true);
        }
    };

    return (
        <div style={{ padding: "50px" }}>

            <h1>Register</h1>

            {message && (
                <p
                    style={{
                        color: isError ? "red" : "green"
                    }}
                >
                    {message}
                </p>
            )}

            <form onSubmit={handleRegister}>

                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) =>
                        setUsername(e.target.value)
                    }
                />

                <br /><br />

                <input
                    type="password"
                    placeholder="Password (min 8 characters)"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                />

                <br /><br />

                <button type="submit">
                    Register
                </button>

            </form>

        </div>
    );
}

export default Register;