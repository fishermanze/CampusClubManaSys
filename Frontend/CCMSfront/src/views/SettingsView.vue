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
          <li class="nav-item active">
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
          <h1 class="page-title">账号设置</h1>
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
      <div class="page-content settings-page">
        <div class="settings-container">
          <!-- 设置导航 -->
          <div class="settings-nav">
            <div class="nav-tabs">
              <button
                :class="['nav-tab', { active: activeTab === 'account' }]"
                @click="activeTab = 'account'"
              >
                <i class="fa fa-user-circle"></i>
                账户信息
              </button>
              <button
                :class="['nav-tab', { active: activeTab === 'security' }]"
                @click="activeTab = 'security'"
              >
                <i class="fa fa-lock"></i>
                安全设置
              </button>
              <button
                :class="['nav-tab', { active: activeTab === 'notification' }]"
                @click="activeTab = 'notification'"
              >
                <i class="fa fa-bell"></i>
                通知设置
              </button>
              <button
                :class="['nav-tab', { active: activeTab === 'privacy' }]"
                @click="activeTab = 'privacy'"
              >
                <i class="fa fa-shield"></i>
                隐私设置
              </button>
            </div>
          </div>

          <!-- 设置内容 -->
          <div class="settings-content">
            <!-- 账户信息设置 -->
            <div v-if="activeTab === 'account'" class="tab-content">
              <h2 class="tab-title">账户信息</h2>
              <form @submit.prevent="updateAccountInfo" class="settings-form">
                <div class="form-group">
                  <label for="realName">真实姓名</label>
                  <input
                    type="text"
                    id="realName"
                    v-model="accountSettings.realName"
                    placeholder="请输入真实姓名"
                    class="form-control"
                  />
                </div>
                <div class="form-group">
                  <label for="email">邮箱</label>
                  <input
                    type="email"
                    id="email"
                    v-model="accountSettings.email"
                    placeholder="请输入邮箱"
                    class="form-control"
                  />
                </div>
                <div class="form-group">
                  <label for="phone">手机号</label>
                  <input
                    type="tel"
                    id="phone"
                    v-model="accountSettings.phone"
                    placeholder="请输入手机号"
                    class="form-control"
                  />
                </div>
                <div class="form-group">
                  <label for="department">学院</label>
                  <input
                    type="text"
                    id="department"
                    v-model="accountSettings.department"
                    placeholder="请输入学院"
                    class="form-control"
                  />
                </div>
                <div class="form-group">
                  <label for="grade">年级</label>
                  <input
                    type="text"
                    id="grade"
                    v-model="accountSettings.grade"
                    placeholder="请输入年级"
                    class="form-control"
                  />
                </div>
                <div class="form-group">
                  <label for="hobbies">兴趣爱好</label>
                  <textarea
                    id="hobbies"
                    v-model="accountSettings.hobbies"
                    placeholder="请输入兴趣爱好，用逗号分隔"
                    rows="3"
                    class="form-control"
                  ></textarea>
                </div>
                <div class="form-actions">
                  <button type="submit" class="btn btn-primary" :disabled="isSaving">
                    {{ isSaving ? "保存中..." : "保存修改" }}
                  </button>
                  <button
                    type="button"
                    class="btn btn-secondary"
                    @click="resetForm('account')"
                  >
                    取消
                  </button>
                </div>
              </form>
            </div>

            <!-- 安全设置 -->
            <div v-if="activeTab === 'security'" class="tab-content">
              <h2 class="tab-title">安全设置</h2>

              <!-- 修改密码 -->
              <div class="settings-card">
                <h3 class="card-title">修改密码</h3>
                <form @submit.prevent="changePassword" class="settings-form">
                  <div class="form-group">
                    <label for="currentPassword">当前密码</label>
                    <input
                      type="password"
                      id="currentPassword"
                      v-model="passwordForm.currentPassword"
                      placeholder="请输入当前密码"
                      class="form-control"
                    />
                  </div>
                  <div class="form-group">
                    <label for="newPassword">新密码</label>
                    <input
                      type="password"
                      id="newPassword"
                      v-model="passwordForm.newPassword"
                      placeholder="请输入新密码"
                      class="form-control"
                    />
                    <div class="password-hint">
                      密码至少包含8个字符，建议使用字母、数字和特殊字符
                    </div>
                  </div>
                  <div class="form-group">
                    <label for="confirmPassword">确认新密码</label>
                    <input
                      type="password"
                      id="confirmPassword"
                      v-model="passwordForm.confirmPassword"
                      placeholder="请再次输入新密码"
                      class="form-control"
                    />
                  </div>
                  <div class="form-actions">
                    <button
                      type="submit"
                      class="btn btn-primary"
                      :disabled="isChangingPassword"
                    >
                      {{ isChangingPassword ? "修改中..." : "修改密码" }}
                    </button>
                  </div>
                </form>
              </div>

              <!-- 绑定手机 -->
              <div class="settings-card">
                <h3 class="card-title">绑定手机</h3>
                <div class="binding-status">
                  <div class="status-item">
                    <span class="status-label">当前绑定手机：</span>
                    <span class="status-value">{{
                      currentUser?.phone ? formatPhone(currentUser.phone) : "未绑定"
                    }}</span>
                  </div>
                  <button class="btn btn-secondary" @click="showBindPhoneModal = true">
                    {{ currentUser?.phone ? "更换手机号" : "绑定手机号" }}
                  </button>
                </div>
              </div>

              <!-- 登录记录 -->
              <div class="settings-card">
                <h3 class="card-title">最近登录记录</h3>
                <div class="login-records">
                  <div v-if="loginRecords.length > 0" class="records-list">
                    <div
                      v-for="record in loginRecords"
                      :key="record.time"
                      class="record-item"
                    >
                      <div class="record-time">{{ formatLoginTime(record.time) }}</div>
                      <div class="record-info">
                        <span class="record-device">{{ record.device }}</span>
                        <span class="record-location">{{ record.location }}</span>
                      </div>
                    </div>
                  </div>
                  <div v-else class="empty-records">暂无登录记录</div>
                </div>
              </div>
            </div>

            <!-- 通知设置 -->
            <div v-if="activeTab === 'notification'" class="tab-content">
              <h2 class="tab-title">通知设置</h2>
              <div class="settings-form">
                <div class="notification-group">
                  <h3 class="group-title">社团通知</h3>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">新社团邀请</label>
                      <span class="notification-desc">当收到新的社团邀请时通知我</span>
                    </div>
                    <label class="switch">
                      <input type="checkbox" v-model="notificationSettings.clubInvites" />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">社团活动提醒</label>
                      <span class="notification-desc">活动开始前提醒我</span>
                    </div>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="notificationSettings.activityReminders"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">社团公告</label>
                      <span class="notification-desc">接收我加入社团的最新公告</span>
                    </div>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="notificationSettings.clubAnnouncements"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>
                </div>

                <div class="notification-group">
                  <h3 class="group-title">系统通知</h3>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">系统消息</label>
                      <span class="notification-desc">接收平台系统消息</span>
                    </div>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="notificationSettings.systemMessages"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">重要提醒</label>
                      <span class="notification-desc">接收平台重要提醒</span>
                    </div>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="notificationSettings.importantReminders"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>
                </div>

                <div class="notification-group">
                  <h3 class="group-title">通知方式</h3>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">站内通知</label>
                      <span class="notification-desc">在平台内接收通知</span>
                    </div>
                    <label class="switch">
                      <input type="checkbox" v-model="notificationSettings.inApp" />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">邮件通知</label>
                      <span class="notification-desc">通过邮件接收通知</span>
                    </div>
                    <label class="switch">
                      <input type="checkbox" v-model="notificationSettings.email" />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="notification-item">
                    <div class="notification-info">
                      <label class="notification-label">短信通知</label>
                      <span class="notification-desc">通过短信接收通知</span>
                    </div>
                    <label class="switch">
                      <input type="checkbox" v-model="notificationSettings.sms" />
                      <span class="slider"></span>
                    </label>
                  </div>
                </div>

                <div class="form-actions">
                  <button
                    type="button"
                    class="btn btn-primary"
                    @click="saveNotificationSettings"
                  >
                    保存设置
                  </button>
                </div>
              </div>
            </div>

            <!-- 隐私设置 -->
            <div v-if="activeTab === 'privacy'" class="tab-content">
              <h2 class="tab-title">隐私设置</h2>
              <div class="settings-form">
                <div class="privacy-group">
                  <h3 class="group-title">个人信息展示</h3>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">允许他人查看我的社团</label>
                      <span class="privacy-desc">控制是否公开您加入的社团信息</span>
                    </div>
                    <select v-model="privacySettings.showClubs" class="form-select">
                      <option value="everyone">所有人可见</option>
                      <option value="friends">仅好友可见</option>
                      <option value="private">仅自己可见</option>
                    </select>
                  </div>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">允许他人查看我的活动</label>
                      <span class="privacy-desc">控制是否公开您参与的活动信息</span>
                    </div>
                    <select v-model="privacySettings.showActivities" class="form-select">
                      <option value="everyone">所有人可见</option>
                      <option value="friends">仅好友可见</option>
                      <option value="private">仅自己可见</option>
                    </select>
                  </div>
                </div>

                <div class="privacy-group">
                  <h3 class="group-title">社交设置</h3>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">允许接收好友请求</label>
                      <span class="privacy-desc">控制是否允许他人向您发送好友请求</span>
                    </div>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="privacySettings.allowFriendRequests"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">允许接收私信</label>
                      <span class="privacy-desc">控制是否允许他人向您发送私信</span>
                    </div>
                    <label class="switch">
                      <input type="checkbox" v-model="privacySettings.allowMessages" />
                      <span class="slider"></span>
                    </label>
                  </div>
                </div>

                <div class="privacy-group">
                  <h3 class="group-title">数据管理</h3>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">导出个人数据</label>
                      <span class="privacy-desc">获取您在平台上的所有个人数据</span>
                    </div>
                    <button class="btn btn-outline-primary" @click="exportUserData">
                      导出数据
                    </button>
                  </div>
                  <div class="privacy-item">
                    <div class="privacy-info">
                      <label class="privacy-label">删除账号</label>
                      <span class="privacy-desc"
                        >删除账号后数据将无法恢复，请谨慎操作</span
                      >
                    </div>
                    <button
                      class="btn btn-outline-danger"
                      @click="showDeleteAccountModal = true"
                    >
                      删除账号
                    </button>
                  </div>
                </div>

                <div class="form-actions">
                  <button
                    type="button"
                    class="btn btn-primary"
                    @click="savePrivacySettings"
                  >
                    保存设置
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>

  <!-- 绑定手机模态框 -->
  <div v-if="showBindPhoneModal" class="modal" @click="closeBindPhoneModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ currentUser?.phone ? "更换手机号" : "绑定手机号" }}</h3>
        <button class="close-btn" @click="closeBindPhoneModal">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="bindPhone">
          <div class="form-group">
            <label for="phoneNumber">手机号</label>
            <input
              type="tel"
              id="phoneNumber"
              v-model="phoneForm.phoneNumber"
              placeholder="请输入手机号"
              class="form-control"
            />
          </div>
          <div class="form-group">
            <label for="verificationCode">验证码</label>
            <div class="verification-input">
              <input
                type="text"
                id="verificationCode"
                v-model="phoneForm.verificationCode"
                placeholder="请输入验证码"
                class="form-control"
              />
              <button
                type="button"
                class="btn btn-secondary"
                @click="sendVerificationCode"
                :disabled="countDown > 0"
              >
                {{ countDown > 0 ? `${countDown}秒后重新发送` : "获取验证码" }}
              </button>
            </div>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="isBinding">
              {{ isBinding ? "绑定中..." : "确认绑定" }}
            </button>
            <button type="button" class="btn btn-secondary" @click="closeBindPhoneModal">
              取消
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- 删除账号模态框 -->
  <div v-if="showDeleteAccountModal" class="modal" @click="closeDeleteAccountModal">
    <div class="modal-content delete-account-modal" @click.stop>
      <div class="modal-header">
        <h3>删除账号</h3>
        <button class="close-btn" @click="closeDeleteAccountModal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="warning-message">
          <i class="fa fa-exclamation-triangle"></i>
          <p>
            警告：删除账号后，您的所有数据将被永久删除且无法恢复。此操作不可逆，请谨慎考虑！
          </p>
        </div>
        <div class="confirm-actions">
          <button class="btn btn-secondary" @click="closeDeleteAccountModal">
            取消删除
          </button>
          <button
            class="btn btn-danger"
            @click="confirmDeleteAccount"
            :disabled="isDeleting"
          >
            {{ isDeleting ? "删除中..." : "确认删除账号" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "../api/apiService";
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

// 当前标签
const activeTab = ref<"account" | "security" | "notification" | "privacy">("account");

// 账户设置
const accountSettings = ref({
  realName: "",
  email: "",
  phone: "",
  department: "",
  grade: "",
  hobbies: "",
});
const isSaving = ref(false);

// 密码表单
const passwordForm = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});
const isChangingPassword = ref(false);

