<script setup>
import {reactive, ref} from "vue";
import {Lock, Message, User, Open} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {get,post} from "@/api/index.js"
import {ElMessage} from "element-plus";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符,且必须有大小写中英文"))
  } else {
    callback()
  }
}

const validatePasswordRepeat = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("密码不一致！"))
  } else {
    callback()
  }
}
const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 6, max: 8, message: '用户名长度必须是 6 - 8', trigger: ['blur', 'change']},
  ],
  password: [
    {required: true, min: 6, max: 15, message: '请输入密码', trigger: ['blur', 'change']},
  ],
  password_repeat: [
    {min: 6, max: 15, message: '用户名长度必须是 6 - 15', trigger: ['blur', 'change']},
    {validator: validatePasswordRepeat, trigger: ['blur', 'change']},],
  email: [
    {required: true, type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: ['blur']}
  ]
}
const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)
const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

function askCode() {
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
        ElMessage.success(`验证码已发送到邮箱${form.email}`)
    const handle = setInterval(() => {
      coldTime.value--
      if(coldTime.value === 0) {
        clearInterval(handle)
      }
    }, 1000)
      }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
      }
  )
}

function register(){
  formRef.value.validate((isValid) => {
    if (isValid) {
      post('api/auth/register', {
            username: form.username,
            password: form.password,
            email: form.email,
            code: form.code,
          }, (message) => {
            ElMessage.success("注册成功！")
            router.push('/')
          }
      )
    } else {
      ElMessage.warning('请填写完整的表单信息!')
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px ">注册新用户</div>
      <div style="font-size: 14px">欢迎注册学习平台，请在下方填写信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input placeholder="在此输入用户名" maxlength="8" v-model="form.username">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="15" type="password" placeholder="在此输入密码"
                    show-password>
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input
              v-model="form.password_repeat"
              maxlength="15"
              type="password"
              placeholder="再次输入密码"
              show-password
          >
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input
              v-model="form.email"
              placeholder="电子邮箱地址"
          >
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="8">
            <el-col :span="17">
              <el-input v-model="form.code" maxlength="6" type="email" placeholder="请输入电子邮箱验证码">
                <template #prefix>
                  <el-icon>
                    <Open/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click="askCode" :disabled="!isEmailValid || coldTime>0">
                {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style=" margin-top: 50px">
      <el-button @click="register" style="width: 140px" type="success" plain>立即注册</el-button>
    </div>
    <div style="font-size: 14px;">
      <span>已有账号？</span>
      <el-link @click="router.push('/')" type="primary">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>