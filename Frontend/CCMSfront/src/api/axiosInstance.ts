import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosError, AxiosResponse } from 'axios'
import { mockApiService } from './mockService'
import {
  getAccessToken,
  getRefreshToken,
  storeTokens,
  clearTokens,
  shouldRefreshToken
} from '@/utils/jwtUtils'
import { authApi } from './apiInterface'

// 扩展ImportMeta接口以支持env属性
declare global {
  interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string
  }
  interface ImportMeta {
    readonly env: ImportMetaEnv
  }
}

// 配置是否使用mock数据
const USE_MOCK = false // 设为true使用mock数据，false使用真实API

// 创建axios实例
const axiosInstance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 检查token是否过期
const isTokenExpired = (): boolean => {
  return shouldRefreshToken()
}

// 刷新token的函数
let isRefreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

const refreshToken = async (): Promise<string> => {
  const accessToken = localStorage.getItem('accessToken')
  if (!accessToken) {
    logout()
    throw new Error('No access token available')
  }

  try {
    // 不使用axiosInstance避免递归调用拦截器
    const response = await axios.post(
      authApi.refreshToken,
      {},
      {
        baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
        params: {
          oldToken: accessToken
        }
      }
    )

    const newToken = response.data
    
    if (!newToken || typeof newToken !== 'string') {
      throw new Error('Invalid token response')
    }
    
    // 存储新token
    localStorage.setItem('accessToken', newToken)
    
    return newToken
  } catch (error) {
    console.error('Token refresh failed:', error)
    logout()
    throw error
  }
}

// 登出函数
const logout = (): void => {
  // 清除所有本地存储的认证信息
  localStorage.removeItem('accessToken')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('userInfo')
  window.location.href = '/login'
}

// 将请求加入等待队列
const subscribeTokenRefresh = (callback: (token: string) => void): void => {
  refreshSubscribers.push(callback)
}

// 通知所有等待的请求
const onRefreshed = (token: string): void => {
  refreshSubscribers.forEach(callback => callback(token))
  refreshSubscribers = []
}

// 请求拦截器
axiosInstance.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // 跳过登录相关接口的token检查
    // 跳过认证相关接口的token检查
    const authPaths = [
      authApi.login,
      authApi.register,
      authApi.refreshToken,
      authApi.loginByCode,
      authApi.sendVerificationCode,
      authApi.resetPassword
    ]
    
    if (authPaths.some(path => config.url?.includes(path))) {
      return config
    }

    // 检查token是否过期
    if (isTokenExpired()) {
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const newToken = await refreshToken()
          isRefreshing = false
          onRefreshed(newToken)
          
          if (config.headers) {
            config.headers['Authorization'] = `Bearer ${newToken}`
          }
        } catch (error) {
          isRefreshing = false
          return Promise.reject(error)
        }
      } else {
        // 等待token刷新完成
        return new Promise<InternalAxiosRequestConfig>((resolve) => {
          subscribeTokenRefresh((newToken) => {
            if (config.headers) {
              config.headers['Authorization'] = `Bearer ${newToken}`
            }
            resolve(config)
          })
        })
      }
    } else {
      // 使用现有token
      const token = getAccessToken()
      if (token && config.headers) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }
    return config
  },
  (error: AxiosError) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    // 如果是登录响应，存储token相关信息
    if (response.config.url?.includes('/auth/login') || response.config.url?.includes('/auth/login/code')) {
      // 适配后端API的响应格式
      const tokenData = response.data;
      if (tokenData.token) {
        localStorage.setItem('accessToken', tokenData.token);
        localStorage.setItem('userId', tokenData.userId?.toString() || '');
        localStorage.setItem('username', tokenData.username || '');
        localStorage.setItem('role', tokenData.role || '');
        localStorage.setItem('userInfo', JSON.stringify({
          userId: tokenData.userId,
          username: tokenData.username,
          role: tokenData.role
        }));
        console.log('Token stored successfully:', { token: tokenData.token, username: tokenData.username });
      }
    }
    return response;
  },
  async (error: AxiosError) => {
    console.error('响应错误:', error);
    
    // 处理401错误（未授权）
    if (error.response?.status === 401) {
      // 如果是刷新token失败，直接登出
      if (error.config?.url?.includes('/auth/refresh')) {
        logout();
        throw error;
      }
      
      // 其他401错误，尝试刷新token
      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const newToken = await refreshToken();
          isRefreshing = false;
          onRefreshed(newToken);
          
          // 重试原始请求
          const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
          if (originalRequest && !originalRequest._retry) {
            originalRequest._retry = true;
            originalRequest.headers = originalRequest.headers || {};
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
            return axios(originalRequest);
          }
          throw error;
        } catch (refreshError) {
          isRefreshing = false;
          throw refreshError;
        }
      } else {
        // 等待token刷新完成后重试
        return new Promise<any>((resolve, reject) => {
          subscribeTokenRefresh((newToken) => {
            const originalRequest = error.config as InternalAxiosRequestConfig;
            if (originalRequest) {
              originalRequest.headers = originalRequest.headers || {};
              originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
              resolve(axios(originalRequest));
            } else {
              reject(error);
            }
          });
        });
      }
    }
    
    // 处理其他错误
    const errorMessage = (error.response?.data as any)?.message || '网络错误，请稍后重试';
    console.error('错误信息:', errorMessage);
    
    return Promise.reject(error);
  }
);

