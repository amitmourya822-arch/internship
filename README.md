# Smart Hand Holding System

A full-stack **Mentor–Mentee (Hand Holding) management dashboard** for organisations running internship / mentorship programs. It lets admins and mentors manage students (mentees), mentors, scheduled meetings, tasks and goals, send meeting notification emails, and view live analytics.

## Description

The system provides a React single-page application backed by a Spring Boot REST API. After registering or logging in, authenticated users receive a JWT; the dashboard shows counts and analytics (students, mentors, meetings, tasks, goals, task completion rate), and dedicated pages allow full CRUD on students, mentors, meetings, tasks, goals and notifications. Scheduling a meeting also creates a notification and (when email is configured) sends the mentee a confirmation email.

## Features

- JWT-based authentication and role-based authorization (`ADMIN`, `MENTOR`, `MENTEE`)
- Dashboard with live analytics (10-second auto-refresh)
- CRUD management for students, mentors, meetings, tasks and goals
- Assign mentors to students
- Notifications with read/unread state
- Email notifications for scheduled meetings (Gmail SMTP)
- Bean Validation on API input
- Centralized CORS (frontend origin only)
- Secure password handling (BCrypt, passwords never returned by the API)
- Secrets via environment variables (no hardcoded credentials)

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite, React Router, Axios |
| Backend | Spring Boot 3.5.5, Java 21 |
| Persistence | Spring Data JPA / Hibernate, MySQL |
| Security | Spring Security, JJWT 0.12.5, BCrypt |
| Email | Spring Boot Mail (Gmail SMTP) |
| Build | Maven, npm |

## Project Structure

```
internship/
├── Backend/                 # Spring Boot REST API (port 8081)
│   ├── pom.xml
│   └── src/main/java/com/handholding/
│       ├── config/          # Security, CORS, data initializer
│       ├── controller/      # REST controllers
│       ├── dto/             # Request/response objects
│       ├── entity/          # JPA entities
│       ├── exception/       # Global error handling
│       ├── repository/      # Spring Data repositories
│       ├── security/        # JWT utilities + filter
│       └── service/         # Business services (email, etc.)
└── frontend/                # React SPA (port 5173)
    └── src/
        ├── components/      # Navbar, ProtectedRoute, Layout
        ├── pages/           # Login, Dashboard, CRUD pages
        └── services/api.js  # Axios client with JWT interceptor
```

## Requirements

- **Java 21** (JDK)
- **Maven** 3.9+
- **Node.js** 18+
- **npm**
- **MySQL** 8+

## Database Setup

1. Start MySQL and create the database:

```sql
CREATE DATABASE IF NOT EXISTS handholding_db;
```

2. Set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables (see below).

The schema is created/updated automatically by Hibernate (`ddl-auto=update` — **non-destructive**).

## Environment Variables

Copy `.env.example` to `.env` (never commit `.env`) and fill in real values:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:mysql://localhost:3306/handholding_db` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Random secret **at least 32 characters** (e.g. `openssl rand -base64 48`) |
| `MAIL_USERNAME` | Gmail address for sending meeting emails |
| `MAIL_PASSWORD` | Gmail **App Password** (16 digits) |
| `ADMIN_USERNAME` | Optional: username of a default admin created on first startup |
| `ADMIN_PASSWORD` | Optional: password for the default admin (min 8 characters) |

The frontend reads its backend URL from `frontend/.env` (`VITE_API_URL`, see `frontend/.env.example`).

> Environment variables must be set in the shell where the backend process starts. `JWT_SECRET` is required — the application will refuse to start without it.

## Backend Setup

```bash
cd Backend
# set environment variables first (see above)
mvn spring-boot:run
```

The API runs at `http://localhost:8081`.

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

The app runs at `http://localhost:5173`. For a production bundle: `npm run build`.

## Ports

- Backend: **8081**
- Frontend: **5173**

## Authentication

- `POST /api/auth/register` — self-registration (always creates a `MENTEE`; roles are provisioned server-side).
- `POST /api/auth/login` — returns a JWT (`token`), the username and role.
- The SPA stores the JWT, sends it as `Authorization: Bearer <token>`, and redirects to `/login` on `401`.
- `GET /api/auth/me` — returns the current authenticated user.

## User Roles

| Role | Access |
|---|---|
| `ADMIN` | Full access, including user management (`/api/users`) |
| `MENTOR` | Read + write management APIs (students, mentors, meetings, tasks, goals, notifications) |
| `MENTEE` | Read access to management APIs; cannot modify data or manage users |

## API Overview

Public endpoints:

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register (creates MENTEE) |
| POST | `/api/auth/login` | Login, returns JWT |

Protected endpoints (JWT required):

| Method | Path | Description |
|---|---|---|
| GET | `/api/auth/me` | Current user info |
| GET/POST/PUT/DELETE | `/api/students[/{id}]` | Manage students (+ `PUT /api/students/{id}/assign-mentor`) |
| GET/POST/PUT/DELETE | `/api/mentors[/{id}]` | Manage mentors |
| GET/POST/PUT/DELETE | `/api/meetings[/{id}]` | Manage meetings (POST creates notification + email) |
| GET/POST/PUT/DELETE | `/api/tasks[/{id}]` | Manage tasks |
| GET/POST/PUT/DELETE | `/api/goals[/{id}]` | Manage goals |
| GET/PUT/DELETE | `/api/notifications[/{id}]` | Manage notifications |
| GET/POST/PUT/DELETE | `/api/users[/{id}]` | Manage users (**ADMIN only**) |

## Future Improvements

- Add feedback / messages between mentors and mentees
- Per-user data ownership (owner columns) for strict MENTEE/MENTOR data isolation
- Password reset flow and email verification
- Rate limiting / account lockout on login
- Run as production builds with HTTPS
- Add automated integration tests

---

*Internship Mini Project — Smart Hand Holding System (Mentor–Mentee Dashboard)*