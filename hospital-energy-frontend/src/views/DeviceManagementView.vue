<template>
  <div class="device-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddDevice">新增设备</el-button>
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

      <el-table
        v-loading="loading"
        :data="deviceList"
        style="width: 100%"
        border
        stripe>
        <el-table-column prop="name" label="设备名称" width="180" show-overflow-tooltip></el-table-column>
        <el-table-column label="所属房间" width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <!-- Directly use roomNumber from DeviceDto -->
            {{ scope.row.roomNumber || 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column label="设备模板" width="220" show-overflow-tooltip>
          <template slot-scope="scope">
            <!-- Directly use deviceTemplateName from DeviceDto -->
            {{ scope.row.deviceTemplateName || 'N/A' }}
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
        <!-- Removed lastOnlineTime column as it's not in DeviceDto.java -->
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="info" size="mini" icon="el-icon-view" @click="handleViewDevice(scope.row)">详情</el-button>
            <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleEditDevice(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" icon="el-icon-delete" @click="handleDeleteDevice(scope.row)">删除</el-button>
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
              <el-select v-model="deviceForm.roomId" placeholder="请选择所属房间" filterable style="width: 100%;" :filter-method="filterDialogRoomOptions" @focus="resetRoomOptionsInDialog">
                <el-option v-for="room in roomOptionsInDialog" :key="room.id" :label="room.roomNumber + (room.department ? ' (' + room.department + ')' : '')" :value="room.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备模板" prop="deviceTemplateId">
              <el-select v-model="deviceForm.deviceTemplateId" placeholder="请选择设备模板" filterable style="width: 100%;" :filter-method="filterDialogTemplateOptions" @focus="resetTemplateOptionsInDialog">
                <el-option v-for="template in templateOptionsInDialog" :key="template.id" :label="template.templateName || (template.manufacturer + ' - ' + template.modelIdentifier)" :value="template.id"></el-option>
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
                <!-- 根据实际后端支持的状态调整，表结构中 status varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Operational' -->
                <!-- 如果后端使用 'Operational' 等，这里也需要对应修改 -->
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
      size="60%"
      destroy-on-close
      @opened="loadDetailedInfoForDrawer"
      @close="clearDetailedInfo">
      <div v-loading="loadingDrawerDetails" style="padding: 20px;">
        <div v-if="currentDevice.id">
          <el-descriptions title="设备基本信息" :column="2" border>
            <el-descriptions-item label="设备ID">{{ currentDevice.id }}</el-descriptions-item>
            <el-descriptions-item label="设备名称">{{ currentDevice.name }}</el-descriptions-item>
            <el-descriptions-item label="安装日期">{{ currentDevice.installationDate | formatDate }}</el-descriptions-item>
            <el-descriptions-item label="设备状态">
              <el-tag :type="statusTagType(currentDevice.status)" size="small">{{ formatStatus(currentDevice.status) }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <el-descriptions v-if="detailedRoomInfo" title="关联房间信息" :column="2" border style="margin-top: 20px;">
            <el-descriptions-item label="房间ID">{{ detailedRoomInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{ detailedRoomInfo.roomNumber }}</el-descriptions-item>
            <el-descriptions-item label="房间名称">{{ detailedRoomInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="楼层">{{ detailedRoomInfo.floor }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ detailedRoomInfo.department }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ detailedRoomInfo.description }}</el-descriptions-item>
          </el-descriptions>
          <div v-else-if="currentDevice.roomId" style="margin-top: 20px; padding:10px; border: 1px solid #EBEEF5; border-radius: 4px;">加载房间详细信息中...</div>


          <el-descriptions v-if="detailedTemplateInfo" title="关联设备模板信息" :column="2" border style="margin-top: 20px;">
            <el-descriptions-item label="模板ID">{{ detailedTemplateInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="模板名称">{{ detailedTemplateInfo.templateName }}</el-descriptions-item>
            <el-descriptions-item label="制造商">{{ detailedTemplateInfo.manufacturer }}</el-descriptions-item>
            <el-descriptions-item label="型号标识">{{ detailedTemplateInfo.modelIdentifier }}</el-descriptions-item>
            <el-descriptions-item label="设备类别">{{ detailedTemplateInfo.deviceCategory }}</el-descriptions-item>
            <el-descriptions-item label="额定功率(W)">{{ detailedTemplateInfo.nominalPowerWatts }}</el-descriptions-item>
            <el-descriptions-item label="预计寿命(年)">{{ detailedTemplateInfo.estimatedLifespanYears }}</el-descriptions-item>
            <el-descriptions-item label="额定耗水量(L/h)">{{ detailedTemplateInfo.nominalWaterConsumptionLph }}</el-descriptions-item>
            <el-descriptions-item label="额定耗气量(m³/h)">{{ detailedTemplateInfo.nominalGasConsumptionM3ph }}</el-descriptions-item>
            <el-descriptions-item label="图片URL" :span="2">
                <el-image v-if="detailedTemplateInfo.imageUrl" :src="detailedTemplateInfo.imageUrl" fit="contain" style="width: 100px; height: 100px">
                    <div slot="error" class="image-slot">
                        <i class="el-icon-picture-outline"></i>
                    </div>
                </el-image>
                <span v-else>无</span>
            </el-descriptions-item>
            <el-descriptions-item label="模板描述" :span="2">{{ detailedTemplateInfo.description }}</el-descriptions-item>
            <el-descriptions-item label="规格参数(JSON)" :span="2">
              <pre v-if="detailedTemplateInfo.specificationsJson" style="background-color: #f5f5f5; padding: 10px; border-radius: 4px;">{{ formatJson(detailedTemplateInfo.specificationsJson) }}</pre>
              <span v-else>无</span>
            </el-descriptions-item>
          </el-descriptions>
          <div v-else-if="currentDevice.deviceTemplateId" style="margin-top: 20px; padding:10px; border: 1px solid #EBEEF5; border-radius: 4px;">加载设备模板详细信息中...</div>

        </div>
        <div v-else style="padding: 20px; text-align: center;">
          <p>暂无设备详情或加载中...</p>
        </div>
      </div>
    </el-drawer>

  </div>
</template>

<script>
import { getDevices, createDevice, updateDevice, getDeviceById } from '@/api/device';
// Assuming getRoomById exists in room.js and getDeviceTemplateById in deviceTemplate.js
import { getRooms, getRoomById } from '@/api/room'; 
import { getDeviceTemplates, getDeviceTemplateById } from '@/api/deviceTemplate'; 
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
    return {
      loading: false,
      formLoading: false,
      deviceList: [],
      roomOptions: [], // For search bar
      templateOptions: [], // For search bar
      allRooms: [], // Master list of all rooms
      allDeviceTemplates: [], // Master list of all device templates
      roomOptionsInDialog: [], // For dialog's room select (filtered)
      templateOptionsInDialog: [], // For dialog's template select (filtered)
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
        status: "ONLINE", // Default status, consider matching DB default 'Operational' if applicable
      },
      deviceRules: {
        name: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
        roomId: [{ required: true, message: "请选择所属房间", trigger: "change" }],
        deviceTemplateId: [{ required: true, message: "请选择设备模板", trigger: "change" }],
        status: [{ required: true, message: "请选择设备状态", trigger: "change" }],
        installationDate: [{ type: 'string', message: '请选择安装日期', trigger: 'change' }],
      },
      drawerVisible: false,
      currentDevice: {}, // For storing basic device DTO for the drawer
      detailedRoomInfo: null, // For storing full room DTO
      detailedTemplateInfo: null, // For storing full template DTO
      loadingDrawerDetails: false, // Loading state for drawer's detailed content
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
        console.log('API Response from getDevices:', JSON.parse(JSON.stringify(response))); // Log raw response
        
        // Correctly assign if response is an array directly
        if (Array.isArray(response)) {
          this.deviceList = response;
          // If the API doesn't return totalElements for non-paged results,
          // and you still want to show total in pagination based on current list length:
          this.pagination.total = response.length; 
        } else if (response && response.content) {
          // Handle paged response if API might return it in other scenarios
          this.deviceList = response.content || [];
          this.pagination.total = response.totalElements || 0;
        } else {
          // Fallback for unexpected structure
          this.deviceList = [];
          this.pagination.total = 0;
          console.warn('Unexpected response structure from getDevices:', response);
        }
        
        console.log('Processed this.deviceList:', JSON.parse(JSON.stringify(this.deviceList)));
      } catch (error) {
        this.$message.error("获取设备列表失败: " + (error.message || '请稍后再试'));
        console.error('Error fetching device list:', error); // Log the error
        this.deviceList = [];
        this.pagination.total = 0;
      } finally {
        this.loading = false;
      }
    },
    async fetchRoomOptions() {
      try {
        // Fetch all rooms, assuming the API supports a way to get all (e.g., large size)
        const response = await getRooms({ page: 0, size: 10000, sort: 'roomNumber,asc' }); // Adjust if API differs, ensure all are fetched
        console.log('API Response from getRooms:', JSON.parse(JSON.stringify(response))); // Log the raw response
        // Correctly access the list of rooms from the 'records' property
        this.allRooms = response.records || []; 
        console.log('Processed this.allRooms:', JSON.parse(JSON.stringify(this.allRooms)));
        this.roomOptions = [...this.allRooms]; // For search bar filter
        // this.roomOptionsInDialog = [...this.allRooms]; // Initialize dialog options - moved to open dialog
      } catch (error) {
        this.$message.error("获取房间列表失败: " + (error.message || '请稍后再试'));
        console.error('Error fetching room options:', error); // Log the error
        this.allRooms = [];
        this.roomOptions = [];
        // this.roomOptionsInDialog = [];
      }
    },
    async fetchTemplateOptions() {
      try {
        // Fetch all templates
        const response = await getDeviceTemplates({ page: 0, size: 10000, sort: 'templateName,asc' }); // Adjust if API differs, ensure all are fetched
        console.log('API Response from getDeviceTemplates:', JSON.parse(JSON.stringify(response))); // Log the raw response
        // Correctly access the list of templates, as the response is the array itself
        this.allDeviceTemplates = response || []; 
        console.log('Processed this.allDeviceTemplates:', JSON.parse(JSON.stringify(this.allDeviceTemplates)));
        this.templateOptions = [...this.allDeviceTemplates]; // For search bar filter
        // this.templateOptionsInDialog = [...this.allDeviceTemplates]; // Initialize dialog options - moved to open dialog
      } catch (error) {
        this.$message.error("获取设备模板列表失败: " + (error.message || '请稍后再试'));
        console.error('Error fetching template options:', error); // Log the error
        this.allDeviceTemplates = [];
        this.templateOptions = [];
        // this.templateOptionsInDialog = [];
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
      // Ensure search dropdowns are repopulated if they were somehow cleared, though typically not needed
      if (this.allRooms.length > 0 && this.roomOptions.length === 0) {
        this.roomOptions = [...this.allRooms];
      }
      if (this.allDeviceTemplates.length > 0 && this.templateOptions.length === 0) {
        this.templateOptions = [...this.allDeviceTemplates];
      }
      this.handleSearch();
    },
    handleAddDevice() {
      this.dialogTitle = "新增设备";
      this.resetForm('deviceForm'); // Resets and sets default values
      this.deviceForm.id = null; // Explicitly set id to null for new device
      
      this.roomOptionsInDialog = [...this.allRooms];
      this.templateOptionsInDialog = [...this.allDeviceTemplates];
      
      this.dialogVisible = true;
    },
    async handleEditDevice(row) {
      this.dialogTitle = "编辑设备";
      this.resetForm('deviceForm'); // Clear form first
      
      this.roomOptionsInDialog = [...this.allRooms];
      this.templateOptionsInDialog = [...this.allDeviceTemplates];

      try {
        const deviceData = await getDeviceById(row.id); 
        
        this.deviceForm.id = deviceData.id;
        this.deviceForm.name = deviceData.name;
        this.deviceForm.roomId = deviceData.roomId; 
        this.deviceForm.deviceTemplateId = deviceData.deviceTemplateId; 
        this.deviceForm.status = deviceData.status;
        
        if (deviceData.installationDate) {
            this.deviceForm.installationDate = parseTime(deviceData.installationDate, '{y}-{m}-{d}');
        } else {
            this.deviceForm.installationDate = null;
        }

        this.dialogVisible = true;
      } catch (error) {
        this.$message.error("获取设备详情失败: " + (error.message || '请稍后再试'));
      }
    },
    async handleViewDevice(row) {
        // Store the basic device info first to open drawer quickly
        // Detailed info will be loaded via @opened event of drawer
        try {
            this.currentDevice = await getDeviceById(row.id); // Fetch basic info first
            this.drawerVisible = true;
        } catch (error) {
             this.$message.error("获取设备基本信息失败: " + (error.message || '请稍后再试'));
             this.currentDevice = { name: '加载失败' };
             this.drawerVisible = true; // Still open drawer to show error
        }
    },
    async loadDetailedInfoForDrawer() {
        if (!this.currentDevice || !this.currentDevice.id) return;

        this.loadingDrawerDetails = true;
        this.detailedRoomInfo = null;
        this.detailedTemplateInfo = null;

        const { roomId, deviceTemplateId } = this.currentDevice;

        try {
            if (roomId) {
                this.detailedRoomInfo = await getRoomById(roomId);
            }
        } catch (roomError) {
            console.error('Error fetching detailed room info:', roomError);
            this.$message.error("获取关联房间详细信息失败");
        }

        try {
            if (deviceTemplateId) {
                this.detailedTemplateInfo = await getDeviceTemplateById(deviceTemplateId);
            }
        } catch (templateError) {
            console.error('Error fetching detailed template info:', templateError);
            this.$message.error("获取关联设备模板详细信息失败");
        } finally {
            this.loadingDrawerDetails = false;
        }
    },
    clearDetailedInfo() {
        // Called when drawer is closed
        this.currentDevice = {};
        this.detailedRoomInfo = null;
        this.detailedTemplateInfo = null;
    },
    formatJson(jsonString) {
      if (!jsonString) return '无';
      try {
        const obj = JSON.parse(jsonString);
        return JSON.stringify(obj, null, 2); // Pretty print JSON
      } catch (e) {
        return jsonString; // Return as is if not valid JSON
      }
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
        status: "ONLINE", // Consider DB default 'Operational'
      };
      if(this.$refs[formName]) {
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
    filterDialogRoomOptions(query) {
      if (query && typeof query === 'string') {
        const lowerQuery = query.toLowerCase();
        this.roomOptionsInDialog = this.allRooms.filter(room => {
          const label = `${room.roomNumber || ''}${room.department ? ' (' + room.department + ')' : ''}`;
          return label.toLowerCase().includes(lowerQuery);
        });
      } else {
        this.roomOptionsInDialog = [...this.allRooms];
      }
    },
    filterDialogTemplateOptions(query) {
      if (query && typeof query === 'string') {
        const lowerQuery = query.toLowerCase();
        this.templateOptionsInDialog = this.allDeviceTemplates.filter(template => {
          const labelPart1 = template.templateName || '';
          const labelPart2 = template.manufacturer || '';
          const labelPart3 = template.modelIdentifier || '';
          const label = template.templateName ? labelPart1 : `${labelPart2} - ${labelPart3}`;
          return label.toLowerCase().includes(lowerQuery);
        });
      } else {
        this.templateOptionsInDialog = [...this.allDeviceTemplates];
      }
    },
    // Methods to reset options on focus, ensuring full list is available before filtering starts if needed
    resetRoomOptionsInDialog() {
        if (this.roomOptionsInDialog.length !== this.allRooms.length) {
             this.roomOptionsInDialog = [...this.allRooms];
        }
    },
    resetTemplateOptionsInDialog() {
        if (this.templateOptionsInDialog.length !== this.allDeviceTemplates.length) {
            this.templateOptionsInDialog = [...this.allDeviceTemplates];
        }
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
