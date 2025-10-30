package com.example.ccms.repository;

import com.example.ccms.entity.ClubImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubImageRepository extends JpaRepository<ClubImage, Long> {
    // 查询社团主图
    Optional<ClubImage> findFirstByClubIdAndIsMainTrue(Long clubId);
    // 查询社团所有图片（按排序升序）
    List<ClubImage> findByClubIdOrderBySortAsc(Long clubId);
    // 批量删除社团图片
    void deleteByClubId(Long clubId);
}