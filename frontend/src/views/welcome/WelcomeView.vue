<template>
  <div class="min-h-screen bg-slate-100 flex items-center justify-center">
    <div class="bg-white rounded-2xl shadow-xl p-12 text-center w-full max-w-3xl mx-4 px-6">
      <h1 class="text-4xl font-bold text-blue-600">
        MCNova Server Launcher
      </h1>

      <p class="mt-4 text-gray-500 text-base">
        MCNSL 已成功加载 🎉
      </p>

      <p class="mt-2 text-gray-500 mb-10 text-base whitespace-nowrap">
        MCNSL 一个基于 Vue 3 和 Tailwind CSS 的新一代 Minecraft 服务器启动器。
      </p>

      <button @click="enterDashboard" class="btn-launch">
        开始使用 MCNSL
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 点击按钮平滑跳转至 /dashboard
const enterDashboard = () => {
  router.push('/app/home')
}
</script>

<style scoped>
/* From Uiverse.io by mrhyddenn - 已转换为局部类名 .btn-launch */
.btn-launch {
  position: relative;
  padding: 12px 35px;          /* 稍微加宽了一点点，让“开始开发”四个字更有呼吸感 */
  border-radius: 9px;
  border: 1px solid rgb(61, 106, 255);
  font-size: 15px;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 2px;
  background: transparent;
  color: rgb(61, 106, 255);   /* 初始文字颜色改为蓝色，配合你的白底卡片 */
  overflow: hidden;
  box-shadow: 0 0 0 0 transparent;
  transition: all 0.2s ease-in;
  cursor: pointer;
  display: inline-block;
}

/* 悬浮时：背景变蓝，文字变白，并触发炫酷的外发光阴影 */
.btn-launch:hover {
  background: rgb(61, 106, 255);
  color: #fff;                /* 悬浮时文字变白 */
  box-shadow: 0 0 30px 5px rgba(0, 142, 236, 0.5); /* 稍微降低了一点发光不透明度，在白底上更有质感 */
  transition: all 0.2s ease-out;
}

/* 悬浮时触发一闪而过的流光动画 */
.btn-launch:hover::before {
  animation: sh02 0.6s 0s linear;
}

/* 内部流光线条的初始状态 */
.btn-launch::before {
  content: '';
  display: block;
  width: 0px;
  height: 86%;
  position: absolute;
  top: 7%;
  left: 0%;
  opacity: 0;
  background: #fff;
  box-shadow: 0 0 50px 30px #fff;
  transform: skewX(-20deg);
}

/* 流光动画帧定义 */
@keyframes sh02 {
  from {
    opacity: 0;
    left: 0%;
  }
  50% {
    opacity: 1;
  }
  to {
    opacity: 0;
    left: 100%;
  }
}

/* 点击时的反馈 */
.btn-launch:active {
  box-shadow: 0 0 0 0 transparent;
  transform: scale(0.97);     /* 增加了轻微的点击下压反馈，体验更好 */
  transition: box-shadow 0.2s ease-in;
}
</style>
