package com.quizshare.repository;

import com.quizshare.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d WHERE d.id IN :ids AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Department> searchByIdsAndKeyword(@Param("ids") List<Long> ids, @Param("keyword") String keyword);

    // Admin: paginated search
    @Query("SELECT d FROM Department d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY d.createdAt DESC")
    Page<Department> searchByKeywordPage(@Param("keyword") String keyword, Pageable pageable);
}
