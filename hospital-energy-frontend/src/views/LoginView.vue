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
          placeholder="请输入用户名"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        >
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="请输入密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        >
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
      if (!value || value.length < 6) { // 确保密码不为空
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
        username: [{ required: true, trigger: "blur", validator: validateUsername }],
        password: [{ required: true, trigger: "blur", validator: validatePassword }],
      },
      loading: false,
      passwordType: "password", // 保持 password 类型以便 showPwd 功能 (如果实现)
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
      // 简单实现，如果需要更复杂的图标切换，可以扩展
      if (this.passwordType === "password") {
        this.passwordType = "text";
      } else {
        this.passwordType = "password";
      }
      this.$nextTick(() => {
        this.$refs.password.focus();
      });
    },
    async handleLogin() { // 将外部方法也声明为 async
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const response = await login(this.loginForm);
            
            let tokenValue = null;
            let userDetails = null;

            // 根据后端实际返回的结构调整以下逻辑
            // 常见结构: response.data 包含 { code, message, data: { token, user } }
            // 或 response 直接是 { token, user }
            // 或 response.data 直接是 token 字符串
            if (response && response.data && typeof response.data.token === 'string') {
              tokenValue = response.data.token;
              userDetails = response.data.user; // 假设用户信息在 response.data.user
            } else if (response && typeof response.token === 'string') {
              tokenValue = response.token;
              userDetails = response.user; // 假设用户信息在 response.user
            } else if (response && typeof response === 'string') { // 如果API直接返回token字符串
              tokenValue = response;
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
              if (userDetails) {
                setUserInfo(userDetails);
              }
              this.$router.push({ path: this.redirect || "/" });
            } else {
              // 如果 request.js 的响应拦截器没有处理业务错误提示，这里可以补充
              this.$message.error( (response && response.message) || '登录失败：无法从响应中获取令牌');
            }
          } catch (error) {
            // request.js 中的响应拦截器通常会处理HTTP错误和部分业务错误，并显示消息
            // 此处的 catch 主要用于捕获 validate 失败后的Promise reject，或 login API调用本身抛出的未被拦截器处理的错误
            console.error("LoginView.vue - handleLogin error:", error);
            // 如果需要，可以在这里添加额外的用户提示，但要注意避免与拦截器中的提示重复
            // this.$message.error('登录请求处理异常，请稍后再试');
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

/* Element UI input 在 el-form-item 中可能需要调整样式以匹配设计 */
/* 以下样式仅为示例，可能需要根据实际情况调整 */
.el-input {
  display: inline-block;
  /* height: 47px; */ /* el-input 有自己的高度控制 */
  width: 100%; /* 通常 input 宽度占满 form-item */
}

/* 如果需要自定义 input 内部样式，可以像之前那样针对原生 input 元素 */
/* 但通常 Element UI 的组件封装得比较好，直接使用其属性和插槽即可 */
/*
input {
  background: transparent;
  border: 0px;
  -webkit-appearance: none;
  appearance: none; 
  border-radius: 0px;
  padding: 12px 5px 12px 15px;
  color: #333; 
  height: 47px;
  caret-color: #606266; 

  &:-webkit-autofill {
    box-shadow: 0 0 0px 1000px #f0f2f5 inset !important; 
    -webkit-text-fill-color: #333 !important;
  }
}
*/
</style>
