<template>
  <div class="home-container">
    <aside class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <i class="fa fa-graduation-cap"></i>
          <span :class="{ hidden: sidebarCollapsed }">校园社团管理</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li class="nav-item active">
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
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/profile" class="nav-link">
              <i class="fa fa-user"></i>
              <span :class="{ hidden: sidebarCollapsed }">个人资料</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/ai-assistant" class="nav-link">
              <i class="fa fa-comments"></i>
              <span :class="{ hidden: sidebarCollapsed }">AI助手</span>
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
          <h1 class="welcome-text">欢迎回来，{{ currentUser?.realName || "用户" }}</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input type="text" placeholder="搜索社团、活动..." />
            <i class="fa fa-search"></i>
          </div>

          <div class="user-profile">
            <div class="notification-icon" @click="toggleNotifications">
              <i class="fa fa-bell"></i>
              <span class="badge" v-if="stats.unreadNotifications > 0">{{
                stats.unreadNotifications
              }}</span>
            </div>
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

            <!-- 通知下拉菜单 -->
            <div v-if="showNotifications" class="notifications-dropdown" @click.stop>
              <div class="dropdown-header">
                <h4>通知</h4>
              </div>
              <div class="notification-item">
                <i class="fa fa-bell text-primary"></i>
                <div class="notification-content">
                  <p>社团活动即将开始</p>
                  <span>10分钟前</span>
                </div>
              </div>
              <div class="notification-item">
                <i class="fa fa-check-circle text-success"></i>
                <div class="notification-content">
                  <p>您已成功加入编程社团</p>
                  <span>2小时前</span>
                </div>
              </div>
              <div class="notification-item">
                <i class="fa fa-info-circle text-warning"></i>
                <div class="notification-content">
                  <p>系统维护通知</p>
                  <span>昨天</span>
                </div>
              </div>
              <router-link
                to="/notifications"
                class="view-all-link"
                @click="showNotifications = false"
              >
                查看全部
              </router-link>
            </div>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 数据统计部分 -->
        <section class="section" id="dashboard-stats">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon bg-primary">
                <i class="fa fa-users"></i>
              </div>
              <div class="stat-content">
                <h3>{{ stats.joinedClubs || "0" }}</h3>
                <p>已加入社团</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon bg-success">
                <i class="fa fa-calendar"></i>
              </div>
              <div class="stat-content">
                <h3>{{ stats.participatedActivities || "0" }}</h3>
                <p>参与活动</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon bg-warning">
                <i class="fa fa-file-text"></i>
              </div>
              <div class="stat-content">
                <h3>{{ stats.pendingApplications || "0" }}</h3>
                <p>待处理申请</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon bg-danger">
                <i class="fa fa-bell"></i>
              </div>
              <div class="stat-content">
                <h3>{{ stats.unreadNotifications || "0" }}</h3>
                <p>未读通知</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 活跃度图表部分 -->
        <section class="section" id="activity-chart">
          <div class="section-header">
            <h2>本周活跃度</h2>
          </div>
          <div class="chart-container">
            <div v-if="chartLoading" class="chart-state">
              <div class="loading-spinner"></div>
              <p>正在加载活跃度数据...</p>
            </div>
            <div v-else-if="chartError" class="chart-state error">
              <i class="fa fa-exclamation-circle"></i>
              <p>{{ chartError }}</p>
            </div>
            <div v-else-if="activityTrend.length === 0" class="chart-state empty">
              <i class="fa fa-line-chart"></i>
              <p>暂无活跃度数据</p>
            </div>
            <div v-else class="activity-chart">
              <div class="chart-bars">
                <div class="chart-bar" v-for="point in activityTrend" :key="point.label">
                  <div class="bar-wrapper">
                    <div
                      class="bar"
                      :style="{
                        height: `${Math.max(8, (point.value / maxChartValue) * 100)}%`,
                      }"
                    ></div>
                  </div>
                  <span class="label">{{ point.label }}</span>
                  <span class="value">{{ point.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 社团详情模态框 -->
    <div v-if="selectedClub" class="modal-overlay" @click="closeClubModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedClub.name }}</h3>
          <button class="close-btn" @click="closeClubModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="club-detail">
            <img :src="selectedClub.logo" alt="社团头像" class="club-detail-avatar" />
            <div class="club-detail-info">
              <h4>{{ selectedClub.name }}</h4>
              <p>{{ selectedClub.description }}</p>
              <div class="club-detail-stats">
                <div class="stat-item">
                  <i class="fa fa-users"></i>
                  <span>{{ selectedClub.memberCount || 0 }}人</span>
                </div>
                <div class="stat-item">
                  <i class="fa fa-calendar"></i>
                  <span>{{ selectedClub.totalActivityCount || 0 }}活动</span>
                </div>
                <div class="stat-item">
                  <i class="fa fa-building"></i>
                  <span>{{ selectedClub.category || "未分类" }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="club-activities">
            <h4>近期活动</h4>
            <ul v-if="selectedClubActivities.length > 0">
              <li v-for="activity in selectedClubActivities" :key="activity.id">
                <i class="fa fa-calendar-check-o"></i>
                <span>{{ activity.title }} - {{ activity.startTime }}</span>
              </li>
            </ul>
            <p v-else class="empty-activities">暂无活动</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeClubModal">关闭</button>
          <button class="btn btn-primary">立即加入</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  authApi,
  clubApi,
  activityApi,
  notificationApi,
  activityParticipantApi,
} from "../api/apiService";
import axiosInstance from "../api/axiosInstance";
import type { User, Club, Activity } from "../types/index";

const router = useRouter();

// 侧边栏状态
const sidebarCollapsed = ref(false);
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 用户信息
const currentUser = ref<User | null>(null);
const showUserMenu = ref(false);
const showNotifications = ref(false);

// 统计数据
const stats = ref({
  joinedClubs: 0,
  participatedActivities: 0,
  pendingApplications: 0,
  unreadNotifications: 0,
});

// 活跃度趋势数据
interface TrendPoint {
  label: string;
  value: number;
}
const activityTrend = ref<TrendPoint[]>([]);
const chartLoading = ref(false);
const chartError = ref("");

// 选中的社团
const selectedClub = ref<Club | null>(null);
// 选中社团的活动列表
const selectedClubActivities = ref<Activity[]>([]);

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userInfoStr = localStorage.getItem("userInfo");
    if (userInfoStr) {
      currentUser.value = JSON.parse(userInfoStr);
    } else {
      const response = await authApi.getCurrentUser();
      if (response.data) {
        currentUser.value = response.data;
        localStorage.setItem("userInfo", JSON.stringify(response.data));
      }
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
  }
};

// 加载统计数据
const loadStats = async () => {
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);

    if (!uid) {
      console.warn("用户ID不存在，无法加载统计数据");
      return;
    }

    // 1. 加载未读通知数
    try {
      const notificationResponse = await notificationApi.getUnreadCount(uid);
      stats.value.unreadNotifications = Number(notificationResponse.data) || 0;
      console.log("未读通知数:", stats.value.unreadNotifications);
    } catch (error) {
      console.error("加载未读通知数失败:", error);
      stats.value.unreadNotifications = 0;
    }

    // 2. 加载已加入社团数
    try {
      const userClubsResp = await clubApi.getUserClubs(uid);
      const memberList = userClubsResp.data || [];
      if (Array.isArray(memberList)) {
        // 过滤出已通过审核的社团成员记录（status=1表示已通过）
        stats.value.joinedClubs = memberList.filter((m: any) => m?.status === 1).length;
      } else {
        stats.value.joinedClubs = 0;
      }
      console.log("已加入社团数:", stats.value.joinedClubs);
    } catch (error) {
      console.error("加载已加入社团数失败:", error);
      stats.value.joinedClubs = 0;
    }

    // 3. 加载参与活动数（使用活动报名API）
    try {
      const participationsResp = await activityParticipantApi.getUserParticipations(uid, {
        page: 0,
        size: 1000, // 获取所有参与记录
      });

      // 解析响应数据
      let participationList: any[] = [];
      const respData = participationsResp.data;

      if (respData) {
        // 处理分页响应格式
        if (respData.content && Array.isArray(respData.content)) {
          participationList = respData.content;
        } else if (
          respData.data &&
          respData.data.content &&
          Array.isArray(respData.data.content)
        ) {
          participationList = respData.data.content;
        } else if (Array.isArray(respData)) {
          participationList = respData;
        } else if (respData.list && Array.isArray(respData.list)) {
          participationList = respData.list;
        }
      }

      // 统计已通过或已参加的活动（status=1已通过，status=3已参加）
      stats.value.participatedActivities = participationList.filter(
        (p: any) => p?.status === 1 || p?.status === 3
      ).length;
      console.log("参与活动数:", stats.value.participatedActivities);
    } catch (error) {
      console.error("加载参与活动数失败:", error);
      // 如果活动报名API失败，尝试使用活动API
      try {
        const actsResp = await activityApi.getUserParticipatedActivities(uid, {
          page: 0,
          size: 1000,
        });
        const respData = actsResp.data;
        let activityList: any[] = [];

        if (respData?.content && Array.isArray(respData.content)) {
          activityList = respData.content;
        } else if (respData?.data?.content && Array.isArray(respData.data.content)) {
          activityList = respData.data.content;
        } else if (Array.isArray(respData)) {
          activityList = respData;
        }

        stats.value.participatedActivities = activityList.length;
        console.log("参与活动数（备用方法）:", stats.value.participatedActivities);
      } catch (fallbackError) {
        console.error("备用方法加载参与活动数也失败:", fallbackError);
        stats.value.participatedActivities = 0;
      }
    }

    // 4. 加载待处理申请数（社团申请 + 活动创建申请）
    try {
      const role = (currentUser.value as any)?.role || localStorage.getItem("role") || "";
      const managerRoles = ["ADMIN", "admin", "CLUB_ADMIN", "club_admin", "club_manager"];
      const isManager = managerRoles.includes(role as string);
      const isSystemAdmin = uid === 1 || role === "ADMIN" || role === "admin";

      let pendingCount = 0;

      if (isManager) {
        // 管理员/社团管理员：统计自己管理社团的待审核申请
        try {
          const clubsResp = await axiosInstance.get(`/clubs/leader/${uid}`);
          const clubsData = clubsResp.data || [];
          const clubs = Array.isArray(clubsData) ? clubsData : clubsData.list || [];

          for (const club of clubs) {
            const clubId = Number(club?.id ?? club?.clubId);
            if (!clubId || Number.isNaN(clubId)) continue;
            try {
              const appsResp = await axiosInstance.get(
                `/clubs/applications/club/${clubId}?status=0`
              );
              let clubApplications: any[] = [];
              const data = appsResp.data;
              if (Array.isArray(data)) {
                clubApplications = data;
              } else if (data?.data && Array.isArray(data.data)) {
                clubApplications = data.data;
              } else if (data?.list && Array.isArray(data.list)) {
                clubApplications = data.list;
              } else if (data?.content && Array.isArray(data.content)) {
                clubApplications = data.content;
              }
              pendingCount += clubApplications.filter((x: any) => x?.status === 0).length;
            } catch (clubErr) {
              console.error(`加载社团 ${clubId} 待审核申请失败:`, clubErr);
            }
          }
        } catch (leaderErr) {
          console.error("加载管理社团列表失败:", leaderErr);
        }
      } else {
        // 普通用户：统计自己提交的待审核申请
        try {
          const appliesResp = await axiosInstance.get(`/clubs/applications/user/${uid}`);
          let applicationList: any[] = [];
          if (Array.isArray(appliesResp.data)) {
            applicationList = appliesResp.data;
          } else if (appliesResp.data?.data && Array.isArray(appliesResp.data.data)) {
            applicationList = appliesResp.data.data;
          } else if (appliesResp.data?.list && Array.isArray(appliesResp.data.list)) {
            applicationList = appliesResp.data.list;
          } else if (
            appliesResp.data?.content &&
            Array.isArray(appliesResp.data.content)
          ) {
            applicationList = appliesResp.data.content;
          }
          pendingCount += applicationList.filter((x: any) => x?.status === 0).length;
        } catch (userAppErr) {
          console.error("加载用户申请列表失败:", userAppErr);
        }
      }

      // 活动创建申请：系统管理员查看全部，普通用户查看自己创建的活动
      try {
        let activityResponse;
        if (isSystemAdmin) {
          activityResponse = await activityApi.getActivities({
            page: 0,
            size: 1000,
          });
        } else {
          activityResponse = await activityApi.getUserCreatedActivities(uid, {
            page: 0,
            size: 1000,
          });
        }

        const activityData = activityResponse.data;
        const activityList =
          activityData?.content ||
          activityData?.data?.content ||
          activityData?.list ||
          (Array.isArray(activityData) ? activityData : []);

        const pendingActivities = Array.isArray(activityList)
          ? activityList.filter((activity: any) => activity?.status === 0)
          : [];
        pendingCount += pendingActivities.length;
      } catch (activityErr) {
        console.error("加载活动创建待审核数失败:", activityErr);
      }

      stats.value.pendingApplications = pendingCount;
      console.log("待处理申请数:", stats.value.pendingApplications);
    } catch (error) {
      console.error("加载待处理申请数失败:", error);
      stats.value.pendingApplications = 0;
    }

    console.log("统计数据加载完成:", stats.value);
  } catch (error) {
    console.error("加载统计数据失败:", error);
  }
};

