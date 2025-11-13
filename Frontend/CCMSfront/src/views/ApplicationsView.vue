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
                          v-if="canCancelApplication(application)"
                          @click="cancelApplication(application)"
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
          <div
            v-if="selectedApplication.clubName || selectedApplication.clubId"
            class="detail-item"
          >
            <span class="detail-label">所属社团：</span>
            <span class="detail-value">
              {{ selectedApplication.clubName || `社团#${selectedApplication.clubId}` }}
            </span>
          </div>
          <div
            v-if="
              selectedApplication.type === 'activity_participate' &&
              selectedApplication.applicantName
            "
            class="detail-item"
          >
            <span class="detail-label">申请人：</span>
            <span class="detail-value">
              {{ selectedApplication.applicantName }}
              <span v-if="selectedApplication.applicantUserId">
                （ID：{{ selectedApplication.applicantUserId }}）
              </span>
            </span>
          </div>
          <div v-if="selectedApplication.activityId" class="detail-item">
            <span class="detail-label">活动ID：</span>
            <span class="detail-value">{{ selectedApplication.activityId }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">申请时间：</span>
            <span class="detail-value">{{
              formatDate(selectedApplication.applyTime)
            }}</span>
          </div>
          <div
            v-if="selectedApplication.approvedByName || selectedApplication.approvedBy"
            class="detail-item"
          >
            <span class="detail-label">审核人：</span>
            <span class="detail-value">
              {{ selectedApplication.approvedByName || "暂无" }}
              <span v-if="selectedApplication.approvedBy">
                （ID：{{ selectedApplication.approvedBy }}）
              </span>
            </span>
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
              selectedApplication.type === 'activity_participate' &&
              selectedApplication.status === 'pending' &&
              canApproveActivityParticipation(selectedApplication)
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
          <!-- 社团加入申请的审核按钮 -->
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
          <!-- 活动创建申请的审核按钮（仅系统管理员可见） -->
          <template
            v-if="
              selectedApplication &&
              selectedApplication.type === 'activity_create' &&
              selectedApplication.status === 'pending' &&
              (currentUser?.id === 1 || storedUserId === 1)
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
          <!-- 社团成员开除申请的审核按钮（仅系统管理员可见） -->
          <template
            v-if="
              selectedApplication &&
              selectedApplication.type === 'club_expel' &&
              selectedApplication.status === 'pending' &&
              (currentUser?.id === 1 || storedUserId === 1)
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
import {
  authApi,
  notificationApi,
  activityApi,
  activityParticipantApi,
} from "../api/apiService";
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

// 本地缓存的用户ID/角色（避免模板直接访问 localStorage 导致 undefined）
const storedUserId = ref<number>(Number(localStorage.getItem("userId") || 0));
const storedRole = ref<string>(localStorage.getItem("role") || "");
const isSystemAdminUser = computed(() => {
  const uid = currentUser.value?.id || storedUserId.value;
  const role = (currentUser.value as any)?.role || storedRole.value;
  if (!uid) return false;
  return uid === 1 || role === "ADMIN" || role === "admin";
});

// 搜索
const searchQuery = ref("");

// 申请列表
interface Application {
  applicationId: number | string;
  type: string;
  typeText: string;
  relatedName: string;
  applyTime: string;
  status: string;
  statusText: string;
  processTime?: string | null;
  remark?: string | null;
  reason?: string | null;
  clubId?: number;
  clubName?: string;
  applicantUserId?: number;
  applicantName?: string;
  activityId?: number;
  approvedBy?: number;
  approvedByName?: string;
  targetUserId?: number;
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

// 本地记录：系统管理员对开除申请的审批结果，解决通知不回写状态的问题
const expelDecisions = ref<Record<string, "approved" | "rejected">>({});
try {
  const raw = localStorage.getItem("expel_decisions");
  if (raw) {
    expelDecisions.value = JSON.parse(raw);
  }
} catch {}
const saveExpelDecisions = () => {
  try {
    localStorage.setItem("expel_decisions", JSON.stringify(expelDecisions.value));
  } catch {}
};

// 通用响应解析
const extractListFromResponse = (respData: any): any[] => {
  if (!respData) return [];
  if (Array.isArray(respData)) return respData;
  if (Array.isArray(respData.content)) return respData.content;
  if (Array.isArray(respData.items)) return respData.items;
  if (Array.isArray(respData.list)) return respData.list;
  if (respData.data) return extractListFromResponse(respData.data);
  if (Array.isArray(respData.records)) return respData.records;
  return [];
};

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
      if (currentUser.value?.id) {
        storedUserId.value = currentUser.value.id;
        localStorage.setItem("userId", String(currentUser.value.id));
      }
    } else {
      const response = await authApi.getCurrentUser();
      if (response.data) {
        currentUser.value = response.data;
        if (currentUser.value?.id) {
          storedUserId.value = currentUser.value.id;
          localStorage.setItem("userId", String(currentUser.value.id));
        }
      }
    }
    const role = (currentUser.value as any)?.role;
    if (role) {
      storedRole.value = role;
      localStorage.setItem("role", String(role));
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
  }
};

