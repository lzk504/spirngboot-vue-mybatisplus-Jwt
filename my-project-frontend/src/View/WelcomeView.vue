<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image
          style="width: 100%; height: 100%;" fit="cover"
          src="https://lzk-gongji.oss-cn-hangzhou.aliyuncs.com/gongji/img/colorhub%20(1).jpg"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px; font-weight: bold;margin-bottom: 5px">欢迎使用</div>
      <div>在这里有好多实用工具</div>
    </div>
    <div style=" width: 400px;z-index: 1;background-color: white;">
     <router-view/>
    </div>
  </div>
</template>

<style scoped>

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: #f2f2f2;
  text-shadow: 0 0 10px black;
}
</style>