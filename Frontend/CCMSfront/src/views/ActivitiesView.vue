<template>
  <div class="activities-container">
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
          <li class="nav-item active">
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
          <h1 class="page-title">活动管理</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input
              type="text"
              placeholder="搜索活动..."
              v-model="searchQuery"
              @keyup="handleSearchInput"
            />
            <i class="fa fa-search" @click="handleSearch"></i>
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
        <div class="activities-section">
          <!-- 活动筛选器 -->
          <div class="filter-section">
            <div class="filter-tabs">
              <button
                v-for="tab in filterTabs"
                :key="tab.key"
                :class="['filter-tab', { active: activeTab === tab.key }]"
                @click="handleTabChange(tab.key)"
                :disabled="loading"
              >
                {{ tab.label }}
              </button>
            </div>
            <div class="filter-actions">
              <button
                class="btn btn-primary"
                :class="{ 'btn-disabled': !canManageActivities }"
                @click="handleCreateClick"
                :disabled="loading || !canManageActivities"
              >
                <i class="fa fa-plus" :class="{ 'icon-disabled': !canManageActivities }"></i>
                创建活动
              </button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载活动数据...</p>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="error" class="error-state">
            <p class="error-message">{{ error }}</p>
            <button class="btn btn-primary" @click="fetchActivities">重新加载</button>
          </div>

          <!-- 活动列表 -->
          <div v-else class="activities-list">
            <div class="table-responsive">
              <table class="activities-table">
                <thead>
                  <tr>
                    <th>活动名称</th>
                    <th>所属社团</th>
                    <th>时间</th>
                    <th>地点</th>
                    <th>参与人数</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="activity in filteredActivities" :key="activity.id">
                    <td class="activity-name">{{ activity.title }}</td>
                    <td>{{ (activity as any).clubName || "未知社团" }}</td>
                    <td>{{ formatDate(activity.startTime) }}</td>
                    <td>{{ activity.location || "地点待定" }}</td>
                    <td>
                      {{ activity.currentParticipants || 0 }}/{{
                        activity.maxParticipants || "不限"
                      }}
                    </td>
                    <td>
                      <span :class="['status-badge', getStatusClass(activity.status)]">
                        {{ getStatusText(activity.status) }}
                      </span>
                    </td>
                    <td>
                      <div class="action-buttons">
                        <button
                          class="btn btn-sm btn-primary"
                          @click="viewActivityDetails(activity)"
                        >
                          <i class="fa fa-eye"></i> 详情
                        </button>
                        <button
                          class="btn btn-sm btn-outline"
                          v-if="(activity as any).canEdit"
                          @click="editActivity(activity)"
                        >
                          <i class="fa fa-edit"></i> 编辑
                        </button>
                        <button
                          class="btn btn-sm btn-outline text-warning"
                          v-if="(activity as any).canCancel"
                          @click="cancelActivity(activity.id)"
                        >
                          <i class="fa fa-times"></i> 取消
                        </button>
                        <button
                          class="btn btn-sm btn-outline text-danger"
                          :class="{ 'btn-disabled': !canManageActivities }"
                          v-if="(activity as any).canEdit && canManageActivities"
                          @click="deleteActivity(activity.id, activity.title)"
                          :disabled="!canManageActivities"
                        >
                          <i
                            class="fa fa-trash"
                            :class="{ 'icon-disabled': !canManageActivities }"
                          ></i>
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="filteredActivities.length === 0" class="empty-state">
              <i class="fa fa-calendar text-gray-400"></i>
              <p>{{ searchQuery ? "未找到匹配的活动" : "暂无活动" }}</p>
              <button
                v-if="!searchQuery"
                class="btn btn-primary"
                @click="fetchActivities"
              >
                刷新
              </button>
              <button
                v-else
                class="btn btn-primary"
                :class="{ 'btn-disabled': !canManageActivities }"
                @click="handleCreateClick"
                :disabled="!canManageActivities"
              >
                <i class="fa fa-plus" :class="{ 'icon-disabled': !canManageActivities }"></i>
                创建活动
              </button>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="!loading && !error && totalPages > 0" class="pagination">
            <button
              class="pagination-btn"
              :disabled="currentPage === 1"
              @click="handlePageChange(currentPage - 1)"
            >
              上一页
            </button>
            <span class="pagination-info"
              >第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span
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

    <!-- 活动详情模态框 -->
    <div v-if="viewingActivity" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop style="max-width: 800px">
        <div class="modal-header">
          <h3>活动详情</h3>
          <button class="close-btn" @click="closeModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="activity-detail">
            <div class="detail-row">
              <label>活动名称：</label>
              <span>{{ viewingActivity.title }}</span>
            </div>
            <div class="detail-row">
              <label>所属社团：</label>
              <span>{{ (viewingActivity as any).clubName || "未知社团" }}</span>
            </div>
            <div class="detail-row">
              <label>活动状态：</label>
              <span :class="['status-badge', getStatusClass(viewingActivity.status)]">
                {{ getStatusText(viewingActivity.status) }}
              </span>
            </div>
            <div class="detail-row">
              <label>开始时间：</label>
              <span>{{ formatDate(viewingActivity.startTime) }}</span>
            </div>
            <div class="detail-row">
              <label>结束时间：</label>
              <span>{{ formatDate(viewingActivity.endTime) }}</span>
            </div>
            <div class="detail-row">
              <label>活动地点：</label>
              <span>{{ viewingActivity.location || "地点待定" }}</span>
            </div>
            <div class="detail-row">
              <label>参与人数：</label>
              <span>
                {{ viewingActivity.currentParticipants || 0 }} /
                {{ viewingActivity.maxParticipants || "不限" }}
              </span>
            </div>
            <div v-if="viewingActivity.description" class="detail-row">
              <label>活动描述：</label>
              <div class="detail-content">{{ viewingActivity.description }}</div>
            </div>
            <div v-if="viewingActivity.content" class="detail-row">
              <label>活动内容：</label>
              <div class="detail-content">{{ viewingActivity.content }}</div>
            </div>
            <div v-if="viewingActivity.enrollmentDeadline" class="detail-row">
              <label>报名截止时间：</label>
              <span>{{ formatDate(viewingActivity.enrollmentDeadline) }}</span>
            </div>
            <div class="detail-row">
              <label>需要审批：</label>
              <span>{{ viewingActivity.needApproval ? "是" : "否" }}</span>
            </div>
          </div>

          <!-- 点赞和评论区域 -->
          <div class="activity-interaction">
            <!-- 点赞按钮 -->
            <div class="like-section">
              <button
                class="like-btn"
                :class="{ liked: activityLiked }"
                @click="toggleActivityLike"
              >
                <i class="fa fa-heart"></i>
                <span>{{ activityLikesCount }}</span>
              </button>
            </div>

            <!-- 评论框 -->
            <div class="comment-input-section">
              <div class="comment-input-wrapper">
                <img
                  :src="currentUser?.avatar || '/default-avatar.png'"
                  alt="头像"
                  class="comment-avatar"
                />
                <textarea
                  v-model="newCommentContent"
                  class="comment-input"
                  placeholder="写下你的评论..."
                  rows="3"
                ></textarea>
              </div>
              <button
                class="btn btn-primary btn-sm"
                @click="submitComment"
                :disabled="!newCommentContent.trim() || submittingComment"
              >
                {{ submittingComment ? "提交中..." : "发表评论" }}
              </button>
            </div>

            <!-- 评论列表 -->
            <div class="comments-section">
              <h4 class="comments-title">评论 ({{ comments.length }})</h4>
              <div v-if="loadingComments" class="loading-comments">加载中...</div>
              <div v-else-if="comments.length === 0" class="no-comments">
                暂无评论，快来发表第一条吧！
              </div>
              <div v-else class="comments-list">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <img
                    :src="comment.userAvatar || '/default-avatar.png'"
                    alt="头像"
                    class="comment-avatar-small"
                  />
                  <div class="comment-content">
                    <div class="comment-header">
                      <span class="comment-username">{{
                        comment.userName || "匿名用户"
                      }}</span>
                      <span class="comment-time">{{
                        formatCommentTime(comment.createdAt)
                      }}</span>
                    </div>
                    <div class="comment-text">{{ comment.content }}</div>
                    <div class="comment-actions">
                      <button
                        class="comment-action-btn"
                        :class="{ liked: comment.isLiked }"
                        @click="toggleCommentLike(comment)"
                      >
                        <i class="fa fa-heart"></i>
                        <span>{{ comment.likesCount || 0 }}</span>
                      </button>
                      <button class="comment-action-btn" @click="replyToComment(comment)">
                        <i class="fa fa-reply"></i>
                        回复
                      </button>
                    </div>
                    <!-- 回复列表 -->
                    <div
                      v-if="comment.replies && comment.replies.length > 0"
                      class="replies-list"
                    >
                      <div
                        v-for="reply in comment.replies"
                        :key="reply.id"
                        class="reply-item"
                      >
                        <img
                          :src="reply.userAvatar || '/default-avatar.png'"
                          alt="头像"
                          class="comment-avatar-small"
                        />
                        <div class="reply-content">
                          <div class="comment-header">
                            <span class="comment-username">{{
                              reply.userName || "匿名用户"
                            }}</span>
                            <span class="comment-time">{{
                              formatCommentTime(reply.createdAt)
                            }}</span>
                          </div>
                          <div class="comment-text">{{ reply.content }}</div>
                          <div class="comment-actions">
                            <button
                              class="comment-action-btn"
                              :class="{ liked: reply.isLiked }"
                              @click="toggleCommentLike(reply)"
                            >
                              <i class="fa fa-heart"></i>
                              <span>{{ reply.likesCount || 0 }}</span>
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal">关闭</button>
          <button
            v-if="(viewingActivity as any).canEdit"
            class="btn btn-primary"
            @click="editActivity(viewingActivity)"
          >
            <i class="fa fa-edit"></i> 编辑
          </button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑活动模态框 -->
    <div
      v-if="showCreateActivityModal || editingActivity"
      class="modal-overlay"
      @click="closeModal"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingActivity ? "编辑活动" : "创建活动" }}</h3>
          <button class="close-btn" @click="closeModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <!-- 活动表单 -->
          <div class="form-group">
            <label>活动名称 *</label>
            <input
              type="text"
              class="form-control"
              placeholder="输入活动名称"
              v-model="activityForm.name"
            />
          </div>
          <div class="form-group">
            <label>所属社团 *</label>
            <select class="form-control" v-model="activityForm.clubId">
              <option value="" disabled>请选择社团</option>
              <option v-for="c in clubsForSelect" :key="c.id" :value="String(c.id)">
                {{ c.name }}
              </option>
            </select>
          </div>
          <div class="form-row">
            <div class="form-group col-md-6">
              <label>开始时间 *</label>
              <input
                type="datetime-local"
                class="form-control"
                v-model="activityForm.startTime"
              />
            </div>
            <div class="form-group col-md-6">
              <label>结束时间 *</label>
              <input
                type="datetime-local"
                class="form-control"
                v-model="activityForm.endTime"
              />
            </div>
          </div>
          <div class="form-group">
            <label>活动地点 *</label>
            <input
              type="text"
              class="form-control"
              placeholder="输入活动地点"
              v-model="activityForm.location"
            />
          </div>
          <div class="form-group">
            <label>活动描述</label>
            <textarea
              class="form-control"
              rows="4"
              placeholder="输入活动描述"
              v-model="activityForm.description"
            ></textarea>
          </div>
          <div class="form-group">
            <label>最大参与人数</label>
            <input
              type="number"
              class="form-control"
              min="1"
              v-model="activityForm.maxParticipants"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal" :disabled="isSubmitting">
            取消
          </button>
          <button class="btn btn-primary" @click="saveActivity" :disabled="isSubmitting">
            {{ isSubmitting ? "保存中..." : "保存" }}
          </button>
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
  activityApi,
  notificationApi,
  clubApi,
  commentApi,
} from "../api/apiService";
import type { User, Activity, ActivityComment } from "../types/index";

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

