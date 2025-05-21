<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside-container">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
        <span class="system-title">智慧能源管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/rooms">
          <i class="el-icon-office-building"></i>
          <span slot="title">房间管理</span>
        </el-menu-item>
        <el-menu-item index="/devices">
          <i class="el-icon-cpu"></i>
          <span slot="title">设备管理</span>
        </el-menu-item>
        <el-menu-item index="/energy-data">
          <i class="el-icon-s-data"></i>
          <span slot="title">能源数据</span>
        </el-menu-item>
        <el-menu-item index="/device-templates">
          <i class="el-icon-document-copy"></i>
          <span slot="title">设备模板</span>
        </el-menu-item>
        <el-submenu index="system">
          <template slot="title">
            <i class="el-icon-setting"></i>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/users">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <i class="el-icon-postcard"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header-container">
        <div class="header-left">
          <!-- 面包屑导航 -->
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }} <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-container">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "LayoutView",
  data() {
    return {
      username: "", // 从 localStorage 或 Vuex 获取
    };
  },
  computed: {
    activeMenu() {
      const route = this.$route;
      const { meta, path } = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
  },
  created() {
    this.getUsername();
  },
  methods: {
    getUsername() {
      // 示例：从 localStorage 获取用户名
      const user = JSON.parse(localStorage.getItem("user"));
      if (user && user.username) {
        this.username = user.username;
      } else {
        this.username = "未登录";
      }
    },
    handleMenuSelect(index) {
      this.$router.push(index);
    },
    handleCommand(command) {
      if (command === "logout") {
        // 清除 token 和用户信息
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        this.$router.push("/login");
      } else if (command === "profile") {
        this.$router.push("/profile");
      }
    },
  },
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside-container {
  background-color: #304156;
  color: #fff;
  overflow-x: hidden; /* 防止水平滚动条 */
}

.logo-container {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #2b2f3a; /* Logo区域背景色稍深一些 */
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.system-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-menu {
  border-right: none; /* 去除菜单右边框 */
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 50px !important; /* 固定 Header 高度 */
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #303133;
  display: flex;
  align-items: center;
}

.main-container {
  padding: 20px;
  background-color: #f0f2f5; /* 主内容区域背景色 */
  overflow-y: auto; /* 内容超出时显示滚动条 */
}
</style>
