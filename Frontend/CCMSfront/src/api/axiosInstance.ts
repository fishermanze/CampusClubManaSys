import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosError, AxiosResponse } from 'axios'
// 导入JWT相关工具函数（暂时使用内联实现）
// 暂时使用内联实现JWT相关功能
const getAccessToken = (): string | null => {
  return localStorage.getItem('userToken') || localStorage.getItem('accessToken')
}

const shouldRefreshToken = (): boolean => {
  // 简化实现，实际应解析token过期时间
  return false
}

const storeTokens = (token: string, data?: any): void => {
  localStorage.setItem('userToken', token)
  if (data) {
    localStorage.setItem('userId', data.userId || '')
    localStorage.setItem('username', data.username || '')
    localStorage.setItem('userInfo', JSON.stringify(data))
  }
}

// 扩展ImportMeta接口以支持env属性
declare global {
  interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string
  }
  interface ImportMeta {
    readonly env: ImportMetaEnv
  }
}



// 创建axios实例
const axiosInstance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api', // 使用环境变量配置
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
  try {
    // 先尝试从localStorage获取accessToken
    const accessToken = localStorage.getItem('accessToken')
    if (!accessToken) {
      console.warn('No access token available in localStorage')
      // 无token时直接执行登出，不抛出错误
      logout()
      return ''
    }

    // 使用axiosInstance调用刷新接口，并添加skipAuthRefresh标志避免递归
    // 创建一个新的axios实例来避免拦截器递归
    const refreshInstance = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
      timeout: 10000
    })
    
    const response = await refreshInstance.post('/api/auth/refresh', {}, {
      params: { oldToken: accessToken }
    })

    const responseData = response.data
    
    if (!responseData.success || !responseData.data || !responseData.data.token) {
      console.error('Invalid token response')
      logout()
      return ''
    }
    
    const newToken = responseData.data.token
    // 存储新token
    storeTokens(newToken, responseData.data)
    console.log('Token refreshed successfully')
    
    return newToken
  } catch (error) {
    console.error('Token refresh failed:', error)
    logout()
    // 不抛出错误，避免用户看到错误提示
    return ''
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
    // 检查是否跳过认证刷新（避免刷新token时的递归调用）
    const skipAuthRefresh = (config as any).skipAuthRefresh;
    
    // 跳过登录相关接口的token检查
    // 跳过认证相关接口的token检查
    // 注意：这里直接使用路径字符串而不是apiInterface中的值，因为apiInterface中可能包含/api前缀
    const authPaths = [
      '/auth/login',
      '/auth/register',
      '/auth/refresh',
      '/auth/login/code',
      '/auth/send-code',
      '/auth/reset-password'
    ]
    
    if (authPaths.some(path => config.url?.includes(path)) || skipAuthRefresh) {
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
        // 使用统一的token存储函数
        storeTokens(tokenData.token, {
          userId: tokenData.userId,
          username: tokenData.username,
          role: tokenData.role,
          ...tokenData
        });
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

export default axiosInstance