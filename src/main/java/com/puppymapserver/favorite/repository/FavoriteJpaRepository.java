package com.puppymapserver.favorite.repository;

import com.puppymapserver.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteJpaRepository extends JpaRepository<Favorite, Long> {

    @Query("select f from Favorite f where f.place.id = :placeId and f.user.id = :userId")
    Optional<Favorite> findByPlaceIdAndUserId(@Param("placeId") Long placeId, @Param("userId") Long userId);

    @Query("select f from Favorite f where f.user.id = :userId order by f.createdDate desc")
    List<Favorite> findAllByUserId(@Param("userId") Long userId);
}
