package com.quizshare.service.admin;

import com.quizshare.dto.response.admin.ChartDataItem;
import com.quizshare.dto.response.admin.DashboardStatsResult;
import com.quizshare.entity.Exam;
import com.quizshare.entity.User;
import com.quizshare.repository.DepartmentRepository;
import com.quizshare.repository.ExamHistoryRepository;
import com.quizshare.repository.ExamRepository;
import com.quizshare.repository.SubjectRepository;
import com.quizshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ExamHistoryRepository examHistoryRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;

    public DashboardStatsResult getStats() {
        return DashboardStatsResult.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByStatus(User.UserStatus.ACTIVE))
                .inactiveUsers(userRepository.countByStatus(User.UserStatus.INACTIVE))
                .bannedUsers(userRepository.countByStatus(User.UserStatus.BANNED))
                .totalExams(examRepository.count())
                .publicExams(examRepository.countByStatus(Exam.ExamStatus.PUBLIC))
                .privateExams(examRepository.countByStatus(Exam.ExamStatus.PRIVATE))
                .draftExams(examRepository.countByStatus(Exam.ExamStatus.DRAFT))
                .totalExamHistories(examHistoryRepository.count())
                .totalDepartments(departmentRepository.count())
                .totalSubjects(subjectRepository.count())
                .build();
    }

    public List<ChartDataItem> getExamHistoryChart(int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        List<Object[]> rows = examHistoryRepository.countHistoriesByDay(startDate);
        return rows.stream()
                .map(row -> new ChartDataItem(row[0].toString(), ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    public List<ChartDataItem> getUserRegisterChart(int months) {
        LocalDateTime startDate = LocalDate.now().withDayOfMonth(1).minusMonths(months - 1L).atStartOfDay();
        List<Object[]> rows = userRepository.countRegistrationsByMonth(startDate);
        return rows.stream()
                .map(row -> new ChartDataItem(row[0].toString(), ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }
}
