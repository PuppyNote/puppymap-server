package com.puppymapserver.review.repository;

import com.puppymapserver.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.place.id = :placeId order by r.createdDate desc")
    List<Review> findAllByPlaceId(@Param("placeId") Long placeId);
}
