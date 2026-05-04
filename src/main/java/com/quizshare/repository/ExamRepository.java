package com.quizshare.repository;

import com.quizshare.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE e.subject.id = :subjectId " +
           "AND (:authorId IS NULL OR e.author.id = :authorId) " +
           "ORDER BY e.createdAt DESC")
    List<Exam> findBySubjectIdAndAuthorId(@Param("subjectId") Long subjectId,
                                           @Param("authorId") Long authorId);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.subject.department.id = :departmentId")
    long countExamsByDepartmentId(@Param("departmentId") Long departmentId);

    List<Exam> findBySubjectId(Long subjectId);
}
