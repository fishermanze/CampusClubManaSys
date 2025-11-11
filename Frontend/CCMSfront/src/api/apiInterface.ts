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
  // 刷新Token
  refreshToken: '/auth/refresh'
};

// 社团相关接口
export const clubApi = {
  // 获取社团列表
  getClubs: '/club/list',
  // 获取社团详情
  getClubById: (id: number) => `/club/${id}`,
  // 创建社团
  createClub: '/club/create',
  // 申请加入社团（调整为后端实际路由：POST /clubs/applications/{clubId}）
  applyJoinClub: (clubId: number) => `/clubs/applications/${clubId}`,
  // 获取某社团的待审批申请列表（调整为后端实际路由：GET /clubs/applications/club/{clubId}?status=0）
  getPendingApplies: (clubId: number) => `/clubs/applications/club/${clubId}?status=0`,
  // 审核入团申请（调整为后端实际路由：PUT /clubs/{clubId}/members/{userId}/approve`）
  handleClubApply: (clubId: number, userId: number) => `/clubs/${clubId}/members/${userId}/approve`,
  // 获取某用户的社团申请列表（新增：GET /clubs/applications/user/{userId}）
  getUserApplies: (userId: number) => `/clubs/applications/user/${userId}`,
  // 踢出成员
  kickMember: '/club/kick',
  // 获取用户加入的社团
  getUserClubs: '/club/my-clubs',
  // 获取社团成员列表
  getClubMembers: (clubId: number) => `/club/${clubId}/members`,
  // 获取社团成员数量
  getClubMemberCount: (clubId: number) => `/club/${clubId}/member-count`
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
  getFavorites: '/favorites',
  // 添加收藏
  addFavorite: '/favorites',
  // 移除收藏
  removeFavorite: (id: number) => `/favorites/${id}`,
  // 检查是否已收藏
  checkFavorite: (type: 'club' | 'activity', id: number) => `/favorites/check?type=${type}&id=${id}`
};

// 消息相关接口
export const messageApi = {
  // 获取消息列表
  getMessages: '/messages',
  // 获取未读消息数量
  getUnreadCount: '/messages/unread',
  // 标记消息已读
  markAsRead: (id: number) => `/messages/${id}/read`,
  // 标记所有消息已读
  markAllAsRead: '/messages/read-all',
  // 发送消息
  sendMessage: '/messages',
  // 删除消息
  deleteMessage: (id: number) => `/messages/${id}`
};

// 评论相关接口
export const commentApi = {
  // 获取评论列表
  getComments: (type: 'club' | 'activity', id: number) => `/${type}s/${id}/comments`,
  // 添加评论
  addComment: (type: 'club' | 'activity', id: number) => `/${type}s/${id}/comments`,
  // 删除评论
  deleteComment: (commentId: number) => `/comments/${commentId}`,
  // 点赞评论
  likeComment: (commentId: number) => `/comments/${commentId}/like`,
  // 取消点赞评论
  unlikeComment: (commentId: number) => `/comments/${commentId}/unlike`
};

// 搜索相关接口
export const searchApi = {
  // 全局搜索
  globalSearch: '/search',
  // 高级搜索
  advancedSearch: '/search/advanced',
  // 获取搜索历史
  getSearchHistory: '/search/history',
  // 清除搜索历史
  clearSearchHistory: '/search/history/clear',
  // 获取热门搜索
  getPopularSearches: '/search/popular'
};

// 标签相关接口
export const tagApi = {
  // 获取所有标签
  getTags: '/tags',
  // 获取热门标签
  getPopularTags: '/tags/popular',
  // 添加标签
  addTag: '/tags',
  // 删除标签
  deleteTag: (id: number) => `/tags/${id}`
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