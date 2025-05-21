<template>
  <div class="register-view">
    <div class="background-overlay"></div>
    <div class="register-card mosaic-glass">
      <div class="register-header">
        <h1>用户注册</h1>
      </div>

      <el-form
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        class="register-form-main"
        auto-complete="on"
        label-position="left"
        @submit.native.prevent="handleRegister"
      >
        <el-form-item prop="username" class="form-group">
          <div class="input-wrapper">
            <el-input
              ref="username"
              v-model="registerForm.username"
              placeholder="设置用户名 (至少3位)"
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

        <el-form-item prop="email" class="form-group">
          <div class="input-wrapper">
            <el-input
              ref="email"
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              name="email"
              type="email" 
              tabindex="2"
              auto-complete="on"
              class="input-field"
              prefix-icon="el-icon-message"
            >
            </el-input>
          </div>
        </el-form-item>

        <el-form-item prop="password" class="form-group">
          <div class="input-wrapper">
            <el-input
              :key="passwordType"
              ref="password"
              v-model="registerForm.password"
              :type="passwordType"
              placeholder="设置密码 (至少6位)"
              name="password"
              tabindex="3"
              auto-complete="on"
              class="input-field"
              prefix-icon="el-icon-lock"
            >
              <span slot="suffix" class="show-pwd" @click="showPwd">
                <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"></i>
              </span>
            </el-input>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword" class="form-group">
          <div class="input-wrapper">
            <el-input
              :key="passwordType2"
              ref="confirmPassword"
              v-model="registerForm.confirmPassword"
              :type="passwordType2"
              placeholder="确认密码"
              name="confirmPassword"
              tabindex="4"
              auto-complete="on"
              class="input-field"
              prefix-icon="el-icon-lock"
              @keyup.enter.native="handleRegister"
            >
              <span slot="suffix" class="show-pwd" @click="showPwd2">
                <i :class="passwordType2 === 'password' ? 'el-icon-view' : 'el-icon-hide'"></i>
              </span>
            </el-input>
          </div>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          class="register-button"
          @click.native.prevent="handleRegister"
        >
          <span v-if="loading" class="loader"></span>
          <span v-else>注 册</span>
        </el-button>

        <div class="login-link-container">
          <el-link type="primary" @click="handleLogin" class="login-link">已有账户？去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth';