// 计算属性：判断是否有管理活动的权限（管理员/社团管理员）
const canManageActivities = computed(() => {
  const role = currentUser.value?.role || "";
  const allowed = ["ADMIN", "admin", "CLUB_ADMIN", "club_admin", "club_manager"];
  return allowed.includes(role);
});

// 搜索
const searchQuery = ref("");

// 活动列表
const activities = ref<Activity[]>([]);
const loading = ref(false);
const error = ref("");

// 社团名称缓存（clubId -> clubName）
const clubNameCache = ref<Map<number, string>>(new Map());

// 筛选标签
const filterTabs = [
  { key: "all", label: "全部" },
  { key: "upcoming", label: "即将开始" },
  { key: "ongoing", label: "进行中" },
  { key: "completed", label: "已结束" },
];
const activeTab = ref("all");

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(1);

// 模态框
const showCreateActivityModal = ref(false);
const editingActivity = ref<Activity | null>(null);
const viewingActivity = ref<Activity | null>(null);
const isSubmitting = ref(false);

// 活动点赞相关
const activityLiked = ref(false);
const activityLikesCount = ref(0);

// 评论相关
const comments = ref<ActivityComment[]>([]);
const loadingComments = ref(false);
const newCommentContent = ref("");
const submittingComment = ref(false);
const replyingToComment = ref<ActivityComment | null>(null);

