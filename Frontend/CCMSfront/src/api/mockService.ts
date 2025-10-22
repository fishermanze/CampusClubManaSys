import type { User, Club, Activity, Stats } from '@/types'

// JWT相关工具函数
const generateMockToken = (userId: number, role: string): string => {
  // 生成模拟的JWT token（实际只是形式上的字符串）
  const expiresIn = 3600; // 1小时
  const expiryTime = Math.floor(Date.now() / 1000) + expiresIn;
  return `mock_jwt_token_${userId}_${role}_${expiryTime}`;
};

const generateMockRefreshToken = (userId: number): string => {
  // 生成模拟的refresh token
  return `mock_refresh_token_${userId}_${Date.now()}`;
};

// 模拟用户数据
const mockUsers: User[] = [
  {
    id: 1,
    username: 'student1',
    password: '123456', // 实际项目中应存储加密后的密码
    email: 'student1@example.com',
    phone: '13800138000',
    avatar: 'https://via.placeholder.com/64',
    name: '张同学',
    role: 'student',
    joinedClubs: 3,
    participatedActivities: 12
  }
];

// 模拟用户数据 (保持原有引用)
const mockUser: User = mockUsers[0];

// 存储token的映射（模拟后端token存储）
const tokenStore: Map<string, { userId: number; expiresAt: number }> = new Map();

// 模拟社团数据
const mockClubs: Club[] = [
  {
    id: 1,
    name: '科技创新社',
    description: '专注于科技创新和项目实践的学生社团',
    coverImage: 'https://via.placeholder.com/300x200',
    memberCount: 120,
    category: '科技',
    tags: ['编程', '项目开发', '人工智能'],
    isJoined: true
  },
  {
    id: 2,
    name: '文学社',
    description: '探讨文学作品，创作原创内容',
    coverImage: 'https://via.placeholder.com/300x200',
    memberCount: 85,
    category: '人文',
    tags: ['阅读', '写作', '诗词'],
    isJoined: false
  },
  {
    id: 3,
    name: '篮球俱乐部',
    description: '热爱篮球运动，定期组织训练和比赛',
    coverImage: 'https://via.placeholder.com/300x200',
    memberCount: 65,
    category: '体育',
    tags: ['篮球', '运动', '团队'],
    isJoined: true
  },
  {
    id: 4,
    name: '摄影协会',
    description: '记录美好生活，分享摄影技巧',
    coverImage: 'https://via.placeholder.com/300x200',
    memberCount: 95,
    category: '艺术',
    tags: ['摄影', '后期', '分享'],
    isJoined: false
  }
]

// 模拟活动数据
const mockActivities: Activity[] = [
  {
    id: 1,
    title: '黑客马拉松编程大赛',
    description: '48小时极限编程挑战，展示创新创意',
    coverImage: 'https://via.placeholder.com/300x200',
    startTime: '2024-06-15T09:00:00',
    endTime: '2024-06-17T17:00:00',
    location: '创新中心A座',
    organizer: '科技创新社',
    participantCount: 45,
    status: 1,
    isJoined: false
  },
  {
    id: 2,
    title: '校园文学沙龙',
    description: '交流阅读心得，探讨经典文学作品',
    coverImage: 'https://via.placeholder.com/300x200',
    startTime: '2024-06-10T14:00:00',
    endTime: '2024-06-10T16:30:00',
    location: '图书馆多功能厅',
    organizer: '文学社',
    participantCount: 30,
    status: 1,
    isJoined: true
  },
  {
    id: 3,
    title: '篮球友谊赛',
    description: '社团之间的篮球交流比赛',
    coverImage: 'https://via.placeholder.com/300x200',
    startTime: '2024-06-12T15:00:00',
    endTime: '2024-06-12T17:00:00',
    location: '体育馆篮球场',
    organizer: '篮球俱乐部',
    participantCount: 20,
    status: 1,
    isJoined: true
  },
  {
    id: 4,
    title: '校园摄影展',
    description: '展示社团成员优秀摄影作品',
    coverImage: 'https://via.placeholder.com/300x200',
    startTime: '2024-06-20T10:00:00',
    endTime: '2024-06-22T18:00:00',
    location: '艺术中心展厅',
    organizer: '摄影协会',
    participantCount: 120,
    status: 1,
    isJoined: false
  },
  {
    id: 5,
    title: 'AI技术讲座',
    description: '邀请行业专家讲解人工智能最新发展',
    coverImage: 'https://via.placeholder.com/300x200',
    startTime: '2024-06-08T19:00:00',
    endTime: '2024-06-08T21:00:00',
    location: '学术报告厅',
    organizer: '科技创新社',
    participantCount: 200,
    status: 1,
    isJoined: false
  }
]

