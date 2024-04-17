import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "@/api"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/View/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import ('@/View/welcome/loginPage.vue'),
                }
            ]
        }, {
            path: '/index',
            name: 'index',
            component: () => import('@/View/indexView.vue')
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    if(to.name.startsWith('welcome') && !isUnauthorized) {
        next('/index')
    } else if(to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})
export default router