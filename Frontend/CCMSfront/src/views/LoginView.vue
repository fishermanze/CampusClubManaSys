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
          <a href="#" @click.prevent="switchTo('forgotPassword')">忘记密码？</a>
        </div>

        <div class="form-switch">
          <span>还没有账号？</span>
          <a href="#" @click.prevent="switchTo('register')">立即注册</a>
        </div>
        <div class="form-switch">
          <span>使用手机号登录？</span>
          <a href="#" @click.prevent="switchTo('codeLogin')">验证码登录</a>
        </div>
      </form>

      <!-- 手机号验证码登录表单 -->
      <form v-else-if="currentForm === 'codeLogin'" @submit.prevent="handleCodeLogin">
        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-mobile"></i>
            <input
              type="tel"
              v-model="codeLoginForm.phone"
              placeholder="手机号"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input flex gap-2">
            <i class="fa fa-shield"></i>
            <input
              type="text"
              v-model="codeLoginForm.verificationCode"
              placeholder="验证码"
              required
              class="form-control flex-1"
            />
            <button
              type="button"
              class="btn btn-secondary btn-sm"
              :disabled="countdown > 0"
              @click="sendVerificationCode('login')"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : "获取验证码" }}
            </button>
          </div>
        </div>

        <button type="submit" class="login-btn">
          <i class="fa fa-sign-in mr-2"></i> 登录
        </button>

        <div class="form-switch">
          <span>使用账号密码登录？</span>
          <a href="#" @click.prevent="switchTo('passwordLogin')">密码登录</a>
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
            <i class="fa fa-mobile"></i>
            <input
              type="tel"
              v-model="registerForm.phone"
              placeholder="手机号"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input flex gap-2">
            <i class="fa fa-shield"></i>
            <input
              type="text"
              v-model="registerForm.verificationCode"
              placeholder="验证码"
              required
              class="form-control flex-1"
            />
            <button
              type="button"
              class="btn btn-secondary btn-sm"
              :disabled="countdown > 0"
              @click="sendVerificationCode('register')"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : "获取验证码" }}
            </button>
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

      <!-- 忘记密码表单 -->
      <form
        v-else-if="currentForm === 'forgotPassword'"
        @submit.prevent="handleForgotPassword"
      >
        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-mobile"></i>
            <input
              type="tel"
              v-model="forgotPasswordForm.phone"
              placeholder="手机号"
              required
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-input flex gap-2">
            <i class="fa fa-shield"></i>
            <input
              type="text"
              v-model="forgotPasswordForm.verificationCode"
              placeholder="验证码"
              required
              class="form-control flex-1"
            />
            <button
              type="button"
              class="btn btn-secondary btn-sm"
              :disabled="countdown > 0"
              @click="sendVerificationCode('forgot')"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : "获取验证码" }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <div class="form-input">
            <i class="fa fa-lock"></i>
            <input
              type="password"
              v-model="forgotPasswordForm.newPassword"
              placeholder="新密码"
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
              v-model="forgotPasswordForm.confirmPassword"
              placeholder="确认新密码"
              required
              class="form-control"
            />
          </div>
        </div>

        <button type="submit" class="login-btn">
          <i class="fa fa-key mr-2"></i> 重置密码
        </button>

        <div class="form-switch">
          <span>返回登录？</span>
          <a href="#" @click.prevent="switchTo('passwordLogin')">登录</a>
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
import type { LoginForm, CodeLoginForm, RegisterForm, ForgotPasswordForm } from "@/types";
import { authApi } from "@/api/apiService";

const router = useRouter();

// 当前表单类型
const currentForm = ref<"passwordLogin" | "codeLogin" | "register" | "forgotPassword">(
  "passwordLogin"
);

// 表单数据
const loginForm = ref<LoginForm>({
  username: "",
  password: "",
  rememberMe: false,
});

const codeLoginForm = ref<CodeLoginForm>({
  phone: "",
  verificationCode: "",
});

const registerForm = ref<RegisterForm>({
  username: "",
  password: "",
  confirmPassword: "",
  realName: "",
  phone: "",
  verificationCode: "",
});

const forgotPasswordForm = ref<ForgotPasswordForm>({
  phone: "",
  verificationCode: "",
  newPassword: "",
  confirmPassword: "",
});

// 验证码倒计时
const countdown = ref(0);

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
    case "codeLogin":
      return "请使用手机号登录";
    case "register":
      return "请注册账号";
    case "forgotPassword":
      return "重置您的密码";
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

