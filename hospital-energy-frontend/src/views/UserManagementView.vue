<template>
  <div class="user-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>用户列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddUser">新增用户</el-button>
      </div>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="用户名">
          <el-input v-model="searchParams.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="searchParams.email" placeholder="请输入邮箱" clearable></el-input>
        </el-form-item>
        <!-- Role search can be added if API supports it or for client-side filtering -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户列表表格 -->
      <el-table
        :data="userList"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="用户名" align="center"></el-table-column>
        <el-table-column prop="email" label="邮箱" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="角色" align="center">
          <template slot-scope="scope">
            <el-tag size="small" v-for="roleItem in scope.row.role ? scope.row.role.split(',') : ['-']" :key="roleItem" style="margin-right: 5px;">{{ roleItem }}</el-tag>
          </template>
        </el-table-column>
        <!-- Timestamps can be added if available in API response -->
        <!-- <el-table-column prop="createdAt" label="创建时间" align="center" :formatter="formatDate"></el-table-column> -->
        <!-- <el-table-column prop="updatedAt" label="更新时间" align="center" :formatter="formatDate"></el-table-column> -->
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-edit"
              @click="handleEditUser(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="handleDeleteUser(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px; text-align: right;"
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
      width="50%"
      @close="resetForm('userForm')"
      :close-on-click-modal="false"
    >
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item v-if="!userForm.id || isChangingPassword" label="密码" prop="password">
          <el-input type="password" v-model="userForm.password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item v-if="!userForm.id || isChangingPassword" label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="userForm.confirmPassword" placeholder="请再次输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-input v-model="userForm.role" placeholder="请输入角色，多个角色用逗号分隔 (例如 ADMIN,USER)"></el-input>
        </el-form-item>
        <el-form-item v-if="userForm.id">
            <el-checkbox v-model="isChangingPassword">修改密码</el-checkbox>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitLoading">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUsers, createUser, updateUser, deleteUser, getUserById } from '@/api/user';
// import { parseTime } from '@/utils'; // If using date formatting

export default {
  name: "UserManagementView",
  data() {
    const validatePassword = (rule, value, callback) => {
      if (this.userForm.id && !this.isChangingPassword) { // Editing user, not changing password
        callback();
        return;
      }
      if (!value) {
        callback(new Error('请输入密码'));
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'));
      } else {
        if (this.userForm.confirmPassword) {
          this.$refs.userForm.validateField('confirmPassword');
        }
        callback();
      }
    };
    const validateConfirmPassword = (rule, value, callback) => {
      if (this.userForm.id && !this.isChangingPassword) { // Editing user, not changing password
        callback();
        return;
      }
      if (!value) {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.userForm.password) {
        callback(new Error('两次输入密码不一致!'));
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
        // role: "" // Add if needed
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
        password: "",
        confirmPassword: "",
        role: "", // e.g., "USER" or "ADMIN,USER"
      },
      isChangingPassword: false, // For edit mode
      userRules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
        ],
        password: [
          { validator: validatePassword, trigger: "blur" }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: "blur" }
        ],
        role: [
          // { required: true, message: "请输入角色", trigger: "blur" } // Role can be optional or have default
        ],
      },
    };
  },
  created() {
    this.fetchUserList();
  },
  methods: {
    // formatDate(row, column, cellValue) {
    //   if (!cellValue) return '';
    //   return parseTime(cellValue, '{y}-{m}-{d} {h}:{i}');
    // },
    async fetchUserList() {
      this.loading = true;
      try {
        const params = {
          page: this.pagination.currentPage - 1,
          size: this.pagination.pageSize,
          sort: 'id,asc',
          username: this.searchParams.username || null,
          email: this.searchParams.email || null,
          // role: this.searchParams.role || null, // Add if API supports role filtering
        };
        // Remove null or empty params
        Object.keys(params).forEach(key => {
          if (params[key] === null || params[key] === '') {
            delete params[key];
          }
        });

        const response = await getUsers(params);

        if (Array.isArray(response)) {
          // Handle direct array response
          this.userList = response;
          this.pagination.total = response.length; 
          // Note: Client-side pagination might be needed if the API always returns all users
          // and you still want to paginate on the frontend.
          // For now, this assumes the API might return a subset based on backend logic,
          // or if not, it displays all returned users.
          // If true client-side pagination is needed for a large flat list,
          // the slicing logic would be applied here.
        } else if (response && (response.content || response.records)) {
          // Handle PageResponseDto structure
          this.userList = response.content || response.records || [];
          this.pagination.total = response.totalElements || response.total || 0;
        } else {
          this.$message.error("获取用户列表失败：响应格式不正确。");
          this.userList = [];
          this.pagination.total = 0;
        }
      } catch (error) {
        this.$message.error("获取用户列表失败: " + (error.response?.data?.message || error.message));
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
        // role: ""
      };
      this.handleSearch();
    },
    handleAddUser() {
      this.dialogTitle = "新增用户";
      this.isChangingPassword = false; // Not relevant for new user, but password fields will show
      this.resetForm('userForm'); // Resets form and sets id to null
      this.dialogVisible = true;
    },
    async handleEditUser(row) {
      this.dialogTitle = "编辑用户";
      this.isChangingPassword = false; // User must check box to change password
      try {
        const userData = await getUserById(row.id);
        this.userForm = {
          id: userData.id,
          username: userData.username,
          email: userData.email,
          role: userData.role || "",
          password: "", // Clear password fields for edit
          confirmPassword: "",
        };
        this.dialogVisible = true;
      } catch (error) {
        this.$message.error("获取用户信息失败: " + (error.response?.data?.message || error.message));
      }
    },
    handleDeleteUser(row) {
      this.$confirm(`确定删除用户 "${row.username}"? 此操作不可恢复。`, "提示", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await deleteUser(row.id);
            this.$message.success("删除成功!");
            if (this.userList.length === 1 && this.pagination.currentPage > 1) {
                this.pagination.currentPage--;
            }
            this.fetchUserList();
          } catch (error) {
            this.$message.error("删除失败: " + (error.response?.data?.message || error.message));
          }
        })
        .catch(() => {
          this.$message.info("已取消删除");
        });
    },
    submitUserForm() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true;
          const payload = {
            username: this.userForm.username,
            email: this.userForm.email,
            role: this.userForm.role || null, // Send null if empty, or backend handles empty string
          };

          if (this.userForm.id) { // Editing
            payload.id = this.userForm.id;
            if (this.isChangingPassword && this.userForm.password) {
              payload.password = this.userForm.password;
            }
          } else { // Creating
            payload.password = this.userForm.password;
          }
          
          try {
            if (payload.id) {
              await updateUser(payload.id, payload);
              this.$message.success("用户更新成功!");
            } else {
              await createUser(payload);
              this.$message.success("用户新增成功!");
            }
            this.dialogVisible = false;
            this.fetchUserList();
          } catch (error) {
             const errorMsg = error.response?.data?.message || error.message || (payload.id ? '更新失败' : '新增失败');
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
      this.userForm = {
        id: null,
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        role: "",
      };
      this.isChangingPassword = false;
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
        this.$refs[formName].clearValidate();
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1; // Reset to first page
      this.fetchUserList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchUserList();
    },
  },
};
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
  min-height: calc(100vh - 130px); /* Adjust based on your layout's header/footer height */
}
.mb-20 {
  margin-bottom: 20px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
.dialog-footer {
  text-align: right;
}
</style>
