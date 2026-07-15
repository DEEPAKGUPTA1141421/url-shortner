package com.example.URLSHORTNER.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.URLSHORTNER.entity.UrlMapping;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Optional<UrlMapping> findByUrlHashAndCustomAliasFalse(String urlHash);

    @Query(value = "SELECT nextval('url_mapping_id_seq')", nativeQuery = true)
    Long nextId();
}
