<template>
  <div class="home-container">
    <!-- 左侧导航栏 -->
    <aside class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <i class="fa fa-graduation-cap"></i>
          <span :class="{ hidden: sidebarCollapsed }">校园社团管理</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li class="nav-item">
            <router-link to="/home" class="nav-link">
              <i class="fa fa-home"></i>
              <span :class="{ hidden: sidebarCollapsed }">首页</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/my-clubs" class="nav-link">
              <i class="fa fa-users"></i>
              <span :class="{ hidden: sidebarCollapsed }">我的社团</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/activities" class="nav-link">
              <i class="fa fa-calendar"></i>
              <span :class="{ hidden: sidebarCollapsed }">活动管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/applications" class="nav-link">
              <i class="fa fa-file-text"></i>
              <span :class="{ hidden: sidebarCollapsed }">申请记录</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/notifications" class="nav-link">
              <i class="fa fa-bell"></i>
              <span :class="{ hidden: sidebarCollapsed }">通知中心</span>
              <span class="badge">3</span>
            </router-link>
          </li>
          <li class="nav-item active">
            <router-link to="/profile" class="nav-link">
              <i class="fa fa-user"></i>
              <span :class="{ hidden: sidebarCollapsed }">个人资料</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/settings" class="nav-link">
              <i class="fa fa-cog"></i>
              <span :class="{ hidden: sidebarCollapsed }">设置</span>
            </router-link>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content" :class="{ 'content-expanded': sidebarCollapsed }">
      <!-- 顶部信息栏 -->
      <header class="top-bar">
        <div class="top-bar-left">
          <button
            class="menu-toggle"
            @click="toggleSidebar"
            :aria-label="sidebarCollapsed ? '展开侧边栏' : '收起侧边栏'"
          >
            <i class="fa fa-bars"></i>
          </button>
          <h1 class="welcome-text">个人资料</h1>
        </div>

        <div class="top-bar-right">
          <div class="user-profile">
            <div class="user-info" @click="toggleUserMenu">
              <img
                :src="currentUser?.avatar || '/default-avatar.png'"
                alt="用户头像"
                class="avatar"
              />
              <span>{{ currentUser?.realName || "用户" }}</span>
              <i class="fa fa-chevron-down"></i>
            </div>

            <!-- 用户下拉菜单 -->
            <div v-if="showUserMenu" class="user-dropdown" @click.stop>
              <router-link
                to="/profile"
                class="dropdown-item"
                @click="showUserMenu = false"
              >
                <i class="fa fa-user"></i> 个人资料
              </router-link>
              <router-link
                to="/settings"
                class="dropdown-item"
                @click="showUserMenu = false"
              >
                <i class="fa fa-cog"></i> 账号设置
              </router-link>
              <div class="dropdown-divider"></div>
              <a href="#" class="dropdown-item logout" @click="logout">
                <i class="fa fa-sign-out"></i> 退出登录
              </a>
            </div>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="page-content profile-page">
        <div class="profile-container">
          <!-- 基本信息 -->
          <div class="profile-header">
            <div class="avatar-section">
              <div class="avatar-upload">
                <img
                  :src="userData.avatar || '/default-avatar.png'"
                  alt="用户头像"
                  class="profile-avatar"
                />
                <label class="avatar-upload-btn">
                  <input type="file" accept="image/*" @change="handleAvatarUpload" />
                  <i class="fa fa-camera"></i>
                </label>
              </div>
              <h2 class="profile-name">{{ userData.realName }}</h2>
              <p class="profile-role">{{ getRoleText(userData.role) }}</p>
            </div>
          </div>

          <!-- 详细资料 -->
          <div class="profile-details">
            <div class="profile-card">
              <h3 class="card-title">基本信息</h3>
              <div class="form-group">
                <label>用户名</label>
                <span class="form-value">{{ userData.username }}</span>
              </div>
              <div class="form-group">
                <label>真实姓名</label>
                <span class="form-value">{{ userData.realName }}</span>
              </div>
              <div class="form-group">
                <label>邮箱</label>
                <span class="form-value">{{ userData.email || "未设置" }}</span>
              </div>
              <div class="form-group">
                <label>手机号</label>
                <span class="form-value">{{ userData.phone || "未设置" }}</span>
              </div>
            </div>

            <div class="profile-card">
              <h3 class="card-title">学校信息</h3>
              <div class="form-group">
                <label>学号</label>
                <span class="form-value">{{ userData.uid }}</span>
              </div>
              <div class="form-group">
                <label>学院</label>
                <span class="form-value">{{ userData.department || "未设置" }}</span>
              </div>
              <div class="form-group">
                <label>年级</label>
                <span class="form-value">{{ userData.grade || "未设置" }}</span>
              </div>
              <div class="form-group">
                <label>兴趣爱好</label>
                <span class="form-value">{{ userData.hobbies || "未设置" }}</span>
              </div>
            </div>
          </div>

          <!-- 社团和活动统计 -->
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-number">{{ userStats.joinedClubs }}</div>
              <div class="stat-label">已加入社团</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ userStats.participatedActivities }}</div>
              <div class="stat-label">参与活动</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ userStats.points }}</div>
              <div class="stat-label">社团积分</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ userStats.achievementLevel }}</div>
              <div class="stat-label">成就等级</div>
            </div>
          </div>

          <!-- 最近活动 -->
          <div class="recent-activities">
            <h3 class="section-title">最近活动</h3>
            <div v-if="recentActivities.length > 0" class="activities-list">
              <div
                v-for="activity in recentActivities"
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-info">
                  <h4>{{ activity.title }}</h4>
                  <p class="activity-club">{{ getclubName(activity.clubId) }}</p>
                  <div class="activity-meta">
                    <span
                      ><i class="fa fa-calendar"></i>
                      {{ formatDate(activity.startTime) }}</span
                    >
                    <span
                      ><i class="fa fa-map-marker"></i>
                      {{ activity.location || "地点待定" }}</span
                    >
                  </div>
                </div>
                <span :class="['status-badge', getStatusClass(activity.status)]">
                  {{ getStatusText(activity.status) }}
                </span>
              </div>
            </div>
            <div v-else class="empty-state">
              <i class="fa fa-calendar-o"></i>
              <p>暂无最近活动</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi, activityApi, clubApi } from "../api/apiService";
