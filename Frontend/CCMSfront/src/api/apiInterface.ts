// API接口定义文件 - 用于前端与后端对接

// 用户相关接口
export const authApi = {
  // 用户登录
  login: '/auth/login',
  // 用户登出
  logout: '/auth/logout',
  // 获取当前用户信息
  getCurrentUser: '/auth/current-user',
  // 用户注册
  register: '/auth/register',
  // 修改密码
  changePassword: '/auth/change-password',
  // 更新用户信息
  updateProfile: '/auth/profile/update',
  // 用户上传头像
  uploadAvatar: '/auth/avatar/upload',
  // 刷新Token
  refreshToken: '/auth/refresh',
  // 验证码登录
  loginByCode: '/auth/login/code',
  // 发送验证码
  sendVerificationCode: '/auth/send-code',
  // 重置密码
  resetPassword: '/auth/reset-password'
};

// 社团相关接口
export const clubApi = {
  // 获取社团列表
  getClubs: '/clubs',
  // 获取社团详情
  getClubById: (id: number) => `/clubs/${id}`,
  // 创建社团
  createClub: '/clubs',
  // 更新社团信息
  updateClub: (id: number) => `/clubs/${id}`,
  // 删除社团
  deleteClub: (id: number) => `/clubs/${id}`,
  // 加入社团
  joinClub: (id: number) => `/clubs/${id}/join`,
  // 退出社团
  leaveClub: (id: number) => `/clubs/${id}/leave`,
  // 获取用户加入的社团
  getUserClubs: '/clubs/user',
  // 搜索社团
  searchClubs: '/clubs/search',
  // 获取热门社团
  getPopularClubs: '/clubs/popular',
  // 获取新创建社团
  getNewClubs: '/clubs/new'
};

// 活动相关接口
export const activityApi = {
  // 获取活动列表
  getActivities: '/activities',
  // 获取活动详情
  getActivityById: (id: number) => `/activities/${id}`,
  // 创建活动
  createActivity: '/activities',
  // 更新活动信息
  updateActivity: (id: number) => `/activities/${id}`,
  // 删除活动
  deleteActivity: (id: number) => `/activities/${id}`,
  // 报名参加活动
  joinActivity: (id: number) => `/activities/${id}/join`,
  // 取消活动报名
  cancelJoinActivity: (id: number) => `/activities/${id}/cancel`,
  // 获取用户参加的活动
  getUserActivities: '/activities/user',
  // 获取社团的活动
  getClubActivities: (clubId: number) => `/clubs/${clubId}/activities`,
  // 搜索活动
  searchActivities: '/activities/search',
  // 获取即将开始的活动
  getUpcomingActivities: '/activities/upcoming',
  // 获取热门活动
  getPopularActivities: '/activities/popular'
};

// 统计相关接口
export const statsApi = {
  // 获取用户统计信息
  getUserStats: '/stats/user',
  // 获取活动统计信息
  getActivityStats: '/stats/activities',
  // 获取社团统计信息
  getClubStats: '/stats/clubs',
  // 获取整体平台统计信息
  getOverallStats: '/stats/overall',
  // 获取活跃度统计
  getActivityTrend: '/stats/trends/activity',
  // 获取用户增长统计
  getUserGrowth: '/stats/trends/users',
  // 获取分类统计
  getCategoryStats: '/stats/categories'
};

// 收藏相关接口
export const favoriteApi = {
  // 获取收藏列表
  getFavorites: '/api/favorites',
  // 添加收藏
  addFavorite: '/api/favorites',
  // 移除收藏
  removeFavorite: (id: number) => `/api/favorites/${id}`,
  // 检查是否已收藏
  checkFavorite: (type: 'club' | 'activity', id: number) => `/api/favorites/check?type=${type}&id=${id}`
};

// 消息相关接口
export const messageApi = {
  // 获取消息列表
  getMessages: '/api/messages',
  // 获取未读消息数量
  getUnreadCount: '/api/messages/unread',
  // 标记消息已读
  markAsRead: (id: number) => `/api/messages/${id}/read`,
  // 标记所有消息已读
  markAllAsRead: '/api/messages/read-all',
  // 发送消息
  sendMessage: '/api/messages',
  // 删除消息
  deleteMessage: (id: number) => `/api/messages/${id}`
};

// 评论相关接口
export const commentApi = {
  // 获取评论列表
  getComments: (type: 'club' | 'activity', id: number) => `/api/${type}s/${id}/comments`,
  // 添加评论
  addComment: (type: 'club' | 'activity', id: number) => `/api/${type}s/${id}/comments`,
  // 删除评论
  deleteComment: (commentId: number) => `/api/comments/${commentId}`,
  // 点赞评论
  likeComment: (commentId: number) => `/api/comments/${commentId}/like`,
  // 取消点赞评论
  unlikeComment: (commentId: number) => `/api/comments/${commentId}/unlike`
};

// AI推荐相关接口
export const aiRecommendApi = {
  // 获取活动推荐
  getActivityRecommendations: '/api/ai/recommend/activities',
  // 获取社团推荐
  getClubRecommendations: '/api/ai/recommend/clubs',
  // 基于兴趣的推荐
  getRecommendationsByInterests: '/api/ai/recommend/interests',
  // 获取相似内容推荐
  getSimilarItems: (type: 'club' | 'activity', id: number) => `/api/ai/recommend/similar?type=${type}&id=${id}`
};

// 搜索相关接口
export const searchApi = {
  // 全局搜索
  globalSearch: '/api/search',
  // 高级搜索
  advancedSearch: '/api/search/advanced',
  // 获取搜索历史
  getSearchHistory: '/api/search/history',
  // 清除搜索历史
  clearSearchHistory: '/api/search/history/clear',
  // 获取热门搜索
  getPopularSearches: '/api/search/popular'
};

// 标签相关接口
export const tagApi = {
  // 获取所有标签
  getTags: '/api/tags',
  // 获取热门标签
  getPopularTags: '/api/tags/popular',
  // 添加标签
  addTag: '/api/tags',
  // 删除标签
  deleteTag: (id: number) => `/api/tags/${id}`
};

// 导出所有API接口
export default {
  auth: authApi,
  club: clubApi,
  activity: activityApi,
  stats: statsApi,
  favorite: favoriteApi,
  message: messageApi,
  comment: commentApi,
  aiRecommend: aiRecommendApi,
  search: searchApi,
  tag: tagApi
};

// API响应结构定义
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// 分页响应结构
export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

// 错误响应结构
export interface ErrorResponse {
  code: number;
  message: string;
  details?: string;
  fieldErrors?: Record<string, string>;
}

// JWT登录响应类型
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    id: number;
    username: string;
    name: string;
    avatar: string;
    role: string;
    email?: string;
    phone?: string;
    joinedClubs?: number;
    participatedActivities?: number;
  };
}

// JWT刷新Token响应类型
export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}