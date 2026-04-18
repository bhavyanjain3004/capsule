package com.capsule.dto;

import com.capsule.model.CapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewResponse {
    private CapsuleStatus status;
    private LocalDateTime unlocksAt;
    private String title;
}
