package com.quizshare.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartDataItem {
    private String label;
    private long count;
}
