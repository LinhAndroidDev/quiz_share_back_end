package com.quizshare.repository;

import com.quizshare.entity.SavedDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedDepartmentRepository extends JpaRepository<SavedDepartment, Long> {

    List<SavedDepartment> findByUserId(Long userId);

    Optional<SavedDepartment> findByUserIdAndDepartmentId(Long userId, Long departmentId);

    boolean existsByUserIdAndDepartmentId(Long userId, Long departmentId);
}
