package com.capsule.dto;


public class DoodleResponse {
    private Long doodleId;

    public DoodleResponse() {}

    public DoodleResponse(Long doodleId) {
        this.doodleId = doodleId;
    }

    public Long getDoodleId() { return doodleId; }
    public void setDoodleId(Long doodleId) { this.doodleId = doodleId; }
}