// 模拟统计数据
const mockStats: Stats = {
  totalClubs: 42,
  totalActivities: 156,
  activeUsers: 2890,
  recentActivities: 12,
  chartData: {
    labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
    activityCount: [15, 20, 25, 22, 30, 35],
    newMembers: [50, 65, 80, 75, 90, 100]
  }
}

// Mock API 服务
export const mockApiService = {
  // 模拟延迟
  delay: (ms: number = 500) => new Promise(resolve => setTimeout(resolve, ms)),
  
  // 用户认证相关API
  auth: {
    // 用户登录
    login: async (credentials: any): Promise<{ data: any }> => {
      await mockApiService.delay();
      const { username, password } = credentials;
      const user = mockUsers.find(u => u.username === username && u.password === password);
      
      if (!user) {
        throw new Error('用户名或密码错误');
      }
      
      // 生成JWT相关token
      const accessToken = generateMockToken(user.id, user.role);
      const refreshToken = generateMockRefreshToken(user.id);
      const expiresIn = 3600; // 1小时
      
      // 存储refresh token信息
      tokenStore.set(refreshToken, {
        userId: user.id,
        expiresAt: Date.now() + expiresIn * 1000 * 24 // refresh token有效期24小时
      });
      
      // 模拟登录成功，返回JWT相关信息
      return {
        data: {
          accessToken,
          refreshToken,
          expiresIn,
          user: {
            id: user.id,
            username: user.username,
            name: user.name,
            avatar: user.avatar,
            role: user.role,
            email: user.email,
            phone: user.phone,
            joinedClubs: user.joinedClubs,
            participatedActivities: user.participatedActivities
          }
        }
      };
    },

    // 验证码登录
    loginByCode: async (credentials: any): Promise<{ data: any }> => {
      await mockApiService.delay();
      const { phone } = credentials;
      const user = mockUsers.find(u => u.phone === phone);
      
      if (!user) {
        throw new Error('用户不存在');
      }
      
      // 生成JWT相关token
      const accessToken = generateMockToken(user.id, user.role);
      const refreshToken = generateMockRefreshToken(user.id);
      const expiresIn = 3600; // 1小时
      
      // 存储refresh token信息
      tokenStore.set(refreshToken, {
        userId: user.id,
        expiresAt: Date.now() + expiresIn * 1000 * 24 // refresh token有效期24小时
      });
      
      return {
        data: {
          accessToken,
          refreshToken,
          expiresIn,
          user: {
            id: user.id,
            username: user.username,
            name: user.name,
            avatar: user.avatar,
            role: user.role,
            email: user.email,
            phone: user.phone,
            joinedClubs: user.joinedClubs,
            participatedActivities: user.participatedActivities
          }
        }
      };
    },

    // 刷新Token
    refreshToken: async (data: any): Promise<{ data: any }> => {
      await mockApiService.delay();
      const { refreshToken } = data;
      const tokenInfo = tokenStore.get(refreshToken);
      
      if (!tokenInfo || Date.now() > tokenInfo.expiresAt) {
        // refresh token不存在或已过期
        throw new Error('Invalid refresh token');
      }
      
      const user = mockUsers.find(u => u.id === tokenInfo.userId);
      if (!user) {
        throw new Error('User not found');
      }
      
      // 生成新的token
      const newAccessToken = generateMockToken(user.id, user.role);
      const newRefreshToken = generateMockRefreshToken(user.id);
      const expiresIn = 3600; // 1小时
      
      // 更新refresh token
      tokenStore.delete(refreshToken);
      tokenStore.set(newRefreshToken, {
        userId: user.id,
        expiresAt: Date.now() + expiresIn * 1000 * 24
      });
      
      return {
        data: {
          accessToken: newAccessToken,
          refreshToken: newRefreshToken,
          expiresIn
        }
      };
    },

    // 发送验证码
    sendVerificationCode: async (phone: string, type: string) => {
      await mockApiService.delay();
      return { 
        data: { 
          success: true, 
          message: '验证码已发送',
          // 为了测试方便，返回验证码
          verificationCode: '123456'
        } 
      };
    },
    
    register: async (data: any) => {
      await mockApiService.delay();
      
      // 模拟注册成功
      const newUser: User = {
        id: mockUsers.length + 1,
        username: data.username,
        password: data.password, // 实际项目中应加密
        email: data.email || '',
        phone: data.phone,
        avatar: 'https://via.placeholder.com/64',
        name: data.name || data.username,
        role: 'student',
        joinedClubs: 0,
        participatedActivities: 0
      };
      
      mockUsers.push(newUser);
      
      // 生成JWT相关token
      const accessToken = generateMockToken(newUser.id, newUser.role);
      const refreshToken = generateMockRefreshToken(newUser.id);
      const expiresIn = 3600; // 1小时
      
      return {
        data: {
          accessToken,
          refreshToken,
          expiresIn,
          user: {
            id: newUser.id,
            username: newUser.username,
            name: newUser.name,
            avatar: newUser.avatar,
            role: newUser.role,
            email: newUser.email,
            phone: newUser.phone,
            joinedClubs: newUser.joinedClubs,
            participatedActivities: newUser.participatedActivities
          }
        }
      };
    },
    
    resetPassword: async (data: any) => {
      await mockApiService.delay();
      const { phone } = data;
      const user = mockUsers.find(u => u.phone === phone);
      
      if (!user) {
        throw new Error('用户不存在');
      }
      
      // 模拟重置密码
      user.password = data.newPassword;
      
      return { data: { success: true, message: '密码重置成功' } };
    },
    
    getCurrentUser: async () => {
      await mockApiService.delay();
      return { data: mockUser };
    },
    
    logout: async () => {
      await mockApiService.delay();
      // 清除所有token（在实际应用中，应该只清除当前用户的refresh token）
      tokenStore.clear();
      return { data: { success: true } };
    }
  },
  
  // 社团相关
  club: {
    getRecommendedClubs: async () => {
      await mockApiService.delay()
      return { data: mockClubs }
    },
    
    getClubDetail: async (clubId: number) => {
      await mockApiService.delay()
      const club = mockClubs.find(c => c.id === clubId)
      if (!club) {
        throw new Error('社团不存在')
      }
      return { data: club }
    },
    
    getClubActivities: async (clubId: number) => {
      await mockApiService.delay()
      // 返回该社团组织的活动
      const club = mockClubs.find(c => c.id === clubId)
      if (!club) {
        throw new Error('社团不存在')
      }
      const activities = mockActivities.filter(a => a.organizer === club.name)
      return { data: activities }
    },
    
    joinClub: async (clubId: number) => {
      await mockApiService.delay()
      return { data: { success: true, message: '加入成功' } }
    },
    
    leaveClub: async (clubId: number) => {
      await mockApiService.delay()
      return { data: { success: true, message: '退出成功' } }
    }
  },
  
  // 活动相关
  activity: {
    getActivities: async (params?: any) => {
      await mockApiService.delay()
      return {
        data: {
          list: mockActivities,
          total: mockActivities.length
        }
      }
    },
    
    getActivityDetail: async (activityId: number) => {
      await mockApiService.delay()
      const activity = mockActivities.find(a => a.id === activityId)
      if (!activity) {
        throw new Error('活动不存在')
      }
      return { data: activity }
    },
    
    joinActivity: async (activityId: number) => {
      await mockApiService.delay()
      return { data: { success: true, message: '报名成功' } }
    },
    
    cancelActivity: async (activityId: number) => {
      await mockApiService.delay()
      return { data: { success: true, message: '取消成功' } }
    }
  },
  
  // 统计相关
  stats: {
    getUserStats: async () => {
      await mockApiService.delay()
      return { data: mockStats }
    },
    
    getActivityStats: async (params?: any) => {
      await mockApiService.delay()
      return { data: mockStats.chartData }
    }
  }
}

export default mockApiService