export default {
  name: "RegisterView",
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入用户名"));
      } else if (value.length < 3) {
        callback(new Error("用户名至少3位"));
      } else {
        callback();
      }
    };
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入邮箱地址"));
      } else {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
          callback(new Error("请输入有效的邮箱地址"));
        } else {
          callback();
        }
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入密码"));
      } else if (value.length < 6) {
        callback(new Error("密码不能少于6位"));
      } else {
        // If confirmPassword has been validated, trigger its validation again
        if (this.registerForm.confirmPassword) {
          this.$refs.registerForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      registerForm: {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", validator: validateUsername },
        ],
        email: [
          { required: true, trigger: "blur", validator: validateEmail },
          { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, trigger: "blur", validator: validatePassword },
        ],
        confirmPassword: [
          { required: true, trigger: "blur", validator: validateConfirmPassword },
        ],
      },
      loading: false,
      passwordType: "password",
      passwordType2: "password",
    };
  },
  methods: {
    showPwd() {
      if (this.passwordType === "password") {
        this.passwordType = "text"; // Changed from empty string to text for clarity
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    showPwd2() {
      if (this.passwordType2 === "password") {
        this.passwordType2 = "text"; // Changed from empty string to text for clarity
      } else {
        this.passwordType2 = "password";
      }
      this.$nextTick(() => {
        this.$refs.confirmPassword.focus();
      });
    },
    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            // API说明: RegistrationRequestDto (包含 username, password, email)
            // 成功响应 (201 CREATED): 返回成功消息字符串
            const response = await register({
              username: this.registerForm.username,
              email: this.registerForm.email,
              password: this.registerForm.password,
            });
            
            // Assuming request.js handles the 201 CREATED and returns the success message string in response.data or response directly
            // API doc: "返回成功消息字符串，例如: 'User registered successfully. Please login.'"
            // Let's assume the success message is in response.data if the request util wraps it, or response itself.
            let successMessage = null;
            if (response && typeof response.data === 'string') {
                successMessage = response.data;
            } else if (typeof response === 'string') {
                successMessage = response;
            } else if (response && response.message && typeof response.message === 'string') { // Fallback if it's in a message field
                successMessage = response.message;
            } else if (response && response.data && response.data.message && typeof response.data.message === 'string'){
                successMessage = response.data.message;
            }

            if (successMessage) {
              this.$message.success(successMessage || "注册成功！请登录。");
              this.$router.push("/login");
            } else {
              // This case might indicate an unexpected response structure from the server or request utility
              this.$message.error( (response && (response.message || (response.data && response.data.message))) || "注册失败，响应格式不正确。请稍后再试。");
            }
          } catch (error) {
            // error.response.data.message is a common pattern for Axios errors with backend-provided messages
            const errMsg = error.response?.data?.message || error.message || "注册请求失败，请检查网络或联系管理员。";
            this.$message.error(errMsg);
          } finally {
            this.loading = false;
          }
        } else {
          console.log("表单验证失败!");
          return false;
        }
      });
    },
    handleLogin() {
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
/* CSS Variables (copied from LoginView.vue for consistency) */
.register-view {
  --primary-color: #409EFF;
  --primary-hover: #66b1ff;
  --text-color-dark: #2b2d42;
  --text-color-light: rgba(255, 255, 255, 0.9);
  --border-color-light: rgba(255, 255, 255, 0.3);
  --border-color-dark: rgba(0, 0, 0, 0.1);
  --background-card: rgba(255, 255, 255, 0.85);
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

.register-view {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-image: url("~@/assets/images/a.png"); /* Ensure your image path is correct */
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
  background: rgba(0, 0, 0, 0.3);
  z-index: 0;
}

/* Register Card Styling */
.register-card {
  background-color: var(--background-card);
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  width: 100%;
  max-width: 450px; /* Slightly wider for more fields */
  overflow: hidden;
  transition: var(--transition-normal);
  z-index: 1;
  box-shadow: var(--shadow-strong);
  border: 1px solid var(--border-color-light);
}

/* Register Header */
.register-header {
  padding: 30px 32px 25px;
  text-align: center;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.register-header h1 {
  font-size: 26px;
  font-weight: 600;
  color: var(--text-color-dark);
  margin-bottom: 5px;
}

/* Register Form */
.register-form-main {
  padding: 30px 32px;
}

.form-group {
  margin-bottom: 20px; /* Adjusted spacing */
}

/* Styling Element UI Inputs (copied and adapted from LoginView.vue) */
.input-field ::v-deep .el-input__inner {
  background-color: rgba(255, 255, 255, 0.7);
  border: 1px solid var(--border-color-dark);
  border-radius: 8px;
  padding: 12px 15px 12px 40px; /* For prefix icon */
  font-size: 15px;
  color: var(--text-color-dark);
  transition: var(--transition-fast);
  height: 48px;
}

.input-field ::v-deep .el-input__inner:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
  background-color: #fff;
}

.input-field ::v-deep .el-input__prefix {
  left: 12px;
  display: flex;
  align-items: center;
  height: 100%;
  color: #889aa4;
}
.input-field ::v-deep .el-input__prefix i {
  font-size: 16px;
}

.input-field.is-focus ::v-deep .el-input__prefix, /* Element UI might not add .is-focus to wrapper */
.input-field ::v-deep .el-input.is-focus .el-input__prefix {
    color: var(--primary-color);
}

/* Password visibility toggle */
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
.show-pwd i {
  font-size: 18px; /* Slightly larger icon for visibility toggle */
}

/* Register Button */
.register-button {
  width: 100%;
  padding: 14px 0;
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
  height: 48px;
}

.register-button:hover {
  background-color: var(--primary-hover);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(64, 158, 255, 0.3);
}

.register-button.is-loading {
  background-color: var(--primary-hover);
}

/* Login Link */
.login-link-container {
  text-align: center;
  margin-top: 25px;
}

.login-link {
  font-size: 14px;
  color: var(--primary-color);
  transition: var(--transition-fast);
}
.login-link:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

/* Responsive adjustments */
@media (max-width: 480px) {
  .register-card {
    border-radius: 0;
    margin: 0;
    max-width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    border: none;
    box-shadow: none;
    background-color: rgba(255, 255, 255, 0.95);
    -webkit-backdrop-filter: blur(5px);
    backdrop-filter: blur(5px);
  }

  .register-header,
  .register-form-main {
    padding: 20px 25px;
  }
   .register-header h1 {
    font-size: 22px;
  }
  .form-group {
    margin-bottom: 18px; /* Slightly reduce margin on small screens */
  }
}
</style>
