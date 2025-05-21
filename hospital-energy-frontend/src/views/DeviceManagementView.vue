<template>
  <div class="device-management-container">
    <el-card class="box-card">
      <el-form :inline="true" :model="searchParams" class="demo-form-inline">
        <el-form-item label="设备名称">
          <el-input v-model="searchParams.name" placeholder="设备名称"></el-input>
        </el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="searchParams.roomId" placeholder="选择房间" clearable>
            <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + ' (' + room.department + ')'" :value="room.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="searchParams.status" placeholder="设备状态" clearable>
            <el-option label="在线" value="online"></el-option>
            <el-option label="离线" value="offline"></el-option>
            <el-option label="故障" value="faulty"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="el-icon-search">查询</el-button>
          <el-button @click="resetSearch" icon="el-icon-refresh">重置</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" icon="el-icon-plus" @click="handleAddDevice" style="margin-bottom: 20px;">新增设备</el-button>

      <el-table
        v-loading="loading"
        :data="deviceList"
        style="width: 100%"
        border
        stripe>
        <el-table-column prop="name" label="设备名称" width="180"></el-table-column>
        <el-table-column label="所属房间" width="200">
          <template slot-scope="scope">
            {{ scope.row.room ? scope.row.room.roomNumber + ' (' + scope.row.room.department + ')' : 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column label="设备模板" width="220">
          <template slot-scope="scope">
            {{ scope.row.deviceTemplate ? scope.row.deviceTemplate.manufacturer + ' - ' + scope.row.deviceTemplate.model : 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column prop="installationDate" label="安装日期" width="150">
            <template slot-scope="scope">
                {{ scope.row.installationDate | formatDate }}
            </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastOnlineTime" label="最后上线时间" width="180">
            <template slot-scope="scope">
                {{ scope.row.lastOnlineTime | formatDateTime }}
            </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleViewDevice(scope.row)">详情</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditDevice(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteDevice(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 新增/编辑设备对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%" @close="resetForm('deviceForm')">
      <el-form :model="deviceForm" :rules="deviceRules" ref="deviceForm" label-width="120px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="deviceForm.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="所属房间" prop="roomId">
          <el-select v-model="deviceForm.roomId" placeholder="请选择所属房间" style="width: 100%;">
            <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + ' (' + room.department + ')'" :value="room.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备模板" prop="templateId">
          <el-select v-model="deviceForm.templateId" placeholder="请选择设备模板" style="width: 100%;">
            <el-option v-for="template in templateOptions" :key="template.id" :label="template.manufacturer + ' - ' + template.model + ' (' + template.category + ')'" :value="template.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="安装日期" prop="installationDate">
          <el-date-picker
            v-model="deviceForm.installationDate"
            type="date"
            placeholder="选择安装日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="deviceForm.status" placeholder="请选择设备状态" style="width: 100%;">
            <el-option label="在线" value="online"></el-option>
            <el-option label="离线" value="offline"></el-option>
            <el-option label="故障" value="faulty"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input type="textarea" v-model="deviceForm.remarks" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDeviceForm" :loading="formLoading">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 设备详情抽屉 -->
    <el-drawer
      :title="currentDevice.name ? '设备详情: ' + currentDevice.name : '设备详情'"
      :visible.sync="drawerVisible"
      direction="rtl"
      size="40%">
      <div v-if="currentDevice.id" style="padding: 20px;">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="设备ID">{{ currentDevice.id }}</el-descriptions-item>
          <el-descriptions-item label="设备名称">{{ currentDevice.name }}</el-descriptions-item>
          <el-descriptions-item label="所属房间">
            {{ currentDevice.room ? currentDevice.room.roomNumber + ' (' + currentDevice.room.department + ')' : 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="设备模板">
            {{ currentDevice.deviceTemplate ? currentDevice.deviceTemplate.manufacturer + ' - ' + currentDevice.deviceTemplate.model + ' (' + currentDevice.deviceTemplate.category + ')' : 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="安装日期">{{ currentDevice.installationDate | formatDate }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusTagType(currentDevice.status)">{{ formatStatus(currentDevice.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后上线时间">{{ currentDevice.lastOnlineTime | formatDateTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentDevice.remarks || '无' }}</el-descriptions-item>
        </el-descriptions>
        <!-- 可以在这里添加更多设备相关信息，如实时数据、控制按钮等 -->
      </div>
      <div v-else style="padding: 20px; text-align: center;">
        <p>暂无设备详情</p>
      </div>
    </el-drawer>

  </div>
</template>

<script>
import { getDevices, createDevice, updateDevice, deleteDevice, getDeviceById } from '@/api/device';
import { getRooms } from '@/api/room';
import { getDeviceTemplates } from '@/api/deviceTemplate';
import { parseTime } from '@/utils'; // 假设你有一个日期格式化工具

export default {
  name: "DeviceManagementView",
  filters: {
    formatDate(time) {
      if (!time) return 'N/A';
      return parseTime(time, '{y}-{m}-{d}');
    },
    formatDateTime(time) {
      if (!time) return 'N/A';
      return parseTime(time, '{y}-{m}-{d} {h}:{i}:{s}');
    }
  },
  data() {
    return {
      loading: false,
      formLoading: false, // 用于表单提交按钮的loading状态
      deviceList: [],
      roomOptions: [],
      templateOptions: [],
      searchParams: {
        name: "",
        roomId: null,
        status: "",
        // API 可能需要的其他搜索参数，例如 deviceTemplateId
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      deviceForm: {
        id: null,
        name: "",
        roomId: null, // 存储房间ID
        templateId: null, // 存储设备模板ID
        installationDate: "",
        status: "online", // 默认值
        remarks: "",
      },
      deviceRules: {
        name: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
        roomId: [{ required: true, message: "请选择所属房间", trigger: "change" }],
        templateId: [{ required: true, message: "请选择设备模板", trigger: "change" }],
        installationDate: [{ required: true, message: "请选择安装日期", trigger: "change" }],
        status: [{ required: true, message: "请选择设备状态", trigger: "change" }],
      },
      drawerVisible: false,
      currentDevice: {}, // 用于存储当前查看或编辑的设备完整信息
    };
  },
  created() {
    this.fetchInitialData();
  },
  methods: {
    fetchInitialData() {
      this.fetchDeviceList();
      this.fetchRoomOptions();
      this.fetchTemplateOptions();
    },
    async fetchDeviceList() {
      this.loading = true;
      try {
        const params = {
          page: this.pagination.currentPage,
          size: this.pagination.pageSize,
          name: this.searchParams.name || undefined, // 后端可能不需要空字符串
          roomId: this.searchParams.roomId || undefined,
          status: this.searchParams.status || undefined,
          // 如果API支持按模板ID搜索，可以添加 templateId: this.searchParams.templateId || undefined
        };
        // 清理未定义的参数，避免发送空的查询参数
        Object.keys(params).forEach(key => params[key] === undefined && delete params[key]);

        const response = await getDevices(params);
        // 假设API返回的数据结构是 { data: { records: [], total: 0 } } 或类似分页结构
        // 请根据你的API实际返回结构调整
        if (response && response.data && Array.isArray(response.data.records)) {
          this.deviceList = response.data.records;
          this.pagination.total = response.data.total || response.data.records.length; // 兼容不同分页返回
        } else if (response && Array.isArray(response.data)) { // 如果直接返回数组
            this.deviceList = response.data;
            this.pagination.total = response.data.length; // 可能需要后端支持分页参数
        } else {
          this.deviceList = [];
          this.pagination.total = 0;
          this.$message.error('获取设备列表失败: 数据格式不正确');
        }
      } catch (error) {
        console.error("Error fetching device list:", error);
        this.$message.error(`获取设备列表失败: ${error.message || '请稍后重试'}`);
        this.deviceList = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },
    async fetchRoomOptions() {
      try {
        // 获取所有房间用于下拉，通常不分页或用一个很大的size
        const response = await getRooms({ pageSize: -1 }); // 假设 pageSize: -1 获取所有
        if (response && response.data && Array.isArray(response.data.records)) {
            this.roomOptions = response.data.records;
        } else if (response && Array.isArray(response.data)) {
            this.roomOptions = response.data;
        } else {
            this.roomOptions = [];
            this.$message.error('获取房间选项失败');
        }
      } catch (error) {
        console.error("Error fetching room options:", error);
        this.$message.error(`获取房间选项失败: ${error.message}`);
        this.roomOptions = [];
      }
    },
    async fetchTemplateOptions() {
      try {
        const response = await getDeviceTemplates({ pageSize: -1 }); // 假设 pageSize: -1 获取所有
         if (response && response.data && Array.isArray(response.data.records)) {
            this.templateOptions = response.data.records;
        } else if (response && Array.isArray(response.data)) {
            this.templateOptions = response.data;
        } else {
            this.templateOptions = [];
            this.$message.error('获取设备模板选项失败');
        }
      } catch (error) {
        console.error("Error fetching template options:", error);
        this.$message.error(`获取设备模板选项失败: ${error.message}`);
        this.templateOptions = [];
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchDeviceList();
    },
    resetSearch() {
      this.searchParams = { name: "", roomId: null, status: "" };
      this.handleSearch();
    },
    handleAddDevice() {
      this.dialogTitle = "新增设备";
      // 重置表单，确保没有上次编辑的残留数据
      this.deviceForm = {
        id: null,
        name: "",
        roomId: null,
        templateId: null,
        installationDate: parseTime(new Date(), '{y}-{m}-{d}'), // 默认当天
        status: "online",
        remarks: "",
      };
      if (this.$refs.deviceForm) {
        this.$refs.deviceForm.clearValidate(); // 清除校验状态
      }
      this.dialogVisible = true;
    },
    async handleEditDevice(row) {
      this.dialogTitle = "编辑设备";
      // 为了获取最新的、完整的设备信息，可以考虑从API获取
      // 如果列表信息已经足够，可以直接使用 row
      // this.currentDevice = { ...row }; // 浅拷贝一份数据到表单
      // this.deviceForm = { ...row };
      try {
        const response = await getDeviceById(row.id);
        if (response && response.data) {
          this.deviceForm = { ...response.data };
          // API返回的 deviceTemplate 和 room 可能是对象，表单需要的是ID
          this.deviceForm.roomId = response.data.room ? response.data.room.id : null;
          this.deviceForm.templateId = response.data.deviceTemplate ? response.data.deviceTemplate.id : null;
          this.dialogVisible = true;
        } else {
          this.$message.error('获取设备详情失败');
        }
      } catch (error) {
        this.$message.error(`获取设备详情失败: ${error.message}`);
      }
    },
    async handleViewDevice(row) {
        this.drawerVisible = true;
        this.currentDevice = {}; // 先清空，显示加载状态或避免旧数据闪烁
        try {
            const response = await getDeviceById(row.id);
            if (response && response.data) {
                this.currentDevice = response.data;
            } else {
                this.$message.error('获取设备详情失败');
                this.currentDevice = { name: row.name }; // 至少显示个名字
            }
        } catch (error) {
            console.error("Error fetching device details for view:", error);
            this.$message.error(`获取设备详情失败: ${error.message}`);
            this.currentDevice = { name: row.name };
        }
    },
    handleDeleteDevice(row) {
      this.$confirm(`确定删除设备 ${row.name} 吗? 此操作不可恢复。`, "警告", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        try {
          await deleteDevice(row.id);
          this.$message({ type: "success", message: "删除成功!" });
          this.fetchDeviceList(); // 刷新列表
        } catch (error) {
          console.error("Error deleting device:", error);
          this.$message.error(`删除失败: ${error.message || '请稍后重试'}`);
        }
      }).catch(() => {
        this.$message({ type: "info", message: "已取消删除" });
      });
    },
    submitDeviceForm() {
      this.$refs.deviceForm.validate(async (valid) => {
        if (valid) {
          this.formLoading = true;
          try {
            const payload = { ...this.deviceForm };
            // 确保 roomId 和 templateId 是存在的，如果它们是对象，则取其 id
            // (在 handleEdit 时已处理，此处为保险)
            if (payload.roomId && typeof payload.roomId === 'object') {
                payload.roomId = payload.roomId.id;
            }
            if (payload.templateId && typeof payload.templateId === 'object') {
                payload.templateId = payload.templateId.id;
            }

            if (this.deviceForm.id) { // 编辑
              await updateDevice(this.deviceForm.id, payload);
              this.$message({ type: "success", message: "更新成功!" });
            } else { // 新增
              await createDevice(payload);
              this.$message({ type: "success", message: "新增成功!" });
            }
            this.dialogVisible = false;
            this.fetchDeviceList(); // 成功后刷新列表
          } catch (error) {
            console.error("Error submitting device form:", error);
            // API的错误信息通常在 error.response.data.message 或 error.message
            const errMsg = error.response?.data?.message || error.message || "操作失败，请稍后重试";
            this.$message.error(errMsg);
          } finally {
            this.formLoading = false;
          }
        } else {
          this.$message.error("表单校验失败，请检查输入！");
          return false;
        }
      });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields(); // 重置表单项的校验状态和值
      }
      // 确保表单数据对象也被重置为初始状态，特别是对于非表单项绑定的属性
      this.deviceForm = {
        id: null,
        name: "",
        roomId: null,
        templateId: null,
        installationDate: parseTime(new Date(), '{y}-{m}-{d}'),
        status: "online",
        remarks: "",
      };
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1; // 页码大小改变时，回到第一页
      this.fetchDeviceList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchDeviceList();
    },
    statusTagType(status) {
      if (status === 'online') return 'success';
      if (status === 'offline') return 'info';
      if (status === 'faulty') return 'danger';
      return 'primary'; // 默认或未知状态
    },
    formatStatus(status) {
      if (status === 'online') return '在线';
      if (status === 'offline') return '离线';
      if (status === 'faulty') return '故障';
      return status || '未知'; // 直接返回状态值如果未匹配
    }
  },
};
</script>

<style scoped>
.device-management-container {
  padding: 20px;
}
.box-card {
  min-height: calc(100vh - 90px); /* 调整以适应你的布局 */
}
.dialog-footer {
  text-align: right;
}
.el-form-item {
    margin-bottom: 22px;
}
.el-select, .el-date-picker {
    width: 100%;
}
</style>