// 活动表单
const activityForm = ref({
  name: "",
  clubId: "",
  startTime: "",
  endTime: "",
  location: "",
  description: "",
  maxParticipants: 0,
});

// 用于下拉选择的社团列表
const clubsForSelect = ref<Array<{ id: number; name: string }>>([]);
const clubsLoaded = ref(false);
const ensureClubsForSelect = async () => {
  if (clubsLoaded.value) return;
  try {
    const resp = await clubApi.getAllClubs();
    const data = resp.data || [];
    const list = (Array.isArray(data) ? data : data.list || []).map((item: any) => ({
      id: item.id,
      name: item.name,
    }));
    clubsForSelect.value = list;
    clubsLoaded.value = true;
  } catch (e) {
    console.error("加载社团列表失败，用于下拉框", e);
    clubsForSelect.value = [];
  }
};

// 过滤后的活动列表
const filteredActivities = computed(() => {
  let result = activities.value;

  // 按标签筛选
  if (activeTab.value !== "all") {
    const now = new Date();
    result = result.filter((activity) => {
      const startTime = new Date(activity.startTime);
      const endTime = new Date(activity.endTime);
      switch (activeTab.value) {
        case "upcoming":
          return startTime > now;
        case "ongoing":
          return startTime <= now && endTime >= now;
        case "completed":
          return endTime < now;
        default:
          return true;
      }
    });
  }

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (activity) =>
        (activity.title || "").toLowerCase().includes(query) ||
        ((activity as any).clubName || "").toLowerCase().includes(query) ||
        (activity.description || "").toLowerCase().includes(query)
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
      console.log("用户信息已加载:", currentUser.value);
    } else {
      const response = await authApi.getCurrentUser();
      if (response.data) {
        currentUser.value = response.data;
        localStorage.setItem("userInfo", JSON.stringify(response.data));
        console.log("用户信息已加载:", currentUser.value);
      }
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
    currentUser.value = null;
  }
};

