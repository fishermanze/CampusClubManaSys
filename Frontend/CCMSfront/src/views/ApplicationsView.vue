<template>
  <div class="applications-container">
    <!-- 侧边栏和顶部导航栏复用HomeView的结构 -->
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
          <li class="nav-item active">
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
          <li class="nav-item">
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
          <h1 class="page-title">申请记录</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input type="text" placeholder="搜索申请..." v-model="searchQuery" />
            <i class="fa fa-search"></i>
          </div>

          <div class="user-profile">
            <div class="notification-icon" @click="toggleNotifications">
              <i class="fa fa-bell"></i>
              <span class="badge" v-if="unreadNotifications > 0">{{
                unreadNotifications
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
        <div class="applications-section">
          <!-- 申请类型筛选器 -->
          <div class="filter-section">
            <div class="filter-tabs">
              <button
                v-for="tab in filterTabs"
                :key="tab.key"
                :class="['filter-tab', { active: activeTab === tab.key }]"
                :data-key="tab.key"
                @click="handleFilterTabChange"
                :disabled="loading"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载申请数据...</p>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="error" class="error-state">
            <p class="error-message">{{ error }}</p>
            <button class="btn btn-primary" @click="fetchApplications">重新加载</button>
          </div>

          <!-- 申请列表 -->
          <div v-else class="applications-list">
            <div class="table-responsive">
              <table class="applications-table">
                <thead>
                  <tr>
                    <th>申请类型</th>
                    <th>相关名称</th>
                    <th>申请时间</th>
                    <th>状态</th>
                    <th>处理时间</th>
                    <th>备注</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="application in filteredApplications"
                    :key="application.applicationId"
                  >
                    <td>
                      <span :class="['type-badge', application.type]">
                        {{ application.typeText }}
                      </span>
                    </td>
                    <td>{{ application.relatedName }}</td>
                    <td>{{ formatDate(application.applyTime) }}</td>
                    <td>
                      <span :class="['status-badge', application.status]">
                        {{ application.statusText }}
                      </span>
                    </td>
                    <td>
                      {{
                        application.processTime
                          ? formatDate(application.processTime)
                          : "-"
                      }}
                    </td>
                    <td>{{ application.remark || "-" }}</td>
                    <td>
                      <div class="action-buttons">
                        <button
                          class="btn btn-sm btn-primary"
                          @click="viewApplicationDetails(application)"
                        >
                          <i class="fa fa-eye"></i> 详情
                        </button>
                        <button
                          class="btn btn-sm btn-outline text-danger"
                          v-if="application.status === 'pending'"
                          @click="cancelApplication(application.applicationId)"
                          :disabled="loading"
                        >
                          <i class="fa fa-times"></i> 取消
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="filteredApplications.length === 0" class="empty-state">
              <i class="fa fa-file-text text-gray-400"></i>
              <p>{{ searchQuery ? "未找到匹配的申请记录" : "暂无申请记录" }}</p>
              <button
                v-if="!searchQuery"
                class="btn btn-primary"
                @click="fetchApplications"
              >
                刷新
              </button>
            </div>
          </div>

          <!-- 分页 -->
          <div
            v-if="!loading && !error && filteredApplications.length > 0"
            class="pagination"
          >
            <button
              class="pagination-btn"
              :disabled="currentPage === 1"
              @click="handlePageChange(currentPage - 1)"
            >
              上一页
            </button>
            <span class="pagination-info"
              >第 {{ currentPage }} / {{ totalPages }} 页</span
            >
            <button
              class="pagination-btn"
              :disabled="currentPage === totalPages"
              @click="handlePageChange(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 申请详情模态框 -->
    <div
      v-if="selectedApplication"
      class="modal-overlay"
      @click="closeApplicationDetails"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>申请详情</h3>
          <button class="close-btn" @click="closeApplicationDetails">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <span class="detail-label">申请ID：</span>
            <span class="detail-value">{{ selectedApplication.applicationId }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">申请类型：</span>
            <span class="detail-value">{{ selectedApplication.typeText }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">相关名称：</span>
            <span class="detail-value">{{ selectedApplication.relatedName }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">申请时间：</span>
            <span class="detail-value">{{
              formatDate(selectedApplication.applyTime)
            }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">申请理由：</span>
            <span class="detail-value">{{ selectedApplication.reason }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">状态：</span>
            <span :class="['status-badge', selectedApplication.status]">
              {{ selectedApplication.statusText }}
            </span>
          </div>
          <div v-if="selectedApplication.processTime" class="detail-item">
            <span class="detail-label">处理时间：</span>
            <span class="detail-value">{{
              formatDate(selectedApplication.processTime)
            }}</span>
          </div>
          <div v-if="selectedApplication.remark" class="detail-item">
            <span class="detail-label">处理备注：</span>
            <span class="detail-value">{{ selectedApplication.remark }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeApplicationDetails">关闭</button>
          <template
            v-if="
              selectedApplication &&
              selectedApplication.type === 'club_join' &&
              selectedApplication.status === 'pending' &&
              canManageThisClub((selectedApplication as any).clubId)
            "
          >
            <button
              class="btn btn-primary"
              :disabled="loading"
              @click="handleApproveSelected(true)"
            >
              通过
            </button>
            <button
              class="btn btn-outline"
              :disabled="loading"
              @click="handleApproveSelected(false)"
            >
              拒绝
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi, notificationApi } from "../api/apiService";
import axiosInstance from "../api/axiosInstance";
import type { User } from "../types/index";

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
const unreadNotifications = ref(0);

// 搜索
const searchQuery = ref("");

// 申请列表
interface Application {
  applicationId: number;
  type: string;
  typeText: string;
  relatedName: string;
  applyTime: string;
  status: string;
  statusText: string;
  processTime?: string;
  remark?: string;
  reason?: string;
  clubId?: number;
  applicantUserId?: number;
}
const applications = ref<Application[]>([]);
const loading = ref(false);
const error = ref("");

// 管理权限与可管理社团集合
const managedClubIds = ref(new Set<number>());

// 筛选标签
const filterTabs = [
  { key: "all", label: "全部" },
  { key: "pending", label: "待处理" },
  { key: "approved", label: "已通过" },
  { key: "rejected", label: "已拒绝" },
];
const activeTab = ref("all");

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(1);

// 选中的申请
const selectedApplication = ref<Application | null>(null);

// 过滤后的申请列表
const filteredApplications = computed(() => {
  let result = applications.value;

  // 按标签筛选
  if (activeTab.value !== "all") {
    result = result.filter((app) => app.status === activeTab.value);
  }

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (app) =>
        app.relatedName.toLowerCase().includes(query) ||
        app.typeText.toLowerCase().includes(query)
    );
  }

  return result;
});

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
      }
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
  }
};

// 加载未读通知数
const loadUnreadNotifications = async () => {
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) return;
    const response = await notificationApi.getUnreadCount(uid);
    unreadNotifications.value = Number(response.data) || 0;
  } catch (error) {
    console.error("加载未读通知数失败:", error);
  }
};

