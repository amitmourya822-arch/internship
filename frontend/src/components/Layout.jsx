import { Link } from "react-router-dom";

function Layout({ children }) {

    return (
        <div style={{ display: "flex" }}>

            <div
                style={{
                    width: "250px",
                    height: "100vh",
                    background: "#1f2937",
                    color: "white",
                    padding: "20px"
                }}
            >
                <h2>Hand Holding</h2>

                <hr />

                <p><Link to="/" style={{color:"white"}}>Dashboard</Link></p>

                <p><Link to="/students" style={{color:"white"}}>Students</Link></p>

                <p><Link to="/mentors" style={{color:"white"}}>Mentors</Link></p>

                <p><Link to="/meetings" style={{color:"white"}}>Meetings</Link></p>

                <p><Link to="/tasks" style={{color:"white"}}>Tasks</Link></p>

                <p><Link to="/goals" style={{color:"white"}}>Goals</Link></p>

            </div>

            <div
                style={{
                    flex: 1,
                    padding: "20px"
                }}
            >
                {children}
            </div>

        </div>
    );
}

export default Layout;