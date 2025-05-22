<template>
  <div class="login-view">
    <div class="background-overlay"></div>
    <div class="login-card mosaic-glass">
      <div class="login-header">
        <h1>医院智慧能源管理系统</h1>
        <!-- <p class="subtitle">Welcome back! Please login to your account.</p> -->
      </div>

      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form-main"
        auto-complete="on"
        label-position="left"
        @submit.native.prevent="handleLogin"
      >
        <el-form-item prop="username" class="form-group">
          <!-- <label for="username-input">用户名</label> -->
          <div class="input-wrapper">
            <el-input
              id="username-input"
              ref="username"
              v-model="loginForm.username"
              placeholder="请输入用户名"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
              class="input-field"
              prefix-icon="el-icon-user"
            >
            </el-input>
          </div>
        </el-form-item>

        <el-form-item prop="password" class="form-group">
          <!-- <label for="password-input">密码</label> -->
          <div class="input-wrapper">
            <el-input
              id="password-input"
              :key="passwordType"
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="请输入密码"
              name="password"
              tabindex="2"
              auto-complete="on"
              class="input-field"
              prefix-icon="el-icon-lock"
              @keyup.enter.native="handleLogin"
            >
              <!-- <span slot="suffix" class="show-pwd" @click="showPwd">
                <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-minus'"></i>
              </span> -->
            </el-input>
          </div>
        </el-form-item>

        <!-- <div class="form-options">
          <el-checkbox v-model="rememberMe" class="remember-me">保持登录</el-checkbox>
          <a href="#" class="forgot-password">忘记密码?</a>
        </div> -->

        <el-button
          :loading="loading"
          type="primary"
          class="login-button"
          @click.native.prevent="handleLogin"
        >
          <span v-if="loading" class="loader"></span>
          <span v-else>登 录</span>
        </el-button>

        <div class="register-link-container">
          <el-link type="primary" @click="handleRegister" class="register-link">没有账户？去注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth';
import { setToken, setUserInfo } from '@/utils/auth'; // 引入 setToken 和 setUserInfo

export default {
  name: "LoginView",
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入用户名"));
      } else {
        callback();
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (!value) { // 仅校验非空，长度校验由 Element UI min 属性或自定义规则处理
        callback(new Error("请输入密码"));
      } else if (value.length < 6) {
        callback(new Error("密码不能少于6位"));
      }
      else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
      },
      loginRules: {
        username: [{ required: true, trigger: "blur", validator: validateUsername }],
        password: [{ required: true, trigger: "blur", validator: validatePassword }],
      },
      loading: false,
      passwordType: "password",
      redirect: undefined,
      // rememberMe: false, // If you want to add "remember me" functionality
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  methods: {
    showPwd() {
      if (this.passwordType === "password") {
        this.passwordType = "text";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const response = await login(this.loginForm);
            
            // The existing logic for handling token and user info seems fine.
            // Adjust based on actual API response structure if needed.
            // Assuming response.data contains { token, username } as per API说明.md
            // The API doc says: LoginResponseDto 对象 (包含 token 和 username)
            // The current code checks for response.data.token and response.data.user
            // Let's align with API doc: token and username
            
            let tokenValue = null;
            let responseUsername = null; // To store username from response

            if (response && response.data) { // Assuming request util returns data directly
                tokenValue = response.data.token;
                responseUsername = response.data.username; // Get username from response
            } else if (response && response.token) { // Fallback if not wrapped in .data
                tokenValue = response.token;
                responseUsername = response.username;
            }
            // 也可能后端返回的 response 本身就是 data 对象，如：{ code, message, data: { token, user } }
            // 这种情况下，request.js 中的拦截器已经处理了外层包装，response 就是 data
            // 例如： if (response && typeof response.token === 'string') 

            if (tokenValue) {
              // 确保存储的 token 不包含 "Bearer " 前缀，因为 request.js 会添加它
              if (tokenValue.startsWith("Bearer ")) {
                tokenValue = tokenValue.substring(7);
              }
              setToken(tokenValue);
              // Store username from response, or the one from form as fallback
              // API doc says LoginResponseDto has username.
              const userInfoToStore = { username: responseUsername || this.loginForm.username };
              setUserInfo(userInfoToStore); 
              this.$router.push({ path: this.redirect || "/" });
            } else {
              this.$message.error((response && response.message) || '登录失败：无法从响应中获取令牌');
            }
          } catch (error) {
            console.error("LoginView.vue - handleLogin error:", error);
            // Error message is usually handled by the request interceptor
            // If not, uncomment or add specific message:
            // this.$message.error(error.message || '登录请求处理异常，请稍后再试');
          } finally {
            this.loading = false;
          }
        } else {
          console.log("表单验证失败!");
          return false;
        }
      });
    },
    handleRegister() {
      this.$router.push('/register');
    },
  },
};
</script>
<!-- 导入全局样式 -->
import "@/assets/styles/index.css";
<style scoped>
/* CSS Variables (inspired by reference example, scoped to this component) */
.login-view {
  --primary-color: #409EFF; /* Element UI Blue */
  --primary-hover: #3a56d4; /* Darker shade for hover from reference */
  --text-color-dark: #2b2d42;
  --text-color-light: rgba(255, 255, 255, 0.9);
  --border-color-light: rgba(255, 255, 255, 0.3); /* Reference --border-color */
  --border-color-input: rgba(255, 255, 255, 0.3); /* Specific for input borders from reference */
  --background-card-ref: rgba(255, 255, 255, 0.4); /* Reference card background */
  --input-background-ref: rgba(255, 255, 255, 0.1); /* Reference input background */
  --input-focus-background-ref: rgba(255, 255, 255, 0.15);
  --input-text-color-ref: rgba(0, 0, 0, 0.4); /* Reference input text color */
  --input-focus-text-color-ref: #000000;
  --shadow-ref: 0 8px 32px rgba(0, 0, 0, 0.3); /* Reference shadow */
  --transition-normal: all 0.3s ease;
  --white-color: #ffffff;
}

/* Base reset and full-page container */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.login-view {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px; /* Padding for smaller screens */
  background-image: url("~@/assets/images/a.png"); /* Path from current project */
  background-size: cover;
  background-position: center;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(201, 205, 221, 0.3); /* Lighter overlay from reference */
  z-index: 0;
}

/* Login Card Styling - Mosaic Glass Effect from reference */
.login-card {
  background-color: var(--background-card-ref);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  width: 100%;
  max-width: 420px;
  overflow: hidden;
  transition: var(--transition-normal);
  z-index: 1;
  box-shadow: var(--shadow-ref);
  /* border: 1px solid var(--border-color-light); /* Optional: reference had commented out border */
}

/* Login Header from reference */
.login-header {
  padding: 36px 32px 28px;
  text-align: center;
  border-bottom: 1px solid var(--border-color-light); /* Using light border color */
  background-color: rgba(211, 57, 57, 0.3); /* Reddish background from reference */
}

.login-header h1 {
  font-size: 28px; /* Reference font size */
  font-weight: 600;
  color: var(--text-color-dark); /* Keep current dark text for title */
  margin-bottom: 8px;
  letter-spacing: 1px; /* Reference letter spacing */
}

.login-header .subtitle { /* Style for subtitle if re-enabled */
  font-size: 14px;
  color: var(--text-color-dark); /* Adjusted for better visibility on reddish bg */
  opacity: 0.9;
}

/* Login Form from reference */
.login-form-main {
  padding: 32px;
}

.form-group {
  margin-bottom: 24px; /* Reference spacing */
}

/* Input field styling from reference */
.input-field ::v-deep .el-input__inner {
  background-color: var(--input-background-ref) !important;
  border: 1px solid var(--border-color-input) !important;
  border-radius: 8px !important;
  padding: 12px 15px 12px 40px !important; /* Adjusted padding for prefix icon */
  font-size: 15px !important;
  color: var(--input-text-color-ref) !important;
  transition: var(--transition-normal) !important;
  height: 40px; /* Let padding define height */
}

.input-field ::v-deep .el-input__inner:focus {
  outline: none !important;
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 4px rgba(74, 107, 255, 0.2) !important; /* Reference focus shadow */
  background-color: var(--input-focus-background-ref) !important;
  color: var(--input-focus-text-color-ref) !important;
}

.input-field ::v-deep .el-input__prefix {
  left: 12px !important; /* Reference icon position */
  display: flex;
  align-items: center;
  height: 100%;
  color: var(--text-color-dark); /* Adjusted for visibility */
}
.input-field ::v-deep .el-input__prefix i {
  font-size: 16px;
}

.input-field ::v-deep .el-input.is-focus .el-input__prefix {
  color: var(--primary-color) !important;
}

/* Login Button from reference */
.login-button {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  color: var(--white-color);
  background-color: rgba(74, 107, 255, 0.8); /* Reference button color */
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: var(--transition-normal);
  display: flex;
  justify-content: center;
  align-items: center;
  letter-spacing: 1px;
  /* margin-bottom: 24px; /* Optional: if other elements follow */
}

.login-button:hover {
  background-color: var(--primary-hover); /* Using updated --primary-hover */
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(74, 107, 255, 0.4); /* Reference hover shadow */
}

.login-button.is-loading {
  background-color: var(--primary-hover);
}

/* Register Link Container */
.register-link-container {
  text-align: center;
  margin-top: 25px;
}

.register-link {
  font-size: 14px;
  color: var(--primary-color);
  transition: var(--transition-normal);
}
.register-link:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

/* Responsive adjustments from reference (basic) */
@media (max-width: 480px) {
  .login-card {
    border-radius: 0; /* Full width on small screens */
    margin: 0;
    max-width: 100%;
    height: 100vh; /* Full height */
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .login-header,
  .login-form-main {
    padding: 20px 25px; /* Reduced padding */
  }
}
</style>
