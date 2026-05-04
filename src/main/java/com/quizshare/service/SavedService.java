package com.quizshare.service;

import com.quizshare.dto.response.DepartmentResult;
import com.quizshare.dto.response.SaveExamResult;
import com.quizshare.dto.response.SavedExamListResult;
import com.quizshare.dto.response.SavedSubjectItem;
import com.quizshare.entity.*;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedService {

    private final SavedExamRepository savedExamRepository;
    private final SavedDepartmentRepository savedDepartmentRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional
    public SaveExamResult toggleSaveExam(Long userId, Long examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        Optional<SavedExam> existing = savedExamRepository.findByUserIdAndExamId(userId, examId);

        if (existing.isPresent()) {
            savedExamRepository.delete(existing.get());
            exam.setSavedNum(Math.max(0, exam.getSavedNum() - 1));
            examRepository.save(exam);
            return SaveExamResult.builder()
                    .userId(userId)
                    .examId(examId)
                    .createAt(null)
                    .build();
        } else {
            SavedExam savedExam = SavedExam.builder()
                    .user(user)
                    .exam(exam)
                    .build();
            savedExam = savedExamRepository.save(savedExam);
            exam.setSavedNum(exam.getSavedNum() + 1);
            examRepository.save(exam);
            return SaveExamResult.builder()
                    .userId(userId)
                    .examId(examId)
                    .createAt(savedExam.getCreatedAt().format(FORMATTER))
                    .build();
        }
    }

    public List<DepartmentResult> getSavedDepartments(Long userId) {
        List<SavedDepartment> savedDepts = savedDepartmentRepository.findByUserId(userId);
        return savedDepts.stream()
                .map(sd -> DepartmentResult.builder()
                        .id(sd.getDepartment().getId())
                        .title(sd.getDepartment().getTitle())
                        .description(sd.getDepartment().getDescription())
                        .image(sd.getDepartment().getImage())
                        .build())
                .collect(Collectors.toList());
    }

    public List<SavedSubjectItem> getSavedSubjects(Long userId, Long departmentId) {
        List<Subject> subjects = subjectRepository.findByDepartmentId(departmentId);
        return subjects.stream()
                .map(s -> SavedSubjectItem.builder()
                        .id(s.getId())
                        .title(s.getTitle())
                        .image(s.getImage())
                        .exemNumber(subjectRepository.countExamsBySubjectId(s.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public SavedExamListResult getSavedExams(Long userId, Long subjectId, String type) {
        List<SavedExam> savedExams = savedExamRepository.findByUserIdAndSubjectId(userId, subjectId);

        List<SavedExamListResult.SavedExamItem> examItems = savedExams.stream()
                .map(se -> {
                    Exam exam = se.getExam();
                    return SavedExamListResult.SavedExamItem.builder()
                            .id(exam.getId())
                            .title(exam.getTitle())
                            .image(exam.getImage())
                            .time(exam.getTime())
                            .number(exam.getNumber())
                            .savedNum(exam.getSavedNum())
                            .status(exam.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());

        String departmentTitle = null;
        String subjectTitle = null;
        Long departmentId = null;

        if (subjectId != null) {
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            if (subject != null) {
                subjectTitle = subject.getTitle();
                departmentId = subject.getDepartment().getId();
                departmentTitle = subject.getDepartment().getTitle();
            }
        }

        return SavedExamListResult.builder()
                .departmentId(departmentId)
                .departmentTitle(departmentTitle)
                .subjectTitle(subjectTitle)
                .examList(examItems)
                .build();
    }
}
