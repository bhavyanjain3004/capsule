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
}
