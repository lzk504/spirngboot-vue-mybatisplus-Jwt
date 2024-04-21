<script setup>
import {Lock, Message, Open} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/api/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const form = reactive({
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const active = ref(0)
const formRef = ref()

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

const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

function askCode() {
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${form.email}&type=reset`, () => {
        ElMessage.success(`验证码已发送到邮箱${form.email}`)
        const handle = setInterval(() => {
          coldTime.value--
          if (coldTime.value === 0) {
            clearInterval(handle)
          }
        }, 1000)
      }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
      }
  )
}

function confirmReset() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post('api/auth/reset-confirm', {
            email: form.email,
            code: form.code,
          }, () => {
            active.value++
          }
      )
    } else {
      ElMessage.warning('请填写完整的信息!')
    }
  })
}

function doReset() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post('api/auth/reset-password', {
            email: form.email,
            code: form.code,
            password: form.password
          }, () => {
            ElMessage.success("密码重置成功")
            router.push('/')
          }
      )
    } else {
      ElMessage.warning('请填写新的密码!')
    }
  })
}
</script>

<template>
  <div>
    <div style="margin: 10px 30px">
      <el-steps style="max-width: 400px" :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>
    <transition name="el-zoom-in-center" mode="out-in" style="height: 100%">
      <div style="text-align: center;margin: 20px" v-if="active === 0">
        <div style="margin-top: 150px">
          <div style="font-size: 25px ">重置密码</div>
          <div style="font-size: 14px">请输入需要重置的电子邮件地址</div>
          <div style="margin-top: 30px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
                    <el-button @click="askCode" :disabled="!isEmailValid || coldTime>0" type="success">
                      {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码' }}
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-form>
          </div>
          <div style="margin-top: 30px">
            <el-button type="success" @click="confirmReset" plain>开始重置密码</el-button>
          </div>
        </div>
      </div>
    </transition>
    <transition name="el-zoom-in-center" mode="out-in" style="height: 100%">
      <div style="text-align: center;margin: 20px" v-if="active === 1">
        <div style="margin-top: 150px">
          <div style="font-size: 25px ">重置密码</div>
          <div style="font-size: 14px">请输入密码，务必牢记</div>
          <div style="margin-top: 30px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
              <el-form-item prop="password">
                <el-input v-model="form.password" maxlength="15" type="password" placeholder="在此输入新密码"
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
                    placeholder="再次输入新密码"
                    show-password
                >
                  <template #prefix>
                    <el-icon>
                      <Lock/>
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
          </div>
          <div style="margin-top: 30px">
            <el-button type="success" @click="doReset" plain>立即重置密码</el-button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>

</style>