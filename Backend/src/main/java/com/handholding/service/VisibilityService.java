package com.handholding.service;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Goal;
import com.handholding.entity.Meeting;
import com.handholding.entity.Mentor;
import com.handholding.entity.Student;
import com.handholding.entity.Task;
import com.handholding.repository.GoalRepository;
import com.handholding.repository.MeetingRepository;
import com.handholding.repository.MentorRepository;
import com.handholding.repository.StudentRepository;
import com.handholding.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class VisibilityService {

    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final MeetingRepository meetingRepository;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;

    public VisibilityService(
            StudentRepository studentRepository,
            MentorRepository mentorRepository,
            MeetingRepository meetingRepository,
            TaskRepository taskRepository,
            GoalRepository goalRepository) {
        this.studentRepository = studentRepository;
        this.mentorRepository = mentorRepository;
        this.meetingRepository = meetingRepository;
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
    }

    private boolean isAdmin(AuthUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private boolean isMentor(AuthUser user) {
        return user != null && "MENTOR".equalsIgnoreCase(user.getRole());
    }

    public boolean isMentorScope(AuthUser user) {
        return isMentor(user);
    }

    private List<String> myMentorNames(AuthUser user) {

        List<Mentor> mentors = mentorRepository.findByEmail(
                user.getUsername()
        );

        Set<String> names = new LinkedHashSet<>();

        for (Mentor mentor : mentors) {
            if (mentor.getName() != null && !mentor.getName().isBlank()) {
                names.add(mentor.getName().trim());
            }
        }

        return new ArrayList<>(names);
    }

    private Student myStudent(AuthUser user) {

        String email = user.getUsername();

        if (email == null || email.isBlank()) {
            return null;
        }

        return studentRepository.findByEmail(email);
    }

    private boolean sameIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    // ------------------------------------------------------------
    // Students
    // ------------------------------------------------------------

    public List<Student> visibleStudents(AuthUser user) {

        if (isAdmin(user)) {
            return studentRepository.findAll();
        }

        if (isMentor(user)) {
            return studentRepository.findByAssignedMentorIn(
                    myMentorNames(user)
            );
        }

        Student student = myStudent(user);

        if (student == null) {
            return List.of();
        }

        return List.of(student);
    }

    public boolean canAccessStudent(AuthUser user, Student student) {

        if (isAdmin(user) || student == null) {
            return true;
        }

        if (isMentor(user)) {
            return myMentorNames(user).contains(
                    student.getAssignedMentor()
            );
        }

        Student self = myStudent(user);

        return self != null
                && sameIgnoreCase(self.getEmail(), student.getEmail());
    }

    // ------------------------------------------------------------
    // Mentors
    // ------------------------------------------------------------

    public List<Mentor> visibleMentors(AuthUser user) {

        if (isAdmin(user)) {
            return mentorRepository.findAll();
        }

        if (isMentor(user)) {
            return mentorRepository.findByEmail(user.getUsername());
        }

        Student self = myStudent(user);

        if (self == null || self.getAssignedMentor() == null) {
            return List.of();
        }

        List<Mentor> result = new ArrayList<>();

        for (Mentor mentor : mentorRepository.findAll()) {
            if (sameIgnoreCase(
                    mentor.getName(),
                    self.getAssignedMentor()
            )) {
                result.add(mentor);
            }
        }

        return result;
    }

    public boolean canAccessMentor(AuthUser user, Mentor mentor) {

        if (isAdmin(user) || mentor == null) {
            return true;
        }

        if (isMentor(user)) {
            for (Mentor mine : mentorRepository.findByEmail(user.getUsername())) {
                if (mine.getId().equals(mentor.getId())) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    // ------------------------------------------------------------
    // Meetings
    // ------------------------------------------------------------

    public List<Meeting> visibleMeetings(AuthUser user) {

        if (isAdmin(user)) {
            return meetingRepository.findAll();
        }

        List<String> names = isMentor(user)
                ? myMentorNames(user)
                : List.of();

        List<Meeting> result = new ArrayList<>();

        for (Meeting meeting : meetingRepository.findAll()) {
            if (isMentor(user)) {
                if (containsIgnoreCase(names, meeting.getMentorName())) {
                    result.add(meeting);
                }
            } else {
                Student self = myStudent(user);
                if (self != null
                        && sameIgnoreCase(
                                meeting.getStudentName(),
                                self.getName()
                        )) {
                    result.add(meeting);
                }
            }
        }

        return result;
    }

    public boolean canAccessMeeting(AuthUser user, Meeting meeting) {

        if (isAdmin(user) || meeting == null) {
            return true;
        }

        if (isMentor(user)) {
            return containsIgnoreCase(
                    myMentorNames(user),
                    meeting.getMentorName()
            );
        }

        Student self = myStudent(user);

        return self != null
                && sameIgnoreCase(
                        meeting.getStudentName(),
                        self.getName()
                );
    }

    // ------------------------------------------------------------
    // Tasks
    // ------------------------------------------------------------

    public List<Task> visibleTasks(AuthUser user) {

        if (isAdmin(user)) {
            return taskRepository.findAll();
        }

        List<String> names = allowedNames(user);

        List<Task> result = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            if (containsIgnoreCase(names, task.getAssignedTo())) {
                result.add(task);
            }
        }

        return result;
    }

    public boolean canAccessTask(AuthUser user, Task task) {

        if (isAdmin(user) || task == null) {
            return true;
        }

        return containsIgnoreCase(
                allowedNames(user),
                task.getAssignedTo()
        );
    }

    // ------------------------------------------------------------
    // Goals
    // ------------------------------------------------------------

    public List<Goal> visibleGoals(AuthUser user) {

        if (isAdmin(user)) {
            return goalRepository.findAll();
        }

        List<String> names = allowedNames(user);

        List<Goal> result = new ArrayList<>();

        for (Goal goal : goalRepository.findAll()) {
            if (containsIgnoreCase(names, goal.getAssignedTo())) {
                result.add(goal);
            }
        }

        return result;
    }

    public boolean canAccessGoal(AuthUser user, Goal goal) {

        if (isAdmin(user) || goal == null) {
            return true;
        }

        return containsIgnoreCase(
                allowedNames(user),
                goal.getAssignedTo()
        );
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

    private List<String> allowedNames(AuthUser user) {

        if (isMentor(user)) {

            Set<String> names = new LinkedHashSet<>(
                    myMentorNames(user)
            );

            for (Student student : visibleStudents(user)) {
                if (student.getName() != null) {
                    names.add(student.getName().trim());
                }
            }

            return new ArrayList<>(names);
        }

        Student self = myStudent(user);

        if (self == null) {
            return List.of();
        }

        List<String> names = new ArrayList<>();

        if (self.getName() != null) {
            names.add(self.getName().trim());
        }

        if (self.getEmail() != null) {
            names.add(self.getEmail().trim());
        }

        return names;
    }

    private boolean containsIgnoreCase(List<String> values, String target) {

        if (target == null) {
            return false;
        }

        for (String value : values) {
            if (sameIgnoreCase(value, target)) {
                return true;
            }
        }

        return false;
    }
}