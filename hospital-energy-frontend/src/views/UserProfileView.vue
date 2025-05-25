<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 用户信息卡片 -->
      <el-col :span="10" :xs="24">
        <el-card class="box-card profile-card" shadow="hover">
          <div slot="header" class="clearfix card-header">
            <span><i class="el-icon-user-solid header-icon"></i>个人信息</span>
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
                <el-tag size="small" v-for="role in currentUser.roles ? currentUser.roles.split(',') : ['未知']" :key="role" style="margin-right: 5px;">{{ role }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else description="无法加载用户信息"></el-empty>
        </el-card>
      </el-col>

      <!-- 修改密码卡片 -->
      <el-col :span="14" :xs="24">
        <el-card class="box-card password-card" shadow="hover">
          <div slot="header" class="clearfix card-header">
            <span><i class="el-icon-lock header-icon"></i>修改密码</span>
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
              <el-button @click="resetPasswordForm('passwordForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getUserInfo } from '@/utils/auth';
import { changePassword } from '@/api/user'; // Import the new function

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
    loadUserProfile() {
      const userInfo = getUserInfo(); // Get from localStorage/sessionStorage via auth utils
      if (userInfo && userInfo.username) {
        // The UserDto from backend has 'role', not 'roles'.
        // Frontend auth.js might store it as 'roles' if it processes the login response.
        // Let's assume getUserInfo() returns an object with { id, username, email, roles (string) }
        this.currentUser = {
            id: userInfo.id,
            username: userInfo.username,
            email: userInfo.email, // Assuming email is also stored by setUserInfo
            roles: userInfo.roles || 'ROLE_USER' // Fallback if roles not present
        };
      } else {
        this.$message.error("无法获取用户信息，请重新登录。");
        this.$router.push('/login');
      }
    },
    async handleChangePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.passwordLoading = true;
          try {
            // Call the dedicated API for changing password
            await changePassword({
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword,
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

.card-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}
.header-icon {
  margin-right: 8px;
  color: #409EFF;
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

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
</style>