// 通知设置
const notificationSettings = ref({
  clubInvites: true,
  activityReminders: true,
  clubAnnouncements: true,
  systemMessages: true,
  importantReminders: true,
  inApp: true,
  email: false,
  sms: false,
});

// 隐私设置
const privacySettings = ref({
  showClubs: "everyone",
  showActivities: "everyone",
  allowFriendRequests: true,
  allowMessages: true,
});

// 绑定手机模态框
const showBindPhoneModal = ref(false);
const phoneForm = ref({
  phoneNumber: "",
  verificationCode: "",
});
const countDown = ref(0);
const isBinding = ref(false);

// 删除账号模态框
const showDeleteAccountModal = ref(false);
const isDeleting = ref(false);

// 登录记录
const loginRecords = ref<
  Array<{
    time: string;
    device: string;
    location: string;
  }>
>([]);

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userInfoStr = localStorage.getItem("userInfo");
    if (userInfoStr) {
      const user = JSON.parse(userInfoStr);
      currentUser.value = user;
      accountSettings.value = {
        realName: user.realName || user.name || "",
        email: user.email || "",
        phone: user.phone || "",
        department: user.department || "",
        grade: user.grade || "",
        hobbies: user.hobbies || "",
      };
      // 进一步拉取详情，回填学院(major)/年级/爱好
      if (user.id) {
        try {
          const detailResp = await axiosInstance.get(`/user/${user.id}`);
          const detail = detailResp.data || {};
          accountSettings.value.department =
            detail.major || accountSettings.value.department || "";
          accountSettings.value.grade = detail.grade || accountSettings.value.grade || "";
          accountSettings.value.hobbies =
            detail.hobbies || accountSettings.value.hobbies || "";
        } catch {}
      }
    } else {
      const response = await authApi.getCurrentUser();
      if (response.data) {
        const user = response.data;
        currentUser.value = user;
        accountSettings.value = {
          realName: user.realName || user.name || "",
          email: user.email || "",
          phone: user.phone || "",
          department: user.department || "",
          grade: user.grade || "",
          hobbies: user.hobbies || "",
        };
        // 进一步拉取详情，回填学院(major)/年级/爱好
        if (user.id) {
          try {
            const detailResp = await axiosInstance.get(`/user/${user.id}`);
            const detail = detailResp.data || {};
            accountSettings.value.department =
              detail.major || accountSettings.value.department || "";
            accountSettings.value.grade =
              detail.grade || accountSettings.value.grade || "";
            accountSettings.value.hobbies =
              detail.hobbies || accountSettings.value.hobbies || "";
          } catch {}
        }
      }
    }
  } catch (error) {
    console.error("加载用户信息失败:", error);
  }
};

