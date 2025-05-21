// src/utils/auth.js

const TOKEN_KEY = 'token';

/**
 * 从 localStorage 获取 token
 * @returns {string | null}
 */
export function getToken() {
  const token = localStorage.getItem(TOKEN_KEY);
  // Ensure that we don't return "null" or "undefined" as actual tokens, or an empty string
  if (token === "null" || token === "undefined" || token === null || token === undefined || token.trim() === "") {
    return null;
  }
  return token;
}

/**
 * 将 token 保存到 localStorage
 * @param {string} token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 从 localStorage 移除 token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}

// 如果需要存储和获取用户信息，也可以在这里添加类似方法
const USER_INFO_KEY = 'user';

export function getUserInfo() {
  const user = localStorage.getItem(USER_INFO_KEY);
  try {
    return user ? JSON.parse(user) : null;
  } catch (e) {
    console.error('Error parsing user info from localStorage', e);
    return null;
  }
}

export function setUserInfo(userInfo) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

export function removeUserInfo() {
  localStorage.removeItem(USER_INFO_KEY);
}