import axiosInstance from "../api/axiosInstance";
import type { User, Activity } from "../types/index";

const router = useRouter();

// 侧边栏状态
const sidebarCollapsed = ref(false);
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 用户信息
const currentUser = ref<User | null>(null);
const showUserMenu = ref(false);

// 用户数据
const userData = ref<User>({
  id: 0,
  username: "",
  realName: "",
  role: "member",
  avatar: "",
  email: "",
  phone: "",
  department: "",
  grade: "",
  hobbies: "",
  // 扩展：用于显示学号（来自 auth-service User.uid）
  uid: "",
});

// 用户统计
const userStats = ref({
  joinedClubs: 0,
  participatedActivities: 0,
  points: 0,
  achievementLevel: "初级",
});

// 最近活动
const recentActivities = ref<Activity[]>([]);
const clubNameCache = ref<Map<number, string>>(new Map());

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userInfoStr = localStorage.getItem("userInfo");
    if (userInfoStr) {
      const user = JSON.parse(userInfoStr);
      currentUser.value = user;
      userData.value = {
        id: user.id || 0,
        username: user.username || "",
        realName: user.realName || "",
        role: user.role || "member",
        avatar: user.avatar || "",
        email: user.email || "",
        phone: user.phone || "",
        department: user.department || "",
        grade: user.grade || "",
        hobbies: user.hobbies || "",
        uid: user.uid || "",
      };
      // 进一步拉取详情，填充学号等档案字段
      if (user.id) {
        try {
          const detailResp = await axiosInstance.get(`/user/${user.id}`);
          const detail = detailResp.data || {};
          // 学号优先使用 auth-service User.uid，不覆盖已有 uid
          userData.value.grade = detail.grade || userData.value.grade || "";
          userData.value.hobbies = detail.hobbies || userData.value.hobbies || "";
          userData.value.department = detail.major || userData.value.department || "";
        } catch {}
      }
    } else {
      const response = await authApi.getCurrentUser();
      if (response.data) {
        const user = response.data;
        currentUser.value = user;
        userData.value = {
          id: user.id || 0,
          username: user.username || "",
          realName: user.realName || "",
          role: user.role || "member",
          avatar: user.avatar || "",
          email: user.email || "",
          phone: user.phone || "",
          department: user.department || "",
          grade: user.grade || "",
          hobbies: user.hobbies || "",
          uid: user.uid || "",
        };
        localStorage.setItem("userInfo", JSON.stringify(user));
        // 进一步拉取详情，填充学号等档案字段
        if (user.id) {
          try {
            const detailResp = await axiosInstance.get(`/user/${user.id}`);
            const detail = detailResp.data || {};
            // 学号优先使用 auth-service User.uid，不覆盖已有 uid
            userData.value.grade = detail.grade || userData.value.grade || "";
            userData.value.hobbies = detail.hobbies || userData.value.hobbies || "";
            userData.value.department = detail.major || userData.value.department || "";
          } catch {}
        }
      }
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
  }
};