// 加载未读通知数
const loadUnreadNotifications = async () => {
  try {
    const uid = currentUser.value?.id || storedUserId.value;
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
    const uid = currentUser.value?.id || storedUserId.value;
    if (!uid) throw new Error("未获取到用户信息");
    const role = (currentUser.value as any)?.role || storedRole.value || "";
    const isSystemAdmin = uid === 1;

    const managerRoles = ["ADMIN", "admin", "CLUB_ADMIN", "club_admin", "club_manager"];
    const isManager = managerRoles.includes(role as string);

    const clubInfoMap = new Map<number, { name?: string }>();
    const activityClubMap = new Map<number, { clubId: number; clubName?: string }>();

    let clubApplicationsSource: any[] = [];
    const managerParticipationApplications: Application[] = [];

    if (isManager) {
      const clubs: any[] = [];
      try {
        const clubsResp = await axiosInstance.get(`/clubs/leader/${uid}`);
        const clubsData = clubsResp.data || [];
        const leaderClubs = Array.isArray(clubsData)
          ? clubsData
          : clubsData.list || clubsData.data || [];
        clubs.push(...leaderClubs);
      } catch (leaderErr) {
        console.error("加载管理社团失败:", leaderErr);
      }

      if (isSystemAdmin) {
        try {
          const allClubsResp = await axiosInstance.get(`/clubs`);
          const allClubsData = allClubsResp.data || [];
          const allClubs = Array.isArray(allClubsData)
            ? allClubsData
            : allClubsData.list || allClubsData.data || [];
          allClubs.forEach((club: any) => {
            const exists = clubs.some(
              (c: any) => Number(c?.id ?? c?.clubId) === Number(club?.id ?? club?.clubId)
            );
            if (!exists) {
              clubs.push(club);
            }
          });
        } catch (allErr) {
          console.error("加载全部社团失败:", allErr);
        }
      }

      const clubIdSet: Set<number> = new Set();
      const pushClubInfo = (candidate: any) => {
        const rawId = candidate?.id ?? candidate?.clubId;
        const clubId = Number(rawId);
        if (!Number.isNaN(clubId) && clubId > 0) {
          clubIdSet.add(clubId);
          const clubName =
            candidate?.name ||
            candidate?.clubName ||
            candidate?.title ||
            candidate?.club_name;
          if (clubName) {
            clubInfoMap.set(clubId, { name: clubName });
          }
        }
      };
      clubs.forEach(pushClubInfo);

      const rawClubIds = (currentUser.value as any)?.clubIds || [];
      if (Array.isArray(rawClubIds)) {
        rawClubIds.forEach((clubId: any) => {
          const numId = Number(clubId);
          if (!Number.isNaN(numId) && numId > 0) {
            clubIdSet.add(numId);
          }
        });
      }

      managedClubIds.value = new Set(clubIdSet);

      for (const clubId of clubIdSet) {
        try {
          const appsResp = await axiosInstance.get(`/clubs/applications/club/${clubId}`);
          const arr = extractListFromResponse(appsResp.data);
          arr.forEach((it: any) => {
            it.clubId = it.clubId || clubId;
            if (!it.clubName) {
              it.clubName = clubInfoMap.get(clubId)?.name || `社团#${clubId}`;
            }
          });
          clubApplicationsSource.push(...arr);
        } catch (clubErr) {
          console.error(`加载社团 ${clubId} 申请失败:`, clubErr);
        }
      }

      for (const clubId of clubIdSet) {
        let clubActivities: any[] = [];
        try {
          const activitiesResp = await activityApi.getClubActivities(clubId, {
            page: 0,
            size: 200,
            sort: "startTime,desc",
          });
          clubActivities = extractListFromResponse(activitiesResp.data);
        } catch (activityErr) {
          console.error(`加载社团 ${clubId} 活动列表失败:`, activityErr);
        }

        const clubName = clubInfoMap.get(clubId)?.name || `社团#${clubId}`;

        for (const activity of clubActivities) {
          const activityId = Number(activity?.id ?? activity?.activityId);
          if (!activityId) continue;

          activityClubMap.set(activityId, { clubId, clubName });

          let pendingParticipants: any[] = [];
          try {
            const participantsResp = await activityParticipantApi.getActivityParticipants(
              activityId,
              {
                status: 0,
                page: 0,
                size: 500,
                sort: "enrollmentTime,desc",
              }
            );
            pendingParticipants = extractListFromResponse(participantsResp.data);
          } catch (participantsErr) {
            console.error(`加载活动 ${activityId} 报名申请失败:`, participantsErr);
          }

          pendingParticipants.forEach((participant: any) => {
            if (Number(participant.userId) === Number(uid)) {
              return;
            }
            managerParticipationApplications.push({
              applicationId: `activity_participate_${activityId}_${participant.userId}_${
                participant.id ?? Date.now()
              }`,
              type: "activity_participate",
              typeText: getTypeText("activity_participate"),
              relatedName:
                activity?.title || participant.activityTitle || `活动#${activityId}`,
              applyTime: participant.enrollmentTime,
              status: getParticipationStatusKey(0),
              statusText: getParticipationStatusText(0),
              processTime: participant.approvalTime,
              remark: participant.approvalRemark,
              reason: participant.enrollmentInfo,
              clubId,
              clubName,
              activityId,
              applicantUserId: participant.userId,
              applicantName: participant.userName || participant.realName,
              approvedBy: participant.approvedBy,
              approvedByName: participant.approvedByName,
            });
          });
        }
      }
    } else {
      managedClubIds.value = new Set();
      try {
        const response = await axiosInstance.get(`/clubs/applications/user/${uid}`);
        clubApplicationsSource = extractListFromResponse(response.data);
      } catch (userErr) {
        console.error("加载用户社团申请失败:", userErr);
      }
    }

    const uniqueClubIds = new Set<number>();
    clubApplicationsSource.forEach((item: any) => {
      const clubId = Number(item.clubId);
      if (!Number.isNaN(clubId) && clubId > 0) {
        uniqueClubIds.add(clubId);
        if (item.clubName && !clubInfoMap.has(clubId)) {
          clubInfoMap.set(clubId, { name: item.clubName });
        }
      }
    });

    for (const clubId of uniqueClubIds) {
      if (clubInfoMap.has(clubId)) continue;
      try {
        const clubResp = await axiosInstance.get(`/clubs/${clubId}`);
        const clubData = clubResp.data;
        if (clubData) {
          const clubName =
            clubData.name || clubData.clubName || clubData.title || `社团#${clubId}`;
          clubInfoMap.set(clubId, { name: clubName });
        }
      } catch (clubInfoErr) {
        console.error(`加载社团 ${clubId} 信息失败:`, clubInfoErr);
        clubInfoMap.set(clubId, { name: `社团#${clubId}` });
      }
    }

    let clubApplications = clubApplicationsSource.map((item: any) => {
      const clubId = Number(item.clubId);
      const clubName =
        item.clubName || clubInfoMap.get(clubId)?.name || `社团#${clubId || 0}`;
      return {
        applicationId: item.id,
        type: "club_join",
        typeText: getTypeText("club_join"),
        relatedName: clubName,
        applyTime: item.joinTime,
        status: getStatusKey(item.status),
        statusText: getStatusText(item.status),
        processTime: item.processTime,
        remark: item.remark,
        reason: item.reason,
        clubId,
        clubName,
        applicantUserId: item.userId,
        applicantName: item.realName,
      } as Application;
    });

    const userParticipationApplications: Application[] = [];
    try {
      const participationsResp = await activityParticipantApi.getUserParticipations(uid, {
        page: 0,
        size: 1000,
        sort: "enrollmentTime,desc",
      });
      const participationList = extractListFromResponse(participationsResp.data);
      participationList.forEach((record: any) => {
        const activityId = Number(record.activityId ?? record.activity?.id);
        if (activityId && !activityClubMap.has(activityId)) {
          const clubIdGuess = record.activity?.clubId;
          if (clubIdGuess) {
            const clubName =
              record.activity?.clubName ||
              clubInfoMap.get(clubIdGuess)?.name ||
              `社团#${clubIdGuess}`;
            activityClubMap.set(activityId, {
              clubId: clubIdGuess,
              clubName,
            });
          }
        }
        const clubInfo =
          activityClubMap.get(activityId || 0) ||
          (record.activity?.clubId
            ? {
                clubId: record.activity?.clubId,
                clubName:
                  record.activity?.clubName ||
                  clubInfoMap.get(record.activity?.clubId)?.name,
              }
            : undefined);

        userParticipationApplications.push({
          applicationId:
            record.id ??
            `activity_participate_self_${activityId ?? "unknown"}_${record.userId}`,
          type: "activity_participate",
          typeText: getTypeText("activity_participate"),
          relatedName:
            record.activityTitle ||
            record.activity?.title ||
            `活动#${activityId || record.activityId || "-"}`,
          applyTime: record.enrollmentTime,
          status: getParticipationStatusKey(record.status),
          statusText: getParticipationStatusText(record.status),
          processTime: record.approvalTime,
          remark: record.approvalRemark,
          reason: record.enrollmentInfo,
          clubId: clubInfo?.clubId,
          clubName: clubInfo?.clubName,
          activityId,
          applicantUserId: record.userId,
          applicantName: record.userName || record.realName,
          approvedBy: record.approvedBy,
          approvedByName: record.approvedByName,
        });
      });
    } catch (e) {
      console.error("加载活动报名申请失败:", e);
    }

    let activityApplications: Application[] = [];
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
        activityData?.content || activityData?.data?.content || activityData?.list || [];
      activityApplications = activityList
        .filter((activity: any) => activity.status === 0)
        .map((activity: any) => {
          const clubName =
            clubInfoMap.get(Number(activity.clubId))?.name ||
            activity.clubName ||
            `社团#${activity.clubId}`;
          return {
            applicationId: `activity_${activity.id}`,
            type: "activity_create",
            typeText: getTypeText("activity_create"),
            relatedName: activity.title,
            applyTime: activity.createdAt,
            status: "pending",
            statusText: "待处理",
            processTime: null,
            remark: null,
            reason: activity.description,
            clubId: activity.clubId,
            clubName,
            activityId: activity.id,
            applicantUserId: activity.organizerId,
          } as Application;
        });
    } catch (activityErr) {
      console.error("加载活动创建申请失败:", activityErr);
    }

    // 加载社团成员开除申请（来自通知中心）
    let expelApplications: Application[] = [];
    try {
      const notificationsResp = await notificationApi.getNotifications(uid, {
        page: 0,
        size: 1000,
      });
      const rawNotifications = normalizeNotificationResponse(notificationsResp.data);
      const expelList = rawNotifications.filter(
        (notif: any) => notif.relatedType === "club_expel"
      );
      expelApplications = expelList.map((notif: any) => {
        const details = parseExpelContent(notif.content || "");
        const app: Application = {
          applicationId: `expel_${notif.id}`,
          type: "club_expel",
          typeText: getTypeText("club_expel"),
          relatedName:
            notif.title ||
            (details.clubName
              ? `社团「${details.clubName}」开除申请`
              : "社团成员开除申请"),
          applyTime: notif.createdAt || notif.created_at,
          // 强制为待处理，避免把“已读”误当作已审批
          status: "pending" as const,
          statusText: "待处理",
          processTime: null,
          remark: null,
          reason: details.reason || notif.content,
          clubId: Number(notif.relatedId || details.clubId) || undefined,
          applicantUserId: details.applicantId || undefined,
          targetUserId: details.targetUserId || undefined,
        };
        // 如果本地记录了该开除申请的审批结果，则覆盖显示状态
        const decision = (expelDecisions.value as any)[app.applicationId];
        if (decision === "approved") {
          (app as any).status = "approved";
          (app as any).statusText = "已通过";
        } else if (decision === "rejected") {
          (app as any).status = "rejected";
          (app as any).statusText = "已拒绝";
        }
        return app;
      });
    } catch (expelErr) {
      console.error("加载开除申请通知失败:", expelErr);
    }

    if (isSystemAdmin) {
      const filterOutSelf = (app: Application) => Number(app.applicantUserId) !== 1;
      clubApplications = clubApplications.filter(filterOutSelf);
      activityApplications = activityApplications.filter(filterOutSelf);
      expelApplications = expelApplications.filter(filterOutSelf);
      const filteredManager = managerParticipationApplications.filter(filterOutSelf);
      managerParticipationApplications.length = 0;
      managerParticipationApplications.push(...filteredManager);
      const filteredUser = userParticipationApplications.filter(filterOutSelf);
      userParticipationApplications.length = 0;
      userParticipationApplications.push(...filteredUser);
    }

    applications.value = [
      ...clubApplications,
      ...managerParticipationApplications,
      ...userParticipationApplications,
      ...activityApplications,
      ...expelApplications,
    ];

    totalPages.value = Math.max(1, Math.ceil(applications.value.length / pageSize.value));
  } catch (err: any) {
    error.value = err.response?.data?.message || "加载申请列表失败";
    console.error("获取申请列表失败:", err);
  } finally {
    loading.value = false;
  }
};

