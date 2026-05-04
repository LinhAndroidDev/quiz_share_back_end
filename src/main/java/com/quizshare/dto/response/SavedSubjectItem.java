package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedSubjectItem {

    private Long id;
    private String title;
    private String image;

    /**
     * Kept as "exem_number" to match legacy client typo
     */
    @JsonProperty("exem_number")
    private long exemNumber;
}
