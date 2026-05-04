package com.quizshare.repository;

import com.quizshare.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByDepartmentId(Long departmentId);

    @Query("SELECT s FROM Subject s WHERE " +
           "(:departmentId IS NULL OR s.department.id = :departmentId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Subject> searchSubjects(@Param("departmentId") Long departmentId,
                                  @Param("keyword") String keyword);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.subject.id = :subjectId")
    long countExamsBySubjectId(@Param("subjectId") Long subjectId);
}