// 规范化通知接口响应
const normalizeNotificationResponse = (respData: any): any[] => {
  if (!respData) return [];
  if (Array.isArray(respData)) return respData;
  if (respData.data) {
    if (Array.isArray(respData.data)) {
      return respData.data;
    }
    return respData.data.items || respData.data.list || respData.data.content || [];
  }
  return respData.items || respData.list || respData.content || [];
};

// 解析开除申请内容
const parseExpelContent = (content: string) => {
  const result: Record<string, any> = {};
  if (!content) {
    return result;
  }

  const matchValue = (label: string) => {
    const regex = new RegExp(`${label}[：:](.+)`);
    const match = content.match(regex);
    return match && match[1] ? match[1].trim() : undefined;
  };

  result.clubName = matchValue("社团");
  const applicantStr = matchValue("申请人");
  if (applicantStr) {
    const num = Number(applicantStr.replace(/\D+/g, ""));
    if (!Number.isNaN(num)) {
      result.applicantId = num;
    }
  }
  const reason = matchValue("说明");
  if (reason) {
    result.reason = reason;
  }
  const targetStr = matchValue("待开除用户ID");
  if (targetStr) {
    const targetNum = Number(targetStr.replace(/\D+/g, ""));
    if (!Number.isNaN(targetNum)) {
      result.targetUserId = targetNum;
    }
  }

  return result;
};

