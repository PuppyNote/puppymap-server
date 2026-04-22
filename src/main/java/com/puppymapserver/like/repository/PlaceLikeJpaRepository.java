package com.puppymapserver.like.repository;

import com.puppymapserver.like.entity.PlaceLike;
import com.puppymapserver.like.repository.dto.PlaceLikeAggDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlaceLikeJpaRepository extends JpaRepository<PlaceLike, Long> {

    long countByPlaceId(Long placeId);

    Optional<PlaceLike> findByPlaceIdAndUserId(Long placeId, Long userId);

    @Modifying
    @Query("DELETE FROM PlaceLike pl WHERE pl.placeId = :placeId AND pl.userId IN :userIds")
    void deleteByPlaceIdAndUserIdIn(@Param("placeId") Long placeId, @Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query(value = "INSERT IGNORE INTO place_likes (place_id, user_id, created_date, updated_date) VALUES (:placeId, :userId, NOW(), NOW())", nativeQuery = true)
    void insertIgnore(@Param("placeId") Long placeId, @Param("userId") Long userId);

    @Query("""
            SELECT new com.puppymapserver.like.repository.dto.PlaceLikeAggDto(
                pl.placeId,
                COUNT(pl),
                SUM(CASE WHEN pl.userId = :userId THEN 1 ELSE 0 END)
            )
            FROM PlaceLike pl
            WHERE pl.placeId IN :placeIds
            GROUP BY pl.placeId
            """)
    List<PlaceLikeAggDto> findLikeAggByPlaceIds(@Param("placeIds") List<Long> placeIds, @Param("userId") Long userId);
}
