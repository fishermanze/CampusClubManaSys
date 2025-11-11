package com.campusclub.user.service.impl;

import com.campusclub.user.dto.UserDTO;
import com.campusclub.user.dto.UserProfileDTO;
import com.campusclub.user.dto.UserDetailDTO;
import com.campusclub.user.entity.User;
import com.campusclub.user.entity.UserProfile;
import com.campusclub.user.entity.UserFollow;
import com.campusclub.user.repository.UserRepository;
import com.campusclub.user.repository.UserProfileRepository;
import com.campusclub.user.repository.UserFollowRepository;
import com.campusclub.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private ModelMapper modelMapper;

    @jakarta.annotation.PostConstruct
    private void configureModelMapper() {
        // 忽略歧义，避免 userId 由多个路径匹配导致报错
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        // 显式指定 UserProfile -> UserProfileDTO 的 userId 映射来源
        try {
            modelMapper.typeMap(com.campusclub.user.entity.UserProfile.class,
                    com.campusclub.user.dto.UserProfileDTO.class)
                    .addMappings(m -> m.map(src -> src.getUser().getId(),
                            com.campusclub.user.dto.UserProfileDTO::setUserId));
        } catch (IllegalArgumentException ignored) {
            // typeMap 可能已存在，忽略
        }
    }

    @Value("${user-service.upload-path}")
    private String uploadPath;

    @Value("${user-service.default-avatar}")
    private String defaultAvatar;

    @Override
    public Page<UserDTO> getUserList(Pageable pageable, String keyword, String role, String status) {
        // 创建查询条件
        return userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("username"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("realName"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("phone"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("email"), "%" + keyword + "%")
                ));
            }

            if (role != null && !role.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public UserDetailDTO getUserDetailById(Long id, Long currentUserId) {
        // 获取用户基本信息
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取用户档案
        UserProfile profile = userProfileRepository.findByUser_Id(id).orElseGet(() -> {
            // 如果用户档案不存在，创建一个默认的
            UserProfile newProfile = new UserProfile();
            newProfile.setUser(user);
            newProfile.setAvatar(defaultAvatar);
            return userProfileRepository.save(newProfile);
        });

        // 转换为DTO
        UserDetailDTO detailDTO = modelMapper.map(user, UserDetailDTO.class);

        // 复制档案信息
        detailDTO.setStudentId(profile.getStudentId());
        detailDTO.setMajor(profile.getMajor());
        detailDTO.setGrade(profile.getGrade());
        detailDTO.setClassName(profile.getClassName());
        detailDTO.setGender(profile.getGender());
        detailDTO.setBirthDate(profile.getBirthDate());
        detailDTO.setHobbies(profile.getHobbies());
        detailDTO.setBio(profile.getBio());
        detailDTO.setAddress(profile.getAddress());
        detailDTO.setEmergencyContact(profile.getEmergencyContact());
        detailDTO.setEmergencyPhone(profile.getEmergencyPhone());
        detailDTO.setSocialMedia(profile.getSocialMedia());
        detailDTO.setPreferredClubTypes(profile.getPreferredClubTypes());
        detailDTO.setAvatar(profile.getAvatar() != null ? profile.getAvatar() : defaultAvatar);

        // 获取关注和粉丝数量
        detailDTO.setFollowingCount((int) userFollowRepository.countByFollower_Id(id));
        detailDTO.setFollowerCount((int) userFollowRepository.countByFollowed_Id(id));

        // 检查当前用户是否关注了该用户
        if (currentUserId != null && !currentUserId.equals(id)) {
            boolean isFollowing = userFollowRepository.existsByFollower_IdAndFollowed_Id(currentUserId, id);
            detailDTO.setFollowing(isFollowing);

            // 检查是否是互相关注
            if (isFollowing) {
                boolean isMutual = userFollowRepository.existsByFollower_IdAndFollowed_Id(id, currentUserId);
                detailDTO.setMutual(isMutual);
            }
        }

        return detailDTO;
    }

    @Override
    public UserDTO updateUserInfo(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新非敏感字段
        if (userDTO.getRealName() != null) {
            user.setRealName(userDTO.getRealName());
        }
        if (userDTO.getEmail() != null) {
            // 检查邮箱是否已被其他用户使用
            userRepository.findByEmail(userDTO.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(id)) {
                            throw new RuntimeException("邮箱已被使用");
                        }
                    });
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            // 检查手机号是否已被其他用户使用
            userRepository.findByPhone(userDTO.getPhone())
                    .ifPresent(u -> {
                        if (!u.getId().equals(id)) {
                            throw new RuntimeException("手机号已被使用");
                        }
                    });
            user.setPhone(userDTO.getPhone());
        }

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取或创建用户档案
        UserProfile profile = userProfileRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    // 初始化审计字段，避免DB非空约束报错
                    newProfile.setCreatedAt(new Date());
                    newProfile.setUpdatedAt(new Date());
                    return newProfile;
                });

        // 更新档案信息
        if (profileDTO.getStudentId() != null) {
            // 检查学号是否已被其他用户使用
            userProfileRepository.findByStudentId(profileDTO.getStudentId())
                    .ifPresent(p -> {
                        if (!p.getUser().getId().equals(userId)) {
                            throw new RuntimeException("学号已被使用");
                        }
                    });
            profile.setStudentId(profileDTO.getStudentId());
        }
        profile.setMajor(profileDTO.getMajor());
        profile.setGrade(profileDTO.getGrade());
        profile.setClassName(profileDTO.getClassName());
        profile.setGender(profileDTO.getGender());
        profile.setBirthDate(profileDTO.getBirthDate());
        profile.setHobbies(profileDTO.getHobbies());
        profile.setBio(profileDTO.getBio());
        profile.setAddress(profileDTO.getAddress());
        profile.setEmergencyContact(profileDTO.getEmergencyContact());
        profile.setEmergencyPhone(profileDTO.getEmergencyPhone());
        profile.setSocialMedia(profileDTO.getSocialMedia());
        profile.setPreferredClubTypes(profileDTO.getPreferredClubTypes());

        // 更新更新时间
        profile.setUpdatedAt(new Date());
        profile = userProfileRepository.save(profile);
        return modelMapper.map(profile, UserProfileDTO.class);
    }

    @Override
    public UserDTO updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(status);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片格式文件");
        }

        // 验证文件大小
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 创建上传目录
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Path uploadDir = Paths.get(uploadPath, dateDir);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("创建上传目录失败", e);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = userId + "_" + System.currentTimeMillis() + extension;
        Path filePath = uploadDir.resolve(filename);

        // 保存文件
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败", e);
        }

        // 更新用户头像路径
        UserProfile profile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("用户档案不存在"));

        // 构建访问路径（这里简化处理，实际应该配置静态资源访问路径）
        String avatarPath = "/uploads/user-avatars/" + dateDir + "/" + filename;
        profile.setAvatar(avatarPath);
        userProfileRepository.save(profile);

        return avatarPath;
    }

    @Override
    public boolean followUser(Long followerId, Long followedId) {
        // 不能关注自己
        if (followerId.equals(followedId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 验证用户是否存在
        userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("关注者不存在"));
        userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("被关注者不存在"));

        // 检查是否已经关注
        if (userFollowRepository.existsByFollower_IdAndFollowed_Id(followerId, followedId)) {
            return true; // 已经关注，返回成功
        }

        // 创建关注关系
        UserFollow follow = new UserFollow();
        follow.setFollower(userRepository.getOne(followerId));
        follow.setFollowed(userRepository.getOne(followedId));

        // 检查是否是互相关注
        if (userFollowRepository.existsByFollower_IdAndFollowed_Id(followedId, followerId)) {
            follow.setIsMutual(true);
            // 更新对方的关注关系为互相关注
            UserFollow reverseFollow = userFollowRepository.findByFollower_IdAndFollowed_Id(followedId, followerId)
                    .orElseThrow(() -> new RuntimeException("找不到反向关注关系"));
            reverseFollow.setIsMutual(true);
            userFollowRepository.save(reverseFollow);
        }

        userFollowRepository.save(follow);
        return true;
    }

    @Override
    public boolean unfollowUser(Long followerId, Long followedId) {
        // 查找关注关系
        Optional<UserFollow> followOptional = userFollowRepository.findByFollower_IdAndFollowed_Id(followerId, followedId);
        if (!followOptional.isPresent()) {
            return true; // 本来就没有关注，返回成功
        }

        UserFollow follow = followOptional.get();

        // 如果是互相关注，更新对方的关注关系
        if (follow.getIsMutual()) {
            userFollowRepository.findByFollower_IdAndFollowed_Id(followedId, followerId)
                    .ifPresent(reverseFollow -> {
                        reverseFollow.setIsMutual(false);
                        userFollowRepository.save(reverseFollow);
                    });
        }

        // 删除关注关系
        userFollowRepository.delete(follow);
        return true;
    }

    @Override
    public Page<UserDTO> getUserFollowings(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查询用户关注的人
        List<UserFollow> follows = userFollowRepository.findByFollower_Id(userId);
        List<Long> followedIds = follows.stream()
                .map(f -> f.getFollowed().getId())
                .collect(Collectors.toList());

        // 分页查询用户信息
        if (followedIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return userRepository.findByIdIn(followedIds, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Page<UserDTO> getUserFollowers(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查询关注该用户的人
        List<UserFollow> follows = userFollowRepository.findByFollowed_Id(userId);
        List<Long> followerIds = follows.stream()
                .map(f -> f.getFollower().getId())
                .collect(Collectors.toList());

        // 分页查询用户信息
        if (followerIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return userRepository.findByIdIn(followerIds, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public boolean isFollowing(Long followerId, Long followedId) {
        return userFollowRepository.existsByFollower_IdAndFollowed_Id(followerId, followedId);
    }

    @Override
    public List<UserDTO> getCommonFollowings(Long userId1, Long userId2) {
        List<User> commonFollowings = userFollowRepository.findCommonFollowings(userId1, userId2);
        return commonFollowings.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateLoginInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setLastLogin(new Date());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);
    }
}