import axiosInstance from './axiosInstance'
import type { LoginForm, CodeLoginForm, RegisterForm, ForgotPasswordForm } from '../types/index'

// 认证API使用JWT工具函数处理token，不需要单独定义响应类型

// 用户认证相关API
export const authApi = {
  // 账号密码登录
  login: async (data: LoginForm) => {
    try {
      const response = await axiosInstance.post('/api/auth/login', data);
      // 存储token到本地存储
      // 适配mock和真实API的不同响应格式
      const responseData = response.data || response;
      if (responseData.accessToken) {
        localStorage.setItem('accessToken', responseData.accessToken);
        localStorage.setItem('refreshToken', responseData.refreshToken);
      }
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },
  
  // 验证码登录
  loginByCode: async (data: CodeLoginForm) => {
    try {
      const response = await axiosInstance.post('/api/auth/login/code', data);
      // 存储token到本地存储
      // 适配mock和真实API的不同响应格式
      const responseData = response.data || response;
      if (responseData.accessToken) {
        localStorage.setItem('accessToken', responseData.accessToken);
        localStorage.setItem('refreshToken', responseData.refreshToken);
      }
      return response;
    } catch (error) {
      console.error('Login by code error:', error);
      throw error;
    }
  },
  
  // 发送验证码
  sendVerificationCode: (phone: string, type: 'login' | 'register' | 'forgot') => {
    return axiosInstance.post('/api/auth/send-code', { phone, type })
  },
  
  // 用户注册
  register: (data: RegisterForm) => {
    const response = axiosInstance.post('/api/auth/register', data);
    // 注册成功后自动登录，存储token
    response.then(res => {
      // 适配mock和真实API的不同响应格式
      const responseData = res.data || res
      if (responseData.accessToken) {
        localStorage.setItem('accessToken', responseData.accessToken);
        localStorage.setItem('refreshToken', responseData.refreshToken);
      }
    });
    return response;
  },
  
  // 忘记密码
  resetPassword: (data: ForgotPasswordForm) => {
    return axiosInstance.post('/api/auth/reset-password', data)
  },
  
  // 获取当前用户信息
  getCurrentUser: () => {
    return axiosInstance.get('/api/auth/user-info')
  },
  
  // 刷新Token
  refreshToken: (data: { refreshToken: string }) => {
    const response = axiosInstance.post('/api/auth/refresh-token', data);
    // 更新本地存储的token
    response.then(res => {
      // 适配mock和真实API的不同响应格式
      const responseData = res.data || res
      if (responseData.accessToken) {
        localStorage.setItem('accessToken', responseData.accessToken);
        localStorage.setItem('refreshToken', responseData.refreshToken);
      }
    });
    return response;
  },
  
  // 退出登录
  logout: () => {
    const response = axiosInstance.post('/api/auth/logout');
    // 确保清除本地存储的token
    response.finally(() => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userInfo');
    });
    return response;
  }
}

// 社团相关API
export const clubApi = {
  // 获取推荐社团列表
  getRecommendedClubs: () => {
    return axiosInstance.get('/clubs/recommended')
  },
  
  // 获取社团详情
  getClubDetail: (clubId: number) => {
    return axiosInstance.get(`/clubs/${clubId}`)
  },
  
  // 获取社团活动列表
  getClubActivities: (clubId: number) => {
    return axiosInstance.get(`/clubs/${clubId}/activities`)
  },
  
  // 加入社团
  joinClub: (clubId: number) => {
    return axiosInstance.post(`/clubs/${clubId}/join`)
  },
  
  // 退出社团
  leaveClub: (clubId: number) => {
    return axiosInstance.post(`/clubs/${clubId}/leave`)
  }
}

// 活动相关API
export const activityApi = {
  // 获取活动列表
  getActivities: (params?: {
    page?: number
    pageSize?: number
    status?: number
    keyword?: string
  }) => {
    return axiosInstance.get('/activities', { params })
  },
  
  // 获取活动详情
  getActivityDetail: (activityId: number) => {
    return axiosInstance.get(`/activities/${activityId}`)
  },
  
  // 报名参加活动
  joinActivity: (activityId: number) => {
    return axiosInstance.post(`/activities/${activityId}/join`)
  },
  
  // 取消活动报名
  cancelActivity: (activityId: number) => {
    return axiosInstance.post(`/activities/${activityId}/cancel`)
  }
}

// 统计相关API
export const statsApi = {
  // 获取用户统计数据
  getUserStats: () => {
    return axiosInstance.get('/stats/user')
  },
  
  // 获取活动统计数据
  getActivityStats: (params?: {
    startDate?: string
    endDate?: string
    type?: string
  }) => {
    return axiosInstance.get('/stats/activities', { params })
  }
}

export default {
  auth: authApi,
  club: clubApi,
  activity: activityApi,
  stats: statsApi
}