package com.college.grademanagement.config;

import com.college.grademanagement.model.*;
import com.college.grademanagement.repository.*;
import com.college.grademanagement.service.MarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically populates the database with default sample data if empty.
 * Ensures consistent demonstration of 10 students, 3 teachers, 5 subjects,
 * examinations, and graded marks across all database backends.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExaminationRepository examinationRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository,
                           ExaminationRepository examinationRepository,
                           MarkRepository markRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.examinationRepository = examinationRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping sample initialization.");
            return;
        }

        log.info("Initializing database with complete college academic sample data...");

        // 1. Seed Users
        User adminUser = new User("admin", passwordEncoder.encode("admin123"),
                "System Administrator", "admin@college.edu", Role.ADMIN);
        User teacherUser1 = new User("prof.smith", passwordEncoder.encode("teacher123"),
                "Prof. John Smith", "john.smith@college.edu", Role.TEACHER);
        User teacherUser2 = new User("prof.johnson", passwordEncoder.encode("teacher123"),
                "Prof. Sarah Johnson", "sarah.johnson@college.edu", Role.TEACHER);
        User teacherUser3 = new User("prof.davis", passwordEncoder.encode("teacher123"),
                "Prof. Robert Davis", "robert.davis@college.edu", Role.TEACHER);

        userRepository.saveAll(List.of(adminUser, teacherUser1, teacherUser2, teacherUser3));

        // 2. Seed Teachers
        Teacher teacher1 = new Teacher("EMP-CS-101", "Prof. John Smith", "john.smith@college.edu",
                "+1-555-0101", "Computer Science", "Associate Professor", teacherUser1);
        Teacher teacher2 = new Teacher("EMP-CS-102", "Prof. Sarah Johnson", "sarah.johnson@college.edu",
                "+1-555-0102", "Information Technology", "Assistant Professor", teacherUser2);
        Teacher teacher3 = new Teacher("EMP-CS-103", "Prof. Robert Davis", "robert.davis@college.edu",
                "+1-555-0103", "Electronics Engineering", "Head of Department", teacherUser3);

        teacherRepository.saveAll(List.of(teacher1, teacher2, teacher3));

        // 3. Seed 10 Students
        List<Student> students = new ArrayList<>();
        students.add(new Student("CS2024001", "Alex Turner", "alex.turner@student.college.edu",
                "+1-555-1001", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 5, 14)));
        students.add(new Student("CS2024002", "Emily Watson", "emily.watson@student.college.edu",
                "+1-555-1002", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 8, 22)));
        students.add(new Student("CS2024003", "Michael Chang", "michael.chang@student.college.edu",
                "+1-555-1003", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2002, 11, 30)));
        students.add(new Student("CS2024004", "Sophia Rodriguez", "sophia.rodriguez@student.college.edu",
                "+1-555-1004", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 2, 18)));
        students.add(new Student("CS2024005", "Daniel Kim", "daniel.kim@student.college.edu",
                "+1-555-1005", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 9, 9)));
        students.add(new Student("IT2024006", "Olivia Taylor", "olivia.taylor@student.college.edu",
                "+1-555-1006", "Information Technology", "Year 3", "Semester 5", LocalDate.of(2003, 4, 5)));
        students.add(new Student("IT2024007", "James Anderson", "james.anderson@student.college.edu",
                "+1-555-1007", "Information Technology", "Year 3", "Semester 5", LocalDate.of(2002, 7, 19)));
        students.add(new Student("EC2024008", "Isabella Martinez", "isabella.martinez@student.college.edu",
                "+1-555-1008", "Electronics Engineering", "Year 3", "Semester 5", LocalDate.of(2003, 12, 12)));
        students.add(new Student("CS2024009", "Liam O'Connor", "liam.oconnor@student.college.edu",
                "+1-555-1009", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 3, 27)));
        students.add(new Student("CS2024010", "Ava Patel", "ava.patel@student.college.edu",
                "+1-555-1010", "Computer Science", "Year 3", "Semester 5", LocalDate.of(2003, 10, 15)));

        studentRepository.saveAll(students);

        // 4. Seed 5 Subjects
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CS501", "Database Management Systems", "Computer Science", "Semester 5", 100.0, 40.0));
        subjects.add(new Subject("CS502", "Design and Analysis of Algorithms", "Computer Science", "Semester 5", 100.0, 40.0));
        subjects.add(new Subject("CS503", "Operating Systems", "Computer Science", "Semester 5", 100.0, 40.0));
        subjects.add(new Subject("CS504", "Computer Networks", "Computer Science", "Semester 5", 100.0, 40.0));
        subjects.add(new Subject("CS505", "Software Engineering & Java", "Computer Science", "Semester 5", 100.0, 40.0));

        subjectRepository.saveAll(subjects);

        // 5. Seed Examinations
        Examination exam1 = new Examination("EXAM-2024-SEM5", "Fall 2024 Semester 5 Final Examination",
                "2024-2025", "Semester 5", LocalDate.of(2024, 11, 15), LocalDate.of(2024, 11, 30));
        Examination exam2 = new Examination("EXAM-2024-MID5", "Fall 2024 Semester 5 Midterm Assessment",
                "2024-2025", "Semester 5", LocalDate.of(2024, 9, 20), LocalDate.of(2024, 9, 28));

        examinationRepository.saveAll(List.of(exam1, exam2));

        // 6. Seed Marks for All 10 Students in Exam 1
        // Marks table (studentIndex, [mark_sub1, mark_sub2, mark_sub3, mark_sub4, mark_sub5])
        double[][] scores = {
                {95.0, 92.0, 94.0, 90.0, 98.0}, // Alex Turner (A+)
                {88.0, 84.0, 86.0, 89.0, 91.0}, // Emily Watson (A)
                {74.0, 78.0, 72.0, 76.0, 79.0}, // Michael Chang (B)
                {82.0, 85.0, 79.0, 83.0, 88.0}, // Sophia Rodriguez (A)
                {65.0, 62.0, 68.0, 64.0, 69.0}, // Daniel Kim (C)
                {71.0, 68.0, 74.0, 70.0, 75.0}, // Olivia Taylor (B)
                {52.0, 46.0, 54.0, 48.0, 55.0}, // James Anderson (D/E)
                {58.0, 34.0, 50.0, 42.0, 60.0}, // Isabella Martinez (Failed Sub 2 with 34/100)
                {62.0, 55.0, 36.0, 32.0, 58.0}, // Liam O'Connor (Failed Sub 3 with 36/100 and Sub 4 with 32/100)
                {94.0, 88.0, 92.0, 87.0, 96.0}  // Ava Patel (A+)
        };

        String[] remarksList = {
                "Outstanding performance",
                "Excellent academic achievement",
                "Good conceptual understanding",
                "Very good analytical skills",
                "Satisfactory coursework completion",
                "Commendable practical effort",
                "Needs improvement in algorithmic proofs",
                "Failed Algorithms; remedial examination required",
                "Failed Operating Systems & Networks; re-sit required",
                "Exemplary practical & theory scores"
        };

        List<Mark> marks = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            double[] studentScores = scores[i];
            String remark = remarksList[i];

            for (int j = 0; j < subjects.size(); j++) {
                Subject sub = subjects.get(j);
                double score = studentScores[j];
                String grade = MarkService.calculateGrade(score, sub.getMaxMarks());
                String status = MarkService.determineStatus(score, sub.getPassingMarks());

                marks.add(new Mark(s, sub, exam1, score, grade, status, remark));
            }
        }

        markRepository.saveAll(marks);

        log.info("Database initialization complete! Sample data loaded successfully.");
    }
}
