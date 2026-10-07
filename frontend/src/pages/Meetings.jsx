import { useEffect, useState } from "react";
import api from "../services/api";

function Meetings() {

    const [meetings, setMeetings] = useState([]);
    const [students, setStudents] = useState([]);

    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        studentName: "",
        mentorName: "",
        meetingDate: "",
        meetingTime: "",
        status: "Scheduled"
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {

        try {

            const meetingRes =
                await api.get("/meetings");

            const studentRes =
                await api.get("/students");

            setMeetings(meetingRes.data);
            setStudents(studentRes.data);

        } catch (error) {

            console.error(error);
        }
    };

    const handleStudentChange = (e) => {

        const selectedStudent =
            students.find(
                student =>
                    student.name === e.target.value
            );

        setFormData({
            ...formData,
            studentName: e.target.value,
            mentorName:
                selectedStudent?.assignedMentor || ""
        });
    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const editMeeting = (meeting) => {

        setFormData({
            studentName: meeting.studentName,
            mentorName: meeting.mentorName,
            meetingDate: meeting.meetingDate,
            meetingTime: meeting.meetingTime,
            status: meeting.status
        });

        setEditingId(meeting.id);
    };

    const saveMeeting = async (e) => {

        e.preventDefault();

        try {

            if (editingId) {

                await api.put(
                    `/meetings/${editingId}`,
                    formData
                );

            } else {

                await api.post(
                    "/meetings",
                    formData
                );
            }

            setFormData({
                studentName: "",
                mentorName: "",
                meetingDate: "",
                meetingTime: "",
                status: "Scheduled"
            });

            setEditingId(null);

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    const deleteMeeting = async (id) => {

        try {

            await api.delete(
                `/meetings/${id}`
            );

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Meeting Scheduling</h1>

            <form onSubmit={saveMeeting}>

                <select
                    value={formData.studentName}
                    onChange={handleStudentChange}
                >
                    <option value="">
                        Select Student
                    </option>

                    {students.map(student => (

                        <option
                            key={student.id}
                            value={student.name}
                        >
                            {student.name}
                        </option>

                    ))}
                </select>

                <br /><br />

                <input
                    type="text"
                    value={formData.mentorName}
                    readOnly
                    placeholder="Assigned Mentor"
                />

                <br /><br />

                <input
                    type="date"
                    name="meetingDate"
                    value={formData.meetingDate}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <input
                    type="time"
                    name="meetingTime"
                    value={formData.meetingTime}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <select
                    name="status"
                    value={formData.status}
                    onChange={handleChange}
                >
                    <option value="Scheduled">
                        Scheduled
                    </option>

                    <option value="Completed">
                        Completed
                    </option>

                    <option value="Cancelled">
                        Cancelled
                    </option>
                </select>

                <br /><br />

                <button type="submit">

                    {editingId
                        ? "Update Meeting"
                        : "Create Meeting"}

                </button>

            </form>

            <hr />

            {meetings.map(meeting => (

                <div
                    key={meeting.id}
                    style={{
                        border: "1px solid #ddd",
                        padding: "10px",
                        marginBottom: "10px"
                    }}
                >

                    <h3>
                        {meeting.studentName}
                    </h3>

                    <p>
                        Mentor:
                        {" "}
                        {meeting.mentorName}
                    </p>

                    <p>
                        Date:
                        {" "}
                        {meeting.meetingDate}
                    </p>

                    <p>
                        Time:
                        {" "}
                        {meeting.meetingTime}
                    </p>

                    <p>
                        Status:
                        {" "}
                        {meeting.status}
                    </p>

                    <button
                        onClick={() =>
                            editMeeting(meeting)
                        }
                    >
                        Edit
                    </button>

                    {" "}

                    <button
                        onClick={() =>
                            deleteMeeting(meeting.id)
                        }
                    >
                        Delete
                    </button>

                </div>

            ))}

        </div>
    );
}

export default Meetings;

