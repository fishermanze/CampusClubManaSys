package com.campusclub.user.repository;

import com.campusclub.user.entity.User;
import com.campusclub.user.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    /**
     * 查找用户之间的关注关系
     */
    Optional<UserFollow> findByFollower_IdAndFollowed_Id(Long followerId, Long followedId);

    /**
     * 查找用户关注的所有人
     */
    List<UserFollow> findByFollower_Id(Long followerId);

    /**
     * 查找关注该用户的所有人
     */
    List<UserFollow> findByFollowed_Id(Long followedId);

    /**
     * 统计用户关注的人数
     */
    long countByFollower_Id(Long followerId);

    /**
     * 统计关注该用户的人数
     */
    long countByFollowed_Id(Long followedId);

    /**
     * 检查用户是否关注了指定用户
     */
    boolean existsByFollower_IdAndFollowed_Id(Long followerId, Long followedId);

    /**
     * 查找两个用户之间的相互关注关系
     */
    @Query("SELECT COUNT(*) FROM UserFollow f1, UserFollow f2 " +
            "WHERE f1.follower.id = :userId1 AND f1.followed.id = :userId2 " +
            "AND f2.follower.id = :userId2 AND f2.followed.id = :userId1")
    int countMutualFollow(Long userId1, Long userId2);

    /**
     * 查找用户的共同关注
     */
    @Query("SELECT uf.followed FROM UserFollow uf " +
            "WHERE uf.follower.id = :userId1 AND uf.followed.id IN " +
            "(SELECT uf2.followed.id FROM UserFollow uf2 WHERE uf2.follower.id = :userId2)")
    List<User> findCommonFollowings(Long userId1, Long userId2);
}