// 加载用户统计（移除404来源，后端统计接口未就绪时本地估算/默认）
const loadUserStats = async () => {
  try {
    // 默认值
    userStats.value = {
      joinedClubs: 0,
      participatedActivities: 0,
      points: 0,
      achievementLevel: "初级",
    };
    // 可选：从活动接口估算参与次数（仅拉取一页，展示数量以返回长度为准）
    const actsResp = await activityApi.getUserParticipatedActivities(userData.value.id, {
      page: 1,
      size: 10,
    });
    const actsData = actsResp.data;
    const contentCandidates = [
      actsData?.data?.content,
      actsData?.data?.list,
      actsData?.content,
      actsData?.list,
      actsData?.items,
      actsData?.records,
      Array.isArray(actsData) ? actsData : null,
    ];
    const arr = contentCandidates.find((c: any) => Array.isArray(c)) || [];
    userStats.value.participatedActivities = arr.length || 0;
  } catch {
    // 忽略统计加载错误
  }
};

// 加载最近活动
const loadRecentActivities = async () => {
  try {
    const response = await activityApi.getUserParticipatedActivities(userData.value.id, {
      page: 1,
      size: 5,
    });
    if (response.data?.data) {
      const data = response.data.data;
      recentActivities.value = (data.items || data.list || []).map((item: any) => ({
        ...item,
        id: item.id,
        title: item.title,
        clubName: item.clubName || "",
      }));

      // 补全真实 clubName
      const clubIds = Array.from(
        new Set(
          recentActivities.value
            .map((a) => a.clubId)
            .filter((id) => id !== undefined && id !== null)
        )
      ) as number[];
      if (clubIds.length > 0) {
        await Promise.all(
          clubIds.map(async (cid) => {
            if (clubNameCache.value.has(cid)) return;
            try {
              const resp = await clubApi.getClubDetail(cid);
              const name = resp?.data?.name ?? resp?.data?.data?.name ?? "";
              clubNameCache.value.set(cid, name || "未知社团");
            } catch (e) {
              clubNameCache.value.set(cid, "未知社团");
            }
          })
        );
        // 写回 recentActivities 中的 clubName
        recentActivities.value = recentActivities.value.map((a) => {
          if (a.clubId && clubNameCache.value.has(a.clubId)) {
            return { ...a, clubName: clubNameCache.value.get(a.clubId) as string };
          }
          return a;
        });
      }
    }
  } catch (error) {
    console.error("加载最近活动失败:", error);
  }
};

// 获取社团名称（从缓存读取）
const getclubName = (clubId: number | undefined) => {
  if (!clubId) return "未知社团";
  return clubNameCache.value.get(clubId) || "未知社团";
};

// 处理头像上传
const handleAvatarUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  // 这里应该调用上传API，暂时只做本地预览
  const reader = new FileReader();
  reader.onload = (e) => {
    const result = e.target?.result as string;
    if (result) {
      userData.value.avatar = result;
    }
  };
  reader.readAsDataURL(file);
};

// 获取角色文本
const getRoleText = (role: string): string => {
  const roleMap: Record<string, string> = {
    member: "普通成员",
    club_manager: "社团管理员",
    admin: "系统管理员",
  };
  return roleMap[role] || "普通成员";
};

