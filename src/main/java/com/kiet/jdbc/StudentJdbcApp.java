package com.kiet.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StudentJdbcApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/kiet_student_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Gyanshu!11849";

    // Encapsulated Student model
    public static class StudentRecord {
        private int studentId;
        private String studentName;
        private String email;
        private String course;
        private double programmingScore;
        private double mathematicsScore;
        private List<String> technicalSkills;

        public StudentRecord() {
            this.technicalSkills = new ArrayList<>();
        }

        public StudentRecord(int studentId, String studentName, String email, String course,
                             double programmingScore, double mathematicsScore, List<String> technicalSkills) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.email = email;
            this.course = course;
            this.programmingScore = programmingScore;
            this.mathematicsScore = mathematicsScore;
            this.technicalSkills = technicalSkills != null ? technicalSkills : new ArrayList<>();
        }

        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCourse() { return course; }
        public void setCourse(String course) { this.course = course; }

        public double getProgrammingScore() { return programmingScore; }
        public void setProgrammingScore(double programmingScore) { this.programmingScore = programmingScore; }

        public double getMathematicsScore() { return mathematicsScore; }
        public void setMathematicsScore(double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

        public List<String> getTechnicalSkills() { return technicalSkills; }
        public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }

        public double calculateAverage() {
            return (programmingScore + mathematicsScore) / 2.0;
        }

        public String getPerformanceCategory() {
            double avg = calculateAverage();
            if (avg >= 85.0) return "Excellent";
            if (avg >= 70.0) return "Good";
            if (avg >= 50.0) return "Satisfactory";
            return "Needs Improvement";
        }

        public String getStrongerSubject() {
            if (programmingScore > mathematicsScore) return "Programming";
            if (mathematicsScore > programmingScore) return "Mathematics";
            return "Equal";
        }

        @Override
        public String toString() {
            return String.format("| %-4d | %-16s | %-22s | %-10s | %-6.1f | %-6.1f | %-6.2f | %-16s | %-12s | %-20s |",
                    studentId, studentName, email, course, programmingScore, mathematicsScore,
                    calculateAverage(), getPerformanceCategory(), getStrongerSubject(),
                    String.join(", ", technicalSkills));
        }
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void initializeDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS students ("
                + "id INT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE, "
                + "course VARCHAR(50) NOT NULL, "
                + "programming_score DOUBLE NOT NULL, "
                + "mathematics_score DOUBLE NOT NULL, "
                + "technical_skills VARCHAR(255) NOT NULL"
                + ")";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSql);
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
        Scanner scanner = new Scanner(System.in);
        System.out.println("===============================================================");
        System.out.println("       STUDENT MANAGEMENT SYSTEM (JDBC CONSOLE)                ");
        System.out.println("===============================================================");

        while (true) {
            System.out.println("\n----------------- MAIN MENU -----------------");
            System.out.println("1. Add New Student (With Validation)");
            System.out.println("2. Display All Students (Average, Category & Stronger Subject)");
            System.out.println("3. Search Students (By ID, Email, or Course)");
            System.out.println("4. Generate Merit List (Sorted by Average Score Descending)");
            System.out.println("5. Identify Students with Same Technical Skills");
            System.out.println("6. Students Needing Programming Improvement (< 50) with Math >= 75");
            System.out.println("7. Exit Application");
            System.out.print("Enter choice (1-7): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1 -> addStudent(scanner);
                case 2 -> displayAllStudents();
                case 3 -> searchStudents(scanner);
                case 4 -> generateMeritList();
                case 5 -> identifySameSkills();
                case 6 -> displayProgImprovementMathStrong();
                case 7 -> {
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Choice out of range. Choose between 1 and 7.");
            }
        }
    }

    private static void printTableHeader() {
        System.out.println("+------+------------------+------------------------+------------+--------+--------+--------+------------------+--------------+----------------------+");
        System.out.println("| ID   | Name             | Email                  | Course     | Prog   | Math   | Avg    | Category         | Stronger Sub | Technical Skills     |");
        System.out.println("+------+------------------+------------------------+------------+--------+--------+--------+------------------+--------------+----------------------+");
    }

    private static void printTableFooter() {
        System.out.println("+------+------------------+------------------------+------------+--------+--------+--------+------------------+--------------+----------------------+");
    }

    private static void addStudent(Scanner scanner) {
        try (Connection conn = getConnection()) {
            System.out.println("\n--- Add Student ---");
            System.out.print("Enter Student ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            // Duplicate ID check
            String checkSql = "SELECT COUNT(*) FROM students WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Error: Student ID " + id + " already exists. Operation rejected.");
                        return;
                    }
                }
            }

            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Student Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter Course Name: ");
            String course = scanner.nextLine().trim();

            System.out.print("Enter Programming Score (0 - 100): ");
            double prog = Double.parseDouble(scanner.nextLine().trim());
            if (prog < 0 || prog > 100) {
                System.out.println("Validation Error: Programming score must be between 0 and 100.");
                return;
            }

            System.out.print("Enter Mathematics Score (0 - 100): ");
            double math = Double.parseDouble(scanner.nextLine().trim());
            if (math < 0 || math > 100) {
                System.out.println("Validation Error: Mathematics score must be between 0 and 100.");
                return;
            }

            System.out.print("Enter Technical Skills (comma separated, e.g. Java, Python): ");
            String skills = scanner.nextLine().trim();

            String insertSql = "INSERT INTO students (id, name, email, course, programming_score, mathematics_score, technical_skills) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, id);
                insertStmt.setString(2, name);
                insertStmt.setString(3, email);
                insertStmt.setString(4, course);
                insertStmt.setDouble(5, prog);
                insertStmt.setDouble(6, math);
                insertStmt.setString(7, skills);
                insertStmt.executeUpdate();
                System.out.println("SUCCESS: Student record successfully registered.");
            }
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    private static void displayAllStudents() {
        String sql = "SELECT * FROM students ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            printTableHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                StudentRecord student = mapRow(rs);
                System.out.println(student);
            }
            printTableFooter();
            if (!found) {
                System.out.println("No records found in database.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
    }

    private static void searchStudents(Scanner scanner) {
        System.out.println("\nSearch by: 1) ID  2) Email  3) Course");
        System.out.print("Choose search criteria (1-3): ");
        String sub = scanner.nextLine().trim();

        String sql;
        String param = "";
        int idParam = -1;

        if ("1".equals(sub)) {
            System.out.print("Enter Student ID: ");
            try {
                idParam = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format.");
                return;
            }
            sql = "SELECT * FROM students WHERE id = ?";
        } else if ("2".equals(sub)) {
            System.out.print("Enter Email Address: ");
            param = scanner.nextLine().trim();
            sql = "SELECT * FROM students WHERE LOWER(email) = LOWER(?)";
        } else if ("3".equals(sub)) {
            System.out.print("Enter Course: ");
            param = scanner.nextLine().trim();
            sql = "SELECT * FROM students WHERE LOWER(course) LIKE LOWER(?)";
        } else {
            System.out.println("Invalid selection.");
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if ("1".equals(sub)) {
                stmt.setInt(1, idParam);
            } else if ("3".equals(sub)) {
                stmt.setString(1, "%" + param + "%");
            } else {
                stmt.setString(1, param);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                printTableHeader();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    StudentRecord student = mapRow(rs);
                    System.out.println(student);
                }
                printTableFooter();
                if (!any) {
                    System.out.println("No matching students found.");
                }
            }
        } catch (Exception e) {
            System.err.println("Search error: " + e.getMessage());
        }
    }

    private static void generateMeritList() {
        String sql = "SELECT * FROM students ORDER BY ((programming_score + mathematics_score) / 2.0) DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n============================= MERIT LIST =============================");
            printTableHeader();
            int rank = 1;
            while (rs.next()) {
                StudentRecord s = mapRow(rs);
                System.out.println(s);
                rank++;
            }
            printTableFooter();
            if (rank == 1) {
                System.out.println("No records found to rank.");
            }
        } catch (Exception e) {
            System.err.println("Error generating merit list: " + e.getMessage());
        }
    }

    private static void identifySameSkills() {
        String sql = "SELECT * FROM students";
        List<StudentRecord> allStudents = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                allStudents.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("Error reading skills: " + e.getMessage());
            return;
        }

        if (allStudents.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }

        // Map individual skill to list of student names
        Map<String, List<String>> skillToStudents = new HashMap<>();
        for (StudentRecord student : allStudents) {
            for (String skill : student.getTechnicalSkills()) {
                String normalized = skill.trim().toLowerCase();
                if (!normalized.isEmpty()) {
                    skillToStudents.computeIfAbsent(normalized, k -> new ArrayList<>())
                            .add(student.getStudentName() + " (ID: " + student.getStudentId() + ")");
                }
            }
        }

        System.out.println("\n--- Students Sharing the Same Technical Skills ---");
        boolean sharedFound = false;
        for (Map.Entry<String, List<String>> entry : skillToStudents.entrySet()) {
            if (entry.getValue().size() > 1) {
                sharedFound = true;
                System.out.printf("Skill [%s]: %s%n",
                        entry.getKey().toUpperCase(), String.join(", ", entry.getValue()));
            }
        }
        if (!sharedFound) {
            System.out.println("No overlapping technical skills detected among students.");
        }
    }

    private static void displayProgImprovementMathStrong() {
        String sql = "SELECT * FROM students WHERE programming_score < 50 AND mathematics_score >= 75";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Students with Programming < 50 and Mathematics >= 75 ---");
            printTableHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                StudentRecord student = mapRow(rs);
                System.out.println(student);
            }
            printTableFooter();
            if (!found) {
                System.out.println("No students match this criterion.");
            }
        } catch (Exception e) {
            System.err.println("Query error: " + e.getMessage());
        }
    }

    private static StudentRecord mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String course = rs.getString("course");
        double prog = rs.getDouble("programming_score");
        double math = rs.getDouble("mathematics_score");
        String skillsStr = rs.getString("technical_skills");

        List<String> skills = new ArrayList<>();
        if (skillsStr != null && !skillsStr.isBlank()) {
            String[] parts = skillsStr.split(",");
            for (String p : parts) {
                if (!p.trim().isEmpty()) {
                    skills.add(p.trim());
                }
            }
        }

        return new StudentRecord(id, name, email, course, prog, math, skills);
    }
}
