<script setup lang="ts">
import { ref } from 'vue'
// 引入同目录下的 JavaManagement 组件
import JavaManagement from './JavaManager.vue'

// 控制显示状态
const isManaging = ref(false)
</script>

<template>
  <div class="p-6 relative overflow-hidden z-10">

    <Transition
      mode="out-in"
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="transform translate-y-4 opacity-0"
      enter-to-class="transform translate-y-0 opacity-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="transform translate-y-0 opacity-100"
      leave-to-class="transform translate-y-4 opacity-0"
    >

      <div v-if="!isManaging" key="menu" class="flex items-center justify-between rounded-2xl bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-2">
          <h3 class="text-xl font-bold text-gray-800">Java 环境管理</h3>
          <p class="text-sm text-gray-500">管理本机 Java 运行环境（JDK/JRE）。</p>
        </div>

        <button
          @click="isManaging = true"
          class="bg-blue-600 hover:bg-blue-700 text-white font-medium px-5 py-3 rounded-2xl shadow-[0_4px_14px_0_rgba(37,99,235,0.4)] transition-all duration-200 text-sm"
        >
          Java 环境管理
        </button>
      </div>

      <div v-else key="manager" class="bg-white rounded-2xl p-6 shadow-sm">
        <button
          @click="isManaging = false"
          class="mb-6 text-sm text-gray-500 hover:text-blue-600 flex items-center gap-1 transition-colors font-medium"
        >
          ← 返回主菜单
        </button>

        <JavaManagement />
      </div>

    </Transition>

  </div>
</template>
