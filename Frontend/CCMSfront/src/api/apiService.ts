import axiosInstance from './axiosInstance'
import { storeTokens } from '../utils/jwtUtils'
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
      // 使用正确的API路径
      const response = await axiosInstance.post('/auth/login', loginData);
      // 存储token到本地存储
      const responseData = response.data || {};
      console.log('Login response:', responseData);
      
      // 根据后端返回格式处理数据
      if (responseData.message=="success" && responseData.data) {
        const { accessToken, user } = responseData.data;
        if (accessToken && user) {
          // 使用统一的token存储函数
          storeTokens(accessToken, {
            userId: user.id,
            username: user.username,
            realName: user.name,
            role: user.role,
            ...user
          });
        }
      } else {
        console.warn('Login failed or invalid response:', responseData);
        throw new Error(responseData.message || '登录失败');
      }
      
      return response;
    } catch (error) {
      console.error('Login error:', error);
      // 确保清除可能存在的无效token
      localStorage.removeItem('accessToken');
      throw error;
    }
  },
  
  // 验证码登录
  loginByCode: async (data: CodeLoginForm) => {
    try {
      const response = await axiosInstance.post('/auth/login/code', data);
      // 存储token到本地存储
      const responseData = response.data || {};
      if (responseData.token) {
        localStorage.setItem('accessToken', responseData.token);
        localStorage.setItem('userId', responseData.userId?.toString() || '');
        localStorage.setItem('username', responseData.username || '');
        localStorage.setItem('role', responseData.role || '');
        localStorage.setItem('userInfo', JSON.stringify({
          userId: responseData.userId,
          username: responseData.username,
          role: responseData.role
        }));
      }
      return response;
    } catch (error) {
      console.error('Login by code error:', error);
      localStorage.removeItem('accessToken');
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
      uid: data.uid, // 使用正确的uid字段
      realName: data.realName,
      confirmPassword:data.confirmPassword
    };
    // 使用正确的API路径
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
    // 调用API获取当前用户信息
    return axiosInstance.get('/auth/current-user')
      .catch(error => {
        console.error('Failed to get current user:', error);
        return Promise.reject(new Error('No user info available'));
      });
  },
  
  // 刷新Token
  refreshToken: (data: { refreshToken: string }) => {
    // 使用正确的API路径和参数格式
    const accessToken = localStorage.getItem('accessToken') || data.refreshToken;
    const response = axiosInstance.post('/auth/refresh', {}, {
      params: { oldToken: accessToken }
    });
    
    // 更新本地存储的token
    response.then(res => {
      const newToken = res.data || res;
      if (typeof newToken === 'string') {
        localStorage.setItem('accessToken', newToken);
        console.log('Token refreshed successfully');
      }
    }).catch(error => {
      console.error('Token refresh failed:', error);
      // 刷新失败时清除token
      localStorage.removeItem('accessToken');
    });
    
    return response;
  },
  
  // 退出登录
  logout: async () => {
    try {
      // 获取当前用户名用于退出登录
      const username = localStorage.getItem('username');
      const response = await axiosInstance.post('/auth/logout', {}, {
        params: { username: username || '' }
      });
      return response;
    } finally {
      // 确保清除本地存储的token和用户信息
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      localStorage.removeItem('userInfo');
      console.log('Logged out, all auth data cleared');
    }
  }
}

// 社团相关API
export const clubApi = {
  // 获取全部社团列表
  getAllClubs: () => {
    return axiosInstance.get('/clubs')
  },

  // 提交入团申请（包含申请信息）
  applyToClub: (clubId: number, payload: {
    userId: number
    realName: string
    gender: string
    major: string
    className: string
    reason?: string
  }) => {
    return axiosInstance.post(`/clubs/applications/${clubId}`, payload)
  },

  // 获取用户加入的社团（返回成员记录列表）
  getUserClubs: (userId: number) => {
    return axiosInstance.get(`/clubs/user/${userId}`)
  },
  
  // 获取社团详情
  getClubDetail: (clubId: number) => {
    return axiosInstance.get(`/clubs/${clubId}`)
  },
  
  // 加入社团（提交申请，后端以成员状态流转）
  joinClub: (clubId: number, userId: number) => {
    return axiosInstance.post(`/clubs/${clubId}/members`, null, { params: { userId } })
  },
  
  // 退出社团
  leaveClub: (clubId: number, userId: number) => {
    return axiosInstance.delete(`/clubs/${clubId}/members/${userId}`)
  },
  
  // 获取社团成员列表
  getClubMembers: (clubId: number) => {
    return axiosInstance.get(`/clubs/${clubId}/members`)
  }
}

