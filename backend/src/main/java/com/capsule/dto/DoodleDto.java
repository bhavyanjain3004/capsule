package com.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoodleDto {
    private Long id;
    private String type;
    private String stickerId;
    private String svgData;
    private Double positionX;
    private Double positionY;
    private Double scale;
    private Double rotation;

    public DoodleDto(Long id, String type, String stickerId, String svgData, Double positionX, Double positionY, Double scale, Double rotation) {
        this.id = id;
        this.type = type;
        this.stickerId = stickerId;
        this.svgData = svgData;
        this.positionX = positionX;
        this.positionY = positionY;
        this.scale = scale;
        this.rotation = rotation;
    }
}
