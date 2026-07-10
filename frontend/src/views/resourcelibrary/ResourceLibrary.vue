<template>
  <div class="flex gap-6">
    <div class="w-56 flex-shrink-0">
      <div class="bg-white/50 rounded-2xl p-2 border border-gray-100">
        <div class="flex flex-col gap-2">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="currentTab = tab.id"
            class="flex items-center gap-3 px-4 py-3 rounded-xl text-base font-medium transition-all w-full"
            :class="[
              currentTab === tab.id
                ? 'bg-blue-100 text-blue-600'
                : 'bg-gray-100 text-slate-700 hover:bg-gray-200',
            ]"
          >
            <img :src="getTabIcon(tab.icon, tab.path)" class="w-5 h-5 object-contain" />
            <span>{{ tab.name }}</span>
          </button>
        </div>
      </div>
    </div>

    <div class="flex-1">
      <div v-if="currentTab === 'version'" class="space-y-4">
        <header class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-gray-900">版本资源</h2>
          <button
            class="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-700 rounded-xl text-sm font-medium shadow-sm transition-colors"
          >
            <span>📁</span> 导入版本
          </button>
        </header>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200/60 p-5">
          <div class="text-sm text-gray-400 mb-4">暂无版本资源</div>
          <div class="flex items-center justify-center h-40 text-gray-400">
            <div class="text-center">
              <div class="text-4xl mb-2">📦</div>
              <div class="text-sm">点击上方按钮导入版本</div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="currentTab === 'loader'" class="space-y-4">
        <header class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-gray-900">加载器资源</h2>
          <button
            class="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-700 rounded-xl text-sm font-medium shadow-sm transition-colors"
          >
            <span>📁</span> 导入加载器
          </button>
        </header>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200/60 p-5">
          <div class="text-sm text-gray-400 mb-4">暂无加载器资源</div>
          <div class="flex items-center justify-center h-40 text-gray-400">
            <div class="text-center">
              <div class="text-4xl mb-2">⚙️</div>
              <div class="text-sm">点击上方按钮导入加载器</div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="currentTab === 'platform'" class="space-y-4">
        <header class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-gray-900">平台资源</h2>
          <button
            class="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-700 rounded-xl text-sm font-medium shadow-sm transition-colors"
          >
            <span>📁</span> 导入平台
          </button>
        </header>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200/60 p-5">
          <div class="text-sm text-gray-400 mb-4">暂无平台资源</div>
          <div class="flex items-center justify-center h-40 text-gray-400">
            <div class="text-center">
              <div class="text-4xl mb-2">🌐</div>
              <div class="text-sm">点击上方按钮导入平台</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tabs = [
  { id: 'version', name: '版本', icon: 'Grass.png', path: '' },
  { id: 'loader', name: '加载器', icon: 'Anvil.png', path: 'loader/' },
  { id: 'platform', name: '平台', icon: 'CommandBlock.png', path: '' },
]

const currentTab = ref('version')

const getTabIcon = (fileName, path = '') => {
  return new URL(`../../assets/icons/minecraft/${path}${fileName}`, import.meta.url).href
}
</script>
