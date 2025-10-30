package com.example.ccms.security;

import com.example.ccms.enums.ErrorCodeEnum;
import com.example.ccms.exception.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 注解解析器：将@CurrentUser注解的参数解析为当前登录用户ID
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            // 实际场景中需从UserDetails或Token中提取用户ID（此处示例直接返回逻辑ID，需替换为真实逻辑）
            // 例如：UserDetails userDetails = (UserDetails) principal;
            //       return ((Users) userDetails).getId();
            return 1L; // 示例值，实际需替换为真实用户ID提取逻辑
        } else {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_LOGGED_IN);
        }
    }
}