// 获取类型文本
const getTypeText = (type: string): string => {
  const typeMap: Record<string, string> = {
    club_join: "加入社团",
    club_quit: "退出社团",
    club_create: "创建社团",
    activity_participate: "参与活动",
    activity_create: "创建活动",
    club_expel: "开除成员",
  };
  return typeMap[type] || "未知类型";
};

const getParticipationStatusKey = (status: number): string => {
  if (status === 0) return "pending";
  if (status === 1 || status === 3) return "approved";
  if (status === 2) return "rejected";
  if (status === 4) return "cancelled";
  return "pending";
};

const getParticipationStatusText = (status: number): string => {
  const map: Record<number, string> = {
    0: "待处理",
    1: "已通过",
    2: "已拒绝",
    3: "已通过",
    4: "已取消",
  };
  return map[status] || "未知";
};

const canCancelApplication = (application: Application): boolean => {
  const uid = currentUser.value?.id || storedUserId.value;
  if (!uid) return false;
  if (application.status !== "pending") return false;

  if (application.type === "activity_participate") {
    return Number(application.applicantUserId) === Number(uid);
  }

  if (application.type === "club_join") {
    if (application.applicantUserId) {
      return Number(application.applicantUserId) === Number(uid);
    }
    return true;
  }

  return false;
};

