package com.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse {
    private String content;
    private List<FileDto> files;
    private List<DoodleDto> doodles;
    private String backgroundTexture;
    private String aiReflection;
}
