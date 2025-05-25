<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 用户信息卡片 -->
      <el-col :span="10" :xs="24">
        <el-card class="box-card profile-card" shadow="hover">
          <div slot="header">
            <span>个人信息</span>
          </div>
          <div v-if="currentUser" class="user-info-content">
            <div class="avatar-section">
              <!-- Placeholder for avatar, can be replaced with el-avatar or img -->
              <div class="default-avatar">
                <i class="el-icon-user-solid"></i>
              </div>
            </div>
            <el-descriptions :column="1" border class="info-descriptions">
              <el-descriptions-item>
                <template slot="label"><i class="el-icon-user"></i> 用户名</template>
                {{ currentUser.username }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label"><i class="el-icon-message"></i> 邮箱</template>
                {{ currentUser.email || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label"><i class="el-icon-coordinate"></i> 角色</template>
                <el-tag size="small" v-for="roleItem in currentUser.role ? currentUser.role.split(',') : ['未知']" :key="roleItem" style="margin-right: 5px;">{{ roleItem }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else description="无法加载用户信息"></el-empty>
        </el-card>
      </el-col>

      <!-- 修改密码卡片 -->
      <el-col :span="14" :xs="24">
        <el-card class="box-card password-card" shadow="hover">
          <div slot="header">
            <span>修改密码</span>
          </div>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px" class="password-form">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入当前密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">提交修改</el-button>
              <el-button icon="el-icon-refresh" @click="resetPasswordForm('passwordForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getUserInfo, setUserInfo } from '@/utils/auth'; // Import setUserInfo to update stored info
// Import updateUser and getUserById
import { updateUser, getUserById } from '@/api/user'; 

export default {
  name: "UserProfileView",
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'));
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'));
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'));
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      currentUser: null,
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: "请输入当前密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, validator: validatePass, trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: "blur" },
        ],
      },
      passwordLoading: false,
    };
  },
  created() {
    this.loadUserProfile();
  },
  methods: {
    async loadUserProfile() {
      let userInfo = getUserInfo(); 
      console.log("UserProfileView: Initial userInfo from auth.js:", JSON.parse(JSON.stringify(userInfo || {})));

      if (userInfo && userInfo.id) { 
        try {
          console.log("UserProfileView: Fetching fresh user info for ID:", userInfo.id);
          const freshUserInfo = await getUserById(userInfo.id);
          console.log("UserProfileView: Fresh user info from API:", JSON.parse(JSON.stringify(freshUserInfo || {})));
          
          this.currentUser = {
            id: freshUserInfo.id,
            username: freshUserInfo.username,
            email: freshUserInfo.email,
            // Directly use the role from the backend.
            // If freshUserInfo.role is null or an empty string, it will be reflected as such.
            // The template v-for="roleItem in currentUser.role ? currentUser.role.split(',') : ['未知']"
            // will handle cases where role might be empty or null by showing '未知'.
            role: freshUserInfo.role 
          };
          console.log("UserProfileView: this.currentUser set to:", JSON.parse(JSON.stringify(this.currentUser)));
          
          const existingAuthInfo = getUserInfo(); 
          const updatedAuthInfo = {
            ...existingAuthInfo, 
            id: freshUserInfo.id,
            username: freshUserInfo.username,
            email: freshUserInfo.email,
            role: freshUserInfo.role, // Store the exact role from backend
          };
          setUserInfo(updatedAuthInfo);
          console.log("UserProfileView: Updated info stored via setUserInfo:", JSON.parse(JSON.stringify(updatedAuthInfo)));

        } catch (error) {
          console.error("UserProfileView: Error fetching fresh user info:", error);
          this.$message.error("获取最新用户信息失败，将使用本地缓存信息。");
          this.currentUser = {
              id: userInfo.id,
              username: userInfo.username,
              email: userInfo.email, 
              role: userInfo.role // Use role from local storage as fallback
          };
           console.log("UserProfileView: Fallback this.currentUser:", JSON.parse(JSON.stringify(this.currentUser)));
        }
      } else if (userInfo && userInfo.username) {
        this.$message.warning("用户ID未在本地存储中找到，部分信息可能无法及时更新。");
        this.currentUser = {
            id: userInfo.id, 
            username: userInfo.username,
            email: userInfo.email,
            role: userInfo.role // Use role from local storage
        };
        console.log("UserProfileView: Fallback (no ID) this.currentUser:", JSON.parse(JSON.stringify(this.currentUser)));
      } else {
        this.$message.error("无法获取用户信息，请重新登录。");
        this.$router.push('/login');
      }
    },
    async handleChangePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.passwordLoading = true;
          if (!this.currentUser || !this.currentUser.id) {
            this.$message.error("无法获取当前用户信息，无法修改密码。");
            this.passwordLoading = false;
            return;
          }
          try {
            // Use updateUser API to change password, as per documented PUT /api/users/{id}
            // The backend UserDto should handle password updates.
            // We only send the password field for update.
            // Other fields like username, email, role are not changed here.
            await updateUser(this.currentUser.id, {
              // It's crucial that the backend API for PUT /api/users/{id}
              // correctly handles a payload that might only contain the password
              // for password change purposes, or that it expects all fields.
              // Based on typical REST practices, sending only fields to be updated is common.
              // The UserDto in API说明.md for PUT /api/users/{id} includes 'password'.
              password: this.passwordForm.newPassword,
              // To be safe and ensure other fields are not accidentally wiped if backend expects full DTO:
              // username: this.currentUser.username,
              // email: this.currentUser.email,
              // role: this.currentUser.role,
            });

            this.$message.success("密码修改成功！部分系统可能需要您重新登录以使新密码生效。");
            this.resetPasswordForm('passwordForm');
            // Optionally, force re-login:
            // import { removeToken, removeUserInfo } from '@/utils/auth';
            // removeToken();
            // removeUserInfo();
            // this.$router.push(`/login?redirect=${this.$route.fullPath}`);
            
          } catch (error) {
            // Error message should ideally come from the API response
            const errorMsg = error.response?.data?.message || error.message || "密码修改失败，请检查当前密码是否正确。";
            this.$message.error(errorMsg);
          } finally {
            this.passwordLoading = false;
          }
        } else {
          return false;
        }
      });
    },
    resetPasswordForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
    },
  },
};
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.box-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

.user-info-content {
  padding: 10px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 20px;
}

.default-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #f0f2f5;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 50px;
  color: #c0c4cc;
  border: 2px solid #e4e7ed;
}

.info-descriptions >>> .el-descriptions-item__label {
  font-weight: 500;
  width: 100px;
}
.info-descriptions >>> .el-descriptions-item__content {
  font-weight: normal;
}
.info-descriptions i {
    margin-right: 5px;
}

.password-form {
  padding: 20px 20px 0 0; /* Adjust padding for form items */
}

.password-card {
  min-height: 360px; /* Ensure consistent height with profile card */
}

/* Keep .clearfix if it's used elsewhere or by other components,
   but it's removed from the card headers for .profile-card and .password-card.
   If it's exclusively for the card headers that were changed, 
   and no longer used, it can be removed from here.
   For now, keeping it in case it's used by other elements not visible.
*/
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
</style>
