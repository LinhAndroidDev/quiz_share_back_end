package com.quizshare.repository;

import com.quizshare.entity.ExamHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {

    @Query("SELECT eh FROM ExamHistory eh WHERE eh.user.id = :userId ORDER BY eh.createdAt DESC")
    List<ExamHistory> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(eh) FROM ExamHistory eh WHERE eh.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
