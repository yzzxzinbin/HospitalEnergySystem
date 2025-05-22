<template>
  <div class="room-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>房间列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddRoom">新增房间</el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="房间号">
          <el-input v-model="searchParams.roomNumber" placeholder="请输入房间号" clearable></el-input>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input v-model="searchParams.floor" placeholder="请输入楼层" clearable></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="searchParams.department" placeholder="请输入部门" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 房间列表表格 -->
      <el-table
        :data="roomList"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="roomNumber" label="房间号" align="center"></el-table-column>
        <el-table-column prop="floor" label="楼层" align="center"></el-table-column>
        <el-table-column prop="department" label="部门" align="center"></el-table-column>
        <el-table-column prop="description" label="描述" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-edit"
              @click="handleEditRoom(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="handleDeleteRoom(scope.row)"
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

    <!-- 新增/编辑房间对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="40%" 
      @close="resetForm('roomForm')"
      :close-on-click-modal="false"
    >
      <el-form :model="roomForm" :rules="roomRules" ref="roomForm" label-width="80px">
        <el-form-item label="房间名称" prop="name">
          <el-input v-model="roomForm.name" placeholder="请输入房间名称"></el-input>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="roomForm.roomNumber" placeholder="请输入房间号"></el-input>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input v-model="roomForm.floor" placeholder="请输入楼层"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="roomForm.department" placeholder="请输入部门"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="roomForm.description" placeholder="请输入房间描述"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRoomForm" :loading="submitLoading">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { getRooms, createRoom, updateRoom, deleteRoom } from '@/api/room';

export default {
  name: "RoomManagementView",
  data() {
    return {
      loading: false,
      submitLoading: false,
      roomList: [], 
      searchParams: {
        roomNumber: "",
        floor: "",
        department: "",
        // Add other searchable fields if needed, e.g. from API spec
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      roomForm: {
        id: null,
        name: "", // Added name field
        roomNumber: "",
        floor: "",
        department: "",
        description: "",
      },
      roomRules: {
        name: [
          { required: true, message: "请输入房间名称", trigger: "blur" },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        roomNumber: [
          { required: true, message: "请输入房间号", trigger: "blur" },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        floor: [
          { required: true, message: "请输入楼层", trigger: "blur" },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        department: [
          { required: true, message: "请输入部门", trigger: "blur" },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '描述最多 200 个字符', trigger: 'blur' }
        ]
      },
    };
  },
  created() {
    this.fetchRoomList();
  },
  methods: {
    fetchRoomList() {
      this.loading = true;
      const params = {
        page: this.pagination.currentPage - 1, // API is 0-indexed
        size: this.pagination.pageSize,
        // sort: 'id,asc', // Default sort, can be dynamic if needed
        // Conditionally add search parameters if they are not empty
        ...(this.searchParams.roomNumber && { roomNumber: this.searchParams.roomNumber }),
        ...(this.searchParams.floor && { floor: this.searchParams.floor }),
        ...(this.searchParams.department && { department: this.searchParams.department }),
      };

      getRooms(params)
        .then(response => {
          // Assuming API returns { records: [], total: 0, currentPage: 0, pageSize: 0, totalPages: 0 }
          // Based on PageResponseDto in RoomController
          this.roomList = response.records; 
          this.pagination.total = response.total;
          // currentPage from backend is 0-indexed, frontend is 1-indexed
          // this.pagination.currentPage = response.currentPage + 1; 
          // No need to set currentPage here as it's driven by user interaction or reset in handleSearch
          this.loading = false;
        })
        .catch(error => {
          this.$message.error("获取房间列表失败: " + (error.message || '请检查网络或联系管理员'));
          this.loading = false;
        });
    },
    handleSearch() {
      this.pagination.currentPage = 1; 
      this.fetchRoomList();
    },
    resetSearch() {
      this.searchParams = {
        roomNumber: "",
        floor: "",
        department: "",
      };
      this.handleSearch();
    },
    handleAddRoom() {
      this.dialogTitle = "新增房间";
      this.roomForm = { id: null, name: "", roomNumber: "", floor: "", department: "", description: "" }; // Added name
      this.dialogVisible = true;
      if (this.$refs.roomForm) {
        this.$refs.roomForm.clearValidate();
      }
    },
    handleEditRoom(row) {
      this.dialogTitle = "编辑房间";
      this.roomForm = { ...row }; 
      this.dialogVisible = true;
       if (this.$refs.roomForm) {
        this.$refs.roomForm.clearValidate();
      }
    },
    handleDeleteRoom(row) {
      this.$confirm(`确定删除房间 ${row.roomNumber} 吗? 此操作不可恢复。`, "提示", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          deleteRoom(row.id).then(() => {
            this.$message({ type: "success", message: "删除成功!" });
            // If current page becomes empty after deletion, go to previous page
            if (this.roomList.length === 1 && this.pagination.currentPage > 1) {
                this.pagination.currentPage--;
            }
            this.fetchRoomList(); 
          }).catch(error => {
            this.$message.error("删除失败: " + (error.message || '请重试'));
          });
        })
        .catch(() => {
          this.$message({ type: "info", message: "已取消删除" });
        });
    },
    submitRoomForm() {
      this.$refs.roomForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          const apiCall = this.roomForm.id 
            ? updateRoom(this.roomForm.id, this.roomForm) 
            : createRoom(this.roomForm);
          
          apiCall.then(() => {
            this.$message({ type: "success", message: this.roomForm.id ? "更新成功!" : "新增成功!" });
            this.dialogVisible = false;
            this.fetchRoomList();
          }).catch(error => {
            const errorMsg = error.response && error.response.data && error.response.data.message 
                           ? error.response.data.message 
                           : (error.message || (this.roomForm.id ? '更新失败' : '新增失败'));
            this.$message.error(errorMsg);
          }).finally(() => {
            this.submitLoading = false;
          });
        } else {
          this.$message.error('请检查表单信息是否完整且正确！');
          return false;
        }
      });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
      this.roomForm = { id: null, name: "", roomNumber: "", floor: "", department: "", description: "" }; // Added name
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1; // Reset to first page when size changes
      this.fetchRoomList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchRoomList();
    },
  },
};
</script>

<style scoped>
.room-management-container {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
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
</style>
