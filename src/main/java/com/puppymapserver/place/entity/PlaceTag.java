package com.puppymapserver.place.entity;

import com.puppymapserver.global.BaseTimeEntity;
import com.puppymapserver.place.entity.enums.TagType;
import com.puppymapserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private Boolean isActive;

    @Builder
    private PlaceTag(Place place, User user, TagType tagType) {
        this.place = place;
        this.user = user;
        this.tagType = tagType;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
