package com.quizshare.repository;

import com.quizshare.entity.ExamHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {

    @Query("SELECT eh FROM ExamHistory eh WHERE eh.user.id = :userId ORDER BY eh.createdAt DESC")
    List<ExamHistory> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(eh) FROM ExamHistory eh WHERE eh.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    // Admin: paginated search with optional filters
    @Query("SELECT eh FROM ExamHistory eh WHERE " +
           "(:userId IS NULL OR eh.user.id = :userId) " +
           "AND (:examId IS NULL OR eh.exam.id = :examId) " +
           "AND (:from IS NULL OR eh.createdAt >= :from) " +
           "AND (:to IS NULL OR eh.createdAt <= :to) " +
           "ORDER BY eh.createdAt DESC")
    Page<ExamHistory> searchHistoriesAdmin(@Param("userId") Long userId,
                                           @Param("examId") Long examId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           Pageable pageable);

    // Admin: exam history chart by day
    @Query(value = "SELECT DATE(created_at) AS day, COUNT(*) AS cnt " +
                   "FROM exam_histories WHERE created_at >= :startDate " +
                   "GROUP BY DATE(created_at) ORDER BY day",
           nativeQuery = true)
    List<Object[]> countHistoriesByDay(@Param("startDate") LocalDateTime startDate);
}
