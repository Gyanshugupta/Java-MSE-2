package com.kiet.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kiet.entity.Student;
import com.kiet.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        validateScores(student.getProgrammingScore(), student.getMathematicsScore());

        if (student.getEmail() != null && studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Student with email " + student.getEmail() + " already exists.");
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudentScoresAndSkills(Long id, Double programmingScore, Double mathematicsScore, String technicalSkills) {
        validateScores(programmingScore, mathematicsScore);

        return studentRepository.findById(id).map(student -> {
            student.setProgrammingScore(programmingScore);
            student.setMathematicsScore(mathematicsScore);
            if (technicalSkills != null) {
                student.setTechnicalSkills(technicalSkills);
            }
            return studentRepository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Student> getMeritList() {
        return studentRepository.findAllOrderByAverageScoreDesc();
    }

    private void validateScores(Double prog, Double math) {
        if (prog != null && (prog < 0 || prog > 100)) {
            throw new IllegalArgumentException("Programming score must be between 0 and 100.");
        }
        if (math != null && (math < 0 || math > 100)) {
            throw new IllegalArgumentException("Mathematics score must be between 0 and 100.");
        }
    }
}