// 更新账户信息
const updateAccountInfo = async () => {
  isSaving.value = true;
  try {
    // 拆分为基础账户信息与档案信息分别更新
    const basePayload = {
      realName: accountSettings.value.realName,
      email: accountSettings.value.email,
      phone: accountSettings.value.phone,
    };
    const profilePayload = {
      // department 在后端无对应字段，暂不提交，避免400
      major: accountSettings.value.department,
      grade: accountSettings.value.grade,
      hobbies: accountSettings.value.hobbies,
      // 其余档案字段留空不修改
    };

    // 顺序更新，确保任一失败能提示
    await axiosInstance.put("/user/me", basePayload);
    await axiosInstance.put("/user/me/profile", profilePayload);

    alert("保存成功");
    // 重新获取并刷新本地缓存，确保页面立即反映
    const meResp = await axiosInstance.get("/user/me");
    if (meResp.data) {
      const user = meResp.data;
      localStorage.setItem("userInfo", JSON.stringify(user));
    }
    await loadUserInfo();
  } catch (err: any) {
    alert(err.response?.data?.message || "保存失败");
  } finally {
    isSaving.value = false;
  }
};

// 修改密码
const changePassword = async () => {
  if (!passwordForm.value.currentPassword || !passwordForm.value.newPassword) {
    alert("请填写完整信息");
    return;
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert("两次输入的密码不一致");
    return;
  }
  if (passwordForm.value.newPassword.length < 8) {
    alert("密码长度至少8位");
    return;
  }
  isChangingPassword.value = true;
  try {
    await axiosInstance.post("/auth/change-password", {
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
    });
    alert("密码修改成功");
    passwordForm.value = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    };
  } catch (err: any) {
    alert(err.response?.data?.message || "修改密码失败");
  } finally {
    isChangingPassword.value = false;
  }
};