// 加载未读通知数
const loadUnreadNotifications = async () => {
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) return;
    const response = await notificationApi.getUnreadCount(uid);
    // 后端直接返回数字
    unreadNotifications.value = Number(response.data) || 0;
  } catch (error) {
    console.error("加载未读通知数失败:", error);
  }
};

// 批量获取社团名称
const fetchClubNames = async (clubIds: number[]) => {
  // 去重并过滤已缓存的clubId
  const uniqueClubIds = [...new Set(clubIds)].filter(
    (id) => id && !clubNameCache.value.has(id)
  );

  if (uniqueClubIds.length === 0) {
    return;
  }
  console.log(uniqueClubIds);
  // 并发获取所有社团信息
  const promises = uniqueClubIds.map(async (clubId) => {
    try {
      const response = await clubApi.getClubDetail(clubId);
      console.log(response);
      if (response.data) {
        const clubName = response.data.name || response.data.clubName || "未知社团";
        clubNameCache.value.set(clubId, clubName);
      }
    } catch (err) {
      console.error(`获取社团${clubId}信息失败:`, err);
      clubNameCache.value.set(clubId, "未知社团");
    }
  });

  await Promise.all(promises);
};

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true;
  error.value = "";
  try {
    const response = await activityApi.getActivities({
      page: currentPage.value - 1, // Spring Data分页从0开始
      size: pageSize.value,
      sort: "id,asc",
    });
    console.log("活动列表响应:", response);

    // Spring Data Page格式: { content: [...], totalPages: ..., totalElements: ..., ... }
    if (response.data) {
      const pageData = response.data;
      const content = pageData.content || [];

      activities.value = content.map(
        (item: any) =>
          ({
            id: item.id,
            title: item.title,
            description: item.description,
            content: item.content,
            coverImage: item.coverImage,
            images: item.images,
            startTime: item.startTime,
            endTime: item.endTime,
            location: item.location,
            organizerId: item.organizerId,
            clubId: item.clubId,
            status: item.status ?? 0,
            maxParticipants: item.maxParticipants,
            currentParticipants: item.currentParticipants ?? 0,
            enrollmentDeadline: item.enrollmentDeadline,
            tags: item.tags,
            needApproval: item.needApproval,
            createdAt: item.createdAt,
            updatedAt: item.updatedAt,
            // 扩展属性（用于UI显示和操作）
            clubName: item.clubName || item.club?.name || "",
            canEdit: currentUser.value?.id === item.organizerId,
            canCancel: item.status === 0 || item.status === 1 || item.status === 2,
          } as Activity & { clubName?: string; canEdit?: boolean; canCancel?: boolean })
      );

      // 收集所有clubId并批量获取社团名称
      const clubIds = activities.value
        .map((activity) => activity.clubId)
        .filter((id) => id !== undefined && id !== null) as number[];

      if (clubIds.length > 0) {
        await fetchClubNames(clubIds);

        // 更新活动列表中的clubName
        activities.value = activities.value.map((activity) => {
          if (activity.clubId && clubNameCache.value.has(activity.clubId)) {
            return {
              ...activity,
              clubName: clubNameCache.value.get(activity.clubId) || "未知社团",
            };
          }
          return activity;
        });
      }

      // 按活动ID升序排序展示
      activities.value = activities.value.sort((a, b) => {
        const ida = (a as any).id ?? 0;
        const idb = (b as any).id ?? 0;
        return ida - idb;
      });

      // 设置分页信息
      totalPages.value = pageData.totalPages || 0;
      if (pageData.totalElements !== undefined) {
        // 如果后端返回了totalElements，也可以用于计算
        console.log(
          `总活动数: ${pageData.totalElements}, 总页数: ${pageData.totalPages}`
        );
      }
    } else {
      console.warn("响应数据格式异常:", response);
      activities.value = [];
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || "加载活动列表失败";
    console.error("获取活动列表失败:", err);
    activities.value = [];
  } finally {
    loading.value = false;
  }
};

