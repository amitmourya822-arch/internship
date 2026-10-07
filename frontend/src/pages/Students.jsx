import { useEffect, useState } from "react";
import api from "../services/api";

function Students() {

    const [students, setStudents] = useState([]);
    const [mentors, setMentors] = useState([]);
    const [search, setSearch] = useState("");
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        course: "",
        phone: "",
        assignedMentor: ""
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {

        try {

            const studentRes =
                await api.get("/students");

            const mentorRes =
                await api.get("/mentors");

            setStudents(studentRes.data);
            setMentors(mentorRes.data);

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

    const editStudent = (student) => {

        setFormData({
            name: student.name,
            email: student.email,
            course: student.course,
            phone: student.phone,
            assignedMentor:
                student.assignedMentor || ""
        });

        setEditingId(student.id);
    };

    const saveStudent = async (e) => {

        e.preventDefault();

        try {

            if (editingId) {

                await api.put(
                    `/students/${editingId}`,
                    formData
                );

            } else {

                await api.post(
                    "/students",
                    formData
                );
            }

            setFormData({
                name: "",
                email: "",
                course: "",
                phone: "",
                assignedMentor: ""
            });

            setEditingId(null);

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    const deleteStudent = async (id) => {

        try {

            await api.delete(
                `/students/${id}`
            );

            loadData();

        } catch (error) {

            console.error(error);
        }
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Students Management</h1>

            <form onSubmit={saveStudent}>

                <input
                    type="text"
                    name="name"
                    placeholder="Student Name"
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
                    name="course"
                    placeholder="Course"
                    value={formData.course}
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

                <select
                    name="assignedMentor"
                    value={formData.assignedMentor}
                    onChange={handleChange}
                >
                    <option value="">
                        Select Mentor
                    </option>

                    {mentors.map((mentor) => (
                        <option
                            key={mentor.id}
                            value={mentor.name}
                        >
                            {mentor.name}
                        </option>
                    ))}
                </select>

                <br /><br />

                <button type="submit">

                    {editingId
                        ? "Update Student"
                        : "Add Student"}

                </button>

            </form>

            <hr />

            <input
                type="text"
                placeholder="Search Student..."
                value={search}
                onChange={(e) =>
                    setSearch(e.target.value)
                }
                style={{
                    padding: "10px",
                    width: "300px",
                    marginBottom: "20px"
                }}
            />

            <hr />

            {students
                .filter(student =>
                    student.name
                        .toLowerCase()
                        .includes(
                            search.toLowerCase()
                        )
                )
                .map(student => (

                    <div key={student.id}>

                        <h3>
                            {student.name}
                        </h3>

                        <p>
                            Email:
                            {" "}
                            {student.email}
                        </p>

                        <p>
                            Course:
                            {" "}
                            {student.course}
                        </p>

                        <p>
                            Phone:
                            {" "}
                            {student.phone}
                        </p>

                        <p>
                            Assigned Mentor:
                            {" "}
                            {student.assignedMentor
                                || "Not Assigned"}
                        </p>

                        <button
                            onClick={() =>
                                editStudent(student)
                            }
                        >
                            Edit
                        </button>

                        {" "}

                        <button
                            onClick={() =>
                                deleteStudent(
                                    student.id
                                )
                            }
                        >
                            Delete
                        </button>

                        <hr />

                    </div>
                ))}
        </div>
    );
}

export default Students;