// 加载活跃度趋势数据
const loadActivityTrend = async () => {
  chartLoading.value = true;
  chartError.value = "";
  try {
    // 直接使用活动报名记录计算最近7天活跃度，避免后端统计接口依赖
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    const participationsResp = await activityParticipantApi.getUserParticipations(uid, {
      page: 0,
      size: 1000,
      sort: "enrollmentTime,desc",
    });
    let participationList: any[] = [];
    const respData = participationsResp.data;
    if (respData?.content && Array.isArray(respData.content)) {
      participationList = respData.content;
    } else if (respData?.data?.content && Array.isArray(respData.data.content)) {
      participationList = respData.data.content;
    } else if (Array.isArray(respData)) {
      participationList = respData;
    } else if (respData?.list && Array.isArray(respData.list)) {
      participationList = respData.list;
    }

    // 仅统计已通过或已参加的记录
    const valid = participationList.filter(
      (p: any) => p?.status === 1 || p?.status === 3
    );

    // 生成最近7天(从6天前到今天)的日期键
    const weekdays = ["日", "一", "二", "三", "四", "五", "六"];
    const days: { label: string; key: string }[] = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setHours(0, 0, 0, 0);
      d.setDate(d.getDate() - i);
      const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
        2,
        "0"
      )}-${String(d.getDate()).padStart(2, "0")}`;
      days.push({
        label: `周${weekdays[d.getDay()]}`,
        key,
      });
    }

    // 统计每一天的活跃次数（以 checkInTime > enrollmentTime > createdAt 的日期为准）
    const countByDay: Record<string, number> = {};
    for (const day of days) countByDay[day.key] = 0;
    for (const item of valid) {
      const ts =
        item.checkInTime || item.enrollmentTime || item.createdAt || item.updatedAt;
      if (!ts) continue;
      const d = new Date(ts);
      if (isNaN(d.getTime())) continue;
      d.setHours(0, 0, 0, 0);
      const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
        2,
        "0"
      )}-${String(d.getDate()).padStart(2, "0")}`;
      if (countByDay[key] !== undefined) {
        countByDay[key] += 1;
      }
    }

    activityTrend.value = days.map((d) => ({
      label: d.label,
      value: countByDay[d.key] || 0,
    }));
  } catch (err: any) {
    console.error("加载活跃度趋势失败:", err);
    chartError.value =
      err?.response?.data?.message || "无法加载活跃度趋势，已显示估算数据";
    const fallbackValues = Array.from({ length: 7 }, (_, idx) => ({
      label: `周${["日", "一", "二", "三", "四", "五", "六"][idx]}`,
      value: Math.max(0, stats.value.participatedActivities - (6 - idx)),
    }));
    activityTrend.value = fallbackValues;
  } finally {
    chartLoading.value = false;
  }
};

