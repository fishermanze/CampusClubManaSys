<template>
  <div class="notifications-container">
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
          <li class="nav-item">
            <router-link to="/applications" class="nav-link">
              <i class="fa fa-file-text"></i>
              <span :class="{ hidden: sidebarCollapsed }">申请记录</span>
            </router-link>
          </li>
          <li class="nav-item active">
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
          <h1 class="page-title">通知中心</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input
              type="text"
              placeholder="搜索通知..."
              v-model="searchQuery"
              @input="handleSearch"
              :disabled="isLoading"
            />
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
        <div class="notifications-section">
          <!-- 通知操作栏 -->
          <div class="notification-actions">
            <div class="filters">
              <select
                v-model="notificationType"
                class="filter-select"
                @change="handleFilterChange"
                :disabled="isLoading"
              >
                <option value="all">全部类型</option>
                <option value="system">系统通知</option>
                <option value="club">社团通知</option>
                <option value="activity">活动通知</option>
                <option value="application">申请通知</option>
              </select>
              <select
                v-model="notificationStatus"
                class="filter-select"
                @change="handleFilterChange"
                :disabled="isLoading"
              >
                <option value="all">全部状态</option>
                <option value="unread">未读</option>
                <option value="read">已读</option>
              </select>
            </div>
            <div class="actions">
              <button
                class="btn btn-secondary"
                @click="markAllAsRead"
                v-if="unreadNotifications > 0"
              >
                <i class="fa fa-check-square-o"></i> 全部标为已读
              </button>
              <button class="btn btn-outline" @click="deleteSelected">
                <i class="fa fa-trash-o"></i> 删除选中
              </button>
              <button
                class="btn btn-outline"
                @click="deleteAllRead"
                v-if="readNotifications.length > 0"
              >
                <i class="fa fa-trash-o"></i> 删除已读
              </button>
            </div>
          </div>

          <!-- 通知列表 -->
          <div class="notifications-list">
            <!-- 加载状态 -->
            <div v-if="isLoading" class="loading-state">
              <div class="loading-spinner"></div>
              <p>加载通知中...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-state">
              <i class="fa fa-exclamation-circle text-danger"></i>
              <p>{{ error }}</p>
              <button class="btn btn-primary" @click="fetchNotifications">
                重新加载
              </button>
            </div>

            <!-- 通知列表 -->
            <template v-else>
              <div
                v-for="notification in currentPageNotifications"
                :key="notification.id"
                :class="[
                  'notification-card',
                  {
                    unread: notification.status === 0,
                    selected: selectedNotifications.includes(notification.id),
                  },
                ]"
                @click="toggleSelectNotification(notification.id)"
              >
                <div class="notification-header">
                  <div class="notification-icon">
                    <i
                      :class="[
                        'fa',
                        getIconClass(notification.notificationType),
                        getTypeColor(notification.notificationType),
                      ]"
                    ></i>
                  </div>
                  <div class="notification-info">
                    <h4 class="notification-title">{{ notification.title }}</h4>
                    <p class="notification-time">
                      {{ formatTime(notification.createdAt) }}
                    </p>
                  </div>
                  <div class="notification-actions">
                    <button
                      class="btn-icon"
                      @click.stop="toggleNotificationRead(notification)"
                      :title="notification.status === 1 ? '标为未读' : '标为已读'"
                      :disabled="isLoading"
                    >
                      <i
                        :class="[
                          'fa',
                          notification.status === 1 ? 'fa-eye' : 'fa-eye-slash',
                        ]"
                      ></i>
                    </button>
                    <button
                      class="btn-icon delete"
                      @click.stop="deleteNotification(notification.id)"
                      title="删除"
                      :disabled="isLoading"
                    >
                      <i class="fa fa-trash-o"></i>
                    </button>
                  </div>
                </div>
                <div class="notification-body">
                  <p>{{ notification.content }}</p>
                </div>
                <div v-if="notification.actionLink" class="notification-footer">
                  <a
                    :href="notification.actionLink.url"
                    target="_blank"
                    class="action-button"
                    @click.stop
                  >
                    <i :class="['fa', notification.actionLink.icon]"></i>
                    {{ notification.actionLink.text }}
                  </a>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-if="filteredNotifications.length === 0" class="empty-state">
                <i class="fa fa-bell-slash-o text-gray-400"></i>
                <p>
                  {{
                    searchQuery ||
                    notificationType !== "all" ||
                    notificationStatus !== "all"
                      ? "未找到匹配的通知"
                      : "暂无通知"
                  }}
                </p>
                <button
                  v-if="
                    searchQuery ||
                    notificationType !== 'all' ||
                    notificationStatus !== 'all'
                  "
                  class="btn btn-secondary"
                  @click="resetFilters"
                >
                  重置筛选条件
                </button>
              </div>
            </template>
          </div>

          <!-- 分页 -->
          <div
            v-if="!isLoading && !error && filteredNotifications.length > 0"
            class="pagination"
          >
            <button
              class="pagination-btn"
              :disabled="currentPage === 1 || isLoading"
              @click="handlePageChange(currentPage - 1)"
            >
              上一页
            </button>
            <span class="pagination-info">
              第 {{ currentPage }} / {{ totalPages }} 页，共
              {{ totalNotifications }} 条通知
            </span>
            <button
              class="pagination-btn"
              :disabled="currentPage === totalPages || isLoading"
              @click="handlePageChange(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi, notificationApi } from "../api/apiService";
