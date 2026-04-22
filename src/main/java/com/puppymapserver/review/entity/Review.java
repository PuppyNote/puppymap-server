package com.puppymapserver.review.entity;

import com.puppymapserver.global.BaseTimeEntity;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rating;

    private String comment;

    @Builder
    private Review(Place place, User user, Integer rating, String comment) {
        this.place = place;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new PuppyMapException("본인의 리뷰만 수정/삭제할 수 있습니다.");
        }
    }

    public void update(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