const maxChartValue = computed(() => {
  const max = activityTrend.value.reduce((acc, item) => Math.max(acc, item.value), 0);
  return max || 1;
});

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
  showNotifications.value = false;
};

// 切换通知菜单
const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value;
  showUserMenu.value = false;
};

// // 打开社团详情模态框
// const openClubModal = async (club: Club) => {
//   selectedClub.value = club;
//   selectedClubActivities.value = [];

//   // 获取社团活动列表
//   if (club.id) {
//     try {
//       const response = await activityApi.getClubActivities(club.id, { page: 1, size: 5 });
//       const activities =
//         response.data?.data?.content || response.data?.data || response.data || [];
//       selectedClubActivities.value = Array.isArray(activities) ? activities : [];
//     } catch (error) {
//       console.error("获取社团活动失败:", error);
//       selectedClubActivities.value = [];
//     }
//   }
// };

// 关闭社团详情模态框
const closeClubModal = () => {
  selectedClub.value = null;
  selectedClubActivities.value = [];
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
  await loadStats();
  await loadActivityTrend();

  // 点击外部关闭菜单
  document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;
    if (!target.closest(".user-profile")) {
      showUserMenu.value = false;
      showNotifications.value = false;
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

.sidebar.sidebar-collapsed {
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

.badge {
  margin-left: auto;
  background-color: #ef4444;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
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

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 8px 15px 8px 40px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
}

.search-box i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.user-profile {
  display: flex;
  align-items: center;
  position: relative;
}

.notification-icon {
  position: relative;
  margin-right: 20px;
  font-size: 20px;
  cursor: pointer;
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

.user-dropdown,
.notifications-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 300px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  margin-top: 10px;
  z-index: 100;
  max-height: 400px;
  overflow-y: auto;
}

.notifications-dropdown {
  right: 60px;
  width: 350px;
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

.notifications-dropdown .dropdown-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.notifications-dropdown .dropdown-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.3s ease;
}

.notification-item:hover {
  background-color: #f9fafb;
}

.notification-item i {
  margin-right: 15px;
  font-size: 18px;
}

.notification-content {
  flex: 1;
}

.notification-content p {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #374151;
}

.notification-content span {
  font-size: 12px;
  color: #9ca3af;
}

.view-all-link {
  display: block;
  text-align: center;
  padding: 12px;
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  border-top: 1px solid #e5e7eb;
}

.view-all-link:hover {
  background-color: #f9fafb;
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

/* 页面内容样式 */
.page-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

/* 区块样式 */
.section {
  margin-bottom: 30px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.stat-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.stat-card.loading {
  opacity: 0.7;
  pointer-events: none;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stat-icon i {
  color: white;
}

.stat-card:nth-child(1) .stat-icon {
  background-color: #667eea;
}

.stat-card:nth-child(2) .stat-icon {
  background-color: #10b981;
}

.stat-card:nth-child(3) .stat-icon {
  background-color: #f59e0b;
}

.stat-card:nth-child(4) .stat-icon {
  background-color: #ef4444;
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  font-size: 28px;
  font-weight: bold;
  color: #111827;
  margin: 0 0 5px 0;
}

.stat-content p {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #111827;
  margin-bottom: 5px;
}

.stat-change {
  font-size: 12px;
  color: #10b981;
}

/* 图表和推荐区域样式 */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.chart-card,
.recommendation-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.chart-filters {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 6px 15px;
  border: 1px solid #e5e7eb;
  background-color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-btn.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

.refresh-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: #6b7280;
  cursor: pointer;
}

.chart-container {
  height: 280px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-chart {
  width: 100%;
  height: 100%;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 100%;
  gap: 12px;
}

.chart-bar {
  flex: 1;
  max-width: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.bar-wrapper {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: linear-gradient(180deg, #eef2ff 0%, #f9fafb 100%);
  border-radius: 6px;
  padding: 4px;
}

.bar {
  width: 100%;
  max-width: 24px;
  background: linear-gradient(180deg, #667eea 0%, #4c51bf 100%);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
  min-height: 8%;
}

.chart-bar .label {
  font-size: 12px;
  color: #6b7280;
}

.chart-bar .value {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.chart-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #6b7280;
  gap: 12px;
  text-align: center;
}

.chart-state i {
  font-size: 32px;
}

.chart-state.error {
  color: #ef4444;
}

.chart-state.empty {
  color: #9ca3af;
}

.recommendation-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.recommendation-item:hover {
  background-color: #f9fafb;
}

.recommendation-item img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  margin-right: 15px;
}

.club-info {
  flex: 1;
}

.club-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 5px;
}

.club-info p {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 5px;
}

.club-stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #9ca3af;
}

.join-btn {
  padding: 6px 15px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.join-btn:hover {
  background-color: #5a67d8;
}

/* 活动表格样式 */
.activities-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.view-all-btn {
  padding: 6px 15px;
  background-color: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.activities-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

table th,
table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #f3f4f6;
}

table th {
  font-weight: 600;
  color: #4b5563;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.ongoing {
  background-color: #dbeafe;
  color: #1d4ed8;
}

.status-badge.upcoming {
  background-color: #dcfce7;
  color: #166534;
}

.status-badge.completed {
  background-color: #f3f4f6;
  color: #6b7280;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.action-btn.primary {
  background-color: #667eea;
  color: white;
}

.action-btn.secondary {
  background-color: #f3f4f6;
  color: #6b7280;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.club-detail {
  display: flex;
  margin-bottom: 20px;
}

.club-detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  margin-right: 20px;
}

.club-detail-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 10px;
}

.club-detail-info p {
  color: #6b7280;
  margin-bottom: 15px;
}

.club-detail-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  color: #6b7280;
}

.stat-item i {
  margin-right: 5px;
}

.club-activities h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 15px;
}

.club-activities ul {
  list-style: none;
  padding: 0;
}

.club-activities li {
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  color: #6b7280;
}

.club-activities li i {
  margin-right: 10px;
  color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #6b7280;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid,
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-section {
    grid-template-columns: 1fr;
  }

  .notifications-dropdown {
    right: 0;
    width: 300px;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 60px;
    position: fixed;
    height: 100vh;
    z-index: 1000;
  }

  .sidebar-header {
    padding: 15px 10px;
  }

  .logo span,
  .nav-link span {
    display: none;
  }

  .nav-link {
    padding: 15px 10px;
    justify-content: center;
  }

  .nav-link i {
    margin-right: 0;
  }

  .main-content {
    margin-left: 60px;
  }

  .top-bar {
    padding: 15px;
    flex-wrap: wrap;
  }

  .search-box {
    width: 100%;
    margin-top: 10px;
  }

  .page-content {
    padding: 15px;
  }

  .stats-grid,
  .stats-cards {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 20px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .notification-icon {
    margin-right: 10px;
  }

  .user-info img {
    width: 35px;
    height: 35px;
  }

  .user-info span {
    display: none;
  }

  .user-dropdown,
  .notifications-dropdown {
    width: 250px;
    right: -50px;
  }
}
</style>
