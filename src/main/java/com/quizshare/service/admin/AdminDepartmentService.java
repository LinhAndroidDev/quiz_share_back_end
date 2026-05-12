package com.quizshare.service.admin;

import com.quizshare.dto.request.admin.CreateDepartmentRequest;
import com.quizshare.dto.request.admin.UpdateDepartmentRequest;
import com.quizshare.dto.response.admin.AdminDepartmentItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.CreatedIdResult;
import com.quizshare.entity.Department;
import com.quizshare.entity.SavedDepartment;
import com.quizshare.entity.User;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.DepartmentRepository;
import com.quizshare.repository.SavedDepartmentRepository;
import com.quizshare.repository.SubjectRepository;
import com.quizshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final SavedDepartmentRepository savedDepartmentRepository;
    private final UserRepository userRepository;

    public AdminPageResult<AdminDepartmentItem> getDepartments(int page, int size, String keyword) {
        Page<Department> depPage = departmentRepository.searchByKeywordPage(keyword, PageRequest.of(page, size));
        List<AdminDepartmentItem> items = depPage.getContent().stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        return AdminPageResult.<AdminDepartmentItem>builder()
                .total(depPage.getTotalElements())
                .page(page)
                .size(size)
                .items(items)
                .build();
    }

    @Transactional
    public CreatedIdResult createDepartment(CreateDepartmentRequest request) {
        Department department = Department.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage())
                .build();
        department = departmentRepository.save(department);

        User admin = resolveCurrentAdmin();
        if (admin != null
                && !savedDepartmentRepository.existsByUserIdAndDepartmentId(admin.getId(), department.getId())) {
            SavedDepartment saved = SavedDepartment.builder()
                    .user(admin)
                    .department(department)
                    .build();
            savedDepartmentRepository.save(saved);
        }

        return new CreatedIdResult(department.getId(), department.getTitle());
    }

    public boolean updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        department.setTitle(request.getTitle());
        department.setDescription(request.getDescription());
        if (request.getImage() != null) {
            department.setImage(request.getImage());
        }
        departmentRepository.save(department);
        return true;
    }

    public boolean deleteDepartment(Long id) {
        departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        long subjectCount = subjectRepository.countByDepartmentId(id);
        if (subjectCount > 0) {
            throw new AppException(ErrorCode.DEPARTMENT_HAS_SUBJECTS,
                    "Không thể xóa: khoa còn " + subjectCount + " môn học");
        }
        departmentRepository.deleteById(id);
        return true;
    }

    private AdminDepartmentItem toItem(Department d) {
        return AdminDepartmentItem.builder()
                .id(d.getId())
                .title(d.getTitle())
                .description(d.getDescription())
                .image(d.getImage())
                .subjectCount(subjectRepository.countByDepartmentId(d.getId()))
                .createdAt(d.getCreatedAt())
                .build();
    }

    /**
     * Admin gọi API có JWT — lấy user hiện tại để gắn saved_departments.
     */
    private User resolveCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            return null;
        }
        return userRepository.findById(user.getId()).orElse(null);
    }
}