import type { User, Notification } from "../types/index";

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

// 通知列表 - 使用导入的 Notification 类型
const notifications = ref<Notification[]>([]);
const isLoading = ref(false);
const error = ref("");

// 筛选
const notificationType = ref("all");
const notificationStatus = ref("all");

// 选中的通知
const selectedNotifications = ref<number[]>([]);

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalNotifications = ref(0);
const totalPages = computed(() => Math.ceil(totalNotifications.value / pageSize.value));

// 当前页通知
const currentPageNotifications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredNotifications.value.slice(start, end);
});

// 过滤后的通知列表
const filteredNotifications = computed(() => {
  let result = notifications.value;

  // 按类型筛选
  if (notificationType.value !== "all") {
    const typeMap: Record<string, number> = {
      system: 1,
      club: 2,
      activity: 3,
      application: 4,
    };
    const typeValue = typeMap[notificationType.value];
    if (typeValue) {
      result = result.filter((notif) => notif.notificationType === typeValue);
    }
  }

  // 按状态筛选
  if (notificationStatus.value !== "all") {
    if (notificationStatus.value === "unread") {
      result = result.filter((notif) => notif.status === 0);
    } else if (notificationStatus.value === "read") {
      result = result.filter((notif) => notif.status === 1);
    }
  }

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (notif) =>
        notif.title.toLowerCase().includes(query) ||
        notif.content.toLowerCase().includes(query)
    );
  }

  return result;
});

