import { useEffect, useState } from "react";
import api, { canWrite } from "../services/api";

function Tasks() {

    const writer = canWrite();
    const [tasks, setTasks] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        assignedTo: "",
        dueDate: "",
        status: ""
    });

    useEffect(() => {
        loadTasks();
    }, []);

    const loadTasks = async () => {
        const response = await api.get("/tasks");
        setTasks(response.data);
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const editTask = (task) => {
        setFormData({
            title: task.title,
            description: task.description,
            assignedTo: task.assignedTo,
            dueDate: task.dueDate,
            status: task.status
        });

        setEditingId(task.id);
    };

    const saveTask = async (e) => {
        e.preventDefault();

        if (editingId) {
            await api.put(`/tasks/${editingId}`, formData);
        } else {
            await api.post("/tasks", formData);
        }

        setFormData({
            title: "",
            description: "",
            assignedTo: "",
            dueDate: "",
            status: ""
        });

        setEditingId(null);
        loadTasks();
    };

    const deleteTask = async (id) => {
        await api.delete(`/tasks/${id}`);
        loadTasks();
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Tasks</h1>

            {writer && (
                <form onSubmit={saveTask}>

                <input
                    type="text"
                    name="title"
                    placeholder="Task Title"
                    value={formData.title}
                    onChange={handleChange}
                />

                <br /><br />

                <input
                    type="text"
                    name="description"
                    placeholder="Description"
                    value={formData.description}
                    onChange={handleChange}
                />

                <br /><br />

                <input
                    type="text"
                    name="assignedTo"
                    placeholder="Assigned To"
                    value={formData.assignedTo}
                    onChange={handleChange}
                />

                <br /><br />

                <input
                    type="text"
                    name="dueDate"
                    placeholder="Due Date"
                    value={formData.dueDate}
                    onChange={handleChange}
                />

                <br /><br />

                <input
                    type="text"
                    name="status"
                    placeholder="Status"
                    value={formData.status}
                    onChange={handleChange}
                />

                <br /><br />

                <button type="submit">
                    {editingId ? "Update Task" : "Add Task"}
                </button>

                </form>
            )}

            <hr />

            {tasks.map(task => (

                <div key={task.id}>

                    <h3>{task.title}</h3>
                    <p>{task.description}</p>
                    <p>{task.assignedTo}</p>
                    <p>{task.dueDate}</p>
                    <p>{task.status}</p>

                    {writer && (
                    <>
                        <button onClick={() => editTask(task)}>
                            Edit
                        </button>

                        {" "}

                        <button onClick={() => deleteTask(task.id)}>
                            Delete
                        </button>
                    </>
                )}

                    <hr />

                </div>

            ))}

        </div>
    );
}

export default Tasks;