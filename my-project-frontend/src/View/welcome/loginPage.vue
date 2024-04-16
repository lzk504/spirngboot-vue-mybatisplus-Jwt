<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import {reactive, ref} from "vue";
import {login} from "@/api";

const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  remember: false,
})

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

function userLogin(){
  formRef.value.validate((valid)=>{
    if(valid){
      login(form.username,form.password,form.remember,()=>{})
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px ">登录</div>
      <div style="font-size: 14px">在进入系统之前请先输入用户名和密码进行登录</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" style="margin-top: 15px" show-password placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="记住我" size="large"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link>忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 15px">
      <el-button @click="userLogin" type="success" plain style="width: 200px">立即登录</el-button>
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