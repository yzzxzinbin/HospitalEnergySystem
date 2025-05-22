<template>
  <div class="device-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备列表</span>
      </div>
      <el-form :inline="true" :model="searchParams" class="demo-form-inline" @submit.native.prevent>
        <el-form-item label="设备名称">
          <el-input v-model="searchParams.name" placeholder="设备名称" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="searchParams.roomId" placeholder="选择房间" clearable filterable>
            <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + (room.department ? ' (' + room.department + ')' : '')" :value="room.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备模板">
          <el-select v-model="searchParams.deviceTemplateId" placeholder="选择模板" clearable filterable>
            <el-option v-for="template in templateOptions" :key="template.id" :label="template.templateName || (template.manufacturer + ' - ' + template.modelIdentifier)" :value="template.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="searchParams.status" placeholder="设备状态" clearable>
            <el-option label="在线" value="ONLINE"></el-option>
            <el-option label="离线" value="OFFLINE"></el-option>
            <el-option label="故障" value="FAULTY"></el-option>
            <el-option label="维护中" value="MAINTENANCE"></el-option>
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
        <el-table-column prop="name" label="设备名称" width="180" show-overflow-tooltip></el-table-column>
        <el-table-column label="所属房间" width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.room ? (scope.row.room.roomNumber + (scope.row.room.department ? ' (' + scope.row.room.department + ')' : '')) : 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column label="设备模板" width="220" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.deviceTemplate ? (scope.row.deviceTemplate.templateName || (scope.row.deviceTemplate.manufacturer + ' - ' + scope.row.deviceTemplate.modelIdentifier)) : 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column prop="installationDate" label="安装日期" width="120" align="center">
            <template slot-scope="scope">
                {{ scope.row.installationDate | formatDate }}
            </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastOnlineTime" label="最后上线时间" width="160" align="center">
            <template slot-scope="scope">
                {{ scope.row.lastOnlineTime | formatDateTime }}
            </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="130" show-overflow-tooltip></el-table-column>
        <el-table-column prop="remarks" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="230" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="mini" icon="el-icon-view" @click="handleViewDevice(scope.row)">详情</el-button>
            <el-button type="text" size="mini" icon="el-icon-edit" @click="handleEditDevice(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" style="color: #F56C6C;" @click="handleDeleteDevice(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 20px; text-align: right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="searchParams.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size.sync="searchParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>

    <!-- 新增/编辑设备对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="60%" @close="resetForm('deviceForm')" :close-on-click-modal="false">
      <el-form :model="deviceForm" :rules="deviceRules" ref="deviceForm" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="deviceForm.name" placeholder="请输入设备名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属房间" prop="roomId">
              <el-select v-model="deviceForm.roomId" placeholder="请选择所属房间" filterable style="width: 100%;">
                <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + (room.department ? ' (' + room.department + ')' : '')" :value="room.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备模板" prop="deviceTemplateId">
              <el-select v-model="deviceForm.deviceTemplateId" placeholder="请选择设备模板" filterable style="width: 100%;">
                <el-option v-for="template in templateOptions" :key="template.id" :label="template.templateName || (template.manufacturer + ' - ' + template.modelIdentifier)" :value="template.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安装日期" prop="installationDate">
              <el-date-picker v-model="deviceForm.installationDate" type="date" placeholder="选择安装日期" style="width: 100%;" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="deviceForm.status" placeholder="请选择设备状态" style="width: 100%;">
                <el-option label="在线" value="ONLINE"></el-option>
                <el-option label="离线" value="OFFLINE"></el-option>
                <el-option label="故障" value="FAULTY"></el-option>
                <el-option label="维护中" value="MAINTENANCE"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model="deviceForm.ipAddress" placeholder="请输入IP地址"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="MAC地址" prop="macAddress">
              <el-input v-model="deviceForm.macAddress" placeholder="请输入MAC地址"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="固件版本" prop="firmwareVersion">
              <el-input v-model="deviceForm.firmwareVersion" placeholder="请输入固件版本"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="硬件版本" prop="hardwareVersion">
              <el-input v-model="deviceForm.hardwareVersion" placeholder="请输入硬件版本"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
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
      size="50%"
      destroy-on-close>
      <div v-if="currentDevice.id" style="padding: 20px;">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备ID">{{ currentDevice.id }}</el-descriptions-item>
          <el-descriptions-item label="设备名称">{{ currentDevice.name }}</el-descriptions-item>
          <el-descriptions-item label="所属房间">
            {{ currentDevice.room ? (currentDevice.room.roomNumber + (currentDevice.room.department ? ' (' + currentDevice.room.department + ')' : '')) : 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="房间位置">
            {{ currentDevice.room ? currentDevice.room.location : 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="设备模板">
            {{ currentDevice.deviceTemplate ? (currentDevice.deviceTemplate.templateName || (currentDevice.deviceTemplate.manufacturer + ' - ' + currentDevice.deviceTemplate.modelIdentifier)) : 'N/A' }}
          </el-descriptions-item>
           <el-descriptions-item label="模板型号">
            {{ currentDevice.deviceTemplate ? currentDevice.deviceTemplate.modelIdentifier : 'N/A' }}
          </el-descriptions-item>
          <el-descriptions-item label="安装日期">{{ currentDevice.installationDate | formatDate }}</el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <el-tag :type="statusTagType(currentDevice.status)" size="small">{{ formatStatus(currentDevice.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后上线时间">{{ currentDevice.lastOnlineTime | formatDateTime }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentDevice.ipAddress || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="MAC地址">{{ currentDevice.macAddress || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="固件版本">{{ currentDevice.firmwareVersion || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="硬件版本">{{ currentDevice.hardwareVersion || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentDevice.createdAt | formatDateTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentDevice.updatedAt | formatDateTime }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" border style="margin-top: 20px;">
            <el-descriptions-item label="备注">{{ currentDevice.remarks || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentDevice.deviceTemplate && currentDevice.deviceTemplate.specificationsJson" style="margin-top: 20px;">
            <h4>模板规格参数:</h4>
            <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px;">{{ currentDevice.deviceTemplate.specificationsJson }}</pre>
        </div>

      </div>
      <div v-else style="padding: 20px; text-align: center;">
        <p>暂无设备详情或加载中...</p>
      </div>
    </el-drawer>

  </div>
</template>

<script>
import { getDevices, createDevice, updateDevice, deleteDevice, getDeviceById } from '@/api/device';
import { getRooms } from '@/api/room'; // Assuming page and size can be passed
import { getDeviceTemplates } from '@/api/deviceTemplate'; // Assuming page and size can be passed
import { parseTime } from '@/utils';

export default {
  name: "DeviceManagementView",
  filters: {
    formatDate(time) {
      if (!time) return '';
      return parseTime(time, '{y}-{m}-{d}');
    },
    formatDateTime(time) {
      if (!time) return '';
      return parseTime(time, '{y}-{m}-{d} {h}:{i}:{s}');
    }
  },
  data() {
    // IP地址校验
    const validateIpAddress = (rule, value, callback) => {
      if (value && !/^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/.test(value)) {
        callback(new Error('请输入有效的IP地址'));
      } else {
        callback();
      }
    };
    // MAC地址校验
    const validateMacAddress = (rule, value, callback) => {
      if (value && !/^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/.test(value)) {
        callback(new Error('请输入有效的MAC地址 (例: 00:1A:2B:3C:4D:5E)'));
      } else {
        callback();
      }
    };

    return {
      loading: false,
      formLoading: false,
      deviceList: [],
      roomOptions: [],
      templateOptions: [],
      searchParams: {
        name: "",
        roomId: null,
        deviceTemplateId: null,
        status: "",
        page: 1,
        size: 10,
      },
      pagination: {
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      deviceForm: {
        id: null,
        name: "",
        roomId: null,
        deviceTemplateId: null,
        installationDate: null, // Store as YYYY-MM-DD string
        status: "ONLINE", // Default status
        ipAddress: "",
        macAddress: "",
        firmwareVersion: "",
        hardwareVersion: "",
        remarks: "",
      },
      deviceRules: {
        name: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
        roomId: [{ required: true, message: "请选择所属房间", trigger: "change" }],
        deviceTemplateId: [{ required: true, message: "请选择设备模板", trigger: "change" }],
        status: [{ required: true, message: "请选择设备状态", trigger: "change" }],
        installationDate: [{ type: 'string', message: '请选择安装日期', trigger: 'change' }],
        ipAddress: [{ validator: validateIpAddress, trigger: 'blur' }],
        macAddress: [{ validator: validateMacAddress, trigger: 'blur' }],
      },
      drawerVisible: false,
      currentDevice: {}, // For storing full device DTO for the drawer
      statusMap: {
        ONLINE: { text: '在线', type: 'success' },
        OFFLINE: { text: '离线', type: 'info' },
        FAULTY: { text: '故障', type: 'danger' },
        MAINTENANCE: { text: '维护中', type: 'warning' },
        UNKNOWN: { text: '未知', type: 'primary' },
      }
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
        // Ensure page and size are numbers
        const params = {
          ...this.searchParams,
          page: Number(this.searchParams.page) -1, // Spring Pageable is 0-indexed
          size: Number(this.searchParams.size),
        };
        const response = await getDevices(params);
        this.deviceList = response.content || [];
        this.pagination.total = response.totalElements || 0;
      } catch (error) {
        this.$message.error("获取设备列表失败: " + (error.message || '请稍后再试'));
        this.deviceList = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },
    async fetchRoomOptions() {
      try {
        // Fetch all rooms, assuming the API supports a way to get all (e.g., large size)
        // Or if your getRooms API without params returns all rooms
        const response = await getRooms({ page: 0, size: 1000 }); // Adjust if API differs
        this.roomOptions = response.content || [];
      } catch (error) {
        this.$message.error("获取房间列表失败: " + (error.message || '请稍后再试'));
        this.roomOptions = [];
      }
    },
    async fetchTemplateOptions() {
      try {
        // Fetch all templates
        const response = await getDeviceTemplates({ page: 0, size: 1000 }); // Adjust if API differs
        this.templateOptions = response.content || [];
      } catch (error) {
        this.$message.error("获取设备模板列表失败: " + (error.message || '请稍后再试'));
        this.templateOptions = [];
      }
    },
    handleSearch() {
      this.searchParams.page = 1; // Reset to first page for new search
      this.fetchDeviceList();
    },
    resetSearch() {
      this.searchParams = {
        name: "",
        roomId: null,
        deviceTemplateId: null,
        status: "",
        page: 1,
        size: this.searchParams.size, // Keep current page size
      };
      this.handleSearch();
    },
    handleAddDevice() {
      this.dialogTitle = "新增设备";
      this.resetForm('deviceForm'); // Resets and sets default values
      this.deviceForm.id = null; // Explicitly set id to null for new device
      this.dialogVisible = true;
    },
    async handleEditDevice(row) {
      this.dialogTitle = "编辑设备";
      this.resetForm('deviceForm'); // Clear form first
      try {
        const response = await getDeviceById(row.id);
        this.deviceForm = { ...response };
        // Ensure IDs for selects are numbers if they come as strings from API (though usually not an issue)
        this.deviceForm.roomId = response.room ? response.room.id : null;
        this.deviceForm.deviceTemplateId = response.deviceTemplate ? response.deviceTemplate.id : null;
         // installationDate from backend is likely already 'yyyy-MM-dd' string or can be parsed
        if (response.installationDate && typeof response.installationDate !== 'string') {
            this.deviceForm.installationDate = parseTime(response.installationDate, '{y}-{m}-{d}');
        }

        this.dialogVisible = true;
      } catch (error) {
        this.$message.error("获取设备详情失败: " + (error.message || '请稍后再试'));
      }
    },
    async handleViewDevice(row) {
        this.currentDevice = {}; // Clear previous data
        this.drawerVisible = true;
        try {
            const response = await getDeviceById(row.id);
            this.currentDevice = response;
        } catch (error) {
            this.$message.error("获取设备详情失败: " + (error.message || '请稍后再试'));
            this.currentDevice = { name: '加载失败' }; // Indicate error in drawer
        }
    },
    handleDeleteDevice(row) {
      this.$confirm(`确定删除设备 "${row.name}"? 此操作不可恢复。`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        try {
          await deleteDevice(row.id);
          this.$message.success("设备删除成功");
          // Refresh list, consider if current page becomes empty
          if (this.deviceList.length === 1 && this.searchParams.page > 1) {
            this.searchParams.page -= 1;
          }
          this.fetchDeviceList();
        } catch (error) {
          this.$message.error("删除设备失败: " + (error.message || '请稍后再试'));
        }
      }).catch(() => {
        this.$message.info("已取消删除");
      });
    },
    submitDeviceForm() {
      this.$refs.deviceForm.validate(async (valid) => {
        if (valid) {
          this.formLoading = true;
          try {
            const payload = { ...this.deviceForm };
            // Ensure IDs are numbers if they are not null
            if (payload.roomId) payload.roomId = Number(payload.roomId);
            if (payload.deviceTemplateId) payload.deviceTemplateId = Number(payload.deviceTemplateId);

            if (payload.id) { // Update existing device
              await updateDevice(payload.id, payload);
              this.$message.success("设备更新成功");
            } else { // Create new device
              await createDevice(payload);
              this.$message.success("设备新增成功");
            }
            this.dialogVisible = false;
            this.fetchDeviceList();
          } catch (error) {
            const errorMsg = error.response?.data?.message || error.message || '操作失败，请稍后再试';
            this.$message.error(errorMsg);
          } finally {
            this.formLoading = false;
          }
        } else {
          this.$message.error("表单校验失败，请检查输入");
          return false;
        }
      });
    },
    resetForm(formName) {
      this.deviceForm = {
        id: null,
        name: "",
        roomId: null,
        deviceTemplateId: null,
        installationDate: parseTime(new Date(), '{y}-{m}-{d}'), // Default to today
        status: "ONLINE",
        ipAddress: "",
        macAddress: "",
        firmwareVersion: "",
        hardwareVersion: "",
        remarks: "",
      };
      if (this.$refs[formName]) {
        this.$refs[formName].clearValidate();
      }
    },
    handleSizeChange(newSize) {
      this.searchParams.size = newSize;
      this.searchParams.page = 1; // Reset to first page when size changes
      this.fetchDeviceList();
    },
    handleCurrentChange(newPage) {
      this.searchParams.page = newPage;
      this.fetchDeviceList();
    },
    statusTagType(status) {
      return (this.statusMap[status] || this.statusMap.UNKNOWN).type;
    },
    formatStatus(status) {
      return (this.statusMap[status] || this.statusMap.UNKNOWN).text;
    }
  },
};
</script>

<style scoped>
.device-management-container {
  padding: 20px;
}
.box-card {
  min-height: calc(100vh - 90px); /* Adjust to fit your layout */
  margin-bottom: 20px;
}
.dialog-footer {
  text-align: right;
}
.el-form-item {
    margin-bottom: 18px; /* Slightly reduced margin for denser form */
}
.el-select, .el-date-picker {
    width: 100%;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.el-drawer__header {
    margin-bottom: 20px; /* Add some space below drawer title */
}
.el-descriptions {
    font-size: 14px; /* Consistent font size for descriptions */
}
.el-descriptions-item__label {
    font-weight: bold;
}
pre {
  white-space: pre-wrap; /* make sure long json string wraps */
  word-break: break-all;
}
</style>
