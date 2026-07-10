import { createRouter, createWebHashHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'

import WelcomeView from '@/views/welcome/WelcomeView.vue'

import Home from '@/views/home/HomeView.vue'

import MonitoringConsole from '@/views/monitoringconsole/MonitoringConsole.vue'

import EnvironmentalManagement from '@/views/environment/EnvironmentalManagement.vue'

import JavaManager from '@/views/environment/JavaManager.vue'

import ToolboxView from '@/views/toolbox/ToolboxView.vue'

import Downloads from '@/views/downloads/DownloadsView.vue'

const router = createRouter({
  history: createWebHashHistory(),

  routes: [
    {
      path: '/',

      name: 'welcome',

      component: WelcomeView,
    },

    {
      path: '/app',

      component: MainLayout,

      children: [
        {
          path: '',

          redirect: '/app/home',
        },

        {
          path: 'home',

          name: 'home',

          component: Home,
        },

        {
          path: 'monitoringconsole',

          name: 'monitoringconsole',

          component: MonitoringConsole,
        },

        {
          path: 'tools',

          name: 'toolbox',

          component: ToolboxView,
        },

        {
          path: 'environmentalmanagement',

          name: 'environmentalmanagement',

          component: EnvironmentalManagement,
        },

        {
          path: 'environmentalmanagement/java',

          name: 'java-manager',

          component: JavaManager,
        },

        /*
         * Minecraft下载
         */

        {
          path: 'downloads',

          name: 'downloads',

          component: Downloads,
        },
      ],
    },
  ],
})

export default router
