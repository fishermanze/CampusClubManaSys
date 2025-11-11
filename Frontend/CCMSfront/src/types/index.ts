// 用户类型 - 基于 auth-service User 实体
export interface User {
  id: number
  username: string
  realName: string
  uid?: string
  role: string // member, club_manager, admin
  avatar?: string
  email?: string
  phone?: string
  department?: string
  grade?: string
  hobbies?: string
  enabled?: boolean
  createdAt?: string
  updatedAt?: string
}

// 用户资料类型 - 基于 user-service UserProfile 实体
export interface UserProfile {
  id: number
  userId: number
  studentId?: string
  major?: string
  grade?: string
  className?: string
  gender?: string
  birthDate?: string
  hobbies?: string
  bio?: string
  address?: string
  emergencyContact?: string
  emergencyPhone?: string
  socialMedia?: string // JSON格式
  preferredClubTypes?: string // JSON格式
  avatar?: string
  createdAt?: string
  updatedAt?: string
}

// 用户关注类型 - 基于 user-service UserFollow 实体
export interface UserFollow {
  id: number
  followerId: number
  followedId: number
  isMutual?: boolean
  createdAt?: string
}

// 社团类型 - 基于 club-service Club 实体
export interface Club {
  id: number
  name: string
  description?: string
  logo?: string
  category?: string
  createTime?: string
  updateTime?: string
  status?: number // 0: 待审核, 1: 正常, 2: 已禁用
  leaderId?: number
  memberCount?: number
  contactInfo?: string
  totalActivityCount?: number
}

// 社团成员类型 - 基于 club-service ClubMember 实体
export interface ClubMember {
  id: number
  clubId: number
  userId: number
  role?: number // 0: 普通成员, 1: 社团负责人, 2: 管理员
  status?: number // 0: 待审核, 1: 已加入, 2: 已退出, 3: 已开除
  joinTime?: string
  updateTime?: string
  activityCount?: number
  totalScore?: number
  level?: number
  yearEvaluationStatus?: number // 0: 未评价, 1: 已评价
}

// 活动类型 - 基于 activity-service Activity 实体
export interface Activity {
  id: number
  title: string
  description?: string
  content?: string
  coverImage?: string
  images?: string // JSON格式存储
  startTime: string
  endTime: string
  location: string
  organizerId: number
  clubId: number
  status: number // 0-草稿 1-已发布 2-进行中 3-已完成 4-已取消
  maxParticipants?: number
  currentParticipants?: number
  enrollmentDeadline?: string
  tags?: string // JSON格式存储
  needApproval?: boolean
  createdAt?: string
  updatedAt?: string
}

// 活动参与者类型 - 基于 activity-service ActivityParticipant 实体
export interface ActivityParticipant {
  id: number
  activityId: number
  userId: number
  status: number // 0-待审核 1-已通过 2-已拒绝 3-已参加 4-未参加
  enrollmentTime?: string
  approvalTime?: string
  approvedBy?: number
  approvalRemark?: string
  checkInTime?: string
  checkOutTime?: string
  enrollmentInfo?: string
  updatedAt?: string
}

// 活动评论类型 - 基于 activity-service ActivityCommentDTO
export interface ActivityComment {
  id: number
  activityId: number
  userId: number
  userName?: string
  userAvatar?: string
  content: string
  parentId?: number
  likesCount?: number
  status?: number // 0-正常 1-已删除 2-已禁用
  createdAt?: string
  updatedAt?: string
  isLiked?: boolean
  replies?: ActivityComment[]
  replyCount?: number
}

// 消息类型 - 基于 message-service Message 实体
export interface Message {
  id: number
  senderId: number
  receiverId: number
  messageType: number // 1-聊天消息 2-通知消息
  content: string
  status: number // 0-未读 1-已读 2-已删除
  relatedId?: number
  relatedType?: string
  createdAt?: string
  updatedAt?: string
}

// 通知类型 - 基于 message-service Notification 实体
export interface Notification {
  id: number
  userId: number
  notificationType: number // 1-系统通知 2-社团通知 3-活动通知 4-互动通知
  title: string
  content: string
  status: number // 0-未读 1-已读
  relatedId?: number
  relatedType?: string
  needPush?: boolean
  createdAt: string
  updatedAt?: string
  actionLink?: {
    url?: string
    icon?: string
    text?: string
  }
}

// 消息阅读记录类型 - 基于 message-service MessageReadRecord 实体
export interface MessageReadRecord {
  id: number
  userId: number
  otherUserId: number
  lastReadMessageId?: number
  lastReadTime?: string
  unreadCount?: number
  isSticky?: boolean
  isMuted?: boolean
  createdAt?: string
  updatedAt?: string
}

// 统计数据类型 - 基于 stats-service StatData 实体
export interface StatData {
  id: number
  statType?: string // 统计类型
  clubId?: number
  userId?: number
  dataKey?: string
  dataValue?: number
  statDate?: string
  extraInfo?: string // JSON格式
}

// 登录表单类型
export interface LoginForm {
  username: string
  password: string
  rememberMe: boolean
}

// 注册表单类型
export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  realName: string
  uid: string
  phone: string
  verificationCode: string
}

// 验证码登录表单类型
export interface CodeLoginForm {
  phone: string
  verificationCode: string
}

// 忘记密码表单类型
export interface ForgotPasswordForm {
  phone: string
  verificationCode: string
  newPassword: string
  confirmPassword: string
}
