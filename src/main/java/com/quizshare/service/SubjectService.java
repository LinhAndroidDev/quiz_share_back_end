package com.quizshare.service;

import com.quizshare.dto.response.SubjectResult;
import com.quizshare.entity.Subject;
import com.quizshare.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectResult> searchSubjects(Long departmentId, String keyword) {
        List<Subject> subjects = subjectRepository.searchSubjects(departmentId, keyword);
        return subjects.stream()
                .map(s -> SubjectResult.builder()
                        .id(s.getId())
                        .title(s.getTitle())
                        .description(s.getDescription())
                        .image(s.getImage())
                        .countExam(subjectRepository.countExamsBySubjectId(s.getId()))
                        .departmentId(s.getDepartment().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
