<template>
  <div class="login-container">
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      auto-complete="on"
      label-position="left"
    >
      <div class="login-title-container">
        <h3 class="login-title">医院智慧能源管理系统</h3>
      </div>

      <el-form-item prop="username">
        <el-input
          ref="username"
          v-model="loginForm.username"
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

      <el-form-item prop="password">
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        >
          <span slot="prefix" class="svg-container">
            <i class="el-icon-lock"></i>
          </span>
          <span slot="suffix" class="show-pwd" @click="showPwd">
            <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-minus'"></i>
          </span>
        </el-input>
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width: 100%; margin-bottom: 30px"
        @click.native.prevent="handleLogin"
      >
        登录
      </el-button>

      <div style="text-align:right;">
        <el-link type="primary" @click="handleRegister">没有账户？去注册</el-link>
      </div>
    </el-form>
  </div>
</template>

<script>
// 实际项目中，会从 src/api/auth.js 导入登录API
// import { login } from '@/api/auth'; 

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
      if (value.length < 6) {
        callback(new Error("密码不能少于6位"));
      } else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", validator: validateUsername },
        ],
        password: [
          { required: true, trigger: "blur", validator: validatePassword },
        ],
      },
      loading: false,
      passwordType: "password",
      redirect: undefined,
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
        this.passwordType = "";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          // 模拟API调用
          setTimeout(() => {
            // 假设登录成功，实际应调用API并处理响应
            // login(this.loginForm).then(response => {
            //   const { data } = response;
            //   localStorage.setItem('token', data.token); // 存储token
            //   localStorage.setItem('user', JSON.stringify(data.user)); // 存储用户信息
            //   this.$router.push({ path: this.redirect || '/' });
            //   this.loading = false;
            // }).catch(error => {
            //   this.$message.error('登录失败，请检查用户名和密码');
            //   this.loading = false;
            // });

            // ---- 模拟代码开始 ----
            if (this.loginForm.username === 'admin' && this.loginForm.password === '123456') {
                localStorage.setItem('token', 'fake-admin-token');
                localStorage.setItem('user', JSON.stringify({ username: 'admin', role: 'admin' }));
                this.$router.push({ path: this.redirect || '/' });
            } else if (this.loginForm.username === 'user' && this.loginForm.password === '123456') {
                localStorage.setItem('token', 'fake-user-token');
                localStorage.setItem('user', JSON.stringify({ username: 'user', role: 'user' }));
                this.$router.push({ path: this.redirect || '/' });
            } else {
                this.$message.error('登录失败，用户名或密码错误');
            }
            this.loading = false;
            // ---- 模拟代码结束 ----

          }, 500);
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    handleRegister() {
      // 跳转到注册页面，如果需要的话
      // this.$router.push('/register');
      this.$message("注册功能暂未开放");
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5; /* 与 Layout 背景色协调 */
}

.login-form {
  width: 400px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title-container {
  position: relative;
}

.login-title {
  font-size: 26px;
  color: #303133; /* Element UI 主要文字颜色 */
  margin: 0px auto 40px auto;
  text-align: center;
  font-weight: bold;
}

.svg-container {
  padding: 6px 5px 6px 15px;
  color: #889aa4; /* 图标颜色 */
  vertical-align: middle;
  width: 30px;
  display: inline-block;
}

.show-pwd {
  position: absolute;
  right: 10px;
  top: 7px;
  font-size: 16px;
  color: #889aa4;
  cursor: pointer;
  user-select: none;
}

.el-form-item {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.0);
  border-radius: 5px;
  color: #454545;
}

.el-input {
  display: inline-block;
  height: 47px;
  width: 85%;
}

input {
  background: transparent;
  border: 0px;
  -webkit-appearance: none;
  border-radius: 0px;
  padding: 12px 5px 12px 15px;
  color: #333; /* 输入文字颜色 */
  height: 47px;
  caret-color: #606266; /* 光标颜色 */

  &:-webkit-autofill {
    box-shadow: 0 0 0px 1000px #f0f2f5 inset !important; /* 浏览器自动填充背景色 */
    -webkit-text-fill-color: #333 !important; /* 浏览器自动填充文字颜色 */
  }
}
</style>
