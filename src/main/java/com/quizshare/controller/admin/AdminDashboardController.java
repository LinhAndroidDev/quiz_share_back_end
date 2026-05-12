package com.quizshare.controller.admin;

import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.ChartDataItem;
import com.quizshare.dto.response.admin.DashboardStatsResult;
import com.quizshare.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<DashboardStatsResult>> getStats() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getStats()));
    }

    @GetMapping("/exam-history-chart")
    public ResponseEntity<BaseResponse<List<ChartDataItem>>> getExamHistoryChart(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getExamHistoryChart(days)));
    }

    @GetMapping("/user-register-chart")
    public ResponseEntity<BaseResponse<List<ChartDataItem>>> getUserRegisterChart(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getUserRegisterChart(months)));
    }
}
