<template>
  <aside
    class="fixed left-4 top-4 bottom-4 bg-white border border-gray-100 rounded-2xl shadow-xl flex flex-col z-50 transition-all duration-300 ease-in-out overflow-hidden"
    :class="isCollapsed ? 'w-20' : 'w-64'"
  >
    <div
      class="h-16 flex items-center justify-center border-b border-gray-50 overflow-hidden relative flex-shrink-0"
    >
      <span
        class="text-2xl font-bold text-blue-600 absolute transition-opacity duration-200"
        :class="isCollapsed ? 'opacity-0' : 'opacity-100'"
      >
        MCNova
      </span>

      <span
        class="text-2xl font-bold text-blue-600 absolute transition-opacity duration-200"
        :class="isCollapsed ? 'opacity-100' : 'opacity-0'"
      >
        M
      </span>
    </div>

    <nav class="flex-1 p-3 space-y-2 overflow-y-auto min-h-0 custom-scrollbar">
      <router-link
        v-for="item in menuItems"
        :key="item.name"
        :to="item.path"
        custom
        v-slot="{ navigate, isActive }"
      >
        <a
          @click="navigate"
          class="w-full flex items-center h-14 rounded-xl transition-all duration-300 relative overflow-hidden group cursor-pointer"
          :class="
            isActive ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-slate-700 hover:bg-gray-200'
          "
        >
          <div
            class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"
          >
            <div
              class="absolute inset-0 bg-gradient-to-r from-transparent via-white/60 to-transparent -translate-x-full animate-[shimmer_1.5s_infinite]"
            ></div>
          </div>

          <div class="relative z-10 w-14 flex items-center justify-center flex-shrink-0">
            <img :src="item.icon" :alt="item.name" class="w-8 h-8 object-contain" />
          </div>

          <span v-if="!isCollapsed" class="relative z-10 ml-2 font-medium whitespace-nowrap">
            {{ item.name }}
          </span>
        </a>
      </router-link>
    </nav>

    <div class="p-4 border-t border-gray-50 flex justify-center flex-shrink-0">
      <button
        @click="emit('toggle')"
        class="p-2 rounded-xl hover:bg-gray-200 transition-colors text-gray-600"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

defineProps<{
  isCollapsed: boolean
}>()

const emit = defineEmits<{
  toggle: []
}>()

const menuItems = [
  {
    name: '主页',
    path: '/app/home',
    icon: new URL('@/assets/icons/Home.png', import.meta.url).href,
  },
  {
    name: '仪表盘',
    path: '/app/dashboard',
    icon: new URL('@/assets/icons/Dashboard.png', import.meta.url).href,
  },
  {
    name: '监控台',
    path: '/app/monitoringconsole',
    icon: new URL('@/assets/icons/MonitoringConsole.png', import.meta.url).href,
  },
  {
    name: '游戏实例日志',
    path: '/app/gamelog',
    icon: new URL('@/assets/icons/GameLog.png', import.meta.url).href,
  },
  {
    name: '新建服务器',
    path: '/app/newservers',
    icon: new URL('@/assets/icons/New.png', import.meta.url).href,
  },
  {
    name: '服务器',
    path: '/app/servers',
    icon: new URL('@/assets/icons/Servers.png', import.meta.url).href,
  },
  {
    name: '控制台',
    path: '/app/controlpanel',
    icon: new URL('@/assets/icons/ControlPanel.png', import.meta.url).href,
  },
  {
    name: '下载',
    path: '/app/downloads',
    icon: new URL('@/assets/icons/Downloads.png', import.meta.url).href,
  },
  {
    name: '资源库',
    path: '/app/resourcelibrary',
    icon: new URL('@/assets/icons/ResourceLibrary.png', import.meta.url).href,
  },
  {
    name: '联机',
    path: '/app/online',
    icon: new URL('@/assets/icons/Online.png', import.meta.url).href,
  },
  {
    name: '工具箱',
    path: '/app/tools',
    icon: new URL('@/assets/icons/Toolbox.png', import.meta.url).href,
  },
  {
    name: '环境管理',
    path: '/app/environmentalManagement',
    icon: new URL('@/assets/icons/EnvironmentalManagement.png', import.meta.url).href,
  },
  {
    name: 'Minecraft',
    path: '/app/minecraft',
    icon: new URL('@/assets/icons/Minecraft.png', import.meta.url).href,
  },
  {
    name: '设置',
    path: '/app/settings',
    icon: new URL('@/assets/icons/Settings.png', import.meta.url).href,
  },
  {
    name: '其他与',
    path: '/app/settings',
    icon: new URL('@/assets/icons/Settings.png', import.meta.url).href,
  },
]
</script>

<style scoped>
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* 极简风隐藏/美化菜单滚动条 */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px; /* 滚动条宽度仅 4px */
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.25); /* 半透明浅灰，非常隐蔽美观 */
  border-radius: 9999px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: rgba(156, 163, 175, 0.45);
}
</style>
