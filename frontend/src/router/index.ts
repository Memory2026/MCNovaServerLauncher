import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'

import WelcomeView from '@/views/welcome/WelcomeView.vue'
import Home from '@/views/home/HomeView.vue'
import MonitoringConsole from '@/views/monitoringconsole/MonitoringConsole.vue'
import EnvironmentalManagement from "@/views/environment/EnvironmentalManagement.vue"
import JavaManager from '@/views/environment/JavaManager.vue'
import ToolboxView from '@/views/toolbox/ToolboxView.vue'


const router = createRouter({

  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [

    // 登录/欢迎页
    {
      path: '/',
      name: 'welcome',
      component: WelcomeView
    },


    // 主面板布局
    {
      path: '/app',
      component: MainLayout,

      children: [

        // 默认进入主页
        {
          path: '',
          redirect: '/app/home'
        },


        // 首页
        {
          path: 'home',
          name: 'home',
          component: Home
        },


        // 仪表盘
        {
          path: 'monitoringconsole',
          name: 'monitoringconsole',
          component: MonitoringConsole
        },

        {
          path: 'tools',
          name: 'toolbox',
          // 引入刚才创建的工具箱页面
          component:ToolboxView
        },

        {
          path: 'environmentalmanagement',
          name: 'environmentalmanagement',
          component: EnvironmentalManagement
        },

        {
          path: 'environmentalmanagement/java',
          name: 'java-manager',
          component: JavaManager
        }

      ]
    }

  ]

})


export default router
