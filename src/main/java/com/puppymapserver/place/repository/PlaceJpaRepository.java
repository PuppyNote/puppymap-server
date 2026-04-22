package com.puppymapserver.place.repository;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceJpaRepository extends JpaRepository<Place, Long> {

    @Query("select p from Place p where p.status = 'APPROVED' and p.deletedAt is null")
    List<Place> findAllApproved();

    @Query("select p from Place p where p.id = :id and p.status = 'APPROVED' and p.deletedAt is null")
    Optional<Place> findApprovedById(@Param("id") Long id);

    @Query("select p from Place p where p.user.id = :userId and p.deletedAt is null order by p.createdDate desc")
    List<Place> findAllByUserId(@Param("userId") Long userId);

    @Query("select p from Place p where p.status = :status and p.deletedAt is null order by p.createdDate desc")
    List<Place> findAllByStatus(@Param("status") PlaceStatus status);

    @Modifying
    @Query("UPDATE Place p SET p.likeCount = (SELECT COUNT(pl) FROM PlaceLike pl WHERE pl.placeId = p.id) WHERE p.id = :placeId")
    void updateLikeCount(@Param("placeId") Long placeId);
}
