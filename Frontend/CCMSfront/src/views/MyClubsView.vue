<template>
  <div class="my-clubs-container">
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
          <li class="nav-item active">
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
          <h1 class="page-title">我的社团</h1>
        </div>

        <div class="top-bar-right">
          <div class="search-box">
            <input type="text" placeholder="搜索社团..." v-model="searchQuery" />
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
        <div class="clubs-section">
          <div class="section-header">
            <h2>{{ showAllClubs ? "全部社团" : "我加入的社团" }}</h2>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 错误提示 -->
          <div v-else-if="error" class="error-state">
            <i class="fa fa-exclamation-circle text-red-500"></i>
            <p>{{ error }}</p>
            <button class="btn btn-primary" @click="fetchUserClubs">重新加载</button>
          </div>

          <!-- 社团列表 -->
          <div v-else-if="filteredClubs.length > 0" class="club-cards">
            <div v-for="club in paginatedClubs" :key="club.id" class="club-card">
              <img :src="club.logo" alt="社团头像" class="club-logo" />
              <div class="club-details">
                <div class="club-header-info">
                  <h3>{{ club.name }}</h3>
                </div>
                <p class="club-description">{{ club.description }}</p>
                <div class="club-stats">
                  <span
                    ><i class="fa fa-users"></i> {{ club.memberCount || 0 }} 成员</span
                  >
                  <span
                    ><i class="fa fa-calendar"></i>
                    {{ club.totalActivityCount || 0 }} 活动</span
                  >
                </div>
                <div class="club-actions">
                  <button class="btn btn-primary" @click="viewClubDetails(club)">
                    查看详情
                  </button>
                  <button
                    class="btn btn-outline"
                    v-if="canManageClub(club)"
                    @click="openManageClub(club)"
                  >
                    管理社团
                  </button>
                  <button
                    class="btn"
                    :class="isJoined(club.id) ? 'btn-disabled' : (isPending(club.id) ? 'btn-disabled' : 'btn-outline')"
                    :disabled="isJoined(club.id) || isPending(club.id)"
                    @click="openApplyModal(club)"
                  >
                    {{ isJoined(club.id) ? "已加入" : (isPending(club.id) ? "待审核" : "申请加入") }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <i class="fa fa-users text-gray-400"></i>
            <p>{{ searchQuery ? "未找到匹配的社团" : "您还没有加入任何社团" }}</p>
            <button class="btn btn-primary" @click="showAllClubs = true">浏览推荐社团</button>
          </div>

          <!-- 分页控制 -->
          <div v-if="!loading && !error && total > pageSize" class="pagination">
            <button
              class="btn btn-outline"
              :disabled="page === 1"
              @click="changePage(page - 1)"
            >
              上一页
            </button>
            <span class="pagination-info">
              第 {{ page }} 页，共 {{ totalPages }} 页，总计 {{ total }} 个社团
            </span>
            <button
              class="btn btn-outline"
              :disabled="page === totalPages"
              @click="changePage(page + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 右下角切换按钮：查看全部/返回我的社团 -->
    <button class="fab-toggle" @click="toggleShowAll">
      {{ showAllClubs ? "返回我的社团" : "查看全部社团" }}
    </button>

    <!-- 社团详情模态框 -->
    <div v-if="showClubModal" class="modal-backdrop" @click.self="showClubModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ selectedClub?.name || "社团详情" }}</h3>
          <button class="close-btn" @click="showClubModal = false">×</button>
        </div>
        <div class="modal-body">
          <div v-if="loadingClubDetails" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else>
            <div class="club-info">
              <img
                v-if="selectedClub?.logo"
                :src="selectedClub?.logo"
                class="club-logo large"
                alt="社团头像"
              />
              <p class="club-description">
                {{ selectedClub?.description || "暂无简介" }}
              </p>
            </div>
            <h4>社团活动</h4>
            <div v-if="clubActivities.length === 0" class="empty-state">
              <i class="fa fa-calendar text-gray-400"></i>
              <p>暂无活动或加载失败</p>
            </div>
            <ul v-else class="activity-list">
              <li v-for="a in clubActivities" :key="a.id" class="activity-item">
                <div class="activity-main">
                  <div class="activity-title">{{ a.title }}</div>
                  <div class="activity-meta">
                    <span
                      ><i class="fa fa-clock-o"></i> {{ a.startTime }} ~
                      {{ a.endTime }}</span
                    >
                    <span><i class="fa fa-map-marker"></i> {{ a.location }}</span>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showClubModal = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 申请加入模态框 -->
    <div
      v-if="showApplyModal"
      class="modal-backdrop"
      @click.self="showApplyModal = false"
    >
      <div class="modal">
        <div class="modal-header">
          <h3>申请加入社团</h3>
          <button class="close-btn" @click="showApplyModal = false">×</button>
        </div>
        <div class="modal-body">
          <div v-if="applyMissingFields.length" class="form-hint warning">
            请补全以下信息后再提交：{{ applyMissingFields.join("、") }}
          </div>
          <div class="form-grid two-col">
            <div class="form-item">
              <label>姓名<span class="req">*</span></label>
              <input v-model="applyForm.realName" placeholder="请输入姓名" />
            </div>
            <div class="form-item">
              <label>性别<span class="req">*</span></label>
              <select v-model="applyForm.gender">
                <option value="" disabled>请选择性别</option>
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-item">
              <label>专业<span class="req">*</span></label>
              <input v-model="applyForm.major" placeholder="请输入专业" />
            </div>
            <div class="form-item">
              <label>班级<span class="req">*</span></label>
              <input v-model="applyForm.className" placeholder="请输入班级" />
            </div>
            <div class="form-item full">
              <label>申请理由</label>
              <textarea
                v-model="applyForm.reason"
                placeholder="简单说明加入理由（可选）"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showApplyModal = false">取消</button>
          <button
            class="btn btn-primary"
            :disabled="submittingApply"
            @click="submitApply"
          >
            提交申请
          </button>
        </div>
      </div>
    </div>

  <!-- 管理社团模态框 -->
  <div v-if="showManageModal" class="modal-backdrop" @click.self="showManageModal = false">
    <div class="modal manage-modal">
      <div class="modal-header">
        <h3>管理社团 - {{ managingClub?.name || (managingClub as any)?.id }}</h3>
        <button class="close-btn" @click="showManageModal = false">×</button>
      </div>
      <div class="modal-body">
        <div v-if="loadingMembers" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-else>
          <table class="members-table">
            <thead>
              <tr>
                <th>用户ID</th>
                <th>姓名</th>
                <th>性别</th>
                <th>专业</th>
                <th>班级</th>
                <th>状态</th>
                <th class="actions-cell">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in manageMembers" :key="m.id">
                <td>{{ m.userId }}</td>
                <td>{{ m.realName || "-" }}</td>
                <td>{{ m.gender || "-" }}</td>
                <td>{{ m.major || "-" }}</td>
                <td>{{ m.className || "-" }}</td>
                <td>
                  <span v-if="m.status === 0">待审核</span>
                  <span v-else-if="m.status === 1">已加入</span>
                  <span v-else-if="m.status === 2">已退出</span>
                  <span v-else-if="m.status === 3">已开除</span>
                  <span v-else>-</span>
                </td>
                <td class="actions-cell">
                  <button
                    class="btn btn-outline"
                    @click="expelApply(m)"
                    :disabled="(currentUser?.id || 0) === m.userId"
                  >
                    申请开除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline" @click="showManageModal = false">关闭</button>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi, clubApi, notificationApi, activityApi } from "../api/apiService";
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
const unreadNotifications = ref(0);

// 搜索
const searchQuery = ref("");

// 社团列表（全部）
const clubs = ref<Club[]>([]);
const loading = ref(false);
const error = ref("");
// 防止重复请求
const clubsLoading = ref(false);
// 已加入/待审核社团集合与视图控制
const joinedClubIds = ref<Set<number>>(new Set());
const pendingClubIds = ref<Set<number>>(new Set());
const showAllClubs = ref(true);

// 分页
const page = ref(1);
const pageSize = ref(12);
const total = ref(0);
const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

// 过滤后的社团列表（按搜索与是否仅显示已加入）
const filteredClubs = computed(() => {
  let base = showAllClubs.value
    ? clubs.value
    : clubs.value.filter((c) => joinedClubIds.value.has(c.id));
  if (!searchQuery.value) return base;
  const query = searchQuery.value.toLowerCase();
  return base.filter(
    (club) =>
      club.name.toLowerCase().includes(query) ||
      club.description?.toLowerCase().includes(query)
  );
});

// 分页切片后的社团列表
const paginatedClubs = computed(() => {
  const start = (page.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredClubs.value.slice(start, end);
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

// 详情模态
const showClubModal = ref(false);
const selectedClub = ref<Club | null>(null);
const clubActivities = ref<Activity[]>([]);
const loadingClubDetails = ref(false);

// 申请加入模态
const showApplyModal = ref(false);
const applyForm = ref({
  realName: "",
  major: "",
  className: "",
  gender: "",
  reason: "",
});
const submittingApply = ref(false);

// 申请缺失项提示
const applyMissingFields = computed(() => {
  const missing: string[] = [];
  if (!applyForm.value.realName) missing.push("姓名");
  if (!applyForm.value.gender) missing.push("性别");
  if (!applyForm.value.major) missing.push("专业");
  if (!applyForm.value.className) missing.push("班级");
  return missing;
});

// 获取用户加入的社团与全部社团；若有加入默认显示“我的社团”，否则显示全部
const fetchUserClubs = async () => {
  if (clubsLoading.value) return;
  loading.value = true;
  clubsLoading.value = true;
  error.value = "";
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    let joinedCount = 0;
    joinedClubIds.value = new Set();
    pendingClubIds.value = new Set();
    if (uid) {
      const userClubsResp = await clubApi.getUserClubs(uid);
      const memberList = userClubsResp.data || [];
      if (Array.isArray(memberList)) {
        joinedCount = memberList.length;
        for (const m of memberList) {
          if (!m || typeof m.clubId !== "number") continue;
          if (m.status === 1) joinedClubIds.value.add(m.clubId);
          if (m.status === 0) pendingClubIds.value.add(m.clubId);
        }
      }
    }
    const allResp = await clubApi.getAllClubs();
    const data = allResp.data || [];
    const allClubs = (Array.isArray(data) ? data : data.list || []).map((item: any) => ({
      id: item.id,
      name: item.name,
      description: item.description,
      logo: item.logo,
      category: item.category,
      memberCount: item.memberCount || 0,
      totalActivityCount: item.totalActivityCount || 0,
      leaderId: item.leaderId,
      status: item.status,
    }));
    // 若当前用户是某社团负责人，则视作已加入（便于“我的社团”展示）
    if (uid) {
      for (const c of allClubs) {
        if (c.leaderId && Number(c.leaderId) === Number(uid)) {
          joinedClubIds.value.add(c.id);
        }
      }
    }
    clubs.value = allClubs;
    total.value = clubs.value.length;
    showAllClubs.value = joinedCount === 0;
  } catch (err: any) {
    error.value = err.response?.data?.message || "加载社团列表失败";
    console.error("获取社团列表失败:", err);
  } finally {
    loading.value = false;
    clubsLoading.value = false;
  }
};

// 是否已加入
const isJoined = (clubId: number): boolean => {
  return joinedClubIds.value.has(clubId);
};

// 是否待审核
const isPending = (clubId: number): boolean => {
  return pendingClubIds.value.has(clubId);
};

// 切换显示全部/仅我加入
const toggleShowAll = () => {
  showAllClubs.value = !showAllClubs.value;
};

// 是否可管理该社团（负责人或平台社团管理员）
const canManageClub = (club: Club): boolean => {
  const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
  const role = (currentUser.value as any)?.role || localStorage.getItem("role") || "";
  const isLeader = club.leaderId && Number(club.leaderId) === Number(uid);
  const isClubAdmin = ["ADMIN", "admin", "CLUB_ADMIN", "club_admin", "club_manager"].includes(
    String(role)
  );
  return Boolean(isLeader || isClubAdmin);
};

// 查看社团详情
const viewClubDetails = async (club: Club) => {
  selectedClub.value = null;
  clubActivities.value = [];
  showClubModal.value = true;
  loadingClubDetails.value = true;
  try {
    const [detailResp, actsResp] = await Promise.all([
      clubApi.getClubDetail(club.id),
      activityApi.getClubActivities(club.id, { page: 1, size: 20 }),
    ]);
    selectedClub.value = detailResp.data || club;
    const data = actsResp.data;
    console.debug('Club activities raw response:', data);
    const contentCandidates = [
      data?.data?.content,
      data?.data?.list,
      data?.data,
      data?.content,
      data?.list,
      data?.items,
      data?.records,
      data // 直接数组的情况
    ];
    let content: any = contentCandidates.find((c: any) => Array.isArray(c));
    // 若未解析到数组，尝试不带分页再请求一次
    if (!Array.isArray(content)) {
      try {
        const retryResp = await activityApi.getClubActivities(club.id);
        const rd = retryResp.data;
        const retryCandidates = [
          rd?.data?.content,
          rd?.data?.list,
          rd?.data,
          rd?.content,
          rd?.list,
          rd?.items,
          rd?.records,
          rd
        ];
        content = retryCandidates.find((c: any) => Array.isArray(c));
        console.debug('Retry activities raw response:', rd);
      } catch (e) {
        // 忽略重试错误，保持空数组
      }
    }
    clubActivities.value = (Array.isArray(content) ? content : []).map((a: any) => ({
      id: a.id,
      title: a.title || a.activityName,
      description: a.description,
      content: a.content,
      coverImage: a.coverImage,
      images: a.images,
      startTime: a.startTime,
      endTime: a.endTime,
      location: a.location,
      organizerId: a.organizerId,
      clubId: a.clubId,
      status: a.status,
      maxParticipants: a.maxParticipants,
      currentParticipants: a.currentParticipants,
      enrollmentDeadline: a.enrollmentDeadline,
      tags: a.tags,
      createdAt: a.createdAt,
      updatedAt: a.updatedAt,
    }));
  } catch (e) {
    console.error("加载社团详情失败", e);
    alert("加载社团详情失败");
    showClubModal.value = false;
  } finally {
    loadingClubDetails.value = false;
  }
};

// 打开申请加入弹窗并校验基本资料
let applyingClubId: number | null = null;
const openApplyModal = (club: Club) => {
  applyingClubId = club.id;
  if (joinedClubIds.value.has(club.id)) return;
  const user = (currentUser.value as any) || {};
  // 从本地资料预填
  applyForm.value.realName = user.realName || user.name || "";
  applyForm.value.major = user.major || "";
  applyForm.value.className = user.className || user.clazz || "";
  applyForm.value.gender = user.gender || "";

  // 基本字段校验，不通过也允许在弹窗中补充
  const missing: string[] = [];
  if (!applyForm.value.realName) missing.push("姓名");
  if (!applyForm.value.major) missing.push("专业");
  if (!applyForm.value.className) missing.push("班级");
  if (!applyForm.value.gender) missing.push("性别");
  if (missing.length > 0) {
    alert(`请完善以下信息后提交：${missing.join("、")}`);
  }
  showApplyModal.value = true;
};

// 提交申请加入
const submitApply = async () => {
  if (!applyingClubId) return;
  // 表单再次校验
  if (
    !applyForm.value.realName ||
    !applyForm.value.major ||
    !applyForm.value.className ||
    !applyForm.value.gender
  ) {
    alert("请完整填写姓名、专业、班级、性别");
    return;
  }
  submittingApply.value = true;
  try {
    const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
    if (!uid) {
      alert("未获取到用户信息，请重新登录");
      return;
    }
    // 先将补全的信息写回用户档案，避免下次仍需补全
    try {
      // 基础信息：真实姓名
      const basePayload: any = {};
      if (applyForm.value.realName && applyForm.value.realName !== currentUser.value?.realName) {
        basePayload.realName = applyForm.value.realName;
      }
      if (Object.keys(basePayload).length > 0) {
        await axiosInstance.put("/user/me", basePayload);
        if (currentUser.value) {
          currentUser.value.realName = applyForm.value.realName;
        }
      }
      // 档案信息：专业/班级/性别
      const profilePayload: any = {
        major: applyForm.value.major,
        className: applyForm.value.className,
        gender: applyForm.value.gender
      };
      await axiosInstance.put("/user/me/profile", profilePayload);
      // 刷新本地缓存，后续预填不再缺失
      try {
        const meResp = await axiosInstance.get("/user/me");
        if (meResp.data) {
          localStorage.setItem("userInfo", JSON.stringify(meResp.data));
          currentUser.value = meResp.data;
        }
      } catch {
        // 退化：直接合并更新当前本地数据
        const merged = { ...(currentUser.value as any) };
        merged.realName = applyForm.value.realName;
        merged.major = applyForm.value.major;
        merged.className = applyForm.value.className;
        merged.gender = applyForm.value.gender;
        localStorage.setItem("userInfo", JSON.stringify(merged));
        currentUser.value = merged;
      }
    } catch (e) {
      // 档案更新失败不阻塞申请提交
      console.warn("更新用户档案失败，将继续提交申请。", e);
    }
    // 提交入团申请（包含已补全信息）
    await clubApi.applyToClub(applyingClubId, {
      userId: uid,
      realName: applyForm.value.realName,
      gender: applyForm.value.gender,
      major: applyForm.value.major,
      className: applyForm.value.className,
      reason: applyForm.value.reason
    });
    alert("申请已提交，等待社长审核");
    showApplyModal.value = false;
    // 不直接标记已加入，等待后端审核；可临时标记为待审核以提示按钮状态
    pendingClubIds.value.add(applyingClubId);
  } catch (e: any) {
    alert(e?.response?.data?.message || "提交申请失败");
  } finally {
    submittingApply.value = false;
  }
};
// （已移除退出社团入口，避免未使用函数警告）

// 管理社团：成员列表与开除申请
const showManageModal = ref(false);
const managingClub = ref<Club | null>(null);
const manageMembers = ref<any[]>([]);
const loadingMembers = ref(false);

const openManageClub = async (club: Club) => {
  managingClub.value = club;
  showManageModal.value = true;
  loadingMembers.value = true;
  manageMembers.value = [];
  try {
    console.log("正在获取社团成员列表，clubId:", club.id);
    const resp = await clubApi.getClubMembers(club.id);
    console.log("获取成员列表响应:", resp);
    const list = resp.data || [];
    manageMembers.value = Array.isArray(list) ? list : list.list || [];
    console.log("解析后的成员列表:", manageMembers.value);
  } catch (e: any) {
    console.error("加载成员列表失败:", e);
    const errorMsg = e?.response?.data?.message || e?.message || "加载成员列表失败";
    alert(`加载成员列表失败: ${errorMsg}`);
  } finally {
    loadingMembers.value = false;
  }
};

const expelApply = async (member: any) => {
  const uid = currentUser.value?.id || Number(localStorage.getItem("userId") || 0);
  if (!uid || !managingClub.value) return;
  if (Number(member.userId) === Number(uid)) {
    alert("不能申请开除自己");
    return;
  }
  const reason = prompt("请输入开除申请描述（将发送给系统管理员审核）：", "");
  if (reason === null) return;
  try {
    const title = "社团成员开除申请";
    const content = `社团：${(managingClub.value as any).name || (managingClub.value as any).id}\n待开除用户ID：${
      member.userId
    }\n姓名：${member.realName || ""}\n性别：${member.gender || ""}\n班级：${
      member.className || ""
    }\n专业：${member.major || ""}\n申请人：${uid}\n说明：${reason || "无"}`;
    const notifications = [
      {
        userId: 1, // 系统管理员
        notificationType: 2,
        title,
        content,
        relatedId: (managingClub.value as any).id,
        relatedType: "club_expel",
      },
      {
        userId: Number(member.userId),
        notificationType: 2,
        title: "您被申请从社团开除",
        content: `社团：${(managingClub.value as any).name || (managingClub.value as any).id}\n说明：${
          reason || "无"
        }\n请等待管理员审核。`,
        relatedId: (managingClub.value as any).id,
        relatedType: "club_expel",
      },
      {
        userId: uid,
        notificationType: 2,
        title: "已提交开除申请",
        content: `已为社团「${(managingClub.value as any).name || (managingClub.value as any).id}」提交对用户${
          member.userId
        }的开除申请。`,
        relatedId: (managingClub.value as any).id,
        relatedType: "club_expel",
      },
    ];
    await axiosInstance.post("/notifications/batch", notifications);
    alert("已提交开除申请，等待系统管理员审核");
  } catch (e: any) {
    alert(e?.response?.data?.message || "提交开除申请失败");
  }
};

// 分页切换
const changePage = (newPage: number) => {
  if (newPage >= 1 && newPage <= totalPages.value) {
    page.value = newPage;
    // 使用前端分页，不重复请求
  }
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
  await fetchUserClubs();

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
.my-clubs-container {
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

.clubs-section {
  margin-bottom: 30px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1f2937;
}

.club-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.club-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.club-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.club-logo {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.club-details {
  padding: 20px;
}

.club-details h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #1f2937;
}

.club-description {
  margin: 0 0 15px 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
}

.club-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.club-stats span {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #6b7280;
}

.club-stats i {
  margin-right: 5px;
}

.club-actions {
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
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover {
  background-color: #5a67d8;
}

.btn-outline {
  background-color: transparent;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}

.btn-outline:hover {
  background-color: #f9fafb;
}

.btn-disabled {
  background-color: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
  grid-column: 1 / -1;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
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
  grid-column: 1 / -1;
}

.error-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.club-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.club-role {
  background-color: #667eea;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 15px;
}

.pagination-info {
  color: #6b7280;
  font-size: 14px;
}

.pagination .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
  grid-column: 1 / -1;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.fab-toggle {
  position: fixed;
  right: 24px;
  bottom: 24px;
  background-color: #667eea;
  color: #fff;
  border: none;
  border-radius: 24px;
  padding: 10px 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  z-index: 110;
}

.fab-toggle:hover {
  background-color: #5a67d8;
}

.hidden {
  display: none;
}

/* 详情模态内大图样式 */
.club-logo.large {
  width: 100%;
  height: 220px;
  border-radius: 8px;
  object-fit: cover;
  margin-bottom: 12px;
}

/* 申请弹窗表单美化 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}
.form-grid.two-col {
  grid-template-columns: 1fr 1fr;
}
.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-item.full {
  grid-column: 1 / -1;
}
.form-item label {
  font-size: 14px;
  color: #374151;
}
.form-item .req {
  color: #ef4444;
  margin-left: 4px;
}
.form-item input,
.form-item select,
.form-item textarea {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  outline: none;
  transition: border-color 0.2s ease;
  background-color: #fff;
}
.form-item input:focus,
.form-item select:focus,
.form-item textarea:focus {
  border-color: #667eea;
}
.form-item textarea {
  min-height: 100px;
  resize: vertical;
}
.form-hint.warning {
  margin-bottom: 12px;
  padding: 10px 12px;
  background-color: #fff7ed;
  border: 1px solid #fdba74;
  color: #9a3412;
  border-radius: 8px;
  font-size: 14px;
}

/* 管理社团模态样式 */
.manage-modal .members-table {
  width: 100%;
  border-collapse: collapse;
}
.manage-modal th,
.manage-modal td {
  padding: 10px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  font-size: 14px;
  color: #374151;
}
.manage-modal .actions-cell {
  text-align: right;
}
/* 通用模态框样式（白底黑字） */
.modal-backdrop {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 120;
  padding: 16px;
}

.modal {
  width: 720px;
  max-width: 90vw;
  background-color: #ffffff;
  color: #111827; /* 黑色系文字 */
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  background-color: #ffffff;
  color: #111827;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 20px;
  background-color: #ffffff;
  color: #111827;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #ffffff;
  color: #111827;
}
</style>
