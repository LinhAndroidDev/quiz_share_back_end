package com.quizshare.repository;

import com.quizshare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    long countByStatus(User.UserStatus status);

    // Admin: paginated search with optional filters
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR u.status = :status) " +
           "AND (:role IS NULL OR u.role = :role) " +
           "ORDER BY u.createdAt DESC")
    Page<User> searchUsers(@Param("keyword") String keyword,
                           @Param("status") User.UserStatus status,
                           @Param("role") User.Role role,
                           Pageable pageable);

    // Admin: user register chart by month
    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS cnt " +
                   "FROM users WHERE created_at >= :startDate " +
                   "GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month",
           nativeQuery = true)
    List<Object[]> countRegistrationsByMonth(@Param("startDate") LocalDateTime startDate);
}
