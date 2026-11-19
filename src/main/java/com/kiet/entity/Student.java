package com.kiet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String course;

    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    @Column(name = "technical_skills", nullable = false)
    private String technicalSkills;

    public Student() {
    }

    public Student(Long id, String name, String email, String course,
                   Double programmingScore, Double mathematicsScore, String technicalSkills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public String getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(String technicalSkills) { this.technicalSkills = technicalSkills; }

    public Double getAverageScore() {
        if (programmingScore == null || mathematicsScore == null) return 0.0;
        return (programmingScore + mathematicsScore) / 2.0;
    }

    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= 85.0) return "Excellent";
        if (avg >= 70.0) return "Good";
        if (avg >= 50.0) return "Satisfactory";
        return "Needs Improvement";
    }

    public String getStrongerSubject() {
        if (programmingScore == null || mathematicsScore == null) return "N/A";
        if (programmingScore > mathematicsScore) return "Programming";
        if (mathematicsScore > programmingScore) return "Mathematics";
        return "Equal";
    }
}
