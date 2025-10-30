package com.example.ccms.service.impl;

import com.example.ccms.dto.request.LoginRequest;
import com.example.ccms.dto.request.PasswordChangeRequest;
import com.example.ccms.dto.request.RegisterRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.LoginResponse;
import com.example.ccms.dto.response.UserVO;
import com.example.ccms.entity.Users;
import com.example.ccms.enums.ErrorCodeEnum;
import com.example.ccms.enums.RoleEnum;
import com.example.ccms.exception.BusinessException;
import com.example.ccms.repository.UsersRepository;
import com.example.ccms.security.JwtTokenProvider;
import com.example.ccms.service.UserService;
import com.example.ccms.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        Users user = (Users) authentication.getPrincipal();
        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());

        return ResultUtil.success("登录成功", response);
    }

    @Override
    @Transactional
    public ApiResponse<Void> register(RegisterRequest request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCodeEnum.USERNAME_EXIST);
        }
        if (usersRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ErrorCodeEnum.PHONE_EXIST);
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCodeEnum.EMAIL_EXIST);
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(RoleEnum.USER);

        usersRepository.save(user);
        return ResultUtil.success("注册成功");
    }

    @Override
    public ApiResponse<UserVO> getUserInfo(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRealName(user.getRealName());
        userVO.setAvatar(user.getAvatar());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setRole(user.getRole().name());
        // 明确泛型类型为UserVO
        return ResultUtil.success(userVO);
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateUserInfo(Long userId, UserVO userVO) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));
        if (userVO.getRealName() != null) {
            user.setRealName(userVO.getRealName());
        }
        if (userVO.getAvatar() != null) {
            user.setAvatar(userVO.getAvatar());
        }
        if (userVO.getPhone() != null) {
            user.setPhone(userVO.getPhone());
        }
        if (userVO.getEmail() != null) {
            user.setEmail(userVO.getEmail());
        }
        usersRepository.save(user);
        return ResultUtil.success();
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(Long userId, PasswordChangeRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
        return ResultUtil.success();
    }
}