// 活动相关API
export const activityApi = {
  // 获取活动列表
  getActivities: (params?: {
    page?: number
    size?: number
    sort?: string
  }) => {
    return axiosInstance.get('/activities', { params })
  },
  
  // 获取活动详情
  getActivityDetail: (activityId: number) => {
    return axiosInstance.get(`/activities/${activityId}`)
  },
  
  // 创建活动
  createActivity: (activityData: any) => {
    return axiosInstance.post('/activities', activityData)
  },
  
  // 更新活动
  updateActivity: (activityId: number, activityData: any) => {
    return axiosInstance.put(`/activities/${activityId}`, activityData)
  },
  
  // 删除活动
  deleteActivity: (activityId: number) => {
    return axiosInstance.delete(`/activities/${activityId}`)
  },
  
  // 更新活动状态
  updateActivityStatus: (activityId: number, status: number) => {
    return axiosInstance.put(`/activities/${activityId}/status`, {}, { params: { status } })
  },
  
  // 获取社团活动列表
  getClubActivities: (clubId: number, params?: {
    page?: number
    size?: number
    sort?: string
  }) => {
    return axiosInstance.get(`/activities/club/${clubId}`, { params })
  },
  
  // 获取用户参与的活动
  getUserParticipatedActivities: (userId: number, params?: {
    page?: number
    size?: number
  }) => {
    return axiosInstance.get(`/activities/user/${userId}/participated`, { params })
  },
  
  // 获取用户创建的活动
  getUserCreatedActivities: (userId: number, params?: {
    page?: number
    size?: number
  }) => {
    return axiosInstance.get(`/activities/user/${userId}/created`, { params })
  },
  
  // 搜索活动
  searchActivities: (keyword: string, params?: {
    page?: number
    size?: number
  }) => {
    return axiosInstance.get('/activities/search', { params: { keyword, ...params } })
  },
  
  // 获取即将开始的活动
  getUpcomingActivities: (limit?: number) => {
    return axiosInstance.get('/activities/upcoming', { params: { limit } })
  },
  
  // 获取正在进行的活动
  getCurrentActivities: (limit?: number) => {
    return axiosInstance.get('/activities/current', { params: { limit } })
  },
  
  // 根据标签获取活动
  getActivitiesByTag: (tag: string, params?: {
    page?: number
    size?: number
  }) => {
    return axiosInstance.get(`/activities/tag/${tag}`, { params })
  },
  
  // 增加浏览量
  incrementViewCount: (activityId: number) => {
    return axiosInstance.post(`/activities/${activityId}/view`)
  },
  
  // 点赞活动
  likeActivity: (activityId: number) => {
    return axiosInstance.post(`/activities/${activityId}/like`)
  },
  
  // 取消点赞活动
  unlikeActivity: (activityId: number) => {
    return axiosInstance.delete(`/activities/${activityId}/like`)
  },
  
  // 获取活动统计
  getActivityStatistics: (clubId: number) => {
    return axiosInstance.get(`/activities/statistics/club/${clubId}`)
  }
}

