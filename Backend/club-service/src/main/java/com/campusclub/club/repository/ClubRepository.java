package com.campusclub.club.repository;

import com.campusclub.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByStatus(Integer status);
    List<Club> findByCategory(String category);
    List<Club> findByNameContaining(String name);
    List<Club> findByLeaderId(Long leaderId);
}