const canApproveActivityParticipation = (application?: Application | null) => {
  if (!application) return false;
  if (application.type !== "activity_participate") return false;
  if (application.status !== "pending") return false;
  if (isSystemAdminUser.value) return true;
  if (application.clubId) {
    return canManageThisClub(application.clubId);
  }
  return false;
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
  if (!selectedApplication.value) {
    return;
  }

  const currentUserId = currentUser.value?.id || storedUserId.value;
  const role = (currentUser.value as any)?.role || storedRole.value;
  const managerRoles = ["ADMIN", "admin", "CLUB_ADMIN", "club_admin", "club_manager"];
  const isSystemAdmin = currentUserId === 1 || role === "ADMIN" || role === "admin";
  const isManager = managerRoles.includes(role as string);

  try {
    // 处理活动创建申请
    if (selectedApplication.value.type === "activity_create") {
      const activityId = (selectedApplication.value as any).activityId;
      if (!activityId) {
        alert("活动ID不存在");
        return;
      }

      // 系统管理员可以审核活动创建申请
      if (isSystemAdmin) {
        const newStatus = approved ? 1 : 2; // 1-已发布，2-已拒绝
        await activityApi.updateActivityStatus(activityId, newStatus);
        // 更新本地状态并刷新列表
        selectedApplication.value.status = approved ? "approved" : "rejected";
        selectedApplication.value.statusText = approved ? "已通过" : "已拒绝";
        await fetchApplications();
        closeApplicationDetails();
      } else {
        alert("只有系统管理员可以审核活动创建申请");
      }
      return;
    }

    if (selectedApplication.value.type === "activity_participate") {
      const activityId = (selectedApplication.value as any).activityId;
      const applicantId = selectedApplication.value.applicantUserId;
      if (!activityId || !applicantId) {
        alert("缺少必要信息：活动或报名用户不存在");
        return;
      }

      if (
        !isSystemAdmin &&
        !(
          selectedApplication.value.clubId &&
          isManager &&
          canManageThisClub(selectedApplication.value.clubId)
        )
      ) {
        alert("您没有权限审批该活动报名");
        return;
      }

      const newStatus = approved ? 1 : 2;
      await activityParticipantApi.approveParticipation(activityId, applicantId, {
        status: newStatus,
      });
      selectedApplication.value.status = getParticipationStatusKey(newStatus);
      selectedApplication.value.statusText = getParticipationStatusText(newStatus);
      await fetchApplications();
      closeApplicationDetails();
      return;
    }

    // 处理社团加入申请
    if (
      selectedApplication.value.type === "club_join" &&
      selectedApplication.value.clubId &&
      selectedApplication.value.applicantUserId
    ) {
      await axiosInstance.put(
        `/clubs/${selectedApplication.value.clubId}/members/${selectedApplication.value.applicantUserId}/approve`,
        null,
        {
          params: {
            approved,
            approverId: currentUserId,
          },
        }
      );
      // 更新本地状态并刷新列表
      selectedApplication.value.status = approved ? "approved" : "rejected";
      selectedApplication.value.statusText = approved ? "已通过" : "已拒绝";
      await fetchApplications();
      closeApplicationDetails();
      return;
    }

    // 处理社团成员开除申请（仅系统管理员可处理）
    if (selectedApplication.value.type === "club_expel") {
      if (!isSystemAdmin) {
        alert("只有系统管理员可以审核社团成员开除申请");
        return;
      }
      const clubId = (selectedApplication.value as any).clubId;
      const targetUserId = (selectedApplication.value as any).targetUserId;
      const expelAppId = (selectedApplication.value.applicationId as unknown) as string;
      if (!clubId || !targetUserId) {
        alert("缺少必要信息：clubId 或 targetUserId");
        return;
      }

      if (approved) {
        await axiosInstance.put(`/clubs/${clubId}/members/${targetUserId}/expel`);
        // 通知申请人和被开除用户（忽略发送失败）
        try {
          const notifications = [
            {
              userId: (selectedApplication.value as any).applicantUserId,
              notificationType: 2,
              title: "开除申请已通过",
              content: `社团ID：${clubId} 的成员开除申请已通过。`,
              relatedId: clubId,
              relatedType: "club_expel_result",
            },
            {
              userId: targetUserId,
              notificationType: 2,
              title: "您已被移除社团",
              content: `很抱歉，您已被移出社团（ID：${clubId}）。如有疑问请联系社团负责人。`,
              relatedId: clubId,
              relatedType: "club_expel_result",
            },
          ];
          await axiosInstance.post("/notifications/batch", notifications);
        } catch {}
        selectedApplication.value.status = "approved";
        selectedApplication.value.statusText = "已通过";
        // 记录审批结果到本地，刷新列表时生效
        (expelDecisions.value as any)[expelAppId] = "approved";
        saveExpelDecisions();
      } else {
        try {
          await axiosInstance.post("/notifications", {
            userId: (selectedApplication.value as any).applicantUserId,
            notificationType: 2,
            title: "开除申请未通过",
            content: `社团ID：${clubId} 的成员开除申请未获通过。`,
            relatedId: clubId,
            relatedType: "club_expel_result",
          });
        } catch {}
        selectedApplication.value.status = "rejected";
        selectedApplication.value.statusText = "已拒绝";
        (expelDecisions.value as any)[expelAppId] = "rejected";
        saveExpelDecisions();
      }

      await fetchApplications();
      closeApplicationDetails();
      return;
    }
  } catch (err: any) {
    alert(err?.response?.data?.message || "审批失败");
  }
};
// 取消申请
const cancelApplication = async (application: Application) => {
  if (!confirm("确定要取消这个申请吗？")) return;
  try {
    if (application.type === "club_join") {
      await axiosInstance.put(`/clubs/applications/${application.applicationId}/cancel`);
    } else if (application.type === "activity_participate") {
      const activityId = (application as any).activityId;
      const applicantId =
        application.applicantUserId || currentUser.value?.id || storedUserId.value;
      if (!activityId || !applicantId) {
        alert("缺少必要信息，无法取消报名");
        return;
      }
      await activityParticipantApi.cancelEnrollment(activityId, Number(applicantId));
    } else {
      alert("当前申请类型暂不支持取消操作");
      return;
    }
    await fetchApplications();
  } catch (err: any) {
    alert(err?.response?.data?.message || "取消申请失败");
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
