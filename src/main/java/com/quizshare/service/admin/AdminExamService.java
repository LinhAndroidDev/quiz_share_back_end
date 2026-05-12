package com.quizshare.service.admin;

import com.quizshare.dto.response.admin.AdminExamDetailResult;
import com.quizshare.dto.response.admin.AdminExamItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.entity.Answer;
import com.quizshare.entity.Exam;
import com.quizshare.entity.Question;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.AnswerRepository;
import com.quizshare.repository.ExamRepository;
import com.quizshare.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public AdminPageResult<AdminExamItem> getExams(int page, int size,
                                                   String keyword, String status,
                                                   Long subjectId, Long departmentId) {
        Exam.ExamStatus statusEnum = parseStatus(status);
        Page<Exam> examPage = examRepository.searchExamsAdmin(
                keyword, statusEnum, subjectId, departmentId, PageRequest.of(page, size));
        List<AdminExamItem> items = examPage.getContent().stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        return AdminPageResult.<AdminExamItem>builder()
                .total(examPage.getTotalElements())
                .page(page)
                .size(size)
                .items(items)
                .build();
    }

    public AdminExamDetailResult getExamDetail(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        List<Question> questions = questionRepository.findByExamIdOrderByQuestionSort(examId);
        List<AdminExamDetailResult.QuestionItem> questionItems = questions.stream()
                .map(q -> {
                    List<Answer> answers = answerRepository.findByQuestionIdOrderBySort(q.getId());
                    List<AdminExamDetailResult.AnswerItem> answerItems = answers.stream()
                            .map(a -> AdminExamDetailResult.AnswerItem.builder()
                                    .id(a.getId())
                                    .content(a.getContent())
                                    .image(a.getImage())
                                    .sort(a.getSort())
                                    .type(a.getType())
                                    .build())
                            .collect(Collectors.toList());
                    return AdminExamDetailResult.QuestionItem.builder()
                            .id(q.getId())
                            .questionTitle(q.getQuestionTitle())
                            .questionImage(q.getQuestionImage())
                            .questionLevel(q.getQuestionLevel().name())
                            .questionSort(q.getQuestionSort())
                            .answers(answerItems)
                            .build();
                })
                .collect(Collectors.toList());

        return AdminExamDetailResult.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .image(exam.getImage())
                .subjectTitle(exam.getSubject().getTitle())
                .departmentTitle(exam.getSubject().getDepartment().getTitle())
                .authorName(exam.getAuthor().getName())
                .time(exam.getTime())
                .number(exam.getNumber())
                .status(exam.getStatus())
                .createdAt(exam.getCreatedAt())
                .questions(questionItems)
                .build();
    }

    public boolean updateStatus(Long examId, Exam.ExamStatus status) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        exam.setStatus(status);
        examRepository.save(exam);
        return true;
    }

    @Transactional
    public boolean deleteExam(Long examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        examRepository.deleteById(examId);
        return true;
    }

    private AdminExamItem toItem(Exam e) {
        return AdminExamItem.builder()
                .id(e.getId())
                .title(e.getTitle())
                .image(e.getImage())
                .subjectId(e.getSubject().getId())
                .subjectTitle(e.getSubject().getTitle())
                .departmentId(e.getSubject().getDepartment().getId())
                .departmentTitle(e.getSubject().getDepartment().getTitle())
                .authorId(e.getAuthor().getId())
                .authorName(e.getAuthor().getName())
                .authorEmail(e.getAuthor().getEmail())
                .time(e.getTime())
                .number(e.getNumber())
                .savedNum(e.getSavedNum())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private Exam.ExamStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return Exam.ExamStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
