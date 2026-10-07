import { useState } from "react";
import api from "../services/api";

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async (e) => {

        e.preventDefault();

        try {

            const response = await api.post(
                "/auth/login",
                {
                    username,
                    password
                }
            );

            localStorage.setItem(
                "token",
                response.data
            );

            alert("Login Success");

            window.location.href = "/";

        } catch (error) {

            console.error(error);

            alert("Login Failed");
        }
    };

    return (
        <div style={{ padding: "50px" }}>

            <h1>Login</h1>

            <form onSubmit={handleLogin}>

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
                    placeholder="Password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                />

                <br /><br />

                <button type="submit">
                    Login
                </button>

            </form>

        </div>
    );
}

export default Login;