// 模拟API请求的处理函数
const mockRequest = async (config: InternalAxiosRequestConfig): Promise<any> => {
  console.log('Mock API Request:', config.url)
  
  // 根据URL路径匹配对应的mock方法
  const url = config.url || ''
  
  // 解析URL，提取资源类型和ID（移除/api前缀）
  const urlParts = url.replace(/^\/api\//, '').split('/')
  
  try {
    // 处理认证相关API
    if (urlParts[0] === 'auth') {
      if (urlParts[1] === 'login') {
        if (urlParts[2] === 'code') {
          const result = await mockApiService.auth.loginByCode(config.data)
          // 返回标准Axios响应格式
          return { 
            data: result.data, 
            status: 200, 
            statusText: 'OK', 
            headers: {}, 
            config: config 
          }
        }
        const result = await mockApiService.auth.login(config.data)
        // 返回标准Axios响应格式
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      } else if (urlParts[1] === 'send-code') {
        const result = await mockApiService.auth.sendVerificationCode(
          config.data.phone, 
          config.data.type
        )
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      } else if (urlParts[1] === 'register') {
        const result = await mockApiService.auth.register(config.data)
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      } else if (urlParts[1] === 'reset-password') {
        const result = await mockApiService.auth.resetPassword(config.data)
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      } else if (urlParts[1] === 'user-info') {
        const result = await mockApiService.auth.getCurrentUser()
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      } else if (urlParts[1] === 'logout') {
        const result = await mockApiService.auth.logout()
        return { 
          data: result.data, 
          status: 200, 
          statusText: 'OK', 
          headers: {}, 
          config: config 
        }
      }
    }
    
    // 处理社团相关API
    if (urlParts[0] === 'clubs') {
      if (urlParts[1] === 'recommended') {
        const result = await mockApiService.club.getRecommendedClubs()
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[2] === 'activities') {
        const clubId = parseInt(urlParts[1])
        const result = await mockApiService.club.getClubActivities(clubId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[2] === 'join') {
        const clubId = parseInt(urlParts[1])
        const result = await mockApiService.club.joinClub(clubId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[2] === 'leave') {
        const clubId = parseInt(urlParts[1])
        const result = await mockApiService.club.leaveClub(clubId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (!isNaN(parseInt(urlParts[1]))) {
        const clubId = parseInt(urlParts[1])
        const result = await mockApiService.club.getClubDetail(clubId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      }
    }
    
    // 处理活动相关API
    if (urlParts[0] === 'activities') {
      if (urlParts.length === 1) {
        const result = await mockApiService.activity.getActivities(config.params)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[2] === 'join') {
        const activityId = parseInt(urlParts[1])
        const result = await mockApiService.activity.joinActivity(activityId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[2] === 'cancel') {
        const activityId = parseInt(urlParts[1])
        const result = await mockApiService.activity.cancelActivity(activityId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (!isNaN(parseInt(urlParts[1]))) {
        const activityId = parseInt(urlParts[1])
        const result = await mockApiService.activity.getActivityDetail(activityId)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      }
    }
    
    // 处理统计相关API
    if (urlParts[0] === 'stats') {
      if (urlParts[1] === 'user') {
        const result = await mockApiService.stats.getUserStats()
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      } else if (urlParts[1] === 'activities') {
        const result = await mockApiService.stats.getActivityStats(config.params)
        return {
          data: result.data,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: config
        }
      }
    }
    
    // 如果没有匹配到对应的mock方法，抛出错误
    throw new Error('No mock response available for: ' + url)
  } catch (error) {
    console.error('Mock API Error:', error)
    throw error
  }
}

// 根据配置决定使用真实API还是mock
if (USE_MOCK) {
  axiosInstance.interceptors.request.use(mockRequest)
} else {
  // 真实API的请求拦截器
      axiosInstance.interceptors.request.use(
        (config: InternalAxiosRequestConfig) => {
          // 在发送请求之前做些什么
          const token = localStorage.getItem('accessToken')
          if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`
          }
          return config
        },
        (error: AxiosError) => {
          throw error
        }
      )
}

export default axiosInstance