// 活动报名相关API
export const activityParticipantApi = {
  // 报名参加活动
  enrollActivity: (participantData: {
    activityId: number
    userId: number
    enrollmentInfo?: string
  }) => {
    return axiosInstance.post('/participations', participantData)
  },
  
  // 取消报名
  cancelEnrollment: (activityId: number, userId: number) => {
    return axiosInstance.delete(`/participations/activity/${activityId}/user/${userId}`)
  },

  // 审批活动报名
  approveParticipation: (
    activityId: number,
    userId: number,
    params: {
      status: number
      remark?: string
    }
  ) => {
    return axiosInstance.put(`/participations/activity/${activityId}/user/${userId}/approve`, null, {
      params
    })
  },
  
  // 获取参与记录
  getParticipation: (activityId: number, userId: number) => {
    return axiosInstance.get(`/participations/activity/${activityId}/user/${userId}`)
  },
  
  // 检查用户是否已参与活动
  hasParticipated: (activityId: number, userId: number) => {
    return axiosInstance.get(`/participations/activity/${activityId}/user/${userId}/exists`)
  },
  
  // 获取用户在活动中的状态
  getUserParticipationStatus: (activityId: number, userId: number) => {
    return axiosInstance.get(`/participations/activity/${activityId}/user/${userId}/status`)
  },
  
  // 获取活动参与者列表
  getActivityParticipants: (activityId: number, params?: {
    status?: number
    page?: number
    size?: number
    sort?: string
  }) => {
    return axiosInstance.get(`/participations/activity/${activityId}`, { params })
  },
  
  // 获取用户参与的活动列表
  getUserParticipations: (userId: number, params?: {
    page?: number
    size?: number
    sort?: string
  }) => {
    return axiosInstance.get(`/participations/user/${userId}`, { params })
  }
}

// 评论相关API
export const commentApi = {
  // 获取活动热门评论
  getHotComments: (activityId: number, limit: number = 5) => {
    return axiosInstance.get(`/comments/activity/${activityId}/hot`, { params: { limit } })
  },
  
  // 获取活动评论列表
  getActivityComments: (activityId: number, params?: {
    page?: number
    size?: number
    sort?: string
  }) => {
    return axiosInstance.get(`/comments/activity/${activityId}`, { params })
  },
  
  // 创建评论
  createComment: (commentData: {
    activityId: number
    userId?: number
    content: string
    parentId?: number
  }) => {
    return axiosInstance.post(`/comments`, commentData)
  },
  
  // 点赞评论
  likeComment: (commentId: number) => {
    return axiosInstance.post(`/comments/${commentId}/like`)
  },
  
  // 取消点赞评论
  unlikeComment: (commentId: number) => {
    return axiosInstance.delete(`/comments/${commentId}/like`)
  },
  
  // 获取评论的回复
  getCommentReplies: (commentId: number) => {
    return axiosInstance.get(`/comments/${commentId}/replies`)
  },
  
  // 删除评论
  deleteComment: (commentId: number) => {
    return axiosInstance.delete(`/comments/${commentId}`)
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
  },
  
  // 获取活跃度趋势统计
  getActivityTrend: (params?: {
    period?: string // 'week', 'month', 'year'
    startDate?: string
    endDate?: string
  }) => {
    return axiosInstance.get('/stats/trends/activity', { params })
  }
}

// 用户社交相关API
export const userSocialApi = {
  // 获取用户关注的人
  getFollowing: (userId: string) => axiosInstance.get(`/users/${userId}/following`),
  
  // 获取关注用户的人
  getFollowers: (userId: string) => axiosInstance.get(`/users/${userId}/followers`),
  
  // 关注用户
  followUser: (userId: string) => axiosInstance.post(`/users/${userId}/follow`),
  
  // 取消关注用户
  unfollowUser: (userId: string) => axiosInstance.delete(`/users/${userId}/follow`),
  
  // 获取共同关注
  getCommonFollowings: (userId1: string, userId2: string) => 
    axiosInstance.get(`/users/common-followings?userId1=${userId1}&userId2=${userId2}`)
};

// 通知相关API
export const notificationApi = {
  // 获取未读通知数量
  getUnreadCount: (userId: number) => {
    return axiosInstance.get(`/notifications/user/${userId}/unread-count`)
  },
  
  // 获取通知列表
  getNotifications: (userId: number, params?: {
    page?: number
    size?: number
    type?: number
  }) => {
    return axiosInstance.get(`/notifications/user/${userId}`, { params })
  },
  
  // 标记通知为已读
  markAsRead: (notificationId: number, userId: number) => {
    return axiosInstance.put(`/notifications/${notificationId}/read`, null, { params: { userId } })
  },
  
  // 标记所有通知为已读
  markAllAsRead: (userId: number) => {
    return axiosInstance.put(`/notifications/user/${userId}/read-all`)
  }
};

export default {
  auth: authApi,
  club: clubApi,
  activity: activityApi,
  stats: statsApi,
  userSocial: userSocialApi,
  notification: notificationApi
}