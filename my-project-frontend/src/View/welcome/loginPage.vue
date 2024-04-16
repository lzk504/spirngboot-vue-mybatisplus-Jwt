<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import {reactive} from "vue";

const form = reactive({
  username: '',
  password: '',
  remember: false,
})
</script>

<template>
  <div style="text-align: center;margin: 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px ">登录</div>
      <div style="font-size: 14px">在进入系统之前请先输入用户名和密码进行登录</div>
    </div>
    <div style="margin-top: 50px">
      <el-form v-model="form">
        <el-form-item>
          <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
          <el-input v-model="form.password" type="text" style="margin-top: 15px" show-password placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-checkbox v-model="form.remember" label="记住我" size="large"/>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link >忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 15px">
      <el-button type="success" plain style="width: 200px">立即登录</el-button>
    </div>
    <el-divider>
      <span style="color: #4b4747">没有账号</span>
    </el-divider>
    <div>
      <el-button type="warning" plain style="width: 200px">注册账号</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>