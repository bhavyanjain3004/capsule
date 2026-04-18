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

    public VerifyResponse(String content, List<FileDto> files, List<DoodleDto> doodles, String backgroundTexture, String aiReflection) {
        this.content = content;
        this.files = files;
        this.doodles = doodles;
        this.backgroundTexture = backgroundTexture;
        this.aiReflection = aiReflection;
    }
}
