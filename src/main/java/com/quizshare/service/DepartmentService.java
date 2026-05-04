package com.quizshare.service;

import com.quizshare.dto.response.DepartmentInfoResult;
import com.quizshare.dto.response.DepartmentResult;
import com.quizshare.dto.response.SubjectResult;
import com.quizshare.entity.Department;
import com.quizshare.entity.Subject;
import com.quizshare.repository.DepartmentRepository;
import com.quizshare.repository.ExamRepository;
import com.quizshare.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;

    public List<DepartmentResult> getDepartmentList(String keyword) {
        List<Department> departments = departmentRepository.searchByKeyword(keyword);
        return departments.stream()
                .map(this::mapToDepartmentResult)
                .collect(Collectors.toList());
    }

    public List<DepartmentInfoResult> listDepartmentInfo() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(dept -> {
                    List<Subject> subjects = subjectRepository.findByDepartmentId(dept.getId());
                    long examNum = examRepository.countExamsByDepartmentId(dept.getId());

                    List<SubjectResult> subjectResults = subjects.stream()
                            .map(s -> SubjectResult.builder()
                                    .id(s.getId())
                                    .title(s.getTitle())
                                    .image(s.getImage())
                                    .description(s.getDescription())
                                    .departmentId(dept.getId())
                                    .build())
                            .collect(Collectors.toList());

                    return DepartmentInfoResult.builder()
                            .id(dept.getId())
                            .title(dept.getTitle())
                            .examNum(examNum)
                            .subjects(subjectResults)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private DepartmentResult mapToDepartmentResult(Department dept) {
        return DepartmentResult.builder()
                .id(dept.getId())
                .title(dept.getTitle())
                .description(dept.getDescription())
                .image(dept.getImage())
                .build();
    }
}
