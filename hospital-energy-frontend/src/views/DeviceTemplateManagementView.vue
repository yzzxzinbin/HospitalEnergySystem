<template>
  <div class="template-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备模板列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddTemplate">新增模板</el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="模板名称">
          <el-input v-model="searchParams.templateName" placeholder="请输入模板名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="制造商">
          <el-input v-model="searchParams.manufacturer" placeholder="请输入制造商" clearable></el-input>
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="searchParams.modelIdentifier" placeholder="请输入型号" clearable></el-input>
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="searchParams.deviceCategory" placeholder="请输入类别" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 模板列表表格 -->
      <el-table :data="templateList" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="templateName" label="模板名称" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="manufacturer" label="制造商" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="modelIdentifier" label="型号" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="deviceCategory" label="类别" align="center"></el-table-column>
        <el-table-column prop="nominalPowerWatts" label="额定功率(W)" align="center"></el-table-column>
        <el-table-column prop="estimatedLifespanYears" label="预计寿命(年)" align="center"></el-table-column>
        <el-table-column prop="description" label="描述" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEditTemplate(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteTemplate(scope.row)">删除</el-button>
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

    <!-- 新增/编辑模板对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%" @close="resetForm('templateForm')" :close-on-click-modal="false">
      <el-form :model="templateForm" :rules="templateRules" ref="templateForm" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="templateForm.templateName" placeholder="请输入模板名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="制造商" prop="manufacturer">
              <el-input v-model="templateForm.manufacturer" placeholder="请输入制造商"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="型号标识" prop="modelIdentifier">
              <el-input v-model="templateForm.modelIdentifier" placeholder="请输入型号标识"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类别" prop="deviceCategory">
              <el-input v-model="templateForm.deviceCategory" placeholder="请输入设备类别"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="额定功率(瓦)" prop="nominalPowerWatts">
              <el-input-number v-model="templateForm.nominalPowerWatts" :min="0" controls-position="right" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计寿命(年)" prop="estimatedLifespanYears">
              <el-input-number v-model="templateForm.estimatedLifespanYears" :min="0" controls-position="right" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="额定耗水量(L/h)" prop="nominalWaterConsumptionLph">
              <el-input-number v-model="templateForm.nominalWaterConsumptionLph" :precision="2" :step="0.1" :min="0" controls-position="right" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="额定耗气量(m³/h)" prop="nominalGasConsumptionM3ph">
              <el-input-number v-model="templateForm.nominalGasConsumptionM3ph" :precision="2" :step="0.1" :min="0" controls-position="right" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="图片" prop="imageUrl">
          <el-input v-model="templateForm.imageUrl" placeholder="请输入图片URL"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="templateForm.description" placeholder="请输入模板描述"></el-input>
        </el-form-item>
        <el-form-item label="规格参数(JSON)" prop="specificationsJson">
          <el-input type="textarea" :rows="4" v-model="templateForm.specificationsJson" placeholder="请输入JSON格式的规格参数"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitTemplateForm" :loading="submitLoading">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { getDeviceTemplates, createDeviceTemplate, updateDeviceTemplate, deleteDeviceTemplate } from '@/api/deviceTemplate';

export default {
  name: "DeviceTemplateManagementView",
  data() {
    return {
      loading: false,
      submitLoading: false,
      templateList: [],
      searchParams: {
        templateName: "",
        manufacturer: "",
        modelIdentifier: "",
        deviceCategory: "",
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      templateForm: {
        id: null,
        templateName: "",
        manufacturer: "",
        modelIdentifier: "",
        deviceCategory: "",
        description: "",
        specificationsJson: "",
        nominalPowerWatts: 0,
        nominalWaterConsumptionLph: 0,
        nominalGasConsumptionM3ph: 0,
        estimatedLifespanYears: 0,
        imageUrl: "",
      },
      templateRules: {
        templateName: [{ required: true, message: "请输入模板名称", trigger: "blur" }],
        manufacturer: [{ required: true, message: "请输入制造商", trigger: "blur" }],
        modelIdentifier: [{ required: true, message: "请输入型号标识", trigger: "blur" }],
        deviceCategory: [{ required: true, message: "请输入设备类别", trigger: "blur" }],
        nominalPowerWatts: [{ type: 'number', message: '额定功率必须为数字值'}],
        estimatedLifespanYears: [{ type: 'number', message: '预计寿命必须为数字值'}],
        // Basic JSON validation for specificationsJson
        specificationsJson: [
          {
            validator: (rule, value, callback) => {
              if (value && value.trim() !== '') {
                try {
                  JSON.parse(value);
                  callback();
                } catch (e) {
                  callback(new Error('请输入有效的JSON格式'));
                }
              } else {
                callback(); // Allow empty or null
              }
            },
            trigger: 'blur'
          }
        ]
      },
    };
  },
  created() {
    this.fetchTemplateList();
  },
  methods: {
    fetchTemplateList() {
      this.loading = true;
      const params = {
        // API for device templates in backend does not support pagination or sorting via these params directly in getAllDeviceTemplates
        // It returns List<DeviceTemplateDto> not PageResponseDto
        // For now, we will fetch all and do client-side pagination if needed, or adjust backend.
        // For a quick fix, I will remove pagination params for now and assume backend sends all.
        // page: this.pagination.currentPage - 1, 
        // size: this.pagination.pageSize,
        ...(this.searchParams.templateName && { templateName: this.searchParams.templateName }),
        ...(this.searchParams.manufacturer && { manufacturer: this.searchParams.manufacturer }),
        ...(this.searchParams.modelIdentifier && { modelIdentifier: this.searchParams.modelIdentifier }),
        ...(this.searchParams.deviceCategory && { deviceCategory: this.searchParams.deviceCategory }),
      };
      // Note: The backend's getAllDeviceTemplates currently returns List<DeviceTemplateDto> and doesn't support pagination or filtering via query params.
      // The frontend search will be client-side for now, or backend needs to be updated.
      getDeviceTemplates(params) // Pass search params, though backend might not use all of them for filtering yet
        .then(response => {
          // Assuming response is an array of templates as per DeviceTemplateController's getAllDeviceTemplates
          let data = response; // If backend returns array directly
          if(response && typeof response.records !== 'undefined') { // If backend returns a paginated structure (needs backend change)
             data = response.records;
             this.pagination.total = response.total;
          } else {
            // Client-side filtering if backend doesn't filter
            if (params.templateName) {
              data = data.filter(t => t.templateName && t.templateName.includes(params.templateName));
            }
            if (params.manufacturer) {
              data = data.filter(t => t.manufacturer && t.manufacturer.includes(params.manufacturer));
            }
            if (params.modelIdentifier) {
              data = data.filter(t => t.modelIdentifier && t.modelIdentifier.includes(params.modelIdentifier));
            }
            if (params.deviceCategory) {
              data = data.filter(t => t.deviceCategory && t.deviceCategory.includes(params.deviceCategory));
            }
            this.pagination.total = data.length;
          }
          
          // Client-side pagination logic
          const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
          const end = this.pagination.currentPage * this.pagination.pageSize;
          this.templateList = data.slice(start, end);
          this.loading = false;
        })
        .catch(error => {
          this.$message.error("获取设备模板列表失败: " + (error.message || '请检查网络或联系管理员'));
          this.loading = false;
        });
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchTemplateList();
    },
    resetSearch() {
      this.searchParams = {
        templateName: "",
        manufacturer: "",
        modelIdentifier: "",
        deviceCategory: "",
      };
      this.handleSearch();
    },
    handleAddTemplate() {
      this.dialogTitle = "新增设备模板";
      this.resetForm('templateForm'); // Use resetForm to initialize
      this.dialogVisible = true;
      if (this.$refs.templateForm) {
        this.$refs.templateForm.clearValidate();
      }
    },
    handleEditTemplate(row) {
      this.dialogTitle = "编辑设备模板";
      this.templateForm = { ...row };
      // Ensure numeric fields are numbers if they come as strings
      this.templateForm.nominalPowerWatts = Number(row.nominalPowerWatts) || 0;
      this.templateForm.estimatedLifespanYears = Number(row.estimatedLifespanYears) || 0;
      this.templateForm.nominalWaterConsumptionLph = Number(row.nominalWaterConsumptionLph) || 0;
      this.templateForm.nominalGasConsumptionM3ph = Number(row.nominalGasConsumptionM3ph) || 0;
      this.dialogVisible = true;
      if (this.$refs.templateForm) {
        this.$refs.templateForm.clearValidate();
      }
    },
    handleDeleteTemplate(row) {
      this.$confirm(`确定删除模板 "${row.templateName || (row.manufacturer + ' ' + row.modelIdentifier)}"? 此操作不可恢复。`, "提示", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          deleteDeviceTemplate(row.id).then(() => {
            this.$message({ type: "success", message: "删除成功!" });
            // If current page becomes empty after deletion, go to previous page
            if (this.templateList.length === 1 && this.pagination.currentPage > 1) {
                this.pagination.currentPage--;
            }
            this.fetchTemplateList();
          }).catch(error => {
            this.$message.error("删除失败: " + (error.message || '请重试'));
          });
        })
        .catch(() => {
          this.$message({ type: "info", message: "已取消删除" });
        });
    },
    submitTemplateForm() {
      this.$refs.templateForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          // Ensure numeric fields are numbers
          const formData = {
            ...this.templateForm,
            nominalPowerWatts: Number(this.templateForm.nominalPowerWatts) || null,
            estimatedLifespanYears: Number(this.templateForm.estimatedLifespanYears) || null,
            nominalWaterConsumptionLph: Number(this.templateForm.nominalWaterConsumptionLph) || null,
            nominalGasConsumptionM3ph: Number(this.templateForm.nominalGasConsumptionM3ph) || null,
          };

          const apiCall = formData.id
            ? updateDeviceTemplate(formData.id, formData)
            : createDeviceTemplate(formData);

          apiCall.then(() => {
            this.$message({ type: "success", message: formData.id ? "更新成功!" : "新增成功!" });
            this.dialogVisible = false;
            this.fetchTemplateList();
          }).catch(error => {
            const errorMsg = error.response && error.response.data && error.response.data.message
                           ? error.response.data.message
                           : (error.message || (formData.id ? '更新失败' : '新增失败'));
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
      this.templateForm = {
        id: null,
        templateName: "",
        manufacturer: "",
        modelIdentifier: "",
        deviceCategory: "",
        description: "",
        specificationsJson: "",
        nominalPowerWatts: 0,
        nominalWaterConsumptionLph: 0,
        nominalGasConsumptionM3ph: 0,
        estimatedLifespanYears: 0,
        imageUrl: "",
      };
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields(); // This might not fully reset due to object nature, direct assignment above is safer.
        this.$refs[formName].clearValidate();
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
      this.fetchTemplateList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchTemplateList();
    },
  },
};
</script>

<style scoped>
.template-management-container {
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
