package com.kiet.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.kiet.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    List<Student> findByCourseIgnoreCase(String course);

    @Query("SELECT s FROM Student s ORDER BY ((s.programmingScore + s.mathematicsScore) / 2.0) DESC")
    List<Student> findAllOrderByAverageScoreDesc();
}
