<template>
  <div class="template-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备模板列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddTemplate">新增模板</el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="制造商">
          <el-input v-model="searchParams.manufacturer" placeholder="请输入制造商"></el-input>
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="searchParams.model" placeholder="请输入型号"></el-input>
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="searchParams.category" placeholder="请输入类别"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 模板列表表格 -->
      <el-table :data="templateList" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="manufacturer" label="制造商" align="center"></el-table-column>
        <el-table-column prop="model" label="型号" align="center"></el-table-column>
        <el-table-column prop="category" label="类别" align="center"></el-table-column>
        <el-table-column prop="ratedPower" label="额定功率(kW)" align="center"></el-table-column>
        <el-table-column prop="lifespan" label="预计寿命(年)" align="center"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="warning" icon="el-icon-edit" @click="handleEditTemplate(scope.row)">编辑</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%" @close="resetForm('templateForm')">
      <el-form :model="templateForm" :rules="templateRules" ref="templateForm" label-width="120px">
        <el-form-item label="制造商" prop="manufacturer">
          <el-input v-model="templateForm.manufacturer" placeholder="请输入制造商名称"></el-input>
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="templateForm.model" placeholder="请输入设备型号"></el-input>
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="templateForm.category" placeholder="如：空调、照明、电梯"></el-input>
        </el-form-item>
        <el-form-item label="额定功率(kW)" prop="ratedPower">
          <el-input-number v-model="templateForm.ratedPower" :precision="2" :step="0.1" :min="0" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="预计寿命(年)" prop="lifespan">
          <el-input-number v-model="templateForm.lifespan" :step="1" :min="0" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="技术规格" prop="specifications">
          <el-input type="textarea" :rows="3" v-model="templateForm.specifications" placeholder="请输入详细的技术规格参数"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitTemplateForm">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
// 实际项目中会从 src/api/deviceTemplate.js 导入 API
// import { getDeviceTemplates, createDeviceTemplate, updateDeviceTemplate, deleteDeviceTemplate } from '@/api/deviceTemplate';

export default {
  name: "DeviceTemplateManagementView",
  data() {
    return {
      loading: false,
      templateList: [],
      searchParams: {
        manufacturer: "",
        model: "",
        category: "",
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
        manufacturer: "",
        model: "",
        category: "",
        ratedPower: 0,
        lifespan: 0,
        specifications: "",
      },
      templateRules: {
        manufacturer: [{ required: true, message: "请输入制造商名称", trigger: "blur" }],
        model: [{ required: true, message: "请输入设备型号", trigger: "blur" }],
        category: [{ required: true, message: "请输入设备类别", trigger: "blur" }],
      },
      // 模拟数据
      mockTemplateData: [
        { id: 1, manufacturer: '格力', model: 'KFR-35GW', category: '空调', ratedPower: 1.2, lifespan: 10, specifications: '变频, R32制冷剂' },
        { id: 2, manufacturer: '飞利浦', model: 'LED筒灯', category: '照明', ratedPower: 0.015, lifespan: 5, specifications: '5W, 4000K色温' },
        { id: 3, manufacturer: '三菱', model: 'Maxiez', category: '电梯', ratedPower: 7.5, lifespan: 15, specifications: '载重1000kg, 速度1.5m/s' },
        { id: 4, manufacturer: '西门子', model: 'CT Scanner', category: '医疗设备', ratedPower: 25, lifespan: 8, specifications: '64排螺旋CT' },
      ]
    };
  },
  created() {
    this.fetchTemplateList();
  },
  methods: {
    fetchTemplateList() {
      this.loading = true;
      // 模拟API调用
      setTimeout(() => {
        let filteredData = this.mockTemplateData.filter(item => {
          return (
            (this.searchParams.manufacturer ? item.manufacturer.toLowerCase().includes(this.searchParams.manufacturer.toLowerCase()) : true) &&
            (this.searchParams.model ? item.model.toLowerCase().includes(this.searchParams.model.toLowerCase()) : true) &&
            (this.searchParams.category ? item.category.toLowerCase().includes(this.searchParams.category.toLowerCase()) : true)
          );
        });
        this.pagination.total = filteredData.length;
        const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
        const end = start + this.pagination.pageSize;
        this.templateList = filteredData.slice(start, end);
        this.loading = false;
      }, 500);
      // 实际API: getDeviceTemplates({ ...this.searchParams, page: this.pagination.currentPage, size: this.pagination.pageSize })
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchTemplateList();
    },
    resetSearch() {
      this.searchParams = { manufacturer: "", model: "", category: "" };
      this.handleSearch();
    },
    handleAddTemplate() {
      this.dialogTitle = "新增设备模板";
      this.templateForm = { id: null, manufacturer: "", model: "", category: "", ratedPower: 0, lifespan: 0, specifications: "" };
      this.dialogVisible = true;
    },
    handleEditTemplate(row) {
      this.dialogTitle = "编辑设备模板";
      this.templateForm = { ...row };
      this.dialogVisible = true;
    },
    handleDeleteTemplate(row) {
      this.$confirm(`确定删除模板 ${row.manufacturer} - ${row.model} 吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        // 模拟API
        this.mockTemplateData = this.mockTemplateData.filter(item => item.id !== row.id);
        this.fetchTemplateList();
        this.$message({ type: "success", message: "删除成功!" });
        // 实际API: deleteDeviceTemplate(row.id).then(...)
      }).catch(() => {
        this.$message({ type: "info", message: "已取消删除" });
      });
    },
    submitTemplateForm() {
      this.$refs.templateForm.validate((valid) => {
        if (valid) {
          // 模拟API
          if (this.templateForm.id) { // 编辑
            const index = this.mockTemplateData.findIndex(item => item.id === this.templateForm.id);
            if (index !== -1) {
              this.mockTemplateData.splice(index, 1, { ...this.templateForm });
            }
            this.$message({ type: "success", message: "更新成功!" });
          } else { // 新增
            this.templateForm.id = Date.now(); // 简单ID
            this.mockTemplateData.unshift({ ...this.templateForm });
            this.$message({ type: "success", message: "新增成功!" });
          }
          this.dialogVisible = false;
          this.fetchTemplateList();
          // 实际API: this.templateForm.id ? updateDeviceTemplate(...) : createDeviceTemplate(...)
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
      this.templateForm = { id: null, manufacturer: "", model: "", category: "", ratedPower: 0, lifespan: 0, specifications: "" };
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
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