// 获取申请列表
const fetchApplications = async () => {
  loading.value = true;
  error.value = "";
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) throw new Error("未获取到用户信息");
    const role = (currentUser.value as any)?.role || localStorage.getItem("role") || "";

    // 管理员/社团管理员：查看自己管理社团的申请
    const isManager = [
      "ADMIN",
      "admin",
      "CLUB_ADMIN",
      "club_admin",
      "club_manager",
    ].includes(role as string);
    let list: any[] = [];
    if (isManager) {
      // 仅拉取当前负责人管理的社团
      const clubsResp = await axiosInstance.get(`/clubs/leader/${uid}`);
      const clubsData = clubsResp.data || [];
      const clubs = Array.isArray(clubsData) ? clubsData : clubsData.list || [];
      managedClubIds.value = new Set(
        clubs.map((c: any) => Number(c.id)).filter((x: number) => !Number.isNaN(x))
      );
      // 汇总这些社团的待审核和全部申请
      const allApps: any[] = [];
      for (const c of clubs) {
        try {
          // 拉取该社团的全部申请（不传status，前端再按标签筛选）
          const appsResp = await axiosInstance.get(`/clubs/applications/club/${c.id}`);
          const arr = Array.isArray(appsResp.data)
            ? appsResp.data
            : Array.isArray(appsResp.data?.data)
            ? appsResp.data.data
            : [];
          // 附带社团名
          arr.forEach((it: any) => (it.clubName = c.name));
          allApps.push(...arr);
        } catch (e) {
          // 忽略单个社团的错误
        }
      }
      list = allApps;
    } else {
      // 普通用户：查看自己的申请
      const response = await axiosInstance.get(`/clubs/applications/user/${uid}`);
      list = Array.isArray(response.data)
        ? response.data
        : Array.isArray(response.data?.data)
        ? response.data.data
        : [];
    }

    applications.value = list.map((item: any) => ({
      applicationId: item.id,
      type: "club_join",
      typeText: getTypeText("club_join"),
      relatedName: item.clubName || `社团#${item.clubId}`,
      applyTime: item.joinTime,
      status: getStatusKey(item.status),
      statusText: getStatusText(item.status),
      processTime: item.processTime,
      remark: item.remark,
      reason: item.reason,
      clubId: item.clubId,
      applicantUserId: item.userId,
    }));

    totalPages.value = Math.max(1, Math.ceil(applications.value.length / pageSize.value));
  } catch (err: any) {
    error.value = err.response?.data?.message || "加载申请列表失败";
    console.error("获取申请列表失败:", err);
  } finally {
    loading.value = false;
  }
};

