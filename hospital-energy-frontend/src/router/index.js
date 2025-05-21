import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  // 登录页
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
  },
  // 主布局页
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/LayoutView.vue'),
    redirect: '/dashboard', // 默认重定向到仪表盘
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'el-icon-s-home' },
      },
      {
        path: 'rooms',
        name: 'RoomManagement',
        component: () => import('../views/RoomManagementView.vue'),
        meta: { title: '房间管理', icon: 'el-icon-office-building' },
      },
      {
        path: 'devices',
        name: 'DeviceManagement',
        component: () => import('../views/DeviceManagementView.vue'),
        meta: { title: '设备管理', icon: 'el-icon-cpu' },
      },
      {
        path: 'energy-data',
        name: 'EnergyDataManagement',
        component: () => import('../views/EnergyDataManagementView.vue'),
        meta: { title: '能源数据', icon: 'el-icon-s-data' },
      },
      {
        path: 'device-templates',
        name: 'DeviceTemplateManagement',
        component: () => import('../views/DeviceTemplateManagementView.vue'),
        meta: { title: '设备模板', icon: 'el-icon-document-copy' },
      },
    //   {
    //     path: 'users',
    //     name: 'UserManagement',
    //     component: () => import('../views/UserManagementView.vue'),
    //     meta: { title: '用户管理', icon: 'el-icon-user' },
    //   },
    //   {
    //     path: 'profile',
    //     name: 'UserProfile',
    //     component: () => import('../views/UserProfileView.vue'),
    //     meta: { title: '个人中心', icon: 'el-icon-setting' },
    //   },
    ],
  },
  // 404 页面
  {
    path: '*' /* 未匹配到的路由都跳转到404 */,
    name: 'NotFound',
    component: () => import('../views/NotFoundView.vue'),
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

// 导航守卫，用于权限控制等
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token'); // 简单示例，实际应更复杂

  if (to.name !== 'Login' && !isAuthenticated) {
    next({ name: 'Login' });
  } else if (to.name === 'Login' && isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;
