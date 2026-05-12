package com.quizshare.repository;

import com.quizshare.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    long countByStatus(Exam.ExamStatus status);

    long countByAuthorId(Long authorId);

    // Admin: paginated search with optional filters
    @Query("SELECT e FROM Exam e WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:subjectId IS NULL OR e.subject.id = :subjectId) " +
           "AND (:departmentId IS NULL OR e.subject.department.id = :departmentId) " +
           "ORDER BY e.createdAt DESC")
    Page<Exam> searchExamsAdmin(@Param("keyword") String keyword,
                                @Param("status") Exam.ExamStatus status,
                                @Param("subjectId") Long subjectId,
                                @Param("departmentId") Long departmentId,
                                Pageable pageable);
}
