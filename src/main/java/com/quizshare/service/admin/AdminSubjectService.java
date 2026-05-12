package com.quizshare.service.admin;

import com.quizshare.dto.request.admin.CreateSubjectRequest;
import com.quizshare.dto.request.admin.UpdateSubjectRequest;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.AdminSubjectItem;
import com.quizshare.dto.response.admin.CreatedIdResult;
import com.quizshare.entity.Department;
import com.quizshare.entity.Subject;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.DepartmentRepository;
import com.quizshare.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;

    public AdminPageResult<AdminSubjectItem> getSubjects(int page, int size,
                                                         String keyword, Long departmentId) {
        Page<Subject> subjectPage = subjectRepository.searchSubjectsPage(
                departmentId, keyword, PageRequest.of(page, size));
        List<AdminSubjectItem> items = subjectPage.getContent().stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        return AdminPageResult.<AdminSubjectItem>builder()
                .total(subjectPage.getTotalElements())
                .page(page)
                .size(size)
                .items(items)
                .build();
    }

    public CreatedIdResult createSubject(CreateSubjectRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        Subject subject = Subject.builder()
                .department(department)
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage())
                .build();
        subject = subjectRepository.save(subject);
        return new CreatedIdResult(subject.getId(), subject.getTitle());
    }

    public boolean updateSubject(Long id, UpdateSubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        subject.setDepartment(department);
        subject.setTitle(request.getTitle());
        subject.setDescription(request.getDescription());
        if (request.getImage() != null) {
            subject.setImage(request.getImage());
        }
        subjectRepository.save(subject);
        return true;
    }

    public boolean deleteSubject(Long id) {
        subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        long examCount = subjectRepository.countExamsBySubjectId(id);
        if (examCount > 0) {
            throw new AppException(ErrorCode.SUBJECT_HAS_EXAMS,
                    "Không thể xóa: môn học còn " + examCount + " đề thi");
        }
        subjectRepository.deleteById(id);
        return true;
    }

    private AdminSubjectItem toItem(Subject s) {
        return AdminSubjectItem.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .image(s.getImage())
                .departmentId(s.getDepartment().getId())
                .departmentTitle(s.getDepartment().getTitle())
                .examCount(subjectRepository.countExamsBySubjectId(s.getId()))
                .createdAt(s.getCreatedAt())
                .build();
    }
}
