<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <i class="fa fa-graduation-cap"></i>
        </div>
        <h2 class="text-2xl font-bold text-gray-800">校园社团管理系统</h2>
        <p class="text-gray-500">{{ currentFormTitle }}</p>
      </div>

      <!-- 账号密码登录表单 -->
      <form v-if="currentForm === 'passwordLogin'" @submit.prevent="handlePasswordLogin">
        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-user"></i>
            <input
              type="text"
              v-model="loginForm.username"
              placeholder="用户名"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-lock"></i>
            <input
              type="password"
              v-model="loginForm.password"
              placeholder="密码"
              required
              class="form-control"
            />
          </div>
        </div>

        <button type="submit" class="login-btn">
          <i class="fa fa-sign-in mr-2"></i> 登录
        </button>

        <div class="login-footer">
          <label class="flex items-center">
            <input type="checkbox" v-model="loginForm.rememberMe" class="mr-2" />
            <span>记住我</span>
          </label>
        </div>

        <div class="form-switch">
          <span>还没有账号？</span>
          <a href="#" @click.prevent="switchTo('register')">立即注册</a>
        </div>
      </form>

      <!-- 注册表单 -->
      <form v-else-if="currentForm === 'register'" @submit.prevent="handleRegister">
        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-user"></i>
            <input
              type="text"
              v-model="registerForm.username"
              placeholder="用户名"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-id-card"></i>
            <input
              type="text"
              v-model="registerForm.realName"
              placeholder="真实姓名"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-id-card-o"></i>
            <input
              type="text"
              v-model="registerForm.uid"
              placeholder="校园统一标识"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-lock"></i>
            <input
              type="password"
              v-model="registerForm.password"
              placeholder="密码"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-lock"></i>
            <input
              type="password"
              v-model="registerForm.confirmPassword"
              placeholder="确认密码"
              required
              class="form-control"
            />
          </div>
        </div>

        <button type="submit" class="login-btn">
          <i class="fa fa-user-plus mr-2"></i> 注册
        </button>

        <div class="form-switch">
          <span>已有账号？</span>
          <a href="#" @click.prevent="switchTo('passwordLogin')">返回登录</a>
        </div>
      </form>
    </div>
  </div>

  <!-- 通知组件 -->
  <div v-if="notification.show" :class="['notification', notification.type]">
    {{ notification.message }}
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "../api/apiService";

const router = useRouter();

// 当前表单类型（只保留登录和注册）
const currentForm = ref<"passwordLogin" | "register">("passwordLogin");

// 表单数据
const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
});

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  realName: "",
  uid: "", // 校园统一标识
});

// 通知
const notification = ref({
  show: false,
  message: "",
  type: "success" as "success" | "error" | "info",
});

// 当前表单标题
const currentFormTitle = computed(() => {
  switch (currentForm.value) {
    case "passwordLogin":
      return "请登录您的账号";
    case "register":
      return "请注册账号";
    default:
      return "校园社团管理系统";
  }
});

// 切换表单
const switchTo = (formType: typeof currentForm.value) => {
  currentForm.value = formType;
};

// 显示通知
const showNotification = (
  message: string,
  type: "success" | "error" | "info" = "success"
) => {
  notification.value = {
    show: true,
    message,
    type,
  };

  setTimeout(() => {
    notification.value.show = false;
  }, 3000);
};

// 处理账号密码登录
const handlePasswordLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    showNotification("用户名和密码不能为空", "error");
    return;
  }

  try {
    // 先清除可能存在的旧token
    localStorage.removeItem("accessToken");

    // 调用登录API
    await authApi.login(loginForm.value);

    // 记住用户名
    if (loginForm.value.rememberMe) {
      localStorage.setItem("rememberedUser", loginForm.value.username);
    } else {
      localStorage.removeItem("rememberedUser");
    }

    // 立即检查token是否已保存
    const token = localStorage.getItem("accessToken");
    if (token) {
      showNotification("登录成功", "success");

      console.log("Token saved:", !!token);
      console.log("Navigating to /home");
      // 直接跳转到首页，不需要延迟
      router.replace("/home");
    } else {
      // 如果token未保存，显示错误信息
      console.error("Login succeeded but no token was saved");
      showNotification("登录成功但认证信息丢失，请重新登录", "error");
    }
  } catch (error: any) {
    // 确保清除可能存在的无效token
    localStorage.removeItem("accessToken");

    console.error("Login error details:", error);
    // 显示更具体的错误信息
    const errorMsg = error.response?.data?.message || "登录失败，请检查用户名和密码";
    showNotification(errorMsg, "error");
  }
};

// 处理注册
const handleRegister = async () => {
  // 验证表单
  if (
    !registerForm.value.username ||
    !registerForm.value.realName ||
    !registerForm.value.uid ||
    !registerForm.value.password
  ) {
    showNotification("请填写完整的注册信息", "error");
    return;
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    showNotification("两次输入的密码不一致", "error");
    return;
  }

  try {
    // 准备注册数据，包含所有需要的字段
    const registerData = {
      username: registerForm.value.username,
      password: registerForm.value.password,
      confirmPassword: registerForm.value.confirmPassword,
      uid: registerForm.value.uid,
      realName: registerForm.value.realName,
      phone: "", // 添加缺失的字段
      verificationCode: "", // 添加缺失的字段
    };

    // 调用注册API
    await authApi.register(registerData);

    showNotification("注册成功，请登录", "success");

    // 切换到登录表单
    setTimeout(() => {
      switchTo("passwordLogin");
    }, 1000);
  } catch (error) {
    showNotification("注册失败，请稍后重试", "error");
  }
};

// 检查是否有记住的用户
const rememberedUser = localStorage.getItem("rememberedUser");
if (rememberedUser) {
  loginForm.value.username = rememberedUser;
  loginForm.value.rememberMe = true;
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background-color: white;
  border-radius: 10px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  width: 100%;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #667eea;
  color: white;
  font-size: 36px;
  border-radius: 50%;
}

.form-input {
  position: relative;
  margin-bottom: 16px;
}

.form-input i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  z-index: 1;
}

.form-input input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-input input:focus {
  border-color: #667eea;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-bottom: 16px;
}

.login-btn:hover {
  background-color: #5a67d8;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}

.form-switch {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #64748b;
}

.form-switch a {
  color: #667eea;
  text-decoration: none;
  margin-left: 4px;
}

.form-switch a:hover {
  text-decoration: underline;
}
</style>
