package com.quizshare.repository;

import com.quizshare.entity.SavedExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedExamRepository extends JpaRepository<SavedExam, Long> {

    Optional<SavedExam> findByUserIdAndExamId(Long userId, Long examId);

    boolean existsByUserIdAndExamId(Long userId, Long examId);

    @Query("SELECT se FROM SavedExam se WHERE se.user.id = :userId " +
           "AND (:subjectId IS NULL OR se.exam.subject.id = :subjectId) " +
           "ORDER BY se.createdAt DESC")
    List<SavedExam> findByUserIdAndSubjectId(@Param("userId") Long userId,
                                              @Param("subjectId") Long subjectId);
}