// 获取类型文本
const getTypeText = (type: string): string => {
  const typeMap: Record<string, string> = {
    club_join: "加入社团",
    club_quit: "退出社团",
    club_create: "创建社团",
    activity_participate: "参与活动",
  };
  return typeMap[type] || "未知类型";
};

// 获取状态键
const getStatusKey = (status: number): string => {
  if (status === 0) return "pending";
  if (status === 1) return "approved";
  if (status === 2) return "rejected";
  return "cancelled";
};

// 获取状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: "待处理",
    1: "已通过",
    2: "已拒绝",
    3: "已取消",
  };
  return statusMap[status] || "未知";
};

// 格式化日期
const formatDate = (date: string | Date): string => {
  if (!date) return "-";
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
    d.getDate()
  ).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(
    d.getMinutes()
  ).padStart(2, "0")}`;
};

// 筛选标签切换
const handleFilterTabChange = (event: Event) => {
  const target = event.target as HTMLElement;
  const tabKey =
    target.getAttribute("data-key") || target.textContent?.trim().toLowerCase();
  if (tabKey && filterTabs.some((tab) => tab.key === tabKey)) {
    activeTab.value = tabKey;
  }
};

// 查看申请详情
const viewApplicationDetails = (application: Application) => {
  selectedApplication.value = application;
};

// 关闭申请详情
const closeApplicationDetails = () => {
  selectedApplication.value = null;
};

// 判断是否可管理该社团
const canManageThisClub = (clubId?: number) => {
  if (!clubId) return false;
  return managedClubIds.value.has(Number(clubId));
};

// 审批所选申请
const handleApproveSelected = async (approved: boolean) => {
  if (
    !selectedApplication.value ||
    !selectedApplication.value.clubId ||
    !selectedApplication.value.applicantUserId
  ) {
    return;
  }
  try {
    await axiosInstance.put(
      `/clubs/${selectedApplication.value.clubId}/members/${selectedApplication.value.applicantUserId}/approve`,
      null,
      {
        params: {
          approved,
          approverId:
            currentUser.value?.id || Number(localStorage.getItem("userId") || 0),
        },
      }
    );
    // 更新本地状态并刷新列表
    selectedApplication.value.status = approved ? "approved" : "rejected";
    selectedApplication.value.statusText = approved ? "已通过" : "已拒绝";
    await fetchApplications();
    closeApplicationDetails();
  } catch (err: any) {
    alert(err?.response?.data?.message || "审批失败");
  }
};
// 取消申请
const cancelApplication = async (applicationId: number) => {
  if (!confirm("确定要取消这个申请吗？")) return;
  try {
    await axiosInstance.put(`/clubs/applications/${applicationId}/cancel`);
    await fetchApplications();
  } catch (err: any) {
    alert(err.response?.data?.message || "取消申请失败");
  }
};

// 分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchApplications();
};

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
  await loadUnreadNotifications();
  await fetchApplications();

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
.applications-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 - 复用HomeView的样式 */
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

.top-bar-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
  color: #374151;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1f2937;
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
  color: #374151;
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

.logout {
  color: #ef4444;
}

/* 页面内容样式 */
.page-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  background-color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab:hover {
  background-color: #f9fafb;
}

.filter-tab.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
  border: none;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover {
  background-color: #5a67d8;
}

.btn-secondary {
  background-color: #6b7280;
  color: white;
}

.btn-secondary:hover {
  background-color: #4b5563;
}

.btn-outline {
  background-color: transparent;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}

.btn-outline:hover {
  background-color: #f9fafb;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.table-responsive {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.applications-table {
  width: 100%;
  border-collapse: collapse;
}

.applications-table th,
.applications-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.applications-table th {
  background-color: #f9fafb;
  font-weight: 600;
  color: #374151;
}

.applications-table tbody tr:hover {
  background-color: #f9fafb;
}

.type-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.club_join {
  background-color: #dbeafe;
  color: #1e40af;
}

.type-badge.club_quit {
  background-color: #fee2e2;
  color: #991b1b;
}

.type-badge.club_create {
  background-color: #dcfce7;
  color: #166534;
}

.type-badge.activity_participate {
  background-color: #fef3c7;
  color: #92400e;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #fef3c7;
  color: #92400e;
}

.status-badge.approved {
  background-color: #dcfce7;
  color: #166534;
}

.status-badge.rejected {
  background-color: #fee2e2;
  color: #991b1b;
}

.status-badge.cancelled {
  background-color: #f3f4f6;
  color: #4b5563;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.text-danger {
  color: #ef4444 !important;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  background-color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f9fafb;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
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
  border-radius: 8px;
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
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
}

/* 详情样式 */
.detail-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.detail-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-label {
  width: 100px;
  font-weight: 500;
  color: #374151;
}

.detail-value {
  flex: 1;
  color: #6b7280;
}

.hidden {
  display: none;
}
</style>
