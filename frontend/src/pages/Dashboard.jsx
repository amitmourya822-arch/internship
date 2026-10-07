import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import api, { canWrite } from "../services/api";

function Dashboard() {
    const writer = canWrite();
    const [studentCount, setStudentCount] = useState(0);
    const [mentorCount, setMentorCount] = useState(0);
    const [meetingCount, setMeetingCount] = useState(0);
    const [taskCount, setTaskCount] = useState(0);
    const [goalCount, setGoalCount] = useState(0);

    const [scheduledMeetings, setScheduledMeetings] = useState(0);
    const [completedMeetings, setCompletedMeetings] = useState(0);

    const [pendingTasks, setPendingTasks] = useState(0);
    const [inProgressTasks, setInProgressTasks] = useState(0);
    const [completedTasks, setCompletedTasks] = useState(0);

    const [recentStudents, setRecentStudents] = useState([]);
    const [recentMeetings, setRecentMeetings] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [completionRate, setCompletionRate] = useState(0);
    const [assignedStudents, setAssignedStudents] = useState(0);
    useEffect(() => {
        loadDashboardData();

        const interval = setInterval(() => {
            loadDashboardData();
        }, 10000);

        return () => clearInterval(interval);
    }, []);

    const loadDashboardData = async () => {
        try {
            const students = await api.get("/students");
            const mentors = await api.get("/mentors");
            const meetings = await api.get("/meetings");
            const tasks = await api.get("/tasks");
            const goals = await api.get("/goals");

            const meetingList = meetings.data || [];
            const taskList = tasks.data || [];
            const totalTasks = taskList.length;

            const completionRateValue =
                totalTasks === 0
                    ? 0
                    : Math.round(
                        (
                            taskList.filter(
                                t => t.status === "Completed"
                            ).length /
                            totalTasks
                        ) * 100
                    );

            setCompletionRate(completionRateValue);
            setStudentCount(students.data.length);
            setMentorCount(mentors.data.length);
            setMeetingCount(meetings.data.length);
            setTaskCount(tasks.data.length);
            setGoalCount(goals.data.length);
            setAssignedStudents(
                students.data.filter(
                    s =>
                        s.assignedMentor &&
                        s.assignedMentor.trim() !== ""
                ).length
            );

            setScheduledMeetings(
                meetingList.filter(
                    (m) => m.status === "Scheduled"
                ).length
            );

            setCompletedMeetings(
                meetingList.filter(
                    (m) => m.status === "Completed"
                ).length
            );

            setPendingTasks(
                taskList.filter(
                    (t) => t.status === "Pending"
                ).length
            );

            setInProgressTasks(
                taskList.filter(
                    (t) => t.status === "In Progress"
                ).length
            );

            setCompletedTasks(
                taskList.filter(
                    (t) => t.status === "Completed"
                ).length
            );

            setRecentStudents(
                students.data.slice(-5).reverse()
            );

            setRecentMeetings(
                meetings.data.slice(-5).reverse()
            );

        } catch (error) {
            console.error("Dashboard Load Error:", error);

            if (error.response) {
                console.log("Status:", error.response.status);
                console.log("URL:", error.config.url);
                console.log("Response:", error.response.data);
            }
        }
    };

    const cardStyle = {
        color: "white",
        padding: "20px",
        borderRadius: "12px",
        textAlign: "center",
        textDecoration: "none",
        fontWeight: "bold",
        boxShadow: "0 2px 10px rgba(0,0,0,0.2)"
    };

    const filteredStudents = recentStudents.filter(
        (student) =>
            student.name
                ?.toLowerCase()
                .includes(searchTerm.toLowerCase())
    );
       const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.location.href = "/login";
};

    return (
        <>
            <Navbar />

            <div style={{ padding: "20px" }}>
                <h1>Smart Hand Holding System</h1>

                <div
                    style={{
                        background: "#1e293b",
                        color: "white",
                        padding: "20px",
                        borderRadius: "12px",
                        marginTop: "20px",
                        marginBottom: "20px"
                    }}
                >
                    <h2>Welcome Back 👋</h2>

                    <p>
                        Mentor-Mentee Management Dashboard
                    </p>

                    <button
                        onClick={logout}
                        style={{
                            padding: "10px",
                            border: "none",
                            borderRadius: "6px",
                            cursor: "pointer"
                        }}
                    >
                        Logout
                    </button>
                </div>

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns:
                            "repeat(auto-fit, minmax(220px, 1fr))",
                        gap: "20px",
                        marginTop: "30px"
                    }}
                >
                    <Link
                        to="/students"
                        style={{
                            ...cardStyle,
                            background: "#2563eb"
                        }}
                    >
                        <h2>{studentCount}</h2>
                        <p>Total Students</p>
                    </Link>

                    <Link
                        to="/mentors"
                        style={{
                            ...cardStyle,
                            background: "#16a34a"
                        }}
                    >
                        <h2>{mentorCount}</h2>
                        <p>Total Mentors</p>
                    </Link>

                    <Link
                        to="/meetings"
                        style={{
                            ...cardStyle,
                            background: "#ea580c"
                        }}
                    >
                        <h2>{meetingCount}</h2>
                        <p>Total Meetings</p>
                    </Link>

                    <Link
                        to="/tasks"
                        style={{
                            ...cardStyle,
                            background: "#9333ea"
                        }}
                    >
                        <h2>{taskCount}</h2>
                        <p>Total Tasks</p>
                    </Link>

                    <Link
                        to="/goals"
                        style={{
                            ...cardStyle,
                            background: "#dc2626"
                        }}
                    >
                        <h2>{goalCount}</h2>
                        <p>Total Goals</p>
                    </Link>

                    <div
                        style={{
                            background: "#f59e0b",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{scheduledMeetings}</h2>
                        <p>Scheduled Meetings</p>
                    </div>

                    <div
                        style={{
                            background: "#16a34a",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{completedMeetings}</h2>
                        <p>Completed Meetings</p>
                    </div>
                    <div
                        style={{
                            background: "#0891b2",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{completionRate}%</h2>
                        <p>Task Completion Rate</p>
                    </div>
                </div>


                <hr />

                <h2>Meeting Alerts</h2>

                {scheduledMeetings > 0 ? (
                    <div
                        style={{
                            background: "#fef3c7",
                            padding: "15px",
                            borderRadius: "10px",
                            marginBottom: "20px"
                        }}
                    >
                        You have {scheduledMeetings} scheduled meeting(s).
                    </div>
                ) : (
                    <div
                        style={{
                            background: "#dcfce7",
                            padding: "15px",
                            borderRadius: "10px",
                            marginBottom: "20px"
                        }}
                    >
                        No pending meetings.
                    </div>
                )}

                <hr />
                <hr />

                <h2>Quick Actions</h2>

                {writer && (
                <div
                    style={{
                        display: "flex",
                        gap: "10px",
                        flexWrap: "wrap",
                        marginBottom: "20px"
                    }}
                >
                    <Link to="/students">
                      <button
                          style={{
                              padding: "10px 15px",
                              border: "none",
                              borderRadius: "8px",
                              cursor: "pointer"
                          }}
                      >
                          Add Student
                      </button>
                    </Link>

                    <Link to="/mentors">
                        <button>Add Mentor</button>
                    </Link>

                    <Link to="/meetings">
                        <button>Schedule Meeting</button>
                    </Link>

                    <Link to="/tasks">
                        <button>Create Task</button>
                    </Link>

                    <Link to="/goals">
                        <button>Add Goal</button>
                    </Link>
                </div>
                )}

                <h2>Recent Students</h2>

                <input
                    type="text"
                    placeholder="Search Student..."
                    value={searchTerm}
                    onChange={(e) =>
                        setSearchTerm(e.target.value)
                    }
                    style={{
                        padding: "10px",
                        width: "300px",
                        marginBottom: "15px",
                        borderRadius: "8px",
                        border: "1px solid #ccc"
                    }}
                />

                {filteredStudents.length > 0 ? (
                    filteredStudents.map((student) => (
                        <div
                            key={student.id}
                            style={{
                                background: "#f8fafc",
                                padding: "10px",
                                marginBottom: "10px",
                                borderRadius: "8px"
                            }}
                        >
                        <strong>{student.name}</strong>
                        <br />
                        Email: {student.email}
                        <br />
                        Course: {student.course}
                        <br />
                        Mentor: {student.assignedMentor || "Not Assigned"}


                        </div>
                    ))
                ) : (
                    <p>No students found.</p>
                )}

                <hr />

                <h2>Recent Meetings</h2>

                {recentMeetings.length > 0 ? (
                    recentMeetings.map((meeting) => (
                        <div
                            key={meeting.id}
                            style={{
                                background: "#f8fafc",
                                padding: "10px",
                                marginBottom: "10px",
                                borderRadius: "8px"
                            }}
                        >
                        <strong>Student: {meeting.studentName}</strong>
                        <br />
                        Mentor: {meeting.mentorName}
                        <br />
                        Date: {meeting.meetingDate}
                        <br />
                        Time: {meeting.meetingTime}
                        <br />
                        Status:
                        <span
                            style={{
                                color:
                                    meeting.status === "Completed"
                                        ? "green"
                                        : "orange",
                                fontWeight: "bold"
                            }}
                        >
                            {" "}{meeting.status}
                        </span>

                        </div>
                    ))
                ) : (
                    <p>No meetings available.</p>
                )}

                <hr />

                <h2>Task Analytics</h2>

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns:
                            "repeat(auto-fit, minmax(220px, 1fr))",
                        gap: "20px",
                        marginTop: "20px"
                    }}
                >
                    <div
                        style={{
                            background: "#e11d48",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{pendingTasks}</h2>
                        <p>Pending Tasks</p>
                    </div>

                    <div
                        style={{
                            background: "#3b82f6",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{inProgressTasks}</h2>
                        <p>In Progress Tasks</p>
                    </div>

                    <div
                        style={{
                            background: "#16a34a",
                            color: "white",
                            padding: "20px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >
                        <h2>{completedTasks}</h2>
                        <p>Completed Tasks</p>
                        <div
                            style={{
                                background: "#0f766e",
                                color: "white",
                                padding: "20px",
                                borderRadius: "12px",
                                textAlign: "center"
                            }}
                        >
                            <h2>{assignedStudents}</h2>
                            <p>Assigned Students</p>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Dashboard;

