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
          <li class="nav-item active">
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
          <h1 class="page-title">AI助手</h1>
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
      <div class="page-content">
        <div class="ai-assistant-container">
          <div class="chat-header">
            <h2>🤖 AI 社团助手</h2>
            <p class="subtitle">为您解答社团管理相关问题</p>
            <button v-if="conversationId" @click="clearConversation" class="btn-clear">
              清除对话
            </button>
          </div>

          <div class="chat-messages" ref="messagesContainer">
            <div v-if="messages.length === 0" class="welcome-message">
              <h3>👋 欢迎使用AI社团助手！</h3>
              <p>我可以帮您：</p>
              <ul>
                <li>解答社团管理相关问题</li>
                <li>提供社团活动建议</li>
                <li>协助处理社团事务</li>
                <li>指导系统功能使用</li>
              </ul>
            </div>

            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
            >
              <div class="message-avatar">
                {{ msg.role === "user" ? "👤" : "🤖" }}
              </div>
              <div class="message-content">
                <div class="message-text" v-html="formatMessage(msg.content)"></div>
                <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
              </div>
            </div>

            <div v-if="isLoading" class="message ai-message">
              <div class="message-avatar">🤖</div>
              <div class="message-content">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>

          <div class="chat-input-container">
            <div class="input-wrapper">
              <textarea
                v-model="userInput"
                @keydown.enter.exact.prevent="sendMessage"
                @keydown.enter.shift.exact="handleShiftEnter"
                placeholder="输入您的问题... (Enter发送, Shift+Enter换行)"
                rows="1"
                ref="inputTextarea"
              ></textarea>
              <button
                @click="sendMessage"
                :disabled="!userInput.trim() || isLoading"
                class="btn-send"
              >
                <span v-if="!isLoading">发送</span>
                <span v-else>发送中...</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "../api/apiService";
import axiosInstance from "../api/axiosInstance";
import type { User } from "../types/index";

interface Message {
  role: "user" | "assistant";
  content: string;
  timestamp: number;
}

const messages = ref<Message[]>([]);
const userInput = ref("");
const isLoading = ref(false);
const conversationId = ref<string | null>(null);
const messagesContainer = ref<HTMLElement | null>(null);
const inputTextarea = ref<HTMLTextAreaElement | null>(null);

const router = useRouter();
const showNotifications = ref(false);

// 侧边栏状态
const sidebarCollapsed = ref(false);
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

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

// 用户信息
const currentUser = ref<User | null>(null);
const showUserMenu = ref(false);

// 发送消息
const sendMessage = async () => {
  const message = userInput.value.trim();
  if (!message || isLoading.value) return;

  // 添加用户消息
  messages.value.push({
    role: "user",
    content: message,
    timestamp: Date.now(),
  });

  userInput.value = "";
  isLoading.value = true;

  // 滚动到底部
  await nextTick();
  scrollToBottom();

  try {
    console.log("发送AI请求:", { message, conversationId: conversationId.value });
    const response = await axiosInstance.post("/ai/chat", {
      message: message,
      conversationId: conversationId.value,
    });
    console.log("AI响应:", response.data);

    if (response.data.code === 200 && response.data.data) {
      const aiMessage = response.data.data.message;
      conversationId.value = response.data.data.conversationId;

      // 添加AI回复
      messages.value.push({
        role: "assistant",
        content: aiMessage,
        timestamp: Date.now(),
      });

      // 滚动到底部
      await nextTick();
      scrollToBottom();
    } else {
      throw new Error(response.data.message || "AI助手暂时不可用");
    }
  } catch (error: any) {
    console.error("发送消息失败:", error);
    console.error("错误详情:", {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
    });
    const errorMsg = error.response?.data?.message || error.message || "请稍后再试";
    messages.value.push({
      role: "assistant",
      content: `抱歉，遇到了一些问题：${errorMsg}`,
      timestamp: Date.now(),
    });
  } finally {
    isLoading.value = false;
  }
};

// 处理 Shift+Enter
const handleShiftEnter = (event: KeyboardEvent) => {
  // 允许 Shift+Enter 换行
  const textarea = event.target as HTMLTextAreaElement;
  const cursorPos = textarea.selectionStart;
  userInput.value =
    userInput.value.substring(0, cursorPos) + "\n" + userInput.value.substring(cursorPos);
};

// 清除对话
const clearConversation = async () => {
  if (!conversationId.value) return;

  if (!confirm("确定要清除当前对话吗？")) return;

  try {
    await axiosInstance.delete(`/ai/conversation/${conversationId.value}`);
    messages.value = [];
    conversationId.value = null;
  } catch (error) {
    console.error("清除对话失败:", error);
    alert("清除对话失败，请稍后再试");
  }
};

// 格式化消息（支持换行）
const formatMessage = (content: string) => {
  return content.replace(/\n/g, "<br>");
};

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp);
  return date.toLocaleTimeString("zh-CN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

// 自动调整textarea高度
onMounted(async () => {
  await loadUserInfo();
  if (inputTextarea.value) {
    inputTextarea.value.addEventListener("input", () => {
      const textarea = inputTextarea.value;
      if (textarea) {
        textarea.style.height = "auto";
        textarea.style.height = Math.min(textarea.scrollHeight, 150) + "px";
      }
    });
  }
  // 点击外部关闭菜单
  document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;
    if (!target.closest(".user-profile")) {
      showUserMenu.value = false;
      showNotifications.value = false;
    }
  });
});
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

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1f2937;
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

.logout {
  color: #ef4444;
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

.ai-assistant-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  max-width: 1000px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  position: relative;
}

.chat-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.btn-clear {
  position: absolute;
  right: 20px;
  top: 20px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-clear:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.top-bar-left {
  display: flex;
  align-items: center;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.welcome-message h3 {
  margin-bottom: 20px;
  color: #333;
}

.welcome-message ul {
  text-align: left;
  display: inline-block;
  margin-top: 15px;
}

.welcome-message li {
  margin: 10px 0;
  line-height: 1.6;
}

.message {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  justify-content: flex-end;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: white;
  flex-shrink: 0;
}

.user-message .message-avatar {
  order: 2;
  margin-left: 10px;
  background: #667eea;
}

.ai-message .message-avatar {
  margin-right: 10px;
  background: #764ba2;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  margin-top: 6px;
  opacity: 0.6;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.chat-input-container {
  padding: 20px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.input-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.input-wrapper textarea {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 24px;
  font-size: 14px;
  resize: none;
  min-height: 48px;
  max-height: 150px;
  font-family: inherit;
  transition: border-color 0.3s;
}

.input-wrapper textarea:focus {
  outline: none;
  border-color: #667eea;
}

.btn-send {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  min-width: 80px;
}

.btn-send:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-assistant-container {
    height: calc(100vh - 80px);
    border-radius: 0;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-header h2 {
    font-size: 20px;
  }
}
</style>
