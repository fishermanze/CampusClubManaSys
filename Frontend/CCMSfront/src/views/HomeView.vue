<template>
  <div class="home-container">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <i class="fa fa-graduation-cap"></i>
          <span>校园社团管理</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li class="nav-item active">
            <a href="#" class="nav-link">
              <i class="fa fa-home"></i>
              <span>首页</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="fa fa-users"></i>
              <span>我的社团</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="fa fa-calendar"></i>
              <span>活动管理</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="fa fa-file-text"></i>
              <span>申请记录</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="fa fa-bell"></i>
              <span>通知中心</span>
              <span class="badge">3</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="fa fa-cog"></i>
              <span>设置</span>
            </a>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部信息栏 -->
      <header class="top-bar">
        <div class="top-bar-left">
          <button class="menu-toggle" @click="toggleSidebar">
            <i class="fa fa-bars"></i>
          </button>
          <h1>欢迎回来，{{ currentUser?.realName || "用户" }}</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input type="text" placeholder="搜索..." />
            <i class="fa fa-search"></i>
          </div>

          <div class="user-profile">
            <div class="notification-icon">
              <i class="fa fa-bell"></i>
              <span class="badge">3</span>
            </div>
            <div class="user-info" @click="toggleUserMenu">
              <img :src="currentUser?.avatarUrl || ''" alt="用户头像" />
              <span>{{ currentUser?.realName || "用户" }}</span>
              <i class="fa fa-chevron-down"></i>
            </div>

            <!-- 用户下拉菜单 -->
            <div v-if="showUserMenu" class="user-dropdown">
              <a href="#" class="dropdown-item"> <i class="fa fa-user"></i> 个人资料 </a>
              <a href="#" class="dropdown-item"> <i class="fa fa-cog"></i> 账号设置 </a>
              <div class="dropdown-divider"></div>
              <a href="#" class="dropdown-item" @click="logout">
                <i class="fa fa-sign-out"></i> 退出登录
              </a>
            </div>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="page-content">
        <!-- 数据统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fa fa-users"></i>
            </div>
            <div class="stat-content">
              <h3>已加入社团</h3>
              <p class="stat-number">{{ stats.joinedClubs }}</p>
              <span class="stat-change">+2 本周</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <i class="fa fa-calendar-check-o"></i>
            </div>
            <div class="stat-content">
              <h3>参与活动</h3>
              <p class="stat-number">{{ stats.participatedActivities }}</p>
              <span class="stat-change">+5 本月</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <i class="fa fa-star"></i>
            </div>
            <div class="stat-content">
              <h3>社团积分</h3>
              <p class="stat-number">{{ stats.points }}</p>
              <span class="stat-change">+15 昨日</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <i class="fa fa-trophy"></i>
            </div>
            <div class="stat-content">
              <h3>成就等级</h3>
              <p class="stat-number">{{ stats.achievementLevel }}</p>
              <span class="stat-change">Lv.3 铜牌会员</span>
            </div>
          </div>
        </div>

        <!-- 活跃度图表和AI推荐 -->
        <div class="charts-section">
          <div class="chart-card">
            <div class="card-header">
              <h3>活跃度趋势</h3>
              <div class="chart-filters">
                <button class="filter-btn active">周</button>
                <button class="filter-btn">月</button>
                <button class="filter-btn">年</button>
              </div>
            </div>
            <div class="chart-container">
              <div id="activityChart"></div>
            </div>
          </div>

          <div class="recommendation-card">
            <div class="card-header">
              <h3>AI社团推荐</h3>
              <button class="refresh-btn">
                <i class="fa fa-refresh"></i>
              </button>
            </div>
            <div class="recommendations">
              <div
                v-for="club in recommendedClubs"
                :key="club.clubId"
                class="recommendation-item"
                @click="showClubModal(club as Club)"
              >
                <img :src="club.logoUrl" alt="社团头像" />
                <div class="club-info">
                  <h4>{{ club.clubName }}</h4>
                  <p>{{ (club as any).category }}</p>
                  <div class="club-stats">
                    <span><i class="fa fa-users"></i> {{ club.memberCount }}人</span>
                    <span
                      ><i class="fa fa-calendar"></i>
                      {{ (club as any).activityCount }}活动</span
                    >
                  </div>
                </div>
                <button class="join-btn">加入</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 近期活动表格 -->
        <div class="activities-card">
          <div class="card-header">
            <h3>近期活动</h3>
            <button class="view-all-btn">查看全部</button>
          </div>
          <div class="activities-table">
            <table>
              <thead>
                <tr>
                  <th>活动名称</th>
                  <th>社团</th>
                  <th>时间</th>
                  <th>地点</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="activity in recentActivities" :key="activity.activityId">
                  <td>{{ activity.activityName }}</td>
                  <td>{{ activity.clubName }}</td>
                  <td>{{ activity.time }}</td>
                  <td>{{ activity.location }}</td>
                  <td>
                    <span :class="['status-badge', activity.status]">
                      {{ activity.statusText }}
                    </span>
                  </td>
                  <td>
                    <button
                      class="action-btn"
                      :class="activity.status === 1 ? 'primary' : 'secondary'"
                    >
                      {{ activity.status === 1 ? "签到" : "详情" }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>

    <!-- 社团详情模态框 -->
    <div v-if="selectedClub" class="modal-overlay" @click="closeClubModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedClub.clubName }}</h3>
          <button class="close-btn" @click="closeClubModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="club-detail">
            <img :src="selectedClub.logoUrl" alt="社团头像" class="club-detail-avatar" />
            <div class="club-detail-info">
              <h4>{{ selectedClub.clubName }}</h4>
              <p>{{ selectedClub.description }}</p>
              <div class="club-detail-stats">
                <div class="stat-item">
                  <i class="fa fa-users"></i>
                  <span>{{ selectedClub.memberCount }}人</span>
                </div>
                <div class="stat-item">
                  <i class="fa fa-calendar"></i>
                  <span>{{ (selectedClub as any).activityCount }}活动</span>
                </div>
                <div class="stat-item">
                  <i class="fa fa-star"></i>
                  <span>{{ selectedClub.rating }}分</span>
                </div>
              </div>
            </div>
          </div>
          <div class="club-activities">
            <h4>近期活动</h4>
            <ul>
              <li
                v-for="(activity, index) in (selectedClub as any).recentActivities"
                :key="index"
              >
                <i class="fa fa-calendar-check-o"></i>
                <span>{{ activity.name }} - {{ activity.time }}</span>
              </li>
            </ul>
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
import { ref, reactive, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import type { User, Club, Activity } from "@/types";

const router = useRouter();

// 当前登录用户
const currentUser = ref<User | null>(null);

// 获取用户信息（暂时注释掉未使用的函数）
// const fetchUserInfo = async () => {
//   try {
//     const response = await authApi.getCurrentUser()
//     const userInfo = (response as any).data || response
//     // 更新当前用户信息
//     currentUser.value = userInfo
//   } catch (error) {
//     console.error('获取用户信息失败:', error)
//     // 如果获取用户信息失败，说明未登录，跳转到登录页
//     router.push('/login')
//   }
// }

// 侧边栏状态
const sidebarCollapsed = ref(false);
// 用户菜单状态
const showUserMenu = ref(false);
// 选中的社团（用于模态框）
const selectedClub = ref<Club | null>(null);

// 统计数据
const stats = reactive({
  joinedClubs: 3,
  participatedActivities: 8,
  favoriteClubs: 5,
  unreadNotifications: 3,
  points: 150,
  achievementLevel: 3,
});

// 获取统计数据（暂时注释掉未使用的函数）
// const fetchStats = async () => {
//   try {
//     const response = await statsApi.getUserStats()
//     const statsData = (response as any).data || response
//     Object.assign(stats, statsData)
//   } catch (error) {
//     console.error('获取统计数据失败:', error)
//   }
// }

// 导入API服务
import { statsApi } from "../api/apiService";

// 推荐社团列表
const recommendedClubs = ref<Array<Club & { isJoined?: boolean }>>([]);

// 获取推荐社团（暂时注释掉未使用的函数）
// const fetchRecommendedClubs = async () => {
//   try {
//     const response = await clubApi.getRecommendedClubs()
//     const clubsData = (response as any).data || response
//     const clubs = Array.isArray(clubsData) ? clubsData : []
//     recommendedClubs.value = clubs.map((club: any) => ({
//       ...club,
//       isJoined: false // 这里可以根据实际情况判断用户是否已加入
//     }))
//   } catch (error) {
//     console.error('获取推荐社团失败:', error)
//   }
// }

// 近期活动列表
const recentActivities = ref<
  Array<Activity & { clubName?: string; time?: string; statusText?: string }>
>([]);

// 获取近期活动（暂时注释掉未使用的函数）
// const fetchRecentActivities = async () => {
//   try {
//     const response = await activityApi.getActivities({ page: 1, pageSize: 5 })
//     const responseData = (response as any).data || response
//     const activityList = responseData.list || []
//     recentActivities.value = activityList.map((activity: any) => {
//       let statusText = ''
//       switch (activity.status) {
//         case 0:
//           statusText = '即将开始'
//           break
//         case 1:
//           statusText = '进行中'
//           break
//         case 2:
//           statusText = '已结束'
//           break
//       }
//       return {
//         ...activity,
//         time: activity.startTime,
//         statusText
//       }
//     })
//   } catch (error) {
//     console.error('获取近期活动失败:', error)
//   }
// }

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// 退出登录
const logout = () => {
  localStorage.removeItem("userToken");
  router.push("/login");
};

// 显示社团详情模态框
const showClubModal = (club: Club) => {
  selectedClub.value = club;
};

// 关闭社团详情模态框
const closeClubModal = () => {
  selectedClub.value = null;
};

// 初始化图表
const initChart = async () => {
  const chartContainer = document.getElementById("activityChart");
  if (chartContainer) {
    try {
      // 获取活动统计数据
      const response = await statsApi.getActivityStats();
      const statsData = (response as any).data || response;

      // 模拟图表渲染
      chartContainer.innerHTML = `
        <div style="height: 250px; display: flex; align-items: center; justify-content: center;">
          <canvas id="activityChartCanvas" width="400" height="200"></canvas>
        </div>
      `;

      // 简单的图表绘制
      const canvas = document.getElementById("activityChartCanvas") as HTMLCanvasElement;
      if (canvas) {
        const ctx = canvas.getContext("2d");
        if (ctx) {
          // 使用后端返回的数据或默认数据
          const data = statsData?.monthlyActivities || [12, 19, 15, 22, 18, 25, 20];
          const colors = [
            "#667eea",
            "#48bb78",
            "#ed64a6",
            "#ed8936",
            "#4299e1",
            "#9f7aea",
            "#4fd1c5",
          ];

          const barWidth = 30;
          const startX = 50;
          const startY = 180;
          const maxHeight = 150;

          // 绘制坐标轴
          ctx.beginPath();
          ctx.moveTo(startX, 20);
          ctx.lineTo(startX, startY);
          ctx.lineTo(380, startY);
          ctx.stroke();

          // 绘制柱子
          data.forEach((value: number, index: number) => {
            const barHeight = (value / Math.max(...data)) * maxHeight;
            const x = startX + 50 + index * (barWidth + 15);
            const y = startY - barHeight;

            ctx.fillStyle = colors[index];
            ctx.fillRect(x, y, barWidth, barHeight);

            // 绘制数值
            ctx.fillStyle = "#333";
            ctx.font = "12px Arial";
            ctx.textAlign = "center";
            ctx.fillText(value.toString(), x + barWidth / 2, y - 5);
          });

          // 绘制标签
          const labels = ["1月", "2月", "3月", "4月", "5月", "6月", "7月"];
          labels.forEach((label, index) => {
            const x = startX + 50 + index * (barWidth + 15);
            ctx.fillText(label, x + barWidth / 2, startY + 20);
          });
        }
      }
    } catch (error) {
      console.error("获取图表数据失败:", error);
      // 如果API调用失败，显示默认图表
      chartContainer.innerHTML = `
        <div style="height: 250px; display: flex; align-items: center; justify-content: center; color: #666;">
          图表数据加载失败
        </div>
      `;
    }
  }
};

// 监听点击外部区域关闭用户菜单
onMounted(() => {
  const handleClickOutside = (event: MouseEvent) => {
    const userInfo = document.querySelector(".user-info");
    const userDropdown = document.querySelector(".user-dropdown");

    if (
      userInfo &&
      userDropdown &&
      !userInfo.contains(event.target as Node) &&
      !userDropdown.contains(event.target as Node)
    ) {
      showUserMenu.value = false;
    }
  };

  document.addEventListener("click", handleClickOutside);

  // 初始化图表
  nextTick(() => {
    initChart();
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

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 200px;
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

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
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

.stat-content h3 {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 5px;
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
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 60px;
  }

  .logo span,
  .nav-link span {
    display: none;
  }

  .top-bar {
    padding: 15px;
  }

  .page-content {
    padding: 15px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
