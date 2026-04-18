package com.capsule.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_doodle")
@Data
@Builder
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

    private Double rotation;
}
