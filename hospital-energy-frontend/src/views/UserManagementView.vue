<template>
  <div class="user-management-container">
    <el-card class="box-card page-card" shadow="hover">
      <div slot="header" class="clearfix card-header">
        <span>用户列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddUser" size="small">新增用户</el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="search-form mb-20">
        <el-form-item label="用户名">
          <el-input v-model="searchParams.username" placeholder="请输入用户名" clearable size="small"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="searchParams.email" placeholder="请输入邮箱" clearable size="small"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="searchParams.role" placeholder="请输入角色" clearable size="small"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" size="small">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch" size="small">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户列表表格 -->
      <el-table
        :data="userList"
        v-loading="loading"
        style="width: 100%;"
        border
        stripe
        highlight-current-row
        height="calc(100vh - 320px)"
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="用户名" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="role" label="角色" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              plain
              icon="el-icon-edit"
              @click="handleEditUser(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              plain
              icon="el-icon-delete"
              @click="handleDeleteUser(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        class="pagination-container"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm('userForm')"
      :close-on-click-modal="false"
      class="modern-dialog"
      append-to-body
      top="10vh"
    >
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="80px" class="dialog-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input type="password" v-model="userForm.password" placeholder="请输入密码 (新增时必填)"></el-input>
        </el-form-item>
         <el-form-item label="新密码" prop="newPassword" v-if="userForm.id">
          <el-input type="password" v-model="userForm.newPassword" placeholder="留空则不修改密码"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <!-- 简单文本输入，未来可改为下拉选择预定义角色 -->
          <el-input v-model="userForm.role" placeholder="请输入角色 (例如: ROLE_USER,ROLE_ADMIN)"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitLoading" size="small">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user';

export default {
  name: "UserManagementView",
  data() {
    const validatePassword = (rule, value, callback) => {
      if (!this.userForm.id && !value) { // Required for new user
        callback(new Error('请输入密码'));
      } else if (value && value.length < 6) {
        callback(new Error('密码长度不能少于6位'));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      submitLoading: false,
      userList: [],
      searchParams: {
        username: "",
        email: "",
        role: "",
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      userForm: {
        id: null,
        username: "",
        email: "",
        password: "", // For new user
        newPassword: "", // For updating existing user's password
        role: "",
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        password: [ // Only for new user creation
          { validator: validatePassword, trigger: 'blur' }
        ],
        newPassword: [ // Only for editing user
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请输入角色', trigger: 'blur' }
        ],
      }
    };
  },
  created() {
    this.fetchUserList();
  },
  methods: {
    async fetchUserList() {
      this.loading = true;
      try {
        const queryParams = {
          page: this.pagination.currentPage - 1,
          size: this.pagination.pageSize,
          sort: 'id,asc',
          // Add search params if they are not empty
          // Backend API must support these query parameters for filtering to work.
          // If search doesn't filter, check backend implementation of GET /api/users.
          ...(this.searchParams.username && { username: this.searchParams.username }),
          ...(this.searchParams.email && { email: this.searchParams.email }),
          ...(this.searchParams.role && { role: this.searchParams.role }),
        };
        // Remove null or empty string parameters
        for (const key in queryParams) {
          if (queryParams[key] === null || queryParams[key] === '') {
            delete queryParams[key];
          }
        }
        const response = await getUsers(queryParams);
        this.userList = response.records || [];
        this.pagination.total = response.total || 0;
      } catch (error) {
        this.$message.error('获取用户列表失败: ' + (error.message || '请稍后再试'));
        this.userList = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchUserList();
    },
    resetSearch() {
      this.searchParams = {
        username: "",
        email: "",
        role: "",
      };
      this.handleSearch();
    },
    handleAddUser() {
      this.dialogTitle = "新增用户";
      this.resetForm('userForm');
      this.userForm.id = null; // Ensure ID is null for add
      this.dialogVisible = true;
    },
    handleEditUser(row) {
      this.dialogTitle = "编辑用户";
      this.userForm = { ...row, password: '', newPassword: '' }; // Copy row data, clear passwords
      this.dialogVisible = true;
    },
    handleDeleteUser(row) {
      if (row.username === 'admin') {
        this.$message.warning('不能删除admin用户');
        return;
      }
      this.$confirm(`确定删除用户 "${row.username}"?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        try {
          await deleteUser(row.id);
          this.$message.success("删除成功!");
          if (this.userList.length === 1 && this.pagination.currentPage > 1) {
            this.pagination.currentPage--;
          }
          this.fetchUserList();
        } catch (error) {
          this.$message.error("删除失败: " + (error.message || '请重试'));
        }
      }).catch(() => {
        this.$message.info("已取消删除");
      });
    },
    submitUserForm() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          const userData = { ...this.userForm };

          if (!userData.id) { // Creating new user
            if (!userData.password) {
              this.$message.error('新增用户时密码不能为空');
              this.submitLoading = false;
              return;
            }
          } else { // Updating existing user
            // If newPassword is set, use it as password, otherwise remove password field
            if (userData.newPassword && userData.newPassword.trim() !== '') {
              userData.password = userData.newPassword;
            } else {
              delete userData.password; // Don't send empty password for update if not changing
            }
          }
          delete userData.newPassword; // Not part of UserDto for backend

          try {
            if (userData.id) { // Update
              await updateUser(userData.id, userData);
              this.$message.success("更新成功!");
            } else { // Create
              await createUser(userData);
              this.$message.success("新增成功!");
            }
            this.dialogVisible = false;
            this.fetchUserList();
          } catch (error) {
             const errorMsg = error.response?.data?.message || error.message || (userData.id ? '更新失败' : '新增失败');
            this.$message.error(errorMsg);
          } finally {
            this.submitLoading = false;
          }
        } else {
          this.$message.error('请检查表单信息是否完整且正确！');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.userForm = { // Reset to initial state
        id: null,
        username: "",
        email: "",
        password: "",
        newPassword: "",
        role: "",
      };
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
        this.$refs[formName].clearValidate();
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.fetchUserList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchUserList();
    },
  }
};
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.page-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form .el-form-item {
  margin-bottom: 10px;
}

.mb-20 {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* Modern Dialog Styles */
.modern-dialog >>> .el-dialog {
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,.1);
}

.modern-dialog >>> .el-dialog__header {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f5f7fa;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modern-dialog >>> .el-dialog__title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.modern-dialog >>> .el-dialog__close {
  font-size: 18px;
  font-weight: bold;
}
.modern-dialog >>> .el-dialog__close:hover {
  color: #409EFF;
}


.modern-dialog >>> .el-dialog__body {
  padding: 25px 20px;
}

.dialog-form .el-input__inner,
.dialog-form .el-textarea__inner {
  border-radius: 4px;
}
.dialog-form .el-input__inner:focus,
.dialog-form .el-textarea__inner:focus {
  border-color: #409EFF;
}


.modern-dialog >>> .el-dialog__footer {
  padding: 10px 20px 15px;
  border-top: 1px solid #ebeef5;
  text-align: right;
}

.modern-dialog .dialog-footer .el-button {
  padding: 8px 15px;
  border-radius: 4px;
}
.modern-dialog .dialog-footer .el-button--primary {
    background-color: #409EFF;
    border-color: #409EFF;
}
.modern-dialog .dialog-footer .el-button--primary:hover {
    background-color: #66b1ff;
    border-color: #66b1ff;
}
.modern-dialog .dialog-footer .el-button:hover {
  opacity: 0.8;
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
