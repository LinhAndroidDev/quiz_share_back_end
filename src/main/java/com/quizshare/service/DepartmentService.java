package com.quizshare.service;

import com.quizshare.dto.response.DepartmentInfoResult;
import com.quizshare.dto.response.DepartmentResult;
import com.quizshare.dto.response.SubjectResult;
import com.quizshare.entity.Department;
import com.quizshare.entity.SavedDepartment;
import com.quizshare.entity.Subject;
import com.quizshare.repository.DepartmentRepository;
import com.quizshare.repository.ExamRepository;
import com.quizshare.repository.SavedDepartmentRepository;
import com.quizshare.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final SavedDepartmentRepository savedDepartmentRepository;

    public List<DepartmentResult> getDepartmentList(Long userId, String keyword) {
        List<SavedDepartment> savedList = savedDepartmentRepository.findByUserId(userId);
        Set<Long> distinctDeptIds = savedList.stream()
                .map(sd -> sd.getDepartment().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (distinctDeptIds.isEmpty()) {
            return List.of();
        }

        List<Department> departments = departmentRepository.searchByIdsAndKeyword(
                List.copyOf(distinctDeptIds), keyword);
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
