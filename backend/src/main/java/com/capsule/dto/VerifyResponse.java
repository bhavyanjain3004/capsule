package com.capsule.dto;

import java.util.List;

public class VerifyResponse {
    private String content;
    private List<FileDto> files;
    private List<DoodleDto> doodles;
    private String backgroundTexture;
    private String canvasJson;
    private String aiReflection;

    public VerifyResponse() {}

    public VerifyResponse(String content, List<FileDto> files, List<DoodleDto> doodles, 
                          String backgroundTexture, String canvasJson, String aiReflection) {
        this.content = content;
        this.files = files;
        this.doodles = doodles;
        this.backgroundTexture = backgroundTexture;
        this.canvasJson = canvasJson;
        this.aiReflection = aiReflection;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<FileDto> getFiles() { return files; }
    public void setFiles(List<FileDto> files) { this.files = files; }
    public List<DoodleDto> getDoodles() { return doodles; }
    public void setDoodles(List<DoodleDto> doodles) { this.doodles = doodles; }
    public String getBackgroundTexture() { return backgroundTexture; }
    public void setBackgroundTexture(String backgroundTexture) { this.backgroundTexture = backgroundTexture; }
    public String getCanvasJson() { return canvasJson; }
    public void setCanvasJson(String canvasJson) { this.canvasJson = canvasJson; }
    public String getAiReflection() { return aiReflection; }
    public void setAiReflection(String aiReflection) { this.aiReflection = aiReflection; }
}
