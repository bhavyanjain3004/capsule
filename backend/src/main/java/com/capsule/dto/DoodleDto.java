package com.capsule.dto;


public class DoodleDto {
    private Long id;
    private String type;
    private String stickerId;
    private String svgData;
    private Double positionX;
    private Double positionY;
    private Double scale;
    private Double rotation;

    public DoodleDto() {}

    public DoodleDto(Long id, String type, String stickerId, String svgData, 
                     Double positionX, Double positionY, Double scale, Double rotation) {
        this.id = id;
        this.type = type;
        this.stickerId = stickerId;
        this.svgData = svgData;
        this.positionX = positionX;
        this.positionY = positionY;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStickerId() { return stickerId; }
    public void setStickerId(String stickerId) { this.stickerId = stickerId; }
    public String getSvgData() { return svgData; }
    public void setSvgData(String svgData) { this.svgData = svgData; }
    public Double getPositionX() { return positionX; }
    public void setPositionX(Double positionX) { this.positionX = positionX; }
    public Double getPositionY() { return positionY; }
    public void setPositionY(Double positionY) { this.positionY = positionY; }
    public Double getScale() { return scale; }
    public void setScale(Double scale) { this.scale = scale; }
    public Double getRotation() { return rotation; }
    public void setRotation(Double rotation) { this.rotation = rotation; }
}
