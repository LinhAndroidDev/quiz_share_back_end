package com.quizshare.repository;

import com.quizshare.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Department> searchByKeyword(@Param("keyword") String keyword);
}
