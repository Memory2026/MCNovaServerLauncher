<script setup lang="ts">

import { ref, onMounted, onUnmounted, nextTick } from 'vue'

interface LogEntry {
  id: number
  message: string
  level: string
}

const logs = ref<LogEntry[]>([])
const isConnected = ref(false)
const isPolling = ref(false)
const logContainer = ref<HTMLElement | null>(null)
let ws: WebSocket | null = null
let pollingInterval: number | null = null
let lastLogId = 0

function parseLogLevel(message: string): string {
  if (message.includes('[ERROR]') || message.includes('[FATAL]')) return 'error'
  if (message.includes('[WARN]')) return 'warn'
  if (message.includes('[INFO]')) return 'info'
  if (message.includes('[DEBUG]')) return 'debug'
  if (message.includes('[TRACE]')) return 'trace'
  return 'info'
}

function getLogLevelClass(level: string): string {
  switch (level) {
    case 'error': return 'text-red-500'
    case 'warn': return 'text-yellow-600'
    case 'debug': return 'text-gray-500'
    case 'trace': return 'text-gray-600'
    default: return 'text-gray-800'
  }
}

function addLog(message: string) {
  logs.value.push({
    id: ++lastLogId,
    message: message,
    level: parseLogLevel(message)
  })
  if (logs.value.length > 500) {
    logs.value = logs.value.slice(-300)
  }
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
}

function connectWebSocket() {
  if (ws) {
    ws.close()
    ws = null
  }

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  ws = new WebSocket(`${protocol}//${window.location.host}/ws/minecraft/logs`)

  ws.onopen = () => {
    console.log('WebSocket连接成功')
    isConnected.value = true
    isPolling.value = false
    if (pollingInterval) {
      clearInterval(pollingInterval)
      pollingInterval = null
    }
  }

  ws.onmessage = (event) => {
    addLog(event.data)
  }

  ws.onerror = () => {
    console.error('WebSocket连接错误')
    isConnected.value = false
    startPolling()
  }

  ws.onclose = () => {
    console.log('WebSocket连接关闭')
    isConnected.value = false
    if (!pollingInterval) {
      startPolling()
    }
  }
}

function startPolling() {
  if (pollingInterval) return

  isPolling.value = true
  pollingInterval = window.setInterval(() => {
    const apiBase = import.meta.env.PROD ? 'http://localhost:8080/api' : '/api'
    fetch(`${apiBase}/minecraft/logs/latest?lastId=${lastLogId}`)
      .then(response => response.json())
      .then(data => {
        if (data.logs && data.logs.length > 0) {
          data.logs.forEach((log: { id: number; message: string }) => {
            if (log.id > lastLogId) {
              addLog(log.message)
              lastLogId = log.id
            }
          })
        }
      })
      .catch(error => {
        console.error('轮询日志失败:', error)
      })
  }, 1000)
}

function clearLogs() {
  logs.value = []
  lastLogId = 0
}

function openLogFolder() {
  window.open('/api/minecraft/logs/folder')
}

function copyLogs() {
  const allLogs = logs.value.map(log => log.message).join('\n')
  navigator.clipboard.writeText(allLogs).then(() => {
    alert('日志已复制到剪贴板')
  })
}

function analyzeLogs() {
  const allLogs = logs.value.map(log => log.message).join('\n')
  const blob = new Blob([allLogs], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  fetch('https://api.mclo.gs/1/log', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ content: allLogs })
  })
  .then(response => response.json())
  .then(data => {
    if (data.url) {
      window.open(data.url, '_blank')
    }
  })
  .catch(error => {
    console.error('上传日志失败:', error)
    alert('上传日志失败，请重试')
  })
}

onMounted(() => {
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
  if (pollingInterval) {
    clearInterval(pollingInterval)
  }
})

</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">主控台</h1>

    <div class="bg-white rounded-2xl shadow-lg overflow-hidden">
      <div class="flex items-center justify-between p-4 border-b border-gray-100">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <div>
            <h2 class="font-semibold text-gray-800">游戏控制台</h2>
            <p class="text-xs text-gray-500">Game Log Console</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <button @click="openLogFolder" class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg text-sm font-medium text-gray-700 transition-colors flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
            </svg>
            打开日志文件夹
          </button>
          <button @click="copyLogs" class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg text-sm font-medium text-gray-700 transition-colors flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
            </svg>
            复制日志
          </button>
          <button @click="analyzeLogs" class="px-4 py-2 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 rounded-lg text-sm font-medium text-white transition-all shadow-md flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            日志分析
          </button>
        </div>
      </div>

      <div ref="logContainer" class="p-4 h-[550px] overflow-y-auto font-mono text-sm leading-relaxed bg-gray-50">
        <div v-if="logs.length === 0" class="flex flex-col items-center justify-center h-full text-gray-400">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 mb-4 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
          </svg>
          <p class="text-lg">等待日志输出...</p>
          <p class="text-sm mt-2">启动 Minecraft 服务器后将显示日志</p>
        </div>
        <div v-for="log in logs" :key="log.id" class="py-1 border-b border-gray-200 last:border-b-0">
          <span :class="getLogLevelClass(log.level)">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>