// 发送验证码
const sendVerificationCode = async (type: "login" | "register" | "forgot") => {
  let phone = "";

  switch (type) {
    case "login":
      phone = codeLoginForm.value.phone;
      break;
    case "register":
      phone = registerForm.value.phone;
      break;
    case "forgot":
      phone = forgotPasswordForm.value.phone;
      break;
  }

  if (!phone) {
    showNotification("请输入手机号", "error");
    return;
  }

  if (!/^1[3-9]\d{9}$/.test(phone)) {
    showNotification("请输入正确的手机号", "error");
    return;
  }

  try {
    // 调用API发送验证码
    await authApi.sendVerificationCode(phone, type);
    showNotification("验证码已发送", "success");

    // 开始倒计时
    countdown.value = 60;
    const timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error) {
    showNotification("验证码发送失败，请稍后重试", "error");
  }
};

// 处理账号密码登录
const handlePasswordLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    showNotification("用户名和密码不能为空", "error");
    return;
  }

  try {
    // 调用登录API
    const response = await authApi.login(loginForm.value);

    // 确保响应数据格式正确
    const responseData = response.data || response;
    if (!responseData.accessToken) {
      showNotification("登录响应格式错误", "error");
      return;
    }

    // 记住用户名
    if (loginForm.value.rememberMe) {
      localStorage.setItem("rememberedUser", loginForm.value.username);
    } else {
      localStorage.removeItem("rememberedUser");
    }

    showNotification("登录成功", "success");

    // 确认token已保存
    setTimeout(() => {
      const token = localStorage.getItem("accessToken");
      console.log("Token saved:", !!token);
      console.log("Navigating to /home");
      // 尝试使用replace方法避免可能的历史记录问题
      router.replace("/home");
    }, 1000);
  } catch (error) {
    showNotification("登录失败，请检查用户名和密码", "error");
  }
};

// 处理验证码登录
const handleCodeLogin = async () => {
  if (!codeLoginForm.value.phone || !codeLoginForm.value.verificationCode) {
    showNotification("手机号和验证码不能为空", "error");
    return;
  }

  try {
    // 调用验证码登录API
    await authApi.loginByCode(codeLoginForm.value);

    // 保存token到localStorage（实际在apiService中已处理）
    // token存储逻辑已在apiService中统一处理

    showNotification("登录成功", "success");

    // 确认token已保存
    setTimeout(() => {
      const token = localStorage.getItem("accessToken");
      console.log("Token saved:", !!token);
      console.log("Navigating to /home");
      // 尝试使用replace方法避免可能的历史记录问题
      router.replace("/home");
    }, 1000);
  } catch (error) {
    showNotification("登录失败，请检查验证码是否正确", "error");
  }
};

// 处理注册
const handleRegister = async () => {
  // 验证表单
  if (
    !registerForm.value.username ||
    !registerForm.value.realName ||
    !registerForm.value.phone ||
    !registerForm.value.password ||
    !registerForm.value.verificationCode
  ) {
    showNotification("请填写完整的注册信息", "error");
    return;
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    showNotification("两次输入的密码不一致", "error");
    return;
  }

  try {
    // 调用注册API
    await authApi.register(registerForm.value);

    showNotification("注册成功，请登录", "success");

    // 切换到登录表单
    setTimeout(() => {
      switchTo("passwordLogin");
    }, 1000);
  } catch (error) {
    showNotification("注册失败，请稍后重试", "error");
  }
};

// 处理忘记密码
const handleForgotPassword = async () => {
  // 验证表单
  if (
    !forgotPasswordForm.value.phone ||
    !forgotPasswordForm.value.verificationCode ||
    !forgotPasswordForm.value.newPassword
  ) {
    showNotification("请填写完整信息", "error");
    return;
  }

  if (forgotPasswordForm.value.newPassword !== forgotPasswordForm.value.confirmPassword) {
    showNotification("两次输入的密码不一致", "error");
    return;
  }

  try {
    // 调用重置密码API
    await authApi.resetPassword(forgotPasswordForm.value);

    showNotification("密码重置成功，请使用新密码登录", "success");

    // 切换到登录表单
    setTimeout(() => {
      switchTo("passwordLogin");
    }, 1000);
  } catch (error) {
    showNotification("密码重置失败，请稍后重试", "error");
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
