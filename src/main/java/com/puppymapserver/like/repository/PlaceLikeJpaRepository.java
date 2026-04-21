package com.puppymapserver.like.repository;

import com.puppymapserver.like.entity.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceLikeJpaRepository extends JpaRepository<PlaceLike, Long> {

    @Query("select pl from PlaceLike pl where pl.place.id = :placeId and pl.user.id = :userId")
    Optional<PlaceLike> findByPlaceIdAndUserId(@Param("placeId") Long placeId, @Param("userId") Long userId);

    @Query("select count(pl) from PlaceLike pl where pl.place.id = :placeId")
    long countByPlaceId(@Param("placeId") Long placeId);
}
