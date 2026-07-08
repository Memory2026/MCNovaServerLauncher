<script setup lang="ts">
import { ref, onMounted } from 'vue'

const API_BASE = 'http://localhost:8080'

interface JavaInfo {
  name: string

  path: string

  version?: string

  current?: boolean
}

const javaList = ref<JavaInfo[]>([])

const isLoading = ref(false)

const isDetecting = ref(false)

/**
 * 获取 Java 环境列表
 */
const fetchJavaList = async () => {
  isLoading.value = true

  try {
    const response = await fetch(`${API_BASE}/api/java/list`)

    const result = await response.json()

    if (result.code === 0 && result.data) {
      javaList.value = result.data
    } else {
      console.warn('获取 Java 列表失败:', result.message)
    }
  } catch (error) {
    console.error('请求 Java 列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

/**
 * 自动检测系统 Java
 */
const handleAutoDetect = async () => {
  isDetecting.value = true

  try {
    const response = await fetch(
      `${API_BASE}/api/java/detect`,

      {
        method: 'POST',
      },
    )

    const result = await response.json()

    if (result.code === 0) {
      await fetchJavaList()
    } else {
      alert(result.message || '未检测到系统 Java')
    }
  } catch (error) {
    console.error('连接后端失败:', error)


    alert('请求失败，请确保 MCNova 后端服务已正常启动')
  } finally {
    isDetecting.value = false
  }
}

onMounted(() => {
  fetchJavaList()
})
</script>

<template>
  <div>
    <div class="flex justify-between items-center border-b border-gray-100 pb-4 mb-4">
      <div>
        <h2 class="text-xl font-bold text-gray-800">Java 运行环境配置</h2>

        <p class="text-xs text-gray-400 mt-1">在此添加、删除或切换本机的 JDK/JRE 版本</p>

      </div>

      <button
        @click="handleAutoDetect"
        :disabled="isDetecting"
        class="flex items-center gap-2 bg-gradient-to-r from-indigo-500 to-blue-600 hover:from-indigo-600 hover:to-blue-700 text-white font-medium px-4 py-2.5 rounded-xl shadow-md transition-all duration-200 text-sm disabled:opacity-60 disabled:cursor-not-allowed"
      >
        <svg
          v-if="isDetecting"
          class="animate-spin h-4 w-4 text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            class="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            stroke-width="4"
          />

          <path
            class="opacity-75"
            fill="currentColor"
            d="
          M4 12a8 8 0 018-8V0
          C5.373 0 0 5.373 0 12h4z
          "
          />
        </svg>

        <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="
          M21 21l-6-6m2-5
          a7 7 0 11-14 0
          7 7 0 0114 0z
          "
          />
        </svg>

        {{ isDetecting ? '正在扫描系统环境...' : '自动检测系统 Java' }}
      </button>
    </div>

    <div v-if="isLoading" class="py-8 text-center text-sm text-gray-400">
      正在加载 Java 环境列表...
    </div>

    <div
      v-else-if="javaList.length === 0"
      class="py-8 text-center text-sm text-gray-400 bg-gray-50 rounded-xl border border-dashed border-gray-200"
    >
      暂无 Java 环境，请点击右上角自动检测
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="(item, index) in javaList"
        :key="index"
        class="p-4 bg-gray-50 rounded-xl border border-gray-150 flex justify-between items-center transition-all hover:border-gray-300"
      >
        <div>
          <div class="font-medium text-gray-700 flex items-center gap-2">
            {{ item.name || '未命名 Java 环境' }}

            <span
              v-if="item.version"
              class="text-xs bg-gray-200 px-1.5 py-0.5 rounded text-gray-600"
            >
              v{{ item.version }}
            </span>
          </div>

          <div class="text-xs text-gray-400 mt-1">
            路径:
            {{ item.path }}
          </div>
        </div>

        <span
          v-if="item.current"
          class="px-2 py-1 bg-green-50 text-green-600 text-xs rounded-md font-medium"
        >
          当前生效
        </span>

        <button v-else class="text-xs text-blue-600 hover:underline font-medium">
          切换到此版本
        </button>
      </div>
    </div>
  </div>
</template>