// 获取状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: "草稿",
    1: "已发布",
    2: "进行中",
    3: "已完成",
    4: "已取消",
  };
  return statusMap[status] || "未知";
};

// 获取状态类名
const getStatusClass = (status: number): string => {
  const classMap: Record<number, string> = {
    0: "draft",
    1: "published",
    2: "ongoing",
    3: "completed",
    4: "cancelled",
  };
  return classMap[status] || "draft";
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

// 标签切换
const handleTabChange = (tabKey: string) => {
  if (tabKey && filterTabs.some((tab) => tab.key === tabKey)) {
    activeTab.value = tabKey;
  }
};

// 搜索
const handleSearchInput = () => {
  // 实时搜索，不需要额外处理
};

const handleSearch = () => {
  // 搜索逻辑已在computed中处理
};

// 查看活动详情
const viewActivityDetails = async (activity: Activity) => {
  viewingActivity.value = activity;
  // 加载评论和点赞状态
  await loadComments();
  await loadActivityLikeStatus();
};

// 加载活动点赞状态
const loadActivityLikeStatus = async () => {
  if (!viewingActivity.value) return;
  try {
    // 这里需要后端提供获取活动点赞状态的接口
    // 暂时使用默认值
    activityLiked.value = false;
    activityLikesCount.value = 0;
  } catch (error) {
    console.error("加载点赞状态失败:", error);
  }
};

// 切换活动点赞
const toggleActivityLike = async () => {
  if (!viewingActivity.value) return;
  try {
    if (activityLiked.value) {
      await activityApi.unlikeActivity(viewingActivity.value.id);
      activityLiked.value = false;
      activityLikesCount.value = Math.max(0, activityLikesCount.value - 1);
    } else {
      await activityApi.likeActivity(viewingActivity.value.id);
      activityLiked.value = true;
      activityLikesCount.value += 1;
    }
  } catch (error: any) {
    console.error("点赞操作失败:", error);
    alert(error.response?.data?.message || "操作失败，请重试");
  }
};

// 加载评论列表（使用热门评论接口）
const loadComments = async () => {
  if (!viewingActivity.value) return;
  loadingComments.value = true;
  try {
    const response = await commentApi.getHotComments(viewingActivity.value.id, 10);
    if (response.data) {
      comments.value = response.data;
      // 加载每个评论的回复
      for (const comment of comments.value) {
        if (comment.replyCount && comment.replyCount > 0) {
          await loadCommentReplies(comment);
        }
      }
    }
  } catch (error: any) {
    console.error("加载评论失败:", error);
    comments.value = [];
  } finally {
    loadingComments.value = false;
  }
};

// 加载评论的回复
const loadCommentReplies = async (comment: ActivityComment) => {
  try {
    const response = await commentApi.getCommentReplies(comment.id);
    if (response.data) {
      comment.replies = response.data;
    }
  } catch (error) {
    console.error("加载回复失败:", error);
  }
};

// 提交评论
const submitComment = async () => {
  if (!viewingActivity.value || !newCommentContent.value.trim()) return;
  submittingComment.value = true;
  try {
    const commentData = {
      activityId: viewingActivity.value.id,
      content: newCommentContent.value.trim(),
      parentId: replyingToComment.value?.id,
    };
    await commentApi.createComment(commentData);
    newCommentContent.value = "";
    replyingToComment.value = null;
    // 重新加载评论
    await loadComments();
  } catch (error: any) {
    console.error("提交评论失败:", error);
    alert(error.response?.data?.message || "评论失败，请重试");
  } finally {
    submittingComment.value = false;
  }
};

// 回复评论
const replyToComment = (comment: ActivityComment) => {
  replyingToComment.value = comment;
  newCommentContent.value = `@${comment.userName || "用户"} `;
};

// 切换评论点赞
const toggleCommentLike = async (comment: ActivityComment) => {
  try {
    if (comment.isLiked) {
      await commentApi.unlikeComment(comment.id);
      comment.isLiked = false;
      comment.likesCount = Math.max(0, (comment.likesCount || 0) - 1);
    } else {
      await commentApi.likeComment(comment.id);
      comment.isLiked = true;
      comment.likesCount = (comment.likesCount || 0) + 1;
    }
  } catch (error: any) {
    console.error("点赞评论失败:", error);
    alert(error.response?.data?.message || "操作失败，请重试");
  }
};

// 格式化评论时间
const formatCommentTime = (time?: string) => {
  if (!time) return "";
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return "刚刚";
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString("zh-CN");
};

// 处理创建活动点击
const handleCreateClick = () => {
  if (!canManageActivities.value) {
    alert("学生无权创建活动！");
    return;
  }
  ensureClubsForSelect();
  showCreateActivityModal.value = true;
};

// 编辑活动
const editActivity = (activity: Activity) => {
  if (!canManageActivities.value) {
    alert("学生无权编辑活动！");
    return;
  }
  editingActivity.value = activity;
  activityForm.value = {
    name: activity.title,
    clubId: String(activity.clubId),
    startTime: new Date(activity.startTime).toISOString().slice(0, 16),
    endTime: new Date(activity.endTime).toISOString().slice(0, 16),
    location: activity.location || "",
    description: activity.description || "",
    maxParticipants: activity.maxParticipants || 0,
  };
  ensureClubsForSelect();
  showCreateActivityModal.value = true;
};

// 取消活动
const cancelActivity = async (activityId: number) => {
  if (!confirm("确定要取消这个活动吗？")) return;
  try {
    await activityApi.updateActivityStatus(activityId, 4); // 4表示已取消
    await fetchActivities();
  } catch (err: any) {
    alert(err.response?.data?.message || "取消活动失败");
  }
};

// 删除活动
const deleteActivity = async (activityId: number, activityName: string) => {
  if (!canManageActivities.value) {
    alert("学生无权删除活动！");
    return;
  }
  if (!confirm(`确定要删除活动"${activityName}"吗？此操作不可恢复！`)) return;
  try {
    await activityApi.deleteActivity(activityId);
    await fetchActivities();
  } catch (err: any) {
    alert(err.response?.data?.message || "删除活动失败");
  }
};

// 保存活动
const saveActivity = async () => {
  if (!canManageActivities.value) {
    alert("学生无权保存活动！");
    return;
  }
  if (!activityForm.value.name || !activityForm.value.clubId) {
    alert("请填写活动名称和所属社团");
    return;
  }
  isSubmitting.value = true;
  try {
    const activityData = {
      title: activityForm.value.name,
      clubId: Number(activityForm.value.clubId),
      startTime: new Date(activityForm.value.startTime).toISOString(),
      endTime: new Date(activityForm.value.endTime).toISOString(),
      location: activityForm.value.location,
      description: activityForm.value.description,
      maxParticipants: activityForm.value.maxParticipants,
      organizerId: Number(currentUser.value?.id || Number(localStorage.getItem("userId") || 0)),
    };
    if (editingActivity.value) {
      await activityApi.updateActivity(editingActivity.value.id, activityData);
    } else {
      await activityApi.createActivity(activityData);
    }
    closeModal();
    // 先刷新分页信息
    await fetchActivities();
    // 若是新建活动，按升序排序，新活动会在最后一页，跳转到最后一页再拉取一次
    if (!editingActivity.value && totalPages.value && totalPages.value > 0) {
      currentPage.value = totalPages.value;
      await fetchActivities();
    }
  } catch (err: any) {
    alert(err.response?.data?.message || "保存活动失败");
  } finally {
    isSubmitting.value = false;
  }
};

// 关闭模态框
const closeModal = () => {
  showCreateActivityModal.value = false;
  editingActivity.value = null;
  viewingActivity.value = null;
  // 清空评论相关数据
  comments.value = [];
  newCommentContent.value = "";
  replyingToComment.value = null;
  activityLiked.value = false;
  activityLikesCount.value = 0;
  activityForm.value = {
    name: "",
    clubId: "",
    startTime: "",
    endTime: "",
    location: "",
    description: "",
    maxParticipants: 0,
  };
};

// 分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchActivities();
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
  await fetchActivities();

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
.activities-container {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.filter-tab:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.filter-actions {
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

.btn-primary:hover:not(:disabled) {
  background-color: #5a67d8;
}

.btn-disabled,
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.icon-disabled {
  opacity: 0.5;
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

.activities-table {
  width: 100%;
  border-collapse: collapse;
}

.activities-table th,
.activities-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.activities-table th {
  background-color: #f9fafb;
  font-weight: 600;
  color: #374151;
}

.activities-table tbody tr:hover {
  background-color: #f9fafb;
}

.activity-name {
  font-weight: 500;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.upcoming {
  background-color: #dbeafe;
  color: #1e40af;
}

.status-badge.ongoing {
  background-color: #dcfce7;
  color: #166534;
}

.status-badge.completed {
  background-color: #f3f4f6;
  color: #4b5563;
}

.status-badge.cancelled {
  background-color: #fee2e2;
  color: #991b1b;
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

/* 活动详情样式 */
.activity-detail {
  padding: 20px 0;
}

.detail-row {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.detail-row label {
  font-weight: 600;
  color: #374151;
  min-width: 120px;
  margin-right: 12px;
}

.detail-row span {
  color: #6b7280;
  flex: 1;
}

.detail-content {
  color: #6b7280;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin-top: 4px;
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

/* 表单样式 */
.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #374151;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group.col-md-6 {
  flex: 1;
  margin-bottom: 0;
}

.hidden {
  display: none;
}

/* 活动互动区域样式 */
.activity-interaction {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #e5e7eb;
}

/* 点赞按钮样式 */
.like-section {
  margin-bottom: 20px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #6b7280;
}

.like-btn:hover {
  background-color: #e5e7eb;
}

.like-btn.liked {
  background-color: #fce7f3;
  border-color: #f9a8d4;
  color: #ec4899;
}

.like-btn.liked i {
  color: #ec4899;
}

.like-btn i {
  font-size: 18px;
}

/* 评论输入区域 */
.comment-input-section {
  margin-bottom: 30px;
}

.comment-input-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-input {
  flex: 1;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.comment-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 评论列表样式 */
.comments-section {
  margin-top: 30px;
}

.comments-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
}

.loading-comments,
.no-comments {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar-small {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.comment-username {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #9ca3af;
}

.comment-text {
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  word-wrap: break-word;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.comment-action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: none;
  border: none;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.comment-action-btn:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.comment-action-btn.liked {
  color: #ec4899;
}

.comment-action-btn.liked i {
  color: #ec4899;
}

.comment-action-btn i {
  font-size: 14px;
}

/* 回复列表样式 */
.replies-list {
  margin-top: 16px;
  padding-left: 16px;
  border-left: 2px solid #e5e7eb;
}

.reply-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-content {
  flex: 1;
}

.reply-content .comment-header {
  margin-bottom: 4px;
}

.reply-content .comment-text {
  margin-bottom: 8px;
  font-size: 13px;
}
</style>
