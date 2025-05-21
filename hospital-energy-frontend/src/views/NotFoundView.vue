<template>
  <div>
    <h1>404 - 页面未找到</h1>
    <p>您访问的页面不存在，请检查URL是否正确，或返回首页。</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  name: "NotFoundView",
  methods: {
    goHome() {
      this.$router.push("/");
    },
  },
};
</script>

<style scoped>
div {
  text-align: center;
  margin-top: 50px;
}
h1 {
  font-size: 48px;
  color: #409eff;
}
p {
  font-size: 18px;
  margin-bottom: 30px;
}
</style>
