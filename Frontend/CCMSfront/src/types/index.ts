// 用户类型
export interface User {
  userId: number
  username: string
  realName: string
  role: 'member' | 'club_manager' | 'admin'
  avatarUrl?: string
  email?: string
  phone?: string
  department?: string
  grade?: string
  hobbies?: string
}

// 社团类型
export interface Club {
  clubId: number
  clubName: string
  description?: string
  establishDate?: string
  rating: number
  advisor?: string
  contactEmail?: string
  clubAim?: string
  logoUrl?: string
  memberCount?: number
}

// 活动类型
export interface Activity {
  activityId: number
  clubId: number
  activityName: string
  activityType?: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  maxParticipants?: number
  imageUrl?: string
  status: number
  clubName?: string
}

// 通知类型
export interface Notification {
  id: number
  content: string
  isNew: boolean
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
  uid:string
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