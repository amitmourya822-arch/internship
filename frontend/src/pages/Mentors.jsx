import { useEffect, useState } from "react";
import api from "../services/api";

function Mentors() {

    const [mentors, setMentors] = useState([]);
    const [students, setStudents] = useState([]);

    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        expertise: "",
        phone: ""
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {

        try {

            const mentorRes =
                await api.get("/mentors");

            const studentRes =
                await api.get("/students");

            setMentors(mentorRes.data);
            setStudents(studentRes.data);

        } catch (error) {

            console.error(error);
        }
    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const editMentor = (mentor) => {

        setFormData({
            name: mentor.name,
            email: mentor.email,
            expertise: mentor.expertise,
            phone: mentor.phone
        });

        setEditingId(mentor.id);
    };

    const saveMentor = async (e) => {

        e.preventDefault();

        try {

            if (editingId) {

                await api.put(
                    `/mentors/${editingId}`,
                    formData
                );

            } else {

                await api.post(
                    "/mentors",
                    formData
                );
            }

            setFormData({
                name: "",
                email: "",
                expertise: "",
                phone: ""
            });

            setEditingId(null);

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    const deleteMentor = async (id) => {

        try {

            await api.delete(
                `/mentors/${id}`
            );

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Mentors Management</h1>

            <form onSubmit={saveMentor}>

                <input
                    type="text"
                    name="name"
                    placeholder="Mentor Name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <input
                    type="text"
                    name="expertise"
                    placeholder="Expertise"
                    value={formData.expertise}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <input
                    type="text"
                    name="phone"
                    placeholder="Phone"
                    value={formData.phone}
                    onChange={handleChange}
                    required
                />

                <br /><br />

                <button type="submit">

                    {editingId
                        ? "Update Mentor"
                        : "Add Mentor"}

                </button>

            </form>

            <hr />

            {mentors.map((mentor) => {

                const assignedStudents =
                    students.filter(
                        student =>
                            student.assignedMentor === mentor.name
                    );

                return (

                    <div
                        key={mentor.id}
                        style={{
                            border: "1px solid #ddd",
                            padding: "15px",
                            marginBottom: "20px",
                            borderRadius: "10px"
                        }}
                    >

                        <h3>{mentor.name}</h3>

                        <p>
                            Email:
                            {" "}
                            {mentor.email}
                        </p>

                        <p>
                            Expertise:
                            {" "}
                            {mentor.expertise}
                        </p>

                        <p>
                            Phone:
                            {" "}
                            {mentor.phone}
                        </p>

                        <h4>
                            Assigned Students
                        </h4>

                        {assignedStudents.length > 0 ? (

                            <ul>

                                {assignedStudents.map(student => (

                                    <li key={student.id}>
                                        {student.name}
                                    </li>

                                ))}

                            </ul>

                        ) : (

                            <p>
                                No Students Assigned
                            </p>

                        )}

                        <button
                            onClick={() =>
                                editMentor(mentor)
                            }
                        >
                            Edit
                        </button>

                        {" "}

                        <button
                            onClick={() =>
                                deleteMentor(mentor.id)
                            }
                        >
                            Delete
                        </button>

                    </div>
                );
            })}
        </div>
    );
}

export default Mentors;
