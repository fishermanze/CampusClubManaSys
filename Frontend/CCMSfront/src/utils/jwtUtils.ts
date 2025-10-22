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
 * 获取token的过期时间戳
 * @param token JWT token字符串
 * @returns number | null - 过期时间戳，如果token无效则返回null
 */
export const getTokenExpiry = (token: string): number | null => {
  const payload = parseJwt(token);
  if (!payload || !payload.exp) return null;
  
  // 返回毫秒级时间戳
  return payload.exp * 1000;
};

/**
 * 获取token中的用户ID
 * @param token JWT token字符串
 * @returns string | number | null - 用户ID，如果不存在则返回null
 */
export const getUserIdFromToken = (token: string): string | number | null => {
  const payload = parseJwt(token);
  if (!payload) return null;
  
  // 通常用户ID可能在sub、userId、user_id、id等字段中
  return payload.sub || payload.userId || payload.user_id || payload.id || null;
};

/**
 * 获取token中的用户角色信息
 * @param token JWT token字符串
 * @returns string[] | string | null - 用户角色，如果不存在则返回null
 */
export const getUserRolesFromToken = (token: string): string[] | string | null => {
  const payload = parseJwt(token);
  if (!payload) return null;
  
  // 通常角色信息可能在role、roles、authorities等字段中
  return payload.role || payload.roles || payload.authorities || null;
};

/**
 * 从localStorage获取当前的access token
 * @returns string | null - access token
 */
export const getAccessToken = (): string | null => {
  return localStorage.getItem('accessToken');
};

/**
 * 从localStorage获取当前的refresh token
 * @returns string | null - refresh token
 */
export const getRefreshToken = (): string | null => {
  return localStorage.getItem('refreshToken');
};

/**
 * 清除localStorage中的所有token相关信息
 */
export const clearTokens = (): void => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('tokenExpiry');
};

/**
 * 存储token相关信息到localStorage
 * @param accessToken JWT access token
 * @param refreshToken JWT refresh token
 * @param expiresIn token有效期（秒）
 */
export const storeTokens = (accessToken: string, refreshToken: string, expiresIn: number): void => {
  localStorage.setItem('accessToken', accessToken);
  localStorage.setItem('refreshToken', refreshToken);
  localStorage.setItem('tokenExpiry', (Date.now() + expiresIn * 1000).toString());
};

/**
 * 检查token是否需要刷新（提前5分钟）
 * @returns boolean - 是否需要刷新token
 */
export const shouldRefreshToken = (): boolean => {
  const expiryStr = localStorage.getItem('tokenExpiry');
  if (!expiryStr) return true;
  
  const expiryTime = parseInt(expiryStr, 10);
  // 提前5分钟刷新token
  const threshold = Date.now() + 5 * 60 * 1000;
  
  return expiryTime < threshold;
};