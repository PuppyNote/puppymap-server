package com.puppymapserver.place.entity;

import com.puppymapserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    private String imageUrl;

    private Integer sortOrder;

    @Builder
    private PlaceImage(Place place, String imageUrl, Integer sortOrder) {
        this.place = place;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
