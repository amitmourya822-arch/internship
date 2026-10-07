package com.handholding;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Goal;
import com.handholding.entity.Meeting;
import com.handholding.entity.Mentor;
import com.handholding.entity.Student;
import com.handholding.entity.Task;
import com.handholding.repository.AuthUserRepository;
import com.handholding.repository.GoalRepository;
import com.handholding.repository.MeetingRepository;
import com.handholding.repository.MentorRepository;
import com.handholding.repository.StudentRepository;
import com.handholding.repository.TaskRepository;
import com.handholding.security.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OwnershipSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long mentorAId;
    private Long mentorBId;
    private Long menteeAId;
    private Long menteeBId;
    private Long meetingAId;
    private Long meetingBId;
    private Long taskAId;
    private Long taskBId;
    private Long goalAId;
    private Long goalBId;

    @BeforeEach
    void seedGraph() {

        user("admin", "ADMIN");

        mentorAId = mentor("Mentor A", "mentora@test.com").getId();
        user("mentora@test.com", "MENTOR");
        mentorBId = mentor("Mentor B", "mentorb@test.com").getId();
        user("mentorb@test.com", "MENTOR");

        menteeAId = student("Mentee A", "alpha@test.com", "Mentor A").getId();
        user("alpha@test.com", "MENTEE");
        menteeBId = student("Mentee B", "beta@test.com", "Mentor B").getId();
        user("beta@test.com", "MENTEE");

        meetingAId = meeting("Mentee A", "Mentor A").getId();
        meetingBId = meeting("Mentee B", "Mentor B").getId();

        taskAId = task("Alpha task", "Mentee A").getId();
        taskBId = task("Beta task", "Mentee B").getId();

        goalAId = goal("Alpha goal", "Mentee A").getId();
        goalBId = goal("Beta goal", "Mentee B").getId();
    }

    private Mentor mentor(String name, String email) {
        Mentor mentor = new Mentor();
        mentor.setName(name);
        mentor.setEmail(email);
        return mentorRepository.save(mentor);
    }

    private Student student(String name, String email, String mentorName) {
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setCourse("BCA");
        student.setPhone("000");
        student.setAssignedMentor(mentorName);
        return studentRepository.save(student);
    }

    private Meeting meeting(String studentName, String mentorName) {
        Meeting meeting = new Meeting();
        meeting.setStudentName(studentName);
        meeting.setMentorName(mentorName);
        meeting.setMeetingDate("2026-10-08");
        meeting.setMeetingTime("10:00");
        meeting.setStatus("Scheduled");
        return meetingRepository.save(meeting);
    }

    private Task task(String title, String assignedTo) {
        Task task = new Task();
        task.setTitle(title);
        task.setAssignedTo(assignedTo);
        task.setStatus("Pending");
        return taskRepository.save(task);
    }

    private Goal goal(String title, String assignedTo) {
        Goal goal = new Goal();
        goal.setTitle(title);
        goal.setAssignedTo(assignedTo);
        goal.setStatus("Pending");
        return goalRepository.save(goal);
    }

    private void user(String username, String role) {
        AuthUser user = new AuthUser();
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("pass1234"));
        authUserRepository.save(user);
    }

    private String token(String username, String role) {
        return JwtUtil.generateToken(username, role);
    }

    private ResultActions doGet(String bearerUser, String role, String url) throws Exception {
        return mockMvc.perform(get(url)
                .header("Authorization", "Bearer " + token(bearerUser, role)));
    }

    private ResultActions doPost(String bearerUser, String role, String url, String json) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", "Bearer " + token(bearerUser, role))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json == null ? "" : json));
    }

    private ResultActions doPut(String bearerUser, String role, String url, String json) throws Exception {
        return mockMvc.perform(put(url)
                .header("Authorization", "Bearer " + token(bearerUser, role))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json == null ? "" : json));
    }

    private ResultActions doAssignMentor(String bearerUser, String role, Long id, String mentorName) throws Exception {
        return mockMvc.perform(put("/api/students/" + id + "/assign-mentor")
                .header("Authorization", "Bearer " + token(bearerUser, role))
                .param("mentorName", mentorName));
    }

    private ResultActions doDelete(String bearerUser, String role, String url) throws Exception {
        return mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + token(bearerUser, role)));
    }

    private String json(Map<String, Object> values) throws Exception {
        return objectMapper.writeValueAsString(values);
    }

    // ------------------------------------------------------------
    // ADMIN
    // ------------------------------------------------------------

    @Test
    void adminSeesAllStudentsAndResources() throws Exception {

        doGet("admin", "ADMIN", "/api/students")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        doGet("admin", "ADMIN", "/api/mentors")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        doGet("admin", "ADMIN", "/api/meetings")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        doGet("admin", "ADMIN", "/api/tasks")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        doGet("admin", "ADMIN", "/api/goals")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void adminCanAccessBothMentorPoolsById() throws Exception {

        doGet("admin", "ADMIN", "/api/students/" + menteeAId)
                .andExpect(status().isOk());

        doGet("admin", "ADMIN", "/api/students/" + menteeBId)
                .andExpect(status().isOk());
    }

    @Test
    void adminCanStillCreateRecords() throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Admin Created");
        body.put("email", "new@test.com");
        body.put("course", "BCA");

        doPost("admin", "ADMIN", "/api/students", json(body))
                .andExpect(status().isCreated());

        doGet("admin", "ADMIN", "/api/students")
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // ------------------------------------------------------------
    // MENTOR A
    // ------------------------------------------------------------

    @Test
    void mentorASeesOnlyOwnStudents() throws Exception {

        doGet("mentora@test.com", "MENTOR", "/api/students")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mentee A"));
    }

    @Test
    void mentorACannotGetByIdOfMentorBStudent() throws Exception {

        doGet("mentora@test.com", "MENTOR", "/api/students/" + menteeAId)
                .andExpect(status().isOk());

        doGet("mentora@test.com", "MENTOR", "/api/students/" + menteeBId)
                .andExpect(status().isNotFound());
    }

    @Test
    void mentorACannotUpdateDeleteOrAssignMentorBStudent() throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Hacked");
        body.put("email", "hacked@test.com");
        body.put("course", "BCA");

        doPut("mentora@test.com", "MENTOR",
                "/api/students/" + menteeBId, json(body))
                .andExpect(status().isForbidden());

        doDelete("mentora@test.com", "MENTOR",
                "/api/students/" + menteeBId)
                .andExpect(status().isForbidden());

        doAssignMentor("mentora@test.com", "MENTOR", menteeBId, "Mentor A")
                .andExpect(status().isForbidden());
    }

    @Test
    void mentorACannotAccessMentorBRecord() throws Exception {

        doGet("mentora@test.com", "MENTOR", "/api/mentors/" + mentorAId)
                .andExpect(status().isOk());

        doGet("mentora@test.com", "MENTOR", "/api/mentors/" + mentorBId)
                .andExpect(status().isNotFound());

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Hacked");
        body.put("email", "h@h.com");
        body.put("expertise", "x");

        doPut("mentora@test.com", "MENTOR", "/api/mentors/" + mentorBId, json(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void mentorACannotScheduleMeetingForMentorBStudent() throws Exception {

        Map<String, Object> other = new HashMap<>();
        other.put("studentName", "Mentee B");
        other.put("mentorName", "Mentor A");
        other.put("meetingDate", "2026-10-09");
        other.put("meetingTime", "12:00");
        other.put("status", "Scheduled");

        doPost("mentora@test.com", "MENTOR", "/api/meetings", json(other))
                .andExpect(status().isForbidden());

        Map<String, Object> own = new HashMap<>(other);
        own.put("studentName", "Mentee A");

        doPost("mentora@test.com", "MENTOR", "/api/meetings", json(own))
                .andExpect(status().isCreated());
    }

    @Test
    void mentorASeesOwnStudentsTasksGoalsNotMentorB() throws Exception {

        doGet("mentora@test.com", "MENTOR", "/api/tasks")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alpha task"));

        doGet("mentora@test.com", "MENTOR", "/api/goals")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alpha goal"));

        doGet("mentora@test.com", "MENTOR", "/api/tasks/" + taskAId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Alpha task"));

        doGet("mentora@test.com", "MENTOR", "/api/goals/" + goalAId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Alpha goal"));

        doGet("mentora@test.com", "MENTOR", "/api/tasks/" + taskBId)
                .andExpect(status().isNotFound());

        doGet("mentora@test.com", "MENTOR", "/api/goals/" + goalBId)
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------
    // MENTOR B (mirror isolation)
    // ------------------------------------------------------------

    @Test
    void mentorBSeesOnlyOwnStudentsAndResources() throws Exception {

        doGet("mentorb@test.com", "MENTOR", "/api/students")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mentee B"));

        doGet("mentorb@test.com", "MENTOR", "/api/students/" + menteeAId)
                .andExpect(status().isNotFound());

        doGet("mentorb@test.com", "MENTOR", "/api/mentors/" + mentorAId)
                .andExpect(status().isNotFound());

        doGet("mentorb@test.com", "MENTOR", "/api/meetings")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentName").value("Mentee B"));
    }

    // ------------------------------------------------------------
    // MENTEE A
    // ------------------------------------------------------------

    @Test
    void menteeASeesOnlyOwnRecord() throws Exception {

        doGet("alpha@test.com", "MENTEE", "/api/students")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mentee A"));
    }

    @Test
    void menteeASeesAssignedMentorOnly() throws Exception {

        doGet("alpha@test.com", "MENTEE", "/api/mentors")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Mentor A"));
    }

    @Test
    void menteeACannotReadAnotherStudentId() throws Exception {

        doGet("alpha@test.com", "MENTEE", "/api/students/" + menteeBId)
                .andExpect(status().isNotFound());
    }

    @Test
    void menteeANotAllowedToWriteAnyStudent() throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Hacked");
        body.put("email", "h@h.com");
        body.put("course", "BCA");

        doPost("alpha@test.com", "MENTEE", "/api/students", json(body))
                .andExpect(status().isForbidden());

        doPut("alpha@test.com", "MENTEE", "/api/students/" + menteeBId, json(body))
                .andExpect(status().isForbidden());

        doDelete("alpha@test.com", "MENTEE", "/api/students/" + menteeBId)
                .andExpect(status().isForbidden());
    }

    @Test
    void menteeASeesOwnResourcesAndNotOthers() throws Exception {

        doGet("alpha@test.com", "MENTEE", "/api/meetings")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentName").value("Mentee A"));

        doGet("alpha@test.com", "MENTEE", "/api/tasks")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alpha task"));

        doGet("alpha@test.com", "MENTEE", "/api/goals")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alpha goal"));
    }

    @Test
    void menteeACannotReadOrWriteAnotherMenteeResources() throws Exception {

        doGet("alpha@test.com", "MENTEE", "/api/meetings/" + meetingAId)
                .andExpect(status().isOk());

        doGet("alpha@test.com", "MENTEE", "/api/tasks/" + taskAId)
                .andExpect(status().isOk());

        doGet("alpha@test.com", "MENTEE", "/api/goals/" + goalAId)
                .andExpect(status().isOk());

        doGet("alpha@test.com", "MENTEE", "/api/meetings/" + meetingBId)
                .andExpect(status().isNotFound());

        doGet("alpha@test.com", "MENTEE", "/api/tasks/" + taskBId)
                .andExpect(status().isNotFound());

        doGet("alpha@test.com", "MENTEE", "/api/goals/" + goalBId)
                .andExpect(status().isNotFound());
    }
}