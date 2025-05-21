<template>
  <div class="register-container">
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
      auto-complete="on"
      label-position="left"
    >
      <div class="title-container">
        <h3 class="title">用户注册</h3>
      </div>

      <el-form-item prop="username">
        <el-input
          ref="username"
          v-model="registerForm.username"
          placeholder="用户名"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        >
          <span slot="prefix" class="svg-container">
            <i class="el-icon-user"></i>
          </span>
        </el-input>
      </el-form-item>

      <el-form-item prop="email">
        <el-input
          ref="email"
          v-model="registerForm.email"
          placeholder="邮箱"
          name="email"
          type="text"
          tabindex="2"
          auto-complete="on"
        >
          <span slot="prefix" class="svg-container">
            <i class="el-icon-message"></i>
          </span>
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          :key="passwordType"
          ref="password"
          v-model="registerForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="3"
          auto-complete="on"
        >
          <span slot="prefix" class="svg-container">
            <i class="el-icon-lock"></i>
          </span>
          <span slot="suffix" class="show-pwd" @click="showPwd">
            <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-minus'"></i>
          </span>
        </el-input>
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          :key="passwordType2"
          ref="confirmPassword"
          v-model="registerForm.confirmPassword"
          :type="passwordType2"
          placeholder="确认密码"
          name="confirmPassword"
          tabindex="4"
          auto-complete="on"
          @keyup.enter.native="handleRegister"
        >
          <span slot="prefix" class="svg-container">
            <i class="el-icon-lock"></i>
          </span>
          <span slot="suffix" class="show-pwd" @click="showPwd2">
            <i :class="passwordType2 === 'password' ? 'el-icon-view' : 'el-icon-minus'"></i>
          </span>
        </el-input>
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width: 100%; margin-bottom: 20px"
        @click.native.prevent="handleRegister"
      >
        注册
      </el-button>

      <div style="text-align:right;">
        <el-link type="primary" @click="handleLogin">已有账户？去登录</el-link>
      </div>
    </el-form>
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
        // Simplified email regex, remove unnecessary escapes
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
          callback(new Error("请输入有效的邮箱地址"));
        } else {
          callback();
        }
      }
    };
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码不能少于6位"));
      } else {
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
        this.passwordType = "";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    showPwd2() {
      if (this.passwordType2 === "password") {
        this.passwordType2 = "";
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
            const response = await register({
              username: this.registerForm.username,
              email: this.registerForm.email,
              password: this.registerForm.password,
            });
            // 假设注册成功后，后端返回创建的 User 对象 (201 CREATED)
            // API说明中成功响应是 201 CREATED，request.js中默认处理 code 200/20000
            // 如果后端严格返回201且不带通用的code结构，request.js可能需要调整，或者这里直接判断response状态
            // 为简单起见，假设 request.js 能正确处理 201 并返回 data
            if (response && response.data) { // 或者根据实际成功标识判断
              this.$message.success("注册成功！请登录。");
              this.$router.push("/login");
            } else {
              // 这种情况理论上应该由request.js的拦截器处理，或者API调用本身就reject了
              this.$message.error(response.message || "注册失败，请稍后再试。");
            }
          } catch (error) {
            const errMsg = error.response?.data?.message || error.message || "注册失败，服务器错误或网络问题。";
            this.$message.error(errMsg);
          } finally {
            this.loading = false;
          }
        } else {
          console.log("error submit!!");
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
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.register-form {
  width: 420px;
  padding: 35px 35px 25px 35px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title-container {
  position: relative;
}

.title {
  font-size: 26px;
  color: #303133;
  margin: 0px auto 30px auto;
  text-align: center;
  font-weight: bold;
}

.svg-container {
  padding: 6px 5px 6px 15px;
  color: #889aa4;
  vertical-align: middle;
  width: 30px;
  display: inline-block;
}

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

.el-form-item {
  border: 1px solid rgba(220, 223, 230, 0.5); /* 更柔和的边框 */
  background: #fff;
  border-radius: 5px;
  color: #454545;
  margin-bottom: 22px; /* 适当的间距 */
}

.el-input {
  display: inline-block;
  height: 47px;
  width: calc(100% - 30px); /* 调整宽度以适应前缀图标 */
}

/* 对 el-input 内部 input 的样式调整 */
.register-form ::v-deep .el-input__inner {
  background: transparent;
  border: 0px;
  -webkit-appearance: none;
  appearance: none; /* 标准属性 */
  border-radius: 0px;
  padding: 12px 5px 12px 15px;
  color: #333;
  height: 47px;
  caret-color: #606266;
}

.register-form ::v-deep .el-input__inner:-webkit-autofill {
  box-shadow: 0 0 0px 1000px #f0f2f5 inset !important;
  -webkit-text-fill-color: #333 !important;
}
</style>
