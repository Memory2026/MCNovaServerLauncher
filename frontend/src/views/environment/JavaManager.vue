<script setup lang="ts">
import { ref, onMounted } from 'vue'

/**
 * 后端地址
 *
 * 开发:
 * localhost:8080
 *
 * 打包:
 * Electron 内置 Spring Boot
 */
const API_BASE = import.meta.env.DEV ? 'http://localhost:8080' : 'http://127.0.0.1:8080'

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
 * 通用 HTTP 请求
 */
async function apiRequest(
  url: string,

  options?: RequestInit,
) {
  const controller = new AbortController()

  const timeout = setTimeout(() => {
    controller.abort()
  }, 10000)

  try {
    const response = await fetch(
      API_BASE + url,

      {
        ...options,

        signal: controller.signal,
      },
    )

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    return await response.json()
  } finally {
    clearTimeout(timeout)
  }
}

/**
 * 获取 Java 环境列表
 */
async function fetchJavaList() {
  isLoading.value = true

  try {
    const result: any = await apiRequest('/api/java/list')

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
async function handleAutoDetect() {
  isDetecting.value = true

  try {
    const result: any = await apiRequest(
      '/api/java/detect',

      {
        method: 'POST',
      },
    )

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
        class="flex items-center gap-2 bg-gradient-to-r from-indigo-500 to-blue-600 text-white font-medium px-4 py-2.5 rounded-xl shadow-md transition-all text-sm disabled:opacity-60"
      >
        <span v-if="isDetecting"> 扫描中... </span>

        <span v-else> 自动检测系统 Java </span>
      </button>
    </div>

    <div v-if="isLoading" class="py-8 text-center text-gray-400">正在加载 Java 环境列表...</div>

    <div
      v-else-if="javaList.length === 0"
      class="py-8 text-center text-sm text-gray-400 bg-gray-50 rounded-xl border border-dashed"
    >
      暂无 Java 环境，请点击右上角自动检测
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="(item, index) in javaList"
        :key="index"
        class="p-4 bg-gray-50 rounded-xl border flex justify-between items-center"
      >
        <div>
          <div class="font-medium text-gray-700">
            {{ item.name || '未命名 Java' }}

            <span v-if="item.version" class="text-xs bg-gray-200 px-2 py-1 rounded">
              v{{ item.version }}
            </span>
          </div>

          <div class="text-xs text-gray-400 mt-2">
            路径:

            {{ item.path }}
          </div>
        </div>

        <span v-if="item.current" class="text-xs text-green-600 bg-green-50 px-2 py-1 rounded">
          当前生效
        </span>

        <button v-else class="text-xs text-blue-600">切换到此版本</button>
      </div>
    </div>
  </div>
</template>
