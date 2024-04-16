import axios from "axios";
import {ElMessage} from "element-plus";

const authItemName = "access_token"
const defaultFailure = (message, code, url) => {
    console.warn(`请求地址:${url},状态码:${code},错误信息:${message}`)
    ElMessage.warning(message)
}
const defaultError = (e) => {
    console.error(e)
    ElMessage.error('发生了一些错误，请联系管理员')
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str) {
        return null
    }
    const authObj = JSON.parse(str)
    if (authObj.expire <= new Date()) {
        deleteAccessToken()
        ElMessage.warning('登录状态已过期请重新登录')
        return null
    }
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if (remember) {
        localStorage.setItem(authItemName, str)
    } else {
        sessionStorage.setItem(authItemName, str)
    }
}


function internalPost(url, data, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(e => error(e))
}


function internalGet(url, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(e => error(e))
}

function login(username, password, remember, success, failure = defaultFailure, error = defaultError) {
    internalPost('api/auth/login', {
        username: username,
        password: password,
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire)
        ElMessage.success(`成功登录、欢迎${data.username}进入系统`)
        success(data)
    }, failure)
}

export {login}