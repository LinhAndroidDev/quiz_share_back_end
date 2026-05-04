package com.quizshare.service;

import com.quizshare.dto.request.CreateExamRequest;
import com.quizshare.dto.request.ListExamRequest;
import com.quizshare.dto.response.*;
import com.quizshare.entity.*;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ListExamResult listExams(ListExamRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        Long authorId = "MY".equalsIgnoreCase(request.getType()) ? request.getUserId() : null;
        List<Exam> exams = examRepository.findBySubjectIdAndAuthorId(request.getSubjectId(), authorId);

        List<ExamItem> examItems = exams.stream()
                .map(this::mapToExamItem)
                .collect(Collectors.toList());

        Department dept = subject.getDepartment();
        return ListExamResult.builder()
                .id(subject.getId())
                .title(subject.getTitle())
                .description(subject.getDescription())
                .departmentId(dept.getId())
                .departmentTitle(dept.getTitle())
                .departmentDescription(dept.getDescription())
                .listExam(examItems)
                .build();
    }

    public ExamQuestionResult getExamQuestions(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        List<Question> questions = questionRepository.findByExamIdOrderByQuestionSort(examId);

        List<QuestionDto> questionDtos = questions.stream()
                .map(q -> {
                    List<Answer> answers = answerRepository.findByQuestionIdOrderBySort(q.getId());
                    List<AnswerDto> answerDtos = answers.stream()
                            .map(a -> AnswerDto.builder()
                                    .answerId(a.getId())
                                    .content(a.getContent())
                                    .image(a.getImage())
                                    .sort(a.getSort())
                                    .type(a.getType())
                                    .build())
                            .collect(Collectors.toList());
                    return QuestionDto.builder()
                            .questionId(q.getId())
                            .questionTitle(q.getQuestionTitle())
                            .questionImage(q.getQuestionImage())
                            .questionLevel(q.getQuestionLevel().name())
                            .questionSort(q.getQuestionSort())
                            .answerList(answerDtos)
                            .build();
                })
                .collect(Collectors.toList());

        return ExamQuestionResult.builder()
                .id(exam.getId())
                .examQuestionList(questionDtos)
                .build();
    }

    @Transactional
    public String createExam(CreateExamRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        User author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Exam exam = Exam.builder()
                .subject(subject)
                .author(author)
                .title(request.getTitle())
                .description(request.getDescription())
                .time(request.getTime())
                .number(request.getNumber())
                .status(parseExamStatus(request.getStatus()))
                .build();

        exam = examRepository.save(exam);

        if (request.getQuestionExamList() != null) {
            for (CreateExamRequest.QuestionRequest qReq : request.getQuestionExamList()) {
                Question question = Question.builder()
                        .exam(exam)
                        .questionTitle(qReq.getQuestionTitle())
                        .questionImage(qReq.getQuestionImageUrl() != null
                                ? qReq.getQuestionImageUrl() : qReq.getQuestionImage())
                        .questionLevel(parseQuestionLevel(qReq.getQuestionLevel()))
                        .questionSort(qReq.getQuestionSort())
                        .build();

                question = questionRepository.save(question);

                if (qReq.getAnswerList() != null) {
                    for (CreateExamRequest.AnswerRequest aReq : qReq.getAnswerList()) {
                        Answer answer = Answer.builder()
                                .question(question)
                                .content(aReq.getContent())
                                .image(aReq.getImage())
                                .sort(aReq.getSort())
                                .type(aReq.getType())
                                .build();
                        answerRepository.save(answer);
                    }
                }
            }
        }

        return "Exam created successfully";
    }

    private ExamItem mapToExamItem(Exam exam) {
        return ExamItem.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .image(exam.getImage())
                .time(exam.getTime())
                .number(exam.getNumber())
                .savedNum(exam.getSavedNum())
                .status(exam.getStatus().name())
                .authorId(exam.getAuthor().getId())
                .authorName(exam.getAuthor().getName())
                .authorEmail(exam.getAuthor().getEmail())
                .build();
    }

    private Exam.ExamStatus parseExamStatus(String status) {
        try {
            return Exam.ExamStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return Exam.ExamStatus.PUBLIC;
        }
    }

    private Question.QuestionLevel parseQuestionLevel(String level) {
        try {
            return Question.QuestionLevel.valueOf(level.toUpperCase());
        } catch (Exception e) {
            return Question.QuestionLevel.MEDIUM;
        }
    }
}
