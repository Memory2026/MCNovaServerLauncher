<script setup lang="ts">

import { ref, onMounted, onUnmounted } from 'vue'


interface SystemInfo {

  cpuUsage:number

  totalMemory:number
  usedMemory:number
  memoryUsage:number

  totalDisk:number
  usedDisk:number
  diskUsage:number

  uploadSpeed:number
  downloadSpeed:number

}


const system = ref<SystemInfo>({

  cpuUsage:0,

  totalMemory:0,
  usedMemory:0,
  memoryUsage:0,

  totalDisk:0,
  usedDisk:0,
  diskUsage:0,

  uploadSpeed:0,
  downloadSpeed:0

})


let ws:WebSocket|null=null



// 字节转换
function formatBytes(bytes:number){

  if(bytes===0)
    return '0 B'


  const units=[
    'B',
    'KB',
    'MB',
    'GB',
    'TB'
  ]


  const index =
    Math.floor(
      Math.log(bytes)
      /
      Math.log(1024)
    )


  return (
    (bytes /
      Math.pow(1024,index))
      .toFixed(2)
    +
    ' '
    +
    units[index]
  )

}



// 连接 WebSocket

function connect(){


  ws = new WebSocket(
    `ws://${location.host}/ws/system`
  )


  ws.onmessage=(event)=>{

    system.value =
      JSON.parse(event.data)

  }



  ws.onerror=()=>{

    console.error(
      '系统监控 WebSocket 连接失败'
    )

  }


}




onMounted(()=>{

  connect()

})



onUnmounted(()=>{

  if(ws){

    ws.close()

  }

})


</script>



<template>

  <div class="p-6">


    <h1 class="text-2xl font-bold mb-6">
      服务器状态监控
    </h1>



    <div class="grid grid-cols-1 md:grid-cols-3 gap-5">



      <!-- CPU -->

      <div class="bg-white rounded-xl shadow p-5">

        <h2 class="font-semibold">
          CPU 使用率
        </h2>


        <p class="text-4xl mt-3 text-blue-600">

          {{system.cpuUsage.toFixed(1)}}%

        </p>


        <div class="mt-3 bg-gray-200 rounded-full h-3">

          <div
            class="bg-blue-500 h-3 rounded-full"
            :style="{
width:system.cpuUsage+'%'
}"
          ></div>


        </div>

      </div>





      <!-- 内存 -->

      <div class="bg-white rounded-xl shadow p-5">


        <h2 class="font-semibold">
          内存
        </h2>


        <p class="text-4xl mt-3 text-green-600">

          {{system.memoryUsage.toFixed(1)}}%

        </p>


        <p class="text-sm text-gray-500 mt-2">

          {{formatBytes(system.usedMemory)}}

          /

          {{formatBytes(system.totalMemory)}}

        </p>


        <div class="mt-3 bg-gray-200 rounded-full h-3">


          <div
            class="bg-green-500 h-3 rounded-full"
            :style="{
width:system.memoryUsage+'%'
}"
          ></div>


        </div>


      </div>





      <!-- 磁盘 -->


      <div class="bg-white rounded-xl shadow p-5">


        <h2 class="font-semibold">
          磁盘
        </h2>


        <p class="text-4xl mt-3 text-purple-600">

          {{system.diskUsage.toFixed(1)}}%

        </p>


        <p class="text-sm text-gray-500 mt-2">

          {{formatBytes(system.usedDisk)}}

          /

          {{formatBytes(system.totalDisk)}}

        </p>


        <div class="mt-3 bg-gray-200 rounded-full h-3">


          <div

            class="bg-purple-500 h-3 rounded-full"

            :style="{
width:system.diskUsage+'%'
}"

          ></div>


        </div>


      </div>


    </div>





    <!-- 网络 -->

    <div class="mt-6 bg-white rounded-xl shadow p-5">


      <h2 class="font-semibold mb-4">
        网络速度
      </h2>


      <div class="grid grid-cols-2 gap-4">


        <div>

          上传：

          <span class="text-blue-600">

{{formatBytes(system.uploadSpeed)}}/s

</span>


        </div>



        <div>

          下载：

          <span class="text-green-600">

{{formatBytes(system.downloadSpeed)}}/s

</span>


        </div>


      </div>


    </div>



  </div>

</template>
