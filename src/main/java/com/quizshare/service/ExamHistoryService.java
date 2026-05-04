package com.quizshare.service;

import com.quizshare.dto.request.GetExamHistoryListRequest;
import com.quizshare.dto.request.SubmitExamRequest;
import com.quizshare.dto.response.*;
import com.quizshare.entity.*;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamHistoryService {

    private final ExamHistoryRepository examHistoryRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional
    public SubmitExamResult submitExam(SubmitExamRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        Map<String, Long> answerList = request.getAnswerList() != null
                ? request.getAnswerList() : new HashMap<>();

        int correct = 0, wrong = 0, skip = 0;
        List<Question> questions = questionRepository.findByExamIdOrderByQuestionSort(exam.getId());

        ExamHistory history = ExamHistory.builder()
                .user(user)
                .exam(exam)
                .startTime(parseDateTime(request.getStartTime()))
                .finishTime(parseDateTime(request.getFinishTime()))
                .build();
        history = examHistoryRepository.save(history);

        for (Question question : questions) {
            Long answerId = answerList.get(String.valueOf(question.getId()));
            Answer selectedAnswer = null;

            if (answerId != null) {
                selectedAnswer = answerRepository.findById(answerId).orElse(null);
                if (selectedAnswer != null && selectedAnswer.getType() == 1) {
                    correct++;
                } else {
                    wrong++;
                }
            } else {
                skip++;
            }

            ExamResult result = ExamResult.builder()
                    .examHistory(history)
                    .question(question)
                    .answer(selectedAnswer)
                    .build();
            examResultRepository.save(result);
        }

        BigDecimal score = questions.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(correct * 10.0 / questions.size())
                .setScale(2, RoundingMode.HALF_UP);

        history.setScore(score);
        history.setCorrectNumber(correct);
        history.setWrongNumber(wrong);
        history.setSkipNumber(skip);
        history = examHistoryRepository.save(history);

        return SubmitExamResult.builder()
                .examHistoryId(history.getId())
                .examId(exam.getId())
                .userId(user.getId())
                .score(score)
                .correctNumber(correct)
                .wrongNumber(wrong)
                .skipNumber(skip)
                .startTime(request.getStartTime())
                .finishTime(request.getFinishTime())
                .createAt(formatDateTime(history.getCreatedAt()))
                .updateAt(formatDateTime(history.getUpdatedAt()))
                .examResult(answerList)
                .build();
    }

    public List<ExamHistoryItem> getExamHistoryList(GetExamHistoryListRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getOffset() / request.getLimit(), request.getLimit());
        List<ExamHistory> histories = examHistoryRepository.findByUserId(
                request.getUserId(), pageRequest);

        return histories.stream()
                .map(h -> ExamHistoryItem.builder()
                        .examHistoryId(h.getId())
                        .title(h.getExam().getTitle())
                        .number(h.getExam().getNumber())
                        .userCreate(h.getExam().getAuthor().getName())
                        .image(h.getExam().getImage())
                        .score(h.getScore())
                        .build())
                .collect(Collectors.toList());
    }

    public ExamHistoryDetailResult getExamHistoryDetail(Long examHistoryId) {
        ExamHistory history = examHistoryRepository.findById(examHistoryId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_HISTORY_NOT_FOUND));

        Exam exam = history.getExam();
        User author = exam.getAuthor();

        return ExamHistoryDetailResult.builder()
                .id(history.getId())
                .examTitle(exam.getTitle())
                .description(exam.getDescription())
                .subjectTitle(exam.getSubject().getTitle())
                .userName(author.getName())
                .userId(author.getId())
                .userAvatar(author.getAvatar())
                .time(exam.getTime())
                .number(exam.getNumber())
                .build();
    }

    public ExamResultDetail getExamResult(Long examHistoryId) {
        ExamHistory history = examHistoryRepository.findById(examHistoryId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_HISTORY_NOT_FOUND));

        List<ExamResult> results = examResultRepository.findByExamHistoryId(examHistoryId);
        Map<String, Long> examResultMap = new HashMap<>();
        for (ExamResult r : results) {
            examResultMap.put(
                    String.valueOf(r.getQuestion().getId()),
                    r.getAnswer() != null ? r.getAnswer().getId() : null);
        }

        return ExamResultDetail.builder()
                .id(history.getId())
                .examId(history.getExam().getId())
                .userId(history.getUser().getId())
                .score(history.getScore())
                .correctNumber(history.getCorrectNumber())
                .wrongNumber(history.getWrongNumber())
                .skipNumber(history.getSkipNumber())
                .startTime(formatDateTime(history.getStartTime()))
                .finishTime(formatDateTime(history.getFinishTime()))
                .examResult(examResultMap)
                .createAt(formatDateTime(history.getCreatedAt()))
                .updateAt(formatDateTime(history.getUpdatedAt()))
                .deleteAt(null)
                .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATETIME_FORMATTER);
    }
}
