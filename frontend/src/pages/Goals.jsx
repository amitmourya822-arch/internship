import { useEffect, useState } from "react";
import api from "../services/api";

function Goals() {

    const [goals, setGoals] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        targetDate: "",
        status: ""
    });

    useEffect(() => {
        loadGoals();
    }, []);

    const loadGoals = async () => {
        const response = await api.get("/goals");
        setGoals(response.data);
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const editGoal = (goal) => {
        setFormData({
            title: goal.title,
            description: goal.description,
            targetDate: goal.targetDate,
            status: goal.status
        });

        setEditingId(goal.id);
    };

    const saveGoal = async (e) => {
        e.preventDefault();

        if (editingId) {
            await api.put(`/goals/${editingId}`, formData);
        } else {
            await api.post("/goals", formData);
        }

        setFormData({
            title: "",
            description: "",
            targetDate: "",
            status: ""
        });

        setEditingId(null);
        loadGoals();
    };

    const deleteGoal = async (id) => {
        await api.delete(`/goals/${id}`);
        loadGoals();
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Goals</h1>

            <form onSubmit={saveGoal}>

                <input
                    type="text"
                    name="title"
                    placeholder="Goal Title"
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
                    name="targetDate"
                    placeholder="Target Date"
                    value={formData.targetDate}
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
                    {editingId ? "Update Goal" : "Add Goal"}
                </button>

            </form>

            <hr />

            {goals.map(goal => (

                <div key={goal.id}>

                    <h3>{goal.title}</h3>
                    <p>{goal.description}</p>
                    <p>{goal.targetDate}</p>
                    <p>{goal.status}</p>

                    <button onClick={() => editGoal(goal)}>
                        Edit
                    </button>

                    {" "}

                    <button onClick={() => deleteGoal(goal.id)}>
                        Delete
                    </button>

                    <hr />

                </div>

            ))}

        </div>
    );
}

export default Goals;