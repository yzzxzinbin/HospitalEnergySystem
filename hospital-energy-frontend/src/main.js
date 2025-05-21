import Vue from 'vue';
import App from './App.vue';
import router from './router'; // 引入 router
import ElementUI from 'element-ui'; // 引入 Element UI
import 'element-ui/lib/theme-chalk/index.css'; // 引入 Element UI 样式
import './assets/styles/index.css'; // 引入全局样式
import * as echarts from 'echarts'; // 引入 echarts

Vue.use(ElementUI);
Vue.use(router); // 使用 router

Vue.prototype.$echarts = echarts; // 将 echarts 挂载到 Vue 原型上，方便组件内使用

Vue.config.productionTip = false;

new Vue({
  router, // 将 router 实例注入到 Vue 实例中
  render: h => h(App),
}).$mount('#app');
