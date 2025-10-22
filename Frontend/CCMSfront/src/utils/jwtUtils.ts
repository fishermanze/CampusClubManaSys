// JWT工具类 - 提供JWT相关的操作函数

/**
 * 解析JWT token，获取其中的payload部分
 * @param token JWT token字符串
 * @returns 解析后的payload对象，如果解析失败则返回null
 */
export const parseJwt = (token: string): any => {
  try {
    const base64Url = token.split('.')[1];
    if (!base64Url) return null;
    
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Failed to parse JWT:', error);
    return null;
  }
};

/**
 * 检查JWT token是否有效（未过期）
 * @param token JWT token字符串
 * @returns boolean - token是否有效
 */
export const isTokenValid = (token: string): boolean => {
  const payload = parseJwt(token);
  if (!payload || !payload.exp) return false;
  
  // 检查token是否已过期
  const now = Date.now() / 1000;
  return payload.exp > now;
};

/**
 * 从localStorage获取当前的access token
 * @returns string | null - access token
 */
export const getAccessToken = (): string | null => {
  return localStorage.getItem('accessToken');
};

/**
 * 清除localStorage中的所有认证相关信息
 */
export const clearTokens = (): void => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  localStorage.removeItem('role');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('tokenExpiry');
};

/**
 * 检查token是否需要刷新
 * @returns boolean - 是否需要刷新token
 */
export const shouldRefreshToken = (): boolean => {
  const token = getAccessToken();
  // 如果没有token或者token无效，需要刷新
  if (!token || !isTokenValid(token)) {
    return true;
  }
  
  // 尝试通过解析token检查是否即将过期
  const payload = parseJwt(token);
  if (payload && payload.exp) {
    // 提前5分钟刷新token
    const now = Date.now() / 1000;
    const threshold = payload.exp - 5 * 60; // 提前5分钟
    return now > threshold;
  }
  
  return false;
};

/**
 * 从localStorage获取当前用户信息
 * @returns any | null - 用户信息对象
 */
export const getUserInfo = (): any | null => {
  try {
    const userInfoStr = localStorage.getItem('userInfo');
    return userInfoStr ? JSON.parse(userInfoStr) : null;
  } catch (error) {
    console.error('Failed to get user info:', error);
    return null;
  }
};

/**
 * 获取当前用户的角色
 * @returns string | null - 用户角色
 */
export const getUserRole = (): string | null => {
  return localStorage.getItem('role') || getUserInfo()?.role || null;
};