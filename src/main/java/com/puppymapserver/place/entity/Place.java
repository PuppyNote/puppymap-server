package com.puppymapserver.place.entity;

import com.puppymapserver.favorite.entity.Favorite;
import com.puppymapserver.global.BaseTimeEntity;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.review.entity.Review;
import com.puppymapserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Enumerated(EnumType.STRING)
    private PlaceStatus status;

    private String rejectionReason;

    private Boolean largeDogAvailable;

    private Boolean parkingAvailable;

    private Boolean offLeashAvailable;

    @Column(nullable = false)
    private long likeCount = 0;

    @Column(nullable = false, length = 1)
    private String adminYn = "N";

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @Builder
    private Place(User user, String title, String content, Double latitude, Double longitude,
                  PlaceCategory category, Boolean largeDogAvailable, Boolean parkingAvailable,
                  Boolean offLeashAvailable) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.status = PlaceStatus.PENDING;
        this.largeDogAvailable = largeDogAvailable;
        this.parkingAvailable = parkingAvailable;
        this.offLeashAvailable = offLeashAvailable;
    }

    public void update(String title, String content, PlaceCategory category,
                       Boolean largeDogAvailable, Boolean parkingAvailable, Boolean offLeashAvailable) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.largeDogAvailable = largeDogAvailable;
        this.parkingAvailable = parkingAvailable;
        this.offLeashAvailable = offLeashAvailable;
    }

    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new PuppyMapException("본인의 제보만 수정/삭제할 수 있습니다.");
        }
    }

    public void approve() {
        this.status = PlaceStatus.APPROVED;
        this.adminYn = "Y";
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = PlaceStatus.REJECTED;
        this.adminYn = "N";
        this.rejectionReason = reason;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
