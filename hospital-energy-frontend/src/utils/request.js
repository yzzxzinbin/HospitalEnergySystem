import axios from 'axios';
import { Message, MessageBox } from 'element-ui';
// import store from '@/store'; // 暂时注释掉，如果后续引入Vuex再取消注释
import { getToken } from './auth'; // 修改为相对路径导入

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 15000 // 请求超时时间
});

// request 拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = getToken(); // 直接调用 getToken
    if (token) {
      // 让每个请求携带自定义 token 请根据实际情况自行修改
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  error => {
    // 处理请求错误
    console.log(error); // for debug
    return Promise.reject(error);
  }
);

// response 拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;

    // HTTP 状态码为 2xx 表示请求成功
    const isSuccessStatus = response.status >= 200 && response.status < 300;

    if (isSuccessStatus) {
      // 如果后端响应体中明确带有 code 字段，则以 code 为准判断业务成功/失败
      if (res && typeof res.code !== 'undefined') {
        if (res.code === 200 || res.code === 20000) { // 业务成功代码
          return res;
        } else { // 业务失败代码
          Message({
            message: res.message || 'Error',
            type: 'error',
            duration: 5 * 1000
          });
          // 特定错误代码处理，例如 token 失效
          if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
            MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确认登出', {
              confirmButtonText: '重新登录',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              localStorage.removeItem('token');
              localStorage.removeItem('user');
              location.reload();
            });
          }
          return Promise.reject(new Error(res.message || 'Error'));
        }
      } else {
        // 如果响应体中没有 code 字段，但 HTTP 状态码是 2xx，则直接返回响应数据
        // 这适用于登录成功直接返回 token 和用户信息，或获取普通资源等场景
        return res;
      }
    } else {
      // 此分支实际上不太可能进入，因为非2xx状态会直接进入下面的error处理函数
      // 但为完整性保留，并使用 response.statusText
      Message({
        message: res.message || response.statusText || 'Request failed',
        type: 'error',
        duration: 5 * 1000
      });
      return Promise.reject(new Error(res.message || response.statusText || 'Request failed'));
    }
  },
  error => {
    console.log('err' + error); // for debug
    let errorMessage = '请求失败，请稍后再试';

    if (error.response) {
      // 服务器返回了错误状态码 (4xx, 5xx)
      const serverRes = error.response.data;
      const status = error.response.status;
      errorMessage = (serverRes && serverRes.message) || error.response.statusText || error.message;

      if (status === 401) {
        // 根据您的API说明，登录失败会返回401
        // LoginView.vue 中已有针对性的错误提示，这里可以仅记录或做通用处理
        // 如果是其他接口的401，可能意味着token失效
         MessageBox.confirm('认证失败或会话已过期，请重新登录。', '认证提示', {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            location.reload();
          }).catch(()=>{
            // 用户取消重新登录
          });
      }
      // 可以根据status添加更多特定错误处理
      // else if (status === 403) { ... }
      // else if (status === 404) { ... }

    } else if (error.request) {
      // 请求已发出，但没有收到响应 (网络问题、服务器未响应)
      errorMessage = '网络请求超时或服务器无响应';
    } else {
      // 设置请求时发生了一些事情，触发了错误 (例如，请求配置错误)
      errorMessage = error.message || '请求发送失败';
    }

    Message({
      message: errorMessage,
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);

export default service;