// 发送验证码
const sendVerificationCode = async () => {
  if (!phoneForm.value.phoneNumber) {
    alert("请输入手机号");
    return;
  }
  try {
    await authApi.sendVerificationCode(phoneForm.value.phoneNumber, "register");
    countDown.value = 60;
    const timer = setInterval(() => {
      countDown.value--;
      if (countDown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (err: any) {
    alert(err.response?.data?.message || "发送验证码失败");
  }
};

// 绑定手机
const bindPhone = async () => {
  if (!phoneForm.value.phoneNumber || !phoneForm.value.verificationCode) {
    alert("请填写完整信息");
    return;
  }
  isBinding.value = true;
  try {
    await axiosInstance.post("/auth/bind-phone", phoneForm.value);
    alert("绑定成功");
    closeBindPhoneModal();
    await loadUserInfo();
  } catch (err: any) {
    alert(err.response?.data?.message || "绑定失败");
  } finally {
    isBinding.value = false;
  }
};

// 关闭绑定手机模态框
const closeBindPhoneModal = () => {
  showBindPhoneModal.value = false;
  phoneForm.value = {
    phoneNumber: "",
    verificationCode: "",
  };
  countDown.value = 0;
};

// 保存通知设置
const saveNotificationSettings = async () => {
  try {
    await axiosInstance.put("/auth/notification-settings", notificationSettings.value);
    alert("保存成功");
  } catch (err: any) {
    alert(err.response?.data?.message || "保存失败");
  }
};

// 保存隐私设置
const savePrivacySettings = async () => {
  try {
    await axiosInstance.put("/auth/privacy-settings", privacySettings.value);
    alert("保存成功");
  } catch (err: any) {
    alert(err.response?.data?.message || "保存失败");
  }
};

// 导出用户数据
const exportUserData = async () => {
  try {
    const response = await axiosInstance.get("/auth/export-data", {
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `user-data-${Date.now()}.json`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    alert("数据导出成功");
  } catch (err: any) {
    alert(err.response?.data?.message || "导出失败");
  }
};

// 确认删除账号
const confirmDeleteAccount = async () => {
  if (!confirm("确定要删除账号吗？此操作不可恢复！")) return;
  isDeleting.value = true;
  try {
    await axiosInstance.delete("/auth/delete-account");
    alert("账号已删除");
    await logout();
  } catch (err: any) {
    alert(err.response?.data?.message || "删除账号失败");
  } finally {
    isDeleting.value = false;
    showDeleteAccountModal.value = false;
  }
};

// 关闭删除账号模态框
const closeDeleteAccountModal = () => {
  showDeleteAccountModal.value = false;
};

// 格式化手机号
const formatPhone = (phone: string): string => {
  if (!phone) return "";
  if (phone.length === 11) {
    return `${phone.slice(0, 3)}****${phone.slice(7)}`;
  }
  return phone;
};

// 格式化登录时间
const formatLoginTime = (time: string): string => {
  const d = new Date(time);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
    d.getDate()
  ).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(
    d.getMinutes()
  ).padStart(2, "0")}`;
};

// 重置表单
const resetForm = (tab: string) => {
  if (tab === "account") {
    loadUserInfo();
  } else if (tab === "security") {
    passwordForm.value = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    };
  }
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

/* 设置页面特定样式 */
.settings-page {
  padding: 30px;
  overflow-y: auto;
}

.settings-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
}

/* 设置导航 */
.settings-nav {
  width: 250px;
  flex-shrink: 0;
}

.nav-tabs {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.nav-tab {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 16px 24px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
  color: #374151;
  border-left: 4px solid transparent;
}

.nav-tab:hover {
  background-color: #f9fafb;
  color: #667eea;
}

.nav-tab.active {
  background-color: #f3f4ff;
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 500;
}

.nav-tab i {
  margin-right: 12px;
  font-size: 18px;
}

/* 设置内容 */
.settings-content {
  flex: 1;
  min-width: 0;
}

.tab-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.tab-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 30px 0;
}

/* 表单样式 */
.settings-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-control {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1f2937;
}

.top-bar-left {
  display: flex;
  align-items: center;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover {
  background-color: #5a67d8;
}

.btn-primary:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background-color: #e5e7eb;
}

.btn-outline-primary {
  background-color: transparent;
  color: #667eea;
  border: 1px solid #667eea;
}

.btn-outline-primary:hover {
  background-color: #f3f4ff;
}

.btn-outline-danger {
  background-color: transparent;
  color: #ef4444;
  border: 1px solid #ef4444;
}

.btn-outline-danger:hover {
  background-color: #fef2f2;
}

.btn-danger {
  background-color: #ef4444;
  color: white;
}

.btn-danger:hover {
  background-color: #dc2626;
}

/* 密码提示 */
.password-hint {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* 设置卡片 */
.settings-card {
  background: #f9fafb;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 30px;
  border: 1px solid #e5e7eb;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px 0;
}

/* 绑定状态 */
.binding-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-label {
  color: #6b7280;
  font-size: 14px;
}

.status-value {
  color: #111827;
  font-weight: 500;
}

/* 登录记录 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.record-time {
  font-weight: 500;
  color: #111827;
}

.record-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.record-device,
.record-location {
  font-size: 14px;
  color: #6b7280;
}

.empty-records {
  text-align: center;
  padding: 40px;
  color: #9ca3af;
}

/* 通知设置 */
.notification-group {
  margin-bottom: 30px;
}

.group-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px 0;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-info {
  flex: 1;
}

.notification-label {
  display: block;
  font-weight: 500;
  color: #111827;
  margin-bottom: 4px;
}

.notification-desc {
  font-size: 14px;
  color: #6b7280;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #d1d5db;
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #667eea;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

/* 隐私设置 */
.privacy-group {
  margin-bottom: 30px;
}

.privacy-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
}

.privacy-item:last-child {
  border-bottom: none;
}

.privacy-info {
  flex: 1;
  max-width: 70%;
}

.privacy-label {
  display: block;
  font-weight: 500;
  color: #111827;
  margin-bottom: 4px;
}

.privacy-desc {
  font-size: 14px;
  color: #6b7280;
}

.form-select {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  background-color: white;
  cursor: pointer;
  min-width: 200px;
}

/* 验证码输入 */
.verification-input {
  display: flex;
  gap: 12px;
}

.verification-input .form-control {
  flex: 1;
}

/* 模态框样式 */
.modal {
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
  background: white;
  border-radius: 12px;
  padding: 0;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.close-btn:hover {
  color: #374151;
}

.modal-body {
  padding: 24px;
}

/* 删除账号模态框 */
.delete-account-modal {
  max-width: 600px;
}

.warning-message {
  background-color: #fef3c7;
  border: 1px solid #fcd34d;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.warning-message i {
  font-size: 24px;
  color: #d97706;
  flex-shrink: 0;
  margin-top: 2px;
}

.warning-message p {
  color: #92400e;
  margin: 0;
  line-height: 1.5;
}

.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .settings-container {
    flex-direction: column;
    gap: 20px;
  }

  .settings-nav {
    width: 100%;
  }

  .nav-tabs {
    display: flex;
    overflow-x: auto;
    white-space: nowrap;
  }

  .nav-tab {
    padding: 12px 16px;
    border-left: none;
    border-bottom: 3px solid transparent;
    min-width: 160px;
  }

  .nav-tab.active {
    border-left-color: transparent;
    border-bottom-color: #667eea;
  }
}

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

  .settings-page {
    padding: 15px;
  }

  .tab-content {
    padding: 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .btn {
    width: 100%;
  }

  .binding-status {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .record-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .record-info {
    align-items: flex-start;
  }

  .privacy-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .privacy-info {
    max-width: 100%;
  }

  .form-select {
    width: 100%;
  }

  .verification-input {
    flex-direction: column;
  }

  .confirm-actions {
    flex-direction: column;
  }

  .modal-content {
    width: 95%;
    margin: 20px;
  }
}
</style>
