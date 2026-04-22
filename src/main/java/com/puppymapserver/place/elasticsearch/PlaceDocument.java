package com.puppymapserver.place.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDateTime;

@Document(indexName = "places")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class PlaceDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Boolean)
    private Boolean largeDogAvailable;

    @Field(type = FieldType.Boolean)
    private Boolean parkingAvailable;

    @Field(type = FieldType.Boolean)
    private Boolean offLeashAvailable;

    @Field(type = FieldType.Date)
    private LocalDateTime createdDate;

    @Builder
    private PlaceDocument(String id, String title, String content, GeoPoint location,
                          String category, Boolean largeDogAvailable, Boolean parkingAvailable,
                          Boolean offLeashAvailable, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.location = location;
        this.category = category;
        this.largeDogAvailable = largeDogAvailable;
        this.parkingAvailable = parkingAvailable;
        this.offLeashAvailable = offLeashAvailable;
        this.createdDate = createdDate;
    }
}
