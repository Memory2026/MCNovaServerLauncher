<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <h1 class="text-3xl font-bold text-slate-800">欢迎回来，指挥官</h1>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div class="p-6 bg-white rounded-2xl border border-gray-100 shadow-sm">
        <h2 class="text-lg font-semibold text-slate-600">服务器状态</h2>
        <p class="text-4xl font-bold text-blue-600 mt-2">Running</p>
      </div>

      <div class="p-6 bg-white rounded-2xl border border-gray-100 shadow-sm">
        <h2 class="text-lg font-semibold text-slate-600">快速启动</h2>
        <button class="mt-4 w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          进入主控台
        </button>
      </div>
    </div>
  </div>
</template>