// 格式化日期
const formatDate = (date: string | Date): string => {
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
    d.getDate()
  ).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(
    d.getMinutes()
  ).padStart(2, "0")}`;
};

// 获取状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: "未开始",
    1: "进行中",
    2: "已结束",
    3: "已取消",
  };
  return statusMap[status] || "未知";
};

// 获取状态类名
const getStatusClass = (status: number): string => {
  const classMap: Record<number, string> = {
    0: "upcoming",
    1: "ongoing",
    2: "completed",
    3: "cancelled",
  };
  return classMap[status] || "upcoming";
};

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// 退出登录
const logout = async () => {
  try {
    await authApi.logout();
  } catch (error) {
    console.error("退出登录失败:", error);
  } finally {
    router.push("/login");
  }
};

// 初始化
onMounted(async () => {
  await loadUserInfo();
  if (userData.value.id) {
    await loadUserStats();
    await loadRecentActivities();
  }

  // 点击外部关闭菜单
  document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;
    if (!target.closest(".user-profile")) {
      showUserMenu.value = false;
    }
  });
});
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 250px;
  background-color: #1f2937;
  color: white;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #374151;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.logo i {
  margin-right: 10px;
  font-size: 24px;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 5px 0;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: #d1d5db;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.nav-link:hover {
  background-color: #374151;
  color: white;
}

.nav-link.active {
  background-color: #667eea;
  color: white;
}

.nav-link i {
  margin-right: 15px;
  font-size: 18px;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f3f4f6;
}

/* 顶部信息栏样式 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.menu-toggle {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.user-profile {
  display: flex;
  align-items: center;
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 300px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  margin-top: 10px;
  z-index: 100;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: #374151;
  text-decoration: none;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
}

.dropdown-item i {
  margin-right: 10px;
}

.dropdown-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 5px 0;
}

/* 个人资料页面特定样式 */
.profile-page {
  padding: 30px;
  overflow-y: auto;
}

.profile-container {
  max-width: 1000px;
  margin: 0 auto;
}

/* 头像区域 */
.profile-header {
  background: white;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  margin-bottom: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.avatar-upload {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f3f4f6;
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 40px;
  height: 40px;
  background-color: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 3px solid white;
  transition: background-color 0.3s ease;
}

.avatar-upload-btn:hover {
  background-color: #5a67d8;
}

.avatar-upload-btn input[type="file"] {
  display: none;
}

.profile-name {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 10px 0;
}

.profile-role {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

/* 详细资料 */
.profile-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.profile-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 25px 0;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.form-group {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.form-group:last-child {
  border-bottom: none;
}

.form-group label {
  width: 80px;
  font-weight: 500;
  color: #6b7280;
  flex-shrink: 0;
}

.form-value {
  flex: 1;
  color: #111827;
  font-size: 16px;
}

/* 统计数据 */
.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  color: #6b7280;
}

/* 最近活动 */
.recent-activities {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 25px 0;
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #f3f4f6;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.activity-item:hover {
  background-color: #f9fafb;
}

.activity-info {
  flex: 1;
}

.activity-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 5px 0;
}

.activity-club {
  color: #667eea;
  margin: 0 0 10px 0;
  font-size: 14px;
}

.activity-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #6b7280;
}

.activity-meta i {
  margin-right: 5px;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-badge.upcoming {
  background-color: #dbeafe;
  color: #1d4ed8;
}

.status-badge.ongoing {
  background-color: #dcfce7;
  color: #166534;
}

.status-badge.completed {
  background-color: #f3f4f6;
  color: #6b7280;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 60px;
    position: fixed;
    height: 100vh;
    z-index: 1000;
  }

  .main-content {
    margin-left: 60px;
  }

  .top-bar {
    padding: 15px;
  }

  .user-info span {
    display: none;
  }

  .profile-page {
    padding: 15px;
  }

  .profile-header {
    padding: 30px 20px;
  }

  .profile-avatar {
    width: 120px;
    height: 120px;
  }

  .profile-details {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .profile-card {
    padding: 20px;
  }

  .profile-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-item {
    padding: 20px;
  }

  .recent-activities {
    padding: 20px;
  }

  .activity-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .activity-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
