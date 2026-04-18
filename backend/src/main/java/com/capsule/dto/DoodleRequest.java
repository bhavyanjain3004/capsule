package com.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoodleRequest {
    private String type;
    private String stickerId;
    private String svgData;
    private Double positionX;
    private Double positionY;
    private Double scale;
    private Double rotation;

    public String getType() { return type; }
    public String getStickerId() { return stickerId; }
    public String getSvgData() { return svgData; }
    public Double getPositionX() { return positionX; }
    public Double getPositionY() { return positionY; }
    public Double getScale() { return scale; }
    public Double getRotation() { return rotation; }
}
