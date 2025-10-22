import axiosInstance from './axiosInstance'
import type { LoginForm, CodeLoginForm, RegisterForm, ForgotPasswordForm } from '../types/index'

// 认证API使用JWT工具函数处理token，不需要单独定义响应类型

// 用户认证相关API
export const authApi = {
  // 账号密码登录
  login: async (data: LoginForm) => {
    try {
      // 只传递username和password给后端
      const loginData = {
        username: data.username,
        password: data.password
      };
      const response = await axiosInstance.post('/auth/login', loginData);
      // 存储token到本地存储
      const responseData = response.data || response;
      if (responseData.token) {
        localStorage.setItem('accessToken', responseData.token);
        localStorage.setItem('userId', responseData.userId.toString());
        localStorage.setItem('username', responseData.username);
        localStorage.setItem('role', responseData.role);
        // 存储用户信息
        localStorage.setItem('userInfo', JSON.stringify({
          userId: responseData.userId,
          username: responseData.username,
          role: responseData.role
        }));
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
      const response = await axiosInstance.post('/auth/login/code', data);
      // 存储token到本地存储
      const responseData = response.data || response;
      if (responseData.token) {
        localStorage.setItem('accessToken', responseData.token);
        localStorage.setItem('userId', responseData.userId.toString());
        localStorage.setItem('username', responseData.username);
        localStorage.setItem('role', responseData.role);
        localStorage.setItem('userInfo', JSON.stringify({
          userId: responseData.userId,
          username: responseData.username,
          role: responseData.role
        }));
      }
      return response;
    } catch (error) {
      console.error('Login by code error:', error);
      throw error;
    }
  },
  
  // 发送验证码
  sendVerificationCode: (phone: string, type: 'login' | 'register' | 'forgot') => {
    return axiosInstance.post('/auth/send-code', { phone, type })
  },
  
  // 用户注册
  register: (data: RegisterForm) => {
    // 根据后端要求，只传递必要字段
    const registerData = {
      username: data.username,
      password: data.password,
      uid: data.phone, // 使用phone作为校园统一标识
      realName: data.realName
    };
    const response = axiosInstance.post('/auth/register', registerData);
    return response;
  },
  
  // 忘记密码
  resetPassword: (data: ForgotPasswordForm) => {
    return axiosInstance.post('/auth/reset-password', data)
  },
  
  // 获取当前用户信息
  getCurrentUser: () => {
    // 从本地存储获取用户信息，或调用API获取
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      return Promise.resolve({ data: JSON.parse(userInfoStr) });
    }
    // 如果后端有提供获取当前用户信息的接口，可以调用
    // return axiosInstance.get('/auth/current-user')
    return Promise.reject(new Error('No user info available'));
  },
  
  // 刷新Token
  refreshToken: (data: { refreshToken: string }) => {
    // 后端API要求使用查询参数传递oldToken
    const response = axiosInstance.post(`/auth/refresh?oldToken=${encodeURIComponent(data.refreshToken)}`);
    // 更新本地存储的token
    response.then(res => {
      const newToken = res.data || res;
      if (typeof newToken === 'string') {
        localStorage.setItem('accessToken', newToken);
      }
    });
    return response;
  },
  
  // 退出登录
  logout: () => {
    // 获取当前用户名用于退出登录
    const username = localStorage.getItem('username');
    const response = axiosInstance.post(`/auth/logout?username=${encodeURIComponent(username || '')}`);
    // 确保清除本地存储的token和用户信息
    response.finally(() => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
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