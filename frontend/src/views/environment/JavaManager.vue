<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { RefreshCw, CheckCircle2, AlertCircle, ChevronDown, ChevronUp, Sparkles, Wrench, Download } from '@lucide/vue'
import jdkIcon from '@/assets/icons/jdk/jdk.png'

const API_BASE = import.meta.env.DEV ? 'http://localhost:8080' : 'http://127.0.0.1:8080'

interface JavaInfo {
  javaPath: string
  version: string
  majorVersion: number
  vendor: string
  javacPath?: string
  jdk: boolean
}

interface RecommendResult {
  success: boolean
  message?: string
  recommended?: JavaInfo
  minecraftVersion?: string
  requiredJava?: number
  currentDefault?: JavaInfo
  all?: JavaInfo[]
}

const javaList = ref<JavaInfo[]>([])
const isLoading = ref(false)
const isDetecting = ref(false)
const isSettingDefault = ref(false)
const defaultJava = ref<JavaInfo | null>(null)
const recommendInfo = ref<RecommendResult | null>(null)
const expandedItems = ref<Set<string>>(new Set())
const selectedMcVersion = ref('1.21')

const mcVersions = [
  { label: '1.21+', value: '1.21', java: 21 },
  { label: '1.20.5-1.20.6', value: '1.20.5', java: 21 },
  { label: '1.17-1.20.4', value: '1.17', java: 17 },
  { label: '1.16及以下', value: '1.16', java: 8 },
]

const filteredMcVersions = computed(() => mcVersions)

async function apiRequest(url: string, options?: RequestInit) {
  const controller = new AbortController()
  const timeout = setTimeout(() => {
    controller.abort()
  }, 15000)

  try {
    const response = await fetch(API_BASE + url, {
      headers: {
        'Content-Type': 'application/json',
      },
      ...options,
      signal: controller.signal,
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`)
    }

    return await response.json()
  } finally {
    clearTimeout(timeout)
  }
}

async function fetchJavaList() {
  isLoading.value = true
  try {
    const result: JavaInfo[] = await apiRequest('/api/java/list')
    javaList.value = result
    await fetchDefaultJava()
    await fetchRecommendInfo()
  } catch (error) {
    console.error('获取 Java 列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

async function fetchDefaultJava() {
  try {
    const result: JavaInfo = await apiRequest('/api/java/default')
    defaultJava.value = result
  } catch (error) {
    console.error('获取默认 Java 失败:', error)
  }
}

async function fetchRecommendInfo() {
  try {
    const result: RecommendResult = await apiRequest(`/api/java/recommend?minecraftVersion=${selectedMcVersion.value}`)
    recommendInfo.value = result
  } catch (error) {
    console.error('获取推荐 Java 失败:', error)
  }
}

async function handleAutoDetect() {
  isDetecting.value = true
  try {
    const result: JavaInfo[] = await apiRequest('/api/java/detect', {
      method: 'POST',
    })
    javaList.value = result
    await fetchDefaultJava()
    await fetchRecommendInfo()
  } catch (error) {
    console.error('自动检测失败:', error)
  } finally {
    isDetecting.value = false
  }
}

async function handleSetDefault(java: JavaInfo) {
  isSettingDefault.value = true
  try {
    const result: JavaInfo = await apiRequest('/api/java/default', {
      method: 'POST',
      body: JSON.stringify({ path: java.javaPath }),
    })
    defaultJava.value = result
    await fetchRecommendInfo()
  } catch (error) {
    console.error('设置默认 Java 失败:', error)
  } finally {
    isSettingDefault.value = false
  }
}

function isCurrentDefault(java: JavaInfo): boolean {
  if (!defaultJava.value) return false
  return defaultJava.value.javaPath === java.javaPath
}

function isRecommended(java: JavaInfo): boolean {
  if (!recommendInfo.value?.recommended) return false
  return recommendInfo.value.recommended.javaPath === java.javaPath
}

function toggleExpand(javaPath: string) {
  if (expandedItems.value.has(javaPath)) {
    expandedItems.value.delete(javaPath)
  } else {
    expandedItems.value.add(javaPath)
  }
}

function getVersionDisplay(java: JavaInfo): string {
  const versionMatch = java.version.match(/"([^"]+)"/)
  return versionMatch && versionMatch[1] ? versionMatch[1] : `Java ${java.majorVersion}`
}

function getJavaHome(javaPath: string): string {
  const parts = javaPath.split('/')
  parts.pop()
  parts.pop()
  return parts.join('/')
}

onMounted(() => {
  fetchJavaList()
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-xl font-bold text-gray-800">Java 环境管理</h2>
        <p class="text-xs text-gray-400 mt-1">管理本机 Java 运行环境（JDK/JRE）</p>
      </div>
      <button
        @click="handleAutoDetect"
        :disabled="isDetecting"
        class="flex items-center gap-2 bg-gradient-to-r from-indigo-500 to-blue-600 text-white font-medium px-4 py-2.5 rounded-xl shadow-md transition-all text-sm disabled:opacity-60 hover:shadow-lg hover:scale-[1.02] active:scale-[0.98]"
      >
        <RefreshCw :size="16" :class="{ 'animate-spin': isDetecting }" />
        <span>{{ isDetecting ? '扫描中...' : '自动检测系统 Java' }}</span>
      </button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 space-y-4">
        <div v-if="isLoading" class="py-12 text-center text-gray-400">
          <RefreshCw class="mx-auto animate-spin mb-4" :size="32" />
          <p>正在加载 Java 环境列表...</p>
        </div>

        <div
          v-else-if="javaList.length === 0"
          class="py-12 text-center rounded-2xl border-2 border-dashed border-gray-200 bg-gradient-to-br from-gray-50 to-gray-100"
        >
          <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-200 flex items-center justify-center">
            <AlertCircle :size="32" class="text-gray-400" />
          </div>
          <p class="text-gray-500 font-medium">暂无 Java 环境</p>
          <p class="text-sm text-gray-400 mt-2">请点击右上角按钮自动检测系统 Java</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="java in javaList"
            :key="java.javaPath"
            class="relative overflow-hidden rounded-2xl border transition-all duration-300"
            :class="[
              isCurrentDefault(java)
                ? 'border-indigo-400 shadow-lg shadow-indigo-100'
                : 'border-gray-200 hover:border-gray-300 hover:shadow-md'
            ]"
          >
            <div
              v-if="isCurrentDefault(java)"
              class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-indigo-500 to-blue-600"
            ></div>

            <div
              class="p-5 cursor-pointer"
              @click="toggleExpand(java.javaPath)"
            >
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <div
                    class="w-12 h-12 rounded-xl flex items-center justify-center transition-all duration-300 overflow-hidden bg-white border border-gray-100"
                  >
                    <img
                      :src="jdkIcon"
                      alt="Java"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="font-semibold text-gray-800">{{ getVersionDisplay(java) }}</span>
                      <span
                        v-if="java.jdk"
                        class="text-xs px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 font-medium"
                      >
                        JDK
                      </span>
                      <span
                        v-else
                        class="text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-600 font-medium"
                      >
                        JRE
                      </span>
                      <span
                        v-if="isCurrentDefault(java)"
                        class="flex items-center gap-1 text-xs px-2 py-0.5 rounded-full bg-indigo-100 text-indigo-700 font-medium"
                      >
                        <CheckCircle2 :size="12" />
                        当前默认
                      </span>
                      <span
                        v-if="isRecommended(java) && !isCurrentDefault(java)"
                        class="flex items-center gap-1 text-xs px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 font-medium"
                      >
                        <Sparkles :size="12" />
                        推荐版本
                      </span>
                    </div>
                    <div class="flex items-center gap-3 mt-1">
                      <span class="text-sm text-gray-500">{{ java.vendor }}</span>
                      <span class="text-gray-300">|</span>
                      <span class="text-xs text-gray-400 truncate max-w-xs">{{ java.javaPath }}</span>
                    </div>
                  </div>
                </div>
                <div class="flex items-center gap-3">
                  <button
                    v-if="!isCurrentDefault(java)"
                    @click.stop="handleSetDefault(java)"
                    :disabled="isSettingDefault"
                    class="flex items-center gap-1.5 text-sm font-medium px-3 py-1.5 rounded-lg transition-all duration-200"
                    :class="[
                      isRecommended(java)
                        ? 'bg-emerald-500 text-white hover:bg-emerald-600'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    ]"
                  >
                    <Wrench :size="14" />
                    <span>{{ isSettingDefault ? '设置中...' : '设为默认' }}</span>
                  </button>
                  <button
                    class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors"
                  >
                    <ChevronDown
                      v-if="!expandedItems.has(java.javaPath)"
                      :size="20"
                    />
                    <ChevronUp v-else :size="20" />
                  </button>
                </div>
              </div>

              <Transition
                enter-active-class="transition duration-300 ease-out"
                enter-from-class="opacity-0 -translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-2"
              >
                <div
                  v-if="expandedItems.has(java.javaPath)"
                  class="mt-4 pt-4 border-t border-gray-100 grid grid-cols-1 md:grid-cols-2 gap-4"
                >
                  <div class="p-3 rounded-xl bg-gray-50">
                    <div class="text-xs text-gray-400 mb-1">Java 路径</div>
                    <div class="text-sm text-gray-700 font-mono truncate">{{ java.javaPath }}</div>
                  </div>
                  <div class="p-3 rounded-xl bg-gray-50">
                    <div class="text-xs text-gray-400 mb-1">JAVA_HOME</div>
                    <div class="text-sm text-gray-700 font-mono truncate">{{ getJavaHome(java.javaPath) }}</div>
                  </div>
                  <div v-if="java.javacPath" class="p-3 rounded-xl bg-gray-50">
                    <div class="text-xs text-gray-400 mb-1">javac 路径</div>
                    <div class="text-sm text-gray-700 font-mono truncate">{{ java.javacPath }}</div>
                  </div>
                  <div class="p-3 rounded-xl bg-gray-50">
                    <div class="text-xs text-gray-400 mb-1">主版本号</div>
                    <div class="text-sm text-gray-700 font-mono">Java {{ java.majorVersion }}</div>
                  </div>
                </div>
              </Transition>
            </div>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <div class="rounded-2xl border border-gray-200 bg-white shadow-sm overflow-hidden">
          <div class="p-4 border-b border-gray-100">
            <h3 class="font-semibold text-gray-800 flex items-center gap-2">
              <Sparkles :size="18" class="text-amber-500" />
              版本推荐
            </h3>
          </div>
          <div class="p-4 space-y-4">
            <div>
              <label class="text-xs text-gray-500 mb-2 block">选择 Minecraft 版本</label>
              <select
                v-model="selectedMcVersion"
                @change="fetchRecommendInfo"
                class="w-full px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
              >
                <option
                  v-for="mc in filteredMcVersions"
                  :key="mc.value"
                  :value="mc.value"
                >
                  {{ mc.label }} (推荐 Java {{ mc.java }})
                </option>
              </select>
            </div>

            <div
              v-if="recommendInfo"
              class="space-y-3"
            >
              <div
                v-if="recommendInfo.success && recommendInfo.recommended"
                class="p-4 rounded-xl bg-gradient-to-br from-emerald-50 to-teal-50 border border-emerald-200"
              >
                <div class="flex items-center gap-2 mb-2">
                  <CheckCircle2 :size="16" class="text-emerald-500" />
                  <span class="text-sm font-medium text-emerald-800">推荐 Java {{ recommendInfo.requiredJava }}</span>
                </div>
                <div class="text-xs text-emerald-600">
                  <div class="font-medium">{{ getVersionDisplay(recommendInfo.recommended) }}</div>
                  <div class="mt-1 truncate">{{ recommendInfo.recommended.vendor }}</div>
                </div>
                <button
                  v-if="!isCurrentDefault(recommendInfo.recommended)"
                  @click="handleSetDefault(recommendInfo.recommended)"
                  :disabled="isSettingDefault"
                  class="w-full mt-3 flex items-center justify-center gap-1.5 text-sm font-medium py-2 rounded-lg bg-emerald-500 text-white hover:bg-emerald-600 transition-colors"
                >
                  <Wrench :size="14" />
                  <span>{{ isSettingDefault ? '设置中...' : '使用此版本' }}</span>
                </button>
              </div>

              <div
                v-else-if="!recommendInfo.success"
                class="p-4 rounded-xl bg-gradient-to-br from-amber-50 to-orange-50 border border-amber-200"
              >
                <div class="flex items-center gap-2 mb-2">
                  <AlertCircle :size="16" class="text-amber-500" />
                  <span class="text-sm font-medium text-amber-800">未找到合适的 Java 版本</span>
                </div>
                <div class="text-xs text-amber-600">
                  {{ recommendInfo.message }}
                </div>
              </div>
            </div>

            <div
              v-if="defaultJava"
              class="p-4 rounded-xl bg-gradient-to-br from-indigo-50 to-blue-50 border border-indigo-200"
            >
              <div class="flex items-center gap-2 mb-2">
                <Download :size="16" class="text-indigo-500" />
                <span class="text-sm font-medium text-indigo-800">当前默认 Java</span>
              </div>
              <div class="text-xs text-indigo-600">
                <div class="font-medium">{{ getVersionDisplay(defaultJava) }}</div>
                <div class="mt-1 truncate">{{ defaultJava.vendor }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="rounded-2xl border border-gray-200 bg-white shadow-sm overflow-hidden">
          <div class="p-4 border-b border-gray-100">
            <h3 class="font-semibold text-gray-800">版本兼容性说明</h3>
          </div>
          <div class="p-4 space-y-3">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Minecraft 1.21+</span>
              <span class="font-medium text-emerald-600">Java 21</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Minecraft 1.20.5-1.20.6</span>
              <span class="font-medium text-emerald-600">Java 21</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Minecraft 1.17-1.20.4</span>
              <span class="font-medium text-blue-600">Java 17</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Minecraft 1.16及以下</span>
              <span class="font-medium text-amber-600">Java 8</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
