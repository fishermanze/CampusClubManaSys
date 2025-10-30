package com.example.ccms.repository;

import com.example.ccms.entity.Club;
import com.example.ccms.enums.RecruitStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    /**
     * 根据创建人ID查询其创建的所有社团
     * @param creatorId 社团创建人ID（用户ID）
     * @return 社团列表
     */
    List<Club> findByCreatorId(Long creatorId);

    /**
     * 校验社团名称是否已存在（用于创建社团时去重）
     * @param name 社团名称
     * @return 是否存在（true=已存在）
     */
    boolean existsByName(String name);

    /**
     * 根据招新状态查询社团（如查询所有开放招新的社团）
     * @param recruitStatus 招新状态（OPEN/CLOSED）
     * @return 符合条件的社团列表
     */
    List<Club> findByRecruitStatus(RecruitStatusEnum recruitStatus);

    /**
     * 根据ID和创建人ID查询社团（用于权限校验，确保只有创建人能修改）
     * @param id 社团ID
     * @param creatorId 创建人ID
     * @return 社团信息（Optional包装，避免空指针）
     */
    Optional<Club> findByIdAndCreatorId(Long id, Long creatorId);
}