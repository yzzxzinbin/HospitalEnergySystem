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
          <el-input v-model="searchParams.roomNumber" placeholder="请输入房间号"></el-input>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input v-model="searchParams.floor" placeholder="请输入楼层"></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="searchParams.department" placeholder="请输入部门"></el-input>
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
        <el-table-column prop="description" label="描述" align="center"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="warning" icon="el-icon-edit" @click="handleEditRoom(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteRoom(scope.row)">删除</el-button>
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
      width="30%"
      @close="resetForm('roomForm')"
    >
      <el-form :model="roomForm" :rules="roomRules" ref="roomForm" label-width="80px">
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
          <el-input type="textarea" v-model="roomForm.description" placeholder="请输入房间描述"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRoomForm">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
// 实际项目中会从 src/api/room.js 导入 API
// import { getRooms, createRoom, updateRoom, deleteRoom } from '@/api/room';

export default {
  name: "RoomManagementView",
  data() {
    return {
      loading: false,
      roomList: [], // 房间列表数据
      searchParams: {
        roomNumber: "",
        floor: "",
        department: "",
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
        roomNumber: "",
        floor: "",
        department: "",
        description: "",
      },
      roomRules: {
        roomNumber: [
          { required: true, message: "请输入房间号", trigger: "blur" },
        ],
        floor: [{ required: true, message: "请输入楼层", trigger: "blur" }],
        department: [
          { required: true, message: "请输入部门", trigger: "blur" },
        ],
      },
      // 模拟数据
      mockRoomData: [
        { id: 1, roomNumber: '101', floor: '1F', department: '门诊部', description: '普通诊室' },
        { id: 2, roomNumber: '102', floor: '1F', department: '门诊部', description: '专家诊室' },
        { id: 3, roomNumber: '201', floor: '2F', department: '住院部', description: '标准病房' },
        { id: 4, roomNumber: '202', floor: '2F', department: '住院部', description: '高级病房' },
        { id: 5, roomNumber: '301', floor: '3F', department: '手术室', description: '1号手术室' },
      ]
    };
  },
  created() {
    this.fetchRoomList();
  },
  methods: {
    fetchRoomList() {
      this.loading = true;
      // 模拟API调用
      setTimeout(() => {
        let filteredData = this.mockRoomData.filter(item => {
          return (
            (this.searchParams.roomNumber ? item.roomNumber.includes(this.searchParams.roomNumber) : true) &&
            (this.searchParams.floor ? item.floor.includes(this.searchParams.floor) : true) &&
            (this.searchParams.department ? item.department.includes(this.searchParams.department) : true)
          );
        });
        this.pagination.total = filteredData.length;
        const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
        const end = start + this.pagination.pageSize;
        this.roomList = filteredData.slice(start, end);
        this.loading = false;
      }, 500);

      // 实际API调用示例:
      // getRooms({ ...this.searchParams, page: this.pagination.currentPage, size: this.pagination.pageSize })
      //   .then(response => {
      //     this.roomList = response.data.records; // 假设后端返回数据结构中列表在 records
      //     this.pagination.total = response.data.total; // 假设后端返回数据结构中总数在 total
      //     this.loading = false;
      //   })
      //   .catch(error => {
      //     this.$message.error("获取房间列表失败");
      //     this.loading = false;
      //   });
    },
    handleSearch() {
      this.pagination.currentPage = 1; // 查询时回到第一页
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
      this.roomForm = { id: null, roomNumber: "", floor: "", department: "", description: "" };
      this.dialogVisible = true;
    },
    handleEditRoom(row) {
      this.dialogTitle = "编辑房间";
      this.roomForm = { ...row }; // 浅拷贝，避免直接修改表格数据
      this.dialogVisible = true;
    },
    handleDeleteRoom(row) {
      this.$confirm(`确定删除房间 ${row.roomNumber} 吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 模拟API调用
          this.mockRoomData = this.mockRoomData.filter(item => item.id !== row.id);
          this.fetchRoomList(); // 重新加载数据
          this.$message({ type: "success", message: "删除成功!" });

          // 实际API调用示例:
          // deleteRoom(row.id).then(() => {
          //   this.$message({ type: "success", message: "删除成功!" });
          //   this.fetchRoomList(); // 重新获取列表
          // }).catch(error => {
          //   this.$message.error("删除失败");
          // });
        })
        .catch(() => {
          this.$message({ type: "info", message: "已取消删除" });
        });
    },
    submitRoomForm() {
      this.$refs.roomForm.validate((valid) => {
        if (valid) {
          // 模拟API调用
          if (this.roomForm.id) { // 编辑
            const index = this.mockRoomData.findIndex(item => item.id === this.roomForm.id);
            if (index !== -1) {
              this.mockRoomData.splice(index, 1, { ...this.roomForm });
            }
            this.$message({ type: "success", message: "更新成功!" });
          } else { // 新增
            this.roomForm.id = Date.now(); // 简单生成唯一ID
            this.mockRoomData.unshift({ ...this.roomForm });
            this.$message({ type: "success", message: "新增成功!" });
          }
          this.dialogVisible = false;
          this.fetchRoomList();

          // 实际API调用示例:
          // const apiCall = this.roomForm.id ? updateRoom(this.roomForm.id, this.roomForm) : createRoom(this.roomForm);
          // apiCall.then(() => {
          //   this.$message({ type: "success", message: this.roomForm.id ? "更新成功!" : "新增成功!" });
          //   this.dialogVisible = false;
          //   this.fetchRoomList();
          // }).catch(error => {
          //   this.$message.error(this.roomForm.id ? "更新失败" : "新增失败");
          // });
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
      this.roomForm = { id: null, roomNumber: "", floor: "", department: "", description: "" };
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
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
