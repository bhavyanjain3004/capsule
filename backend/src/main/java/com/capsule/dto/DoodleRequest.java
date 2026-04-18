package com.capsule.dto;

public class DoodleRequest {
    private String type;
    private String stickerId;
    private String svgData;
    private Double positionX;
    private Double positionY;
    private Double scale;
    private Double rotation;

    public DoodleRequest() {}

    public DoodleRequest(String type, String stickerId, String svgData, 
                        Double positionX, Double positionY, Double scale, Double rotation) {
        this.type = type;
        this.stickerId = stickerId;
        this.svgData = svgData;
        this.positionX = positionX;
        this.positionY = positionY;
        this.scale = scale;
        this.rotation = rotation;
    }

    public String getType() { return type; }
    public String getStickerId() { return stickerId; }
    public String getSvgData() { return svgData; }
    public Double getPositionX() { return positionX; }
    public Double getPositionY() { return positionY; }
    public Double getScale() { return scale; }
    public Double getRotation() { return rotation; }
}