// 已读通知列表
const readNotifications = computed(() => {
  return notifications.value.filter((notif) => notif.status === 1);
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

// 获取通知列表
const fetchNotifications = async () => {
  isLoading.value = true;
  error.value = "";
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) throw new Error("未获取到用户ID");
    const response = await notificationApi.getNotifications(uid, {
      page: currentPage.value,
      size: pageSize.value,
    });
    if (response.data?.data) {
      const data = response.data.data;
      notifications.value = (data.items || data.list || []).map((item: any) => ({
        id: item.id,
        userId: item.userId,
        notificationType: item.notificationType || 1,
        title: item.title || "通知",
        content: item.content || "",
        status: item.status || 0,
        relatedId: item.relatedId,
        relatedType: item.relatedType,
        needPush: item.needPush !== false,
        createdAt: item.createdAt || item.created_at,
        updatedAt: item.updatedAt || item.updated_at,
      }));
      totalNotifications.value = data.total || notifications.value.length;
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || "加载通知列表失败";
    console.error("获取通知列表失败:", err);
  } finally {
    isLoading.value = false;
  }
};

// 获取图标类
const getIconClass = (notificationType: number): string => {
  const iconMap: Record<number, string> = {
    1: "fa-bell",
    2: "fa-users",
    3: "fa-calendar",
    4: "fa-file-text",
  };
  return iconMap[notificationType] || "fa-bell";
};

// 获取类型颜色
const getTypeColor = (type: number): string => {
  const colorMap: Record<number, string> = {
    1: "text-primary",
    2: "text-success",
    3: "text-warning",
    4: "text-info",
  };
  return colorMap[type] || "text-secondary";
};

// 格式化时间
const formatTime = (date: string | Date): string => {
  if (!date) return "";
  const d = new Date(date);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
    d.getDate()
  ).padStart(2, "0")}`;
};

// 切换通知已读状态
const toggleNotificationRead = async (notification: Notification) => {
  try {
    if (notification.status === 1) {
      // 标为未读 - 这里可能需要后端支持
      notification.status = 0;
      unreadNotifications.value++;
    } else {
      const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
      if (!uid) throw new Error("未获取到用户ID");
      await notificationApi.markAsRead(notification.id, uid);
      notification.status = 1;
      unreadNotifications.value = Math.max(0, unreadNotifications.value - 1);
    }
  } catch (err: any) {
    console.error("更新通知状态失败:", err);
  }
};

// 标记所有为已读
const markAllAsRead = async () => {
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) throw new Error("未获取到用户ID");
    await notificationApi.markAllAsRead(uid);
    notifications.value.forEach((notif) => {
      notif.status = 1;
    });
    unreadNotifications.value = 0;
  } catch (err: any) {
    alert(err.response?.data?.message || "标记失败");
  }
};

// 删除通知
const deleteNotification = async (notificationId: number) => {
  if (!confirm("确定要删除这条通知吗？")) return;
  try {
    // 这里需要调用删除API，暂时只做前端删除
    notifications.value = notifications.value.filter(
      (notif) => notif.id !== notificationId
    );
    if (selectedNotifications.value.includes(notificationId)) {
      selectedNotifications.value = selectedNotifications.value.filter(
        (id) => id !== notificationId
      );
    }
  } catch (err: any) {
    alert(err.response?.data?.message || "删除失败");
  }
};

// 删除选中的通知
const deleteSelected = () => {
  if (selectedNotifications.value.length === 0) {
    alert("请先选择要删除的通知");
    return;
  }
  if (!confirm(`确定要删除选中的${selectedNotifications.value.length}条通知吗？`)) return;
  notifications.value = notifications.value.filter(
    (notif) => !selectedNotifications.value.includes(notif.id)
  );
  selectedNotifications.value = [];
};

// 删除所有已读通知
const deleteAllRead = () => {
  if (readNotifications.value.length === 0) {
    alert("没有已读通知");
    return;
  }
  if (!confirm(`确定要删除所有已读通知吗？共${readNotifications.value.length}条`)) return;
  notifications.value = notifications.value.filter((notif) => notif.status !== 1);
  selectedNotifications.value = [];
};

// 切换选中通知
const toggleSelectNotification = (notificationId: number) => {
  const index = selectedNotifications.value.indexOf(notificationId);
  if (index > -1) {
    selectedNotifications.value.splice(index, 1);
  } else {
    selectedNotifications.value.push(notificationId);
  }
};

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1;
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 重置筛选
const resetFilters = () => {
  notificationType.value = "all";
  notificationStatus.value = "all";
  searchQuery.value = "";
  currentPage.value = 1;
};

// 分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
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
  await fetchNotifications();

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
.notifications-container {
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

.notification-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  gap: 15px;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  outline: none;
  background-color: white;
}

.actions {
  display: flex;
  gap: 10px;
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

.btn-icon {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 5px;
  color: #6b7280;
  transition: color 0.3s ease;
}

.btn-icon:hover {
  color: #374151;
}

.btn-icon.delete:hover {
  color: #ef4444;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.notification-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
  position: relative;
}

.notification-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.notification-card.unread {
  border-left-color: #667eea;
  background-color: #f8fafc;
}

.notification-card.selected {
  background-color: #eff6ff;
  border-left-color: #3b82f6;
}

.notification-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notification-icon {
  margin-right: 15px;
  font-size: 24px;
}

.notification-info {
  flex: 1;
}

.notification-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.notification-time {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
}

.notification-actions {
  display: flex;
  gap: 10px;
  margin: 0;
  padding: 0;
  background: none;
  box-shadow: none;
}

.notification-body {
  margin-bottom: 15px;
  padding-left: 39px;
}

.notification-body p {
  margin: 0;
  color: #6b7280;
  line-height: 1.6;
}

.notification-footer {
  padding-left: 39px;
}

.action-button {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  background-color: #f3f4f6;
  color: #667eea;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.action-button:hover {
  background-color: #e5e7eb;
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
  margin: 0 0 16px 0;
  font-size: 16px;
}

.loading-state {
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

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-state {
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

.error-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-state p {
  margin: 0 0 16px 0;
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
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

.text-primary {
  color: #3b82f6;
}

.text-secondary {
  color: #6b7280;
}

.text-success {
  color: #10b981;
}

.text-warning {
  color: #f59e0b;
}

.text-danger {
  color: #ef4444;
}

.text-gray-400 {
  color: #9ca3af;
}

.hidden {
  display: none;
}
</style>
