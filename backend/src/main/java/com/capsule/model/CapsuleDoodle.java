package com.capsule.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_doodle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleDoodle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Column(nullable = false)
    private String type; // 'sticker' or 'freehand'

    @Column(name = "sticker_id")
    private String stickerId;

    @Column(name = "svg_data", columnDefinition = "TEXT")
    private String svgData;

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;

    private Double scale;

    @Column(name = "rotation")
    private Double rotation;

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getStickerId() { return stickerId; }
    public String getSvgData() { return svgData; }
    public Double getPositionX() { return positionX; }
    public Double getPositionY() { return positionY; }
    public Double getScale() { return scale; }
    public Double getRotation() { return rotation; }

    public void setCapsule(Capsule capsule) { this.capsule = capsule; }
    public void setType(String type) { this.type = type; }
    public void setStickerId(String stickerId) { this.stickerId = stickerId; }
    public void setSvgData(String svgData) { this.svgData = svgData; }
    public void setPositionX(Double positionX) { this.positionX = positionX; }
    public void setPositionY(Double positionY) { this.positionY = positionY; }
    public void setScale(Double scale) { this.scale = scale; }
    public void setRotation(Double rotation) { this.rotation = rotation; }

    public static CapsuleDoodleBuilder builder() {
        return new CapsuleDoodleBuilder();
    }

    public static class CapsuleDoodleBuilder {
        private Capsule capsule;
        private String type;
        private String stickerId;
        private String svgData;
        private Double positionX;
        private Double positionY;
        private Double scale;
        private Double rotation;

        public CapsuleDoodleBuilder capsule(Capsule capsule) {
            this.capsule = capsule;
            return this;
        }

        public CapsuleDoodleBuilder type(String type) {
            this.type = type;
            return this;
        }

        public CapsuleDoodleBuilder stickerId(String stickerId) {
            this.stickerId = stickerId;
            return this;
        }

        public CapsuleDoodleBuilder svgData(String svgData) {
            this.svgData = svgData;
            return this;
        }

        public CapsuleDoodleBuilder positionX(Double positionX) {
            this.positionX = positionX;
            return this;
        }

        public CapsuleDoodleBuilder positionY(Double positionY) {
            this.positionY = positionY;
            return this;
        }

        public CapsuleDoodleBuilder scale(Double scale) {
            this.scale = scale;
            return this;
        }

        public CapsuleDoodleBuilder rotation(Double rotation) {
            this.rotation = rotation;
            return this;
        }

        public CapsuleDoodle build() {
            CapsuleDoodle doodle = new CapsuleDoodle();
            doodle.setCapsule(capsule);
            doodle.setType(type);
            doodle.setStickerId(stickerId);
            doodle.setSvgData(svgData);
            doodle.setPositionX(positionX);
            doodle.setPositionY(positionY);
            doodle.setScale(scale);
            doodle.setRotation(rotation);
            return doodle;
        }
    }
}
