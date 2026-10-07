import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../services/api";

function Notifications() {

    const [notifications, setNotifications] =
        useState([]);

    useEffect(() => {
        loadNotifications();
    }, []);

    const loadNotifications = async () => {

        const response =
            await api.get("/notifications");

        setNotifications(response.data.reverse());
    };const markAsRead = async (id) => {

          await api.put(
              `/notifications/read/${id}`
          );

          loadNotifications();
      };

      const deleteNotification = async (id) => {

          await api.delete(
              `/notifications/${id}`
          );

          loadNotifications();
      };

    return (
        <>
            <Navbar />

            <div style={{ padding: "20px" }}>

                <h1>Notifications</h1>

                {notifications.length === 0 ? (
                    <p>No Notifications Available</p>
                ) : (
                    notifications.map(n => (

                        <div
                            key={n.id}
                            style={{
                                background: "#f8fafc",
                                padding: "15px",
                                marginBottom: "10px",
                                borderRadius: "10px"
                            }}
                        ><button
                             onClick={() => markAsRead(n.id)}
                         >
                             Mark Read
                         </button>

                         <button
                             onClick={() => deleteNotification(n.id)}
                         >
                             Delete
                         </button>
                            <h3>{n.title}</h3>

                            <p>{n.message}</p>

                            <small>
                                {n.createdAt}
                            </small>
                        </div>

                    ))
                )}
            </div>
        </>
    );
}

export default Notifications;

