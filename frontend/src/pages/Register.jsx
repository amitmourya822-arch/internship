import { useState } from "react";
import api from "../services/api";

function Register() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("USER");

    const handleRegister = async (e) => {

        e.preventDefault();

        try {

            const response = await api.post(
                "/auth/register",
                {
                    username,
                    password,
                    role
                }
            );

            alert(response.data);

            window.location.href = "/login";

        } catch (error) {

            console.error(error);

            alert("Registration Failed");
        }
    };

    return (
        <div style={{ padding: "50px" }}>

            <h1>Register</h1>

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
                    placeholder="Password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                />

                <br /><br />

                <select
                    value={role}
                    onChange={(e) =>
                        setRole(e.target.value)
                    }
                >
                    <option value="USER">
                        USER
                    </option>

                    <option value="ADMIN">
                        ADMIN
                    </option>
                </select>

                <br /><br />

                <button type="submit">
                    Register
                </button>

            </form>

        </div>
    );
}

export default Register;