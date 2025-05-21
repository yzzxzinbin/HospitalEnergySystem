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

<style scoped>
/* CSS Variables (inspired by example, scoped to this component) */
.login-view {
  --primary-color: #409EFF; /* Element UI Blue, or your preferred primary color */
  --primary-hover: #66b1ff; /* Lighter shade for hover, or a darker one like #3a56d4 */
  --text-color-dark: #2b2d42; /* For dark text on light backgrounds */
  --text-color-light: rgba(255, 255, 255, 0.9); /* For light text on dark backgrounds */
  --border-color-light: rgba(255, 255, 255, 0.3); /* Light border for dark UI elements */
  --border-color-dark: rgba(0, 0, 0, 0.1); /* Dark border for light UI elements */
  --background-card: rgba(255, 255, 255, 0.85); /* Semi-transparent white for the card */
  --shadow-strong: 0 12px 40px rgba(0, 0, 0, 0.25);
  --shadow-soft: 0 4px 15px rgba(0, 0, 0, 0.1);
  --transition-fast: all 0.2s ease-in-out;
  --transition-normal: all 0.3s ease;
}

/* Base reset and full-page container */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.login-view {
  position: fixed; /* Changed from flex to fixed for full viewport coverage */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh; /* Ensure it covers full height */
  padding: 20px; /* Padding for smaller screens */
  background-image: url("~@/assets/images/a.png"); /* Adjusted path */
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
  background: rgba(0, 0, 0, 0.3); /* Darker overlay for better contrast */
  z-index: 0;
}

/* Login Card Styling */
.login-card {
  background-color: var(--background-card);
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  width: 100%;
  max-width: 420px; /* Max width of the login card */
  overflow: hidden;
  transition: var(--transition-normal);
  z-index: 1;
  box-shadow: var(--shadow-strong);
  border: 1px solid var(--border-color-light);
}

/* Login Header */
.login-header {
  padding: 30px 32px 25px;
  text-align: center;
  border-bottom: 1px solid rgba(0,0,0,0.05); /* Softer border */
  /* background-color: rgba(255, 255, 255, 0.1); */
}

.login-header h1 {
  font-size: 26px; /* Slightly reduced font size */
  font-weight: 600;
  color: var(--text-color-dark);
  margin-bottom: 5px;
}

.login-header .subtitle {
  font-size: 14px;
  color: #606266; /* Element UI secondary text color */
  opacity: 0.9;
}

/* Login Form */
.login-form-main {
  padding: 30px 32px;
}

.form-group {
  margin-bottom: 22px; /* Spacing between form items */
}

.form-group label { /* If you choose to use labels */
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color-dark);
}

.input-wrapper {
  position: relative;
}

/* Styling Element UI Inputs */
.input-field ::v-deep .el-input__inner {
  background-color: rgba(255, 255, 255, 0.7); /* Slightly more opaque */
  border: 1px solid var(--border-color-dark);
  border-radius: 8px;
  padding: 12px 15px 12px 40px; /* Adjusted padding for prefix icon */
  font-size: 15px;
  color: var(--text-color-dark);
  transition: var(--transition-fast);
  height: 48px; /* Consistent height */
}

.input-field ::v-deep .el-input__inner:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2); /* Softer focus shadow */
  background-color: #fff;
}

.input-field ::v-deep .el-input__prefix {
  left: 12px; /* Prefix icon position */
  display: flex;
  align-items: center;
  height: 100%;
  color: #889aa4; /* Icon color */
}
.input-field ::v-deep .el-input__prefix i {
  font-size: 16px;
}

.input-field.is-focus ::v-deep .el-input__prefix { /* This class might not be on el-input directly */
  color: var(--primary-color);
}
/* For focused state, Element UI usually changes icon color via its own classes */
.input-field ::v-deep .el-input.is-focus .el-input__prefix {
    color: var(--primary-color);
}


/* Form Options (Remember me, Forgot Password) */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 13px;
}

.remember-me ::v-deep .el-checkbox__label {
  color: #606266;
  font-size: 13px;
}
.remember-me ::v-deep .el-checkbox__inner {
  border-color: var(--border-color-dark);
}

.forgot-password {
  color: var(--primary-color);
  text-decoration: none;
  transition: var(--transition-fast);
}

.forgot-password:hover {
  text-decoration: underline;
  color: var(--primary-hover);
}

/* Login Button */
.login-button {
  width: 100%;
  padding: 14px 0; /* Adjusted padding */
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  background-color: var(--primary-color);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: var(--transition-normal);
  display: flex;
  justify-content: center;
  align-items: center;
  letter-spacing: 1px;
  height: 48px; /* Consistent height */
}

.login-button:hover {
  background-color: var(--primary-hover);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(64, 158, 255, 0.3);
}

.login-button.is-loading {
  background-color: var(--primary-hover); /* Keep color during loading */
}

/* Register Link */
.register-link-container {
  text-align: center; /* Center the link */
  margin-top: 25px;
}

.register-link {
  font-size: 14px;
  color: var(--primary-color);
  transition: var(--transition-fast);
}
.register-link:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}


/* Responsive adjustments */
@media (max-width: 480px) {
  .login-card {
    border-radius: 0; /* Full width on small screens */
    margin: 0;
    max-width: 100%;
    height: 100vh; /* Full height */
    display: flex;
    flex-direction: column;
    justify-content: center;
    border: none;
    box-shadow: none;
    background-color: rgba(255, 255, 255, 0.95); /* More opaque on small screens */
    -webkit-backdrop-filter: blur(5px);
    backdrop-filter: blur(5px);
  }

  .login-header,
  .login-form-main {
    padding: 20px 25px; /* Reduced padding */
  }
   .login-header h1 {
    font-size: 22px;
  }
}

/* Password visibility toggle - if re-enabled */
.show-pwd {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #889aa4;
  cursor: pointer;
  user-select: none;
}
.show-pwd:hover {
  color: var(--primary-color);
}
</style>
