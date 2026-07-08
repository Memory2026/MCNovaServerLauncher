<template>
  <div class="min-h-screen bg-slate-50">

    <!-- 固定侧边栏 -->
    <Sidebar
      :is-collapsed="isCollapsed"
      @toggle="toggleSidebar"
    />


    <!-- 右侧区域 -->
    <div
      class="transition-all duration-300"
      :class="
        isCollapsed
        ? 'ml-[112px]'
        : 'ml-[288px]'
      "
    >


      <!-- 顶部栏 -->
      <header
        class="
          fixed
          top-0
          right-0
          h-16
          bg-white
          border-b
          border-gray-100
          flex
          items-center
          px-6
          z-40
          transition-all
          duration-300
        "
        :class="
          isCollapsed
          ? 'left-[112px]'
          : 'left-[288px]'
        "
      >

        <h2 class="text-xl font-semibold text-slate-700">
          主控台
        </h2>

      </header>




      <!-- 页面内容 -->
      <main
        class="
          pt-20
          px-6
          pb-6
          min-h-screen
        "
      >

        <RouterView v-slot="{ Component }">

          <transition
            name="fade"
            mode="out-in"
          >

            <component
              :is="Component"
            />

          </transition>

        </RouterView>


      </main>


    </div>


  </div>
</template>



<script setup lang="ts">

import { ref } from 'vue'

import Sidebar from '@/components/layout/Sidebar.vue'



/*
  从本地读取菜单状态

  true  = 折叠
  false = 展开
*/
const isCollapsed = ref(
  localStorage.getItem('sidebarCollapsed') === 'true'
)



/*
  切换菜单状态
*/
function toggleSidebar(){

  isCollapsed.value =
    !isCollapsed.value


  localStorage.setItem(
    'sidebarCollapsed',
    String(isCollapsed.value)
  )

}


</script>



<style scoped>

.fade-enter-active,
.fade-leave-active {

  transition:
    opacity 0.3s ease;

}


.fade-enter-from,
.fade-leave-to {

  opacity:0;

}

</style>
