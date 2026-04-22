package com.puppymapserver.like.entity;

import com.puppymapserver.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"place_id", "user_id"})
})
public class PlaceLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static PlaceLike of(Long placeId, Long userId) {
        PlaceLike like = new PlaceLike();
        like.placeId = placeId;
        like.userId = userId;
        return like;
    }
}
