package com.quizshare.service.admin;

import com.quizshare.dto.response.admin.AdminExamHistoryItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.entity.ExamHistory;
import com.quizshare.repository.ExamHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminExamHistoryService {

    private final ExamHistoryRepository examHistoryRepository;

    public AdminPageResult<AdminExamHistoryItem> getHistories(int page, int size,
                                                              Long userId, Long examId,
                                                              String from, String to) {
        LocalDateTime fromDt = from != null && !from.isBlank()
                ? LocalDate.parse(from).atStartOfDay() : null;
        LocalDateTime toDt = to != null && !to.isBlank()
                ? LocalDate.parse(to).plusDays(1).atStartOfDay() : null;

        Page<ExamHistory> historyPage = examHistoryRepository.searchHistoriesAdmin(
                userId, examId, fromDt, toDt, PageRequest.of(page, size));

        List<AdminExamHistoryItem> items = historyPage.getContent().stream()
                .map(this::toItem)
                .collect(Collectors.toList());

        return AdminPageResult.<AdminExamHistoryItem>builder()
                .total(historyPage.getTotalElements())
                .page(page)
                .size(size)
                .items(items)
                .build();
    }

    private AdminExamHistoryItem toItem(ExamHistory eh) {
        return AdminExamHistoryItem.builder()
                .id(eh.getId())
                .userId(eh.getUser().getId())
                .userName(eh.getUser().getName())
                .userEmail(eh.getUser().getEmail())
                .examId(eh.getExam().getId())
                .examTitle(eh.getExam().getTitle())
                .score(eh.getScore())
                .correctNumber(eh.getCorrectNumber())
                .wrongNumber(eh.getWrongNumber())
                .skipNumber(eh.getSkipNumber())
                .startTime(eh.getStartTime())
                .finishTime(eh.getFinishTime())
                .createdAt(eh.getCreatedAt())
                .build();
    }
}
