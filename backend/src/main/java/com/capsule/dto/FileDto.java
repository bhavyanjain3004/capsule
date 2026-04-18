package com.capsule.dto;


public class FileDto {
    private String fileUrl;
    private String fileType;

    public FileDto() {}

    public FileDto(String fileUrl, String fileType) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
