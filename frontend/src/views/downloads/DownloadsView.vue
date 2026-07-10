<template>
  <div class="p-6 bg-gray-50 min-h-screen text-gray-800">
    <template v-if="currentView === 'list'">
      <header class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold tracking-wide text-gray-900">版本管理</h1>

        <div class="flex items-center gap-6">
          <div class="flex bg-gray-100 p-1 rounded-xl border border-gray-200/50">
            <button
              v-for="tab in ['正式版', '快照', '已安装']"
              :key="tab"
              :class="[
                'px-4 py-1.5 text-sm font-medium rounded-lg transition-all',
                tab === '正式版'
                  ? 'bg-zinc-800 text-white shadow-sm'
                  : 'text-gray-500 hover:text-gray-800',
              ]"
            >
              {{ tab }}
            </button>
          </div>

          <div class="flex items-center gap-3">
            <button
              class="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-700 rounded-xl text-sm font-medium shadow-sm transition-colors"
            >
              <span>📁</span> 添加已有文件夹
            </button>
            <button
              @click="fetchVersions"
              :disabled="loading"
              class="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 hover:bg-gray-50 text-gray-700 rounded-xl text-sm font-medium shadow-sm transition-colors disabled:opacity-50"
            >
              <span>🔄</span> {{ loading ? '同步中...' : '刷新' }}
            </button>
          </div>
        </div>
      </header>

      <div
        v-if="downloadProgress > 0"
        class="mb-6 p-4 bg-blue-50 border border-blue-100 rounded-xl shadow-sm"
      >
        <div class="flex justify-between text-sm font-medium text-blue-700 mb-2">
          <span>{{ downloadStep || '正在下载游戏核心及依赖...' }}</span>
          <span>{{ downloadProgress }}%</span>
        </div>
        <div class="w-full bg-blue-200 rounded-full h-2.5">
          <div
            class="bg-blue-600 h-2.5 rounded-full transition-all duration-300"
            :style="{ width: downloadProgress + '%' }"
          ></div>
        </div>
      </div>

      <div
        v-if="versionError"
        class="mb-6 p-4 bg-amber-50 border border-amber-100 rounded-xl text-sm text-amber-800"
      >
        {{ versionError }}
      </div>

      <div v-if="loading" class="space-y-3">
        <div class="h-36 bg-gray-200/60 animate-pulse rounded-xl"></div>
        <div v-for="i in 4" :key="i" class="h-20 bg-gray-200/60 animate-pulse rounded-xl"></div>
      </div>

      <template v-else>
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 mb-6">
          <h2 class="text-base font-bold text-gray-800 mb-4 tracking-tight">最新版本</h2>

          <div class="space-y-4">
            <div
              v-if="latestRelease"
              @click="enterDownloadDetail(latestRelease)"
              class="flex justify-between items-center group cursor-pointer"
            >
              <div class="flex items-center gap-4">
                <img
                  :src="getStaticIcon('Grass.png')"
                  alt="Release"
                  class="w-11 h-11 flex-shrink-0 object-contain rounded-lg"
                />
                <div class="flex flex-col">
                  <span
                    class="text-base font-medium text-gray-900 group-hover:text-blue-600 transition-colors"
                    >{{ latestRelease.versionId || latestRelease.name }}</span
                  >
                  <span class="text-xs text-gray-400 font-normal mt-0.5"
                    >最新正式版，发布于 {{ formatDateTime(latestRelease.releaseTime) }}</span
                  >
                </div>
              </div>
              <span
                class="text-xs text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-100 px-2 py-1 rounded"
                >进入下载配置</span
              >
            </div>

            <div
              v-if="latestSnapshot"
              @click="enterDownloadDetail(latestSnapshot)"
              class="flex justify-between items-center group cursor-pointer"
            >
              <div class="flex items-center gap-4">
                <img
                  :src="getStaticIcon('CommandBlock.png')"
                  alt="Snapshot"
                  class="w-11 h-11 flex-shrink-0 object-contain rounded-lg"
                />
                <div class="flex flex-col">
                  <span
                    class="text-base font-medium text-gray-900 group-hover:text-blue-600 transition-colors"
                    >{{ latestSnapshot.versionId || latestSnapshot.name }}</span
                  >
                  <span class="text-xs text-gray-400 font-normal mt-0.5"
                    >最新预览版，发布于 {{ formatDateTime(latestSnapshot.releaseTime) }}</span
                  >
                </div>
              </div>
              <span
                class="text-xs text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity bg-gray-100 px-2 py-1 rounded"
                >进入下载配置</span
              >
            </div>
          </div>
        </div>

        <div class="space-y-4">
          <div
            v-for="group in versionGroups"
            :key="group.id"
            class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden"
          >
            <div
              @click="toggleGroup(group.id)"
              class="flex justify-between items-center p-4 cursor-pointer hover:bg-gray-50/60 transition-colors select-none"
            >
              <span class="text-base font-bold text-gray-800">{{ group.title }}</span>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="2.5"
                stroke="currentColor"
                :class="[
                  'w-5 h-5 text-gray-400 transition-transform duration-200',
                  activeGroups.includes(group.id) ? 'rotate-180' : '',
                ]"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                />
              </svg>
            </div>

            <div
              v-show="activeGroups.includes(group.id)"
              class="border-t border-gray-100 bg-gray-50/30 p-4 space-y-3"
            >
              <div
                v-for="version in getVersionsByGroup(group.id)"
                :key="version.id || version.versionId"
                @click.stop="enterDownloadDetail(version)"
                class="flex items-center justify-between p-4 bg-white border border-gray-100 hover:border-gray-200 rounded-xl shadow-sm hover:shadow-md transition-all duration-200 cursor-pointer group/item"
              >
                <div class="flex items-center gap-4">
                  <img
                    :src="getVersionIcon(version.type)"
                    alt="Version Icon"
                    class="w-12 h-12 flex-shrink-0 object-contain rounded-lg"
                  />

                  <div class="flex flex-col gap-0.5">
                    <span
                      class="text-base font-bold text-gray-900 tracking-tight group-hover/item:text-blue-600 transition-colors"
                    >
                      {{ version.versionId || version.name }}
                    </span>
                    <div class="text-xs text-gray-400 font-normal">
                      <span class="text-gray-500">{{ translateType(version.type) }}</span>
                      <span class="mx-1.5">·</span>
                      {{ formatDateTime(version.releaseTime) }}
                    </div>
                  </div>
                </div>

                <div
                  class="flex items-center gap-3 text-gray-400 group-hover/item:text-gray-600 transition-colors pr-1"
                >
                  <span
                    class="opacity-0 group-hover/item:opacity-100 transition-opacity text-xs bg-gray-100 px-2 py-1 rounded text-gray-500"
                  >
                    进入配置
                  </span>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke-width="2.5"
                    stroke="currentColor"
                    class="w-4 h-4"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      d="M8.25 4.5l7.5 7.5-7.5 7.5"
                    />
                  </svg>
                </div>
              </div>

              <div
                v-if="getVersionsByGroup(group.id).length === 0"
                class="text-center py-8 text-xs text-gray-400"
              >
                暂无该分类下的版本数据
              </div>
            </div>
          </div>
        </div>
      </template>
    </template>

    <template v-else-if="currentView === 'detail'">
      <div class="flex flex-col items-center relative mb-8">
        <button
          @click="currentView = 'list'"
          class="absolute left-0 top-1 text-gray-400 hover:text-gray-700 transition-colors text-xl font-medium flex items-center gap-1"
        >
          <span>&lt;</span>
        </button>

        <img
          :src="getVersionIcon(selectedVersion.type)"
          class="w-16 h-16 object-contain mb-2"
          alt="Version Big Icon"
        />
        <input
          v-model="customVersionName"
          class="text-2xl font-bold text-gray-900 text-center bg-transparent border-b border-gray-200/60 focus:border-zinc-800 focus:outline-none py-1 min-w-[200px] transition-colors"
          placeholder="版本名称"
        />
        <span class="text-xs text-gray-400 mt-0.5">{{ translateType(selectedVersion.type) }}</span>
      </div>

      <div class="mb-6">
        <h2 class="text-sm font-medium text-gray-500 mb-2">下载源</h2>
        <label
          class="flex justify-between items-center p-4 bg-gray-100/70 border border-gray-200/60 rounded-xl cursor-pointer select-none"
        >
          <div class="flex items-center gap-3">
            <input type="radio" checked disabled class="w-4 h-4 text-zinc-800 focus:ring-0" />
            <span class="text-sm font-bold text-gray-800">Mojang 官方源</span>
          </div>
          <span class="text-xs text-gray-400">官方直连，海外服务器</span>
        </label>
      </div>

      <div class="mb-8">
        <h2 class="text-sm font-medium text-gray-500 mb-2">模组加载器</h2>
        <div class="space-y-3">
          <div
            class="flex flex-col border rounded-xl overflow-hidden transition-all duration-200 bg-white"
            :class="[
              selectedLoader === 'vanilla' ? 'border-zinc-800 shadow-sm' : 'border-gray-200/60',
            ]"
          >
            <div
              class="flex justify-between items-center p-4 cursor-pointer select-none hover:bg-gray-50/50"
              @click="handleLoaderSelect('vanilla')"
            >
              <div class="flex items-center gap-4">
                <img :src="getStaticIcon('Grass.png')" class="w-9 h-9 object-contain rounded" />
                <div class="flex flex-col">
                  <span class="text-sm font-bold text-gray-800">不安装（原版）</span>
                  <span class="text-xs text-gray-400 mt-0.5"
                    >纯净的Minecraft，无任何模组加载器</span
                  >
                </div>
              </div>
              <div v-if="selectedLoader === 'vanilla'" class="text-zinc-800 font-bold text-lg">
                ✓
              </div>
            </div>
          </div>

          <div
            class="flex flex-col border rounded-xl overflow-hidden transition-all duration-200 bg-white"
            :class="[
              selectedLoader === 'forge' ? 'border-zinc-800 shadow-sm' : 'border-gray-200/60',
            ]"
          >
            <div
              class="flex justify-between items-center p-4 cursor-pointer select-none hover:bg-gray-50/50"
              @click="handleLoaderSelect('forge')"
            >
              <div class="flex items-center gap-4">
                <img :src="getLoaderIcon('Anvil.png')" class="w-9 h-9 object-contain rounded" />
                <div class="flex flex-col">
                  <div class="flex items-center gap-2">
                    <span class="text-sm font-bold text-gray-800">Forge</span>
                    <span
                      class="text-xs px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-600 font-medium scale-95"
                    >
                      {{ loaderSelectedVersions['forge'] || '匹配中...' }}
                    </span>
                  </div>
                  <span class="text-xs text-gray-400 mt-0.5">Forge 模组加载核心组件</span>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <div v-if="selectedLoader === 'forge'" class="text-zinc-800 font-bold text-md">
                  ✓
                </div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2.5"
                  stroke="currentColor"
                  :class="[
                    'w-4 h-4 text-gray-400 transition-transform duration-200',
                    expandedLoader === 'forge' ? 'rotate-180' : '',
                  ]"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </div>
            </div>

            <div
              v-show="expandedLoader === 'forge' && allLoaderMap['forge'].length > 0"
              class="bg-gray-50 border-t border-gray-100 p-4 space-y-2"
            >
              <div class="text-xs font-semibold text-gray-400 mb-1">
                全部版本 ({{ allLoaderMap['forge'].length }})
              </div>
              <div class="max-h-40 overflow-y-auto space-y-1 pr-1 custom-scrollbar">
                <div
                  v-for="(v, index) in allLoaderMap['forge']"
                  :key="v"
                  @click.stop="loaderSelectedVersions['forge'] = v"
                  class="flex justify-between items-center px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors"
                  :class="[
                    loaderSelectedVersions['forge'] === v
                      ? 'bg-zinc-100 text-zinc-900 font-medium'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <div class="flex items-center gap-2">
                    <img :src="getLoaderIcon('Anvil.png')" class="w-4 h-4 object-contain" />
                    <span>{{ v }}</span>
                    <span
                      v-if="index === 0"
                      class="text-[10px] bg-blue-100 text-blue-700 px-1.5 py-0.2 rounded font-normal scale-90 origin-left"
                      >最新推荐</span
                    >
                  </div>
                  <span v-if="loaderSelectedVersions['forge'] === v" class="text-xs text-zinc-800"
                    >●</span
                  >
                </div>
              </div>
            </div>
          </div>

          <div
            class="flex flex-col border rounded-xl overflow-hidden transition-all duration-200 bg-white"
            :class="[
              selectedLoader === 'neoforge' ? 'border-zinc-800 shadow-sm' : 'border-gray-200/60',
            ]"
          >
            <div
              class="flex justify-between items-center p-4 cursor-pointer select-none hover:bg-gray-50/50"
              @click="handleLoaderSelect('neoforge')"
            >
              <div class="flex items-center gap-4">
                <img :src="getLoaderIcon('NeoForge.png')" class="w-9 h-9 object-contain rounded" />
                <div class="flex flex-col">
                  <div class="flex items-center gap-2">
                    <span class="text-sm font-bold text-gray-800">NeoForge</span>
                    <span
                      class="text-xs px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-600 font-medium scale-95"
                    >
                      {{ loaderSelectedVersions['neoforge'] || '匹配中...' }}
                    </span>
                  </div>
                  <span class="text-xs text-gray-400 mt-0.5">NeoForge 新代模组加载核心组件</span>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <div v-if="selectedLoader === 'neoforge'" class="text-zinc-800 font-bold text-md">
                  ✓
                </div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2.5"
                  stroke="currentColor"
                  :class="[
                    'w-4 h-4 text-gray-400 transition-transform duration-200',
                    expandedLoader === 'neoforge' ? 'rotate-180' : '',
                  ]"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </div>
            </div>

            <div
              v-show="expandedLoader === 'neoforge' && allLoaderMap['neoforge'].length > 0"
              class="bg-gray-50 border-t border-gray-100 p-4 space-y-2"
            >
              <div class="text-xs font-semibold text-gray-400 mb-1">
                全部版本 ({{ allLoaderMap['neoforge'].length }})
              </div>
              <div class="max-h-40 overflow-y-auto space-y-1 pr-1 custom-scrollbar">
                <div
                  v-for="(v, index) in allLoaderMap['neoforge']"
                  :key="v"
                  @click.stop="loaderSelectedVersions['neoforge'] = v"
                  class="flex justify-between items-center px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors"
                  :class="[
                    loaderSelectedVersions['neoforge'] === v
                      ? 'bg-zinc-100 text-zinc-900 font-medium'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <div class="flex items-center gap-2">
                    <img :src="getLoaderIcon('NeoForge.png')" class="w-4 h-4 object-contain" />
                    <span>{{ v }}</span>
                    <span
                      v-if="index === 0"
                      class="text-[10px] bg-blue-100 text-blue-700 px-1.5 py-0.2 rounded font-normal scale-90 origin-left"
                      >最新推荐</span
                    >
                  </div>
                  <span
                    v-if="loaderSelectedVersions['neoforge'] === v"
                    class="text-xs text-zinc-800"
                    >●</span
                  >
                </div>
              </div>
            </div>
          </div>

          <div
            class="flex flex-col border rounded-xl overflow-hidden transition-all duration-200 bg-white"
            :class="[
              selectedLoader === 'fabric' ? 'border-zinc-800 shadow-sm' : 'border-gray-200/60',
            ]"
          >
            <div
              class="flex justify-between items-center p-4 cursor-pointer select-none hover:bg-gray-50/50"
              @click="handleLoaderSelect('fabric')"
            >
              <div class="flex items-center gap-4">
                <img :src="getLoaderIcon('Fabric.png')" class="w-9 h-9 object-contain rounded" />
                <div class="flex flex-col">
                  <div class="flex items-center gap-2">
                    <span class="text-sm font-bold text-gray-800">Fabric</span>
                    <span
                      class="text-xs px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-600 font-medium scale-95"
                    >
                      {{ loaderSelectedVersions['fabric'] || '匹配中...' }}
                    </span>
                  </div>
                  <span class="text-xs text-gray-400 mt-0.5">轻量化 Fabric 高性能加载器</span>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <div v-if="selectedLoader === 'fabric'" class="text-zinc-800 font-bold text-md">
                  ✓
                </div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2.5"
                  stroke="currentColor"
                  :class="[
                    'w-4 h-4 text-gray-400 transition-transform duration-200',
                    expandedLoader === 'fabric' ? 'rotate-180' : '',
                  ]"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </div>
            </div>

            <div
              v-show="expandedLoader === 'fabric' && fabricVersionsDetail.length > 0"
              class="bg-gray-50 border-t border-gray-100 p-4"
            >
              <div class="text-xs text-gray-400 mb-3 font-medium">Fabric 核心版本</div>
              <div class="max-h-32 overflow-y-auto space-y-1 pr-1 custom-scrollbar mb-4">
                <div
                  v-for="(item, index) in fabricVersionsDetail"
                  :key="item.version"
                  @click.stop="handleFabricVersionSelect(item.version)"
                  class="flex justify-between items-center px-3 py-2 rounded-lg cursor-pointer transition-colors"
                  :class="[
                    loaderSelectedVersions['fabric'] === item.version
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <div class="flex items-center gap-3">
                    <img :src="getLoaderIcon('Fabric.png')" class="w-4 h-4 object-contain" />
                    <div class="flex flex-col">
                      <div class="flex items-center gap-2">
                        <span class="text-sm font-medium">{{ item.version }}</span>
                        <span
                          v-if="item.recommended"
                          class="text-[10px] bg-blue-100 text-blue-700 px-1.5 py-0.2 rounded font-normal scale-90 origin-left"
                        >
                          最新推荐</span
                        >
                      </div>
                      <span v-if="item.releaseTime" class="text-xs text-gray-400 mt-0.5">
                        {{ item.recommended ? '最新版' : '' }}，发布于 {{ formatFabricReleaseTime(item.releaseTime) }}
                      </span>
                    </div>
                  </div>
                  <span
                    v-if="loaderSelectedVersions['fabric'] === item.version"
                    class="text-[10px] text-zinc-800"
                    >●</span
                  >
                </div>
              </div>

              <div class="border-t border-gray-200 pt-3">
                <div class="flex items-center justify-between mb-3">
                  <div class="flex items-center gap-2">
                    <span class="text-xs text-gray-400 font-medium">配套 Fabric API（可选依赖）</span>
                  </div>
                  <button
                    class="text-[10px] bg-zinc-100 text-zinc-600 px-1.5 py-0.2 rounded hover:bg-zinc-200 transition-colors"
                    @click.stop="handleFabricApiAutoMatch()"
                  >
                    自动匹配
                  </button>
                </div>
                <div
                  v-if="fabricApiMap.list.length > 0"
                  class="max-h-32 overflow-y-auto space-y-1 pr-1 custom-scrollbar"
                >
                  <div
                    @click.stop="fabricApiMap.selectedVersion = 'none'"
                    class="flex justify-between items-center px-3 py-1.5 rounded-lg cursor-pointer transition-colors"
                    :class="[
                      fabricApiMap.selectedVersion === 'none'
                        ? 'bg-zinc-200/70 text-zinc-900 font-bold'
                        : 'hover:bg-gray-100 text-gray-600',
                    ]"
                  >
                    <div class="flex items-center gap-3">
                      <img :src="getLoaderIcon('Barrier.png')" class="w-4 h-4 object-contain" />
                      <span>不安装 Fabric API 扩展</span>
                    </div>
                    <span
                      v-if="fabricApiMap.selectedVersion === 'none'"
                      class="text-[10px] text-zinc-800"
                      >●</span
                    >
                  </div>

                  <div
                    v-for="(api, index) in fabricApiMap.list"
                    :key="api"
                    @click.stop="fabricApiMap.selectedVersion = api"
                    class="flex justify-between items-center px-3 py-1.5 rounded-lg cursor-pointer transition-colors"
                    :class="[
                      fabricApiMap.selectedVersion === api
                        ? 'bg-zinc-200/70 text-zinc-900 font-bold'
                        : 'hover:bg-gray-100 text-gray-600',
                    ]"
                  >
                    <div class="flex items-center gap-3">
                      <img :src="getLoaderIcon('Fabric.png')" class="w-4 h-4 object-contain" />
                      <div class="flex items-center gap-2">
                        <span>{{ api }}</span>
                      <span
                        v-if="index === 0"
                        class="text-[9px] bg-blue-200 text-blue-700 px-1 rounded font-normal"
                        >推荐匹配</span
                      >
                      </div>
                    </div>
                    <span
                      v-if="fabricApiMap.selectedVersion === api"
                      class="text-[10px] text-zinc-800"
                      >●</span
                    >
                  </div>
                </div>
                <div v-else class="text-[11px] text-gray-400/70 italic pl-3 py-1">
                  当前游戏版本暂无配套 Fabric API 数据
                </div>
              </div>
            </div>
          </div>

          <div
            class="flex flex-col border rounded-xl overflow-hidden transition-all duration-200 bg-white"
            :class="[
              selectedLoader === 'optifine' ? 'border-zinc-800 shadow-sm' : 'border-gray-200/60',
            ]"
          >
            <div
              class="flex justify-between items-center p-4 cursor-pointer select-none hover:bg-gray-50/50"
              @click="handleLoaderSelect('optifine')"
            >
              <div class="flex items-center gap-4">
                <img :src="getLoaderIcon('GrassPath.png')" class="w-9 h-9 object-contain rounded" />
                <div class="flex flex-col">
                  <div class="flex items-center gap-2">
                    <span class="text-sm font-bold text-gray-800">OptiFine</span>
                    <span
                      class="text-xs px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-600 font-medium scale-95"
                    >
                      {{ loaderSelectedVersions['optifine'] || '匹配中...' }}
                    </span>
                  </div>
                  <span class="text-xs text-gray-400 mt-0.5">OptiFine 经典高清优化及光影扩展</span>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <div v-if="selectedLoader === 'optifine'" class="text-zinc-800 font-bold text-md">
                  ✓
                </div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2.5"
                  stroke="currentColor"
                  :class="[
                    'w-4 h-4 text-gray-400 transition-transform duration-200',
                    expandedLoader === 'optifine' ? 'rotate-180' : '',
                  ]"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </div>
            </div>

            <div
              v-show="expandedLoader === 'optifine' && allLoaderMap['optifine'].length > 0"
              class="bg-gray-50 border-t border-gray-100 p-4 space-y-2"
            >
              <div class="text-xs font-semibold text-gray-400 mb-1">
                全部版本 ({{ allLoaderMap['optifine'].length }})
              </div>
              <div class="max-h-40 overflow-y-auto space-y-1 pr-1 custom-scrollbar">
                <div
                  v-for="(v, index) in allLoaderMap['optifine']"
                  :key="v"
                  @click.stop="loaderSelectedVersions['optifine'] = v"
                  class="flex justify-between items-center px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors"
                  :class="[
                    loaderSelectedVersions['optifine'] === v
                      ? 'bg-zinc-100 text-zinc-900 font-medium'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <div class="flex items-center gap-2">
                    <img :src="getLoaderIcon('GrassPath.png')" class="w-4 h-4 object-contain" />
                    <span>{{ v }}</span>
                    <span
                      v-if="index === 0"
                      class="text-[10px] bg-blue-100 text-blue-700 px-1.5 py-0.2 rounded font-normal scale-90 origin-left"
                      >最新推荐</span
                    >
                  </div>
                  <span
                    v-if="loaderSelectedVersions['optifine'] === v"
                    class="text-xs text-zinc-800"
                    >●</span
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-between items-center border-t border-gray-100 pt-4">
        <button
          @click="currentView = 'list'"
          class="px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-600 rounded-xl text-sm font-medium transition-colors"
        >
          取消
        </button>
        <button
          @click="executeDownload"
          class="px-6 py-2.5 bg-zinc-900 hover:bg-zinc-800 text-white rounded-xl text-sm font-medium shadow-sm transition-colors"
        >
          确认下载
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import axios from 'axios' // 已修复：从标准 'axios' 库导入

const API_BASE = 'http://localhost:8080/api'
const WS_BASE = 'ws://localhost:8080/ws'

const versionGroups = [
  { id: 'release', title: '正式版' },
  { id: 'snapshot', title: '预览版' },
  { id: 'old', title: '远古版' },
  { id: 'april_fools', title: '愚人节版' },
]

const currentView = ref('list')
const selectedVersion = ref(null)
const selectedLoader = ref('vanilla')
const expandedLoader = ref('')
const customVersionName = ref('')

const allLoaderMap = reactive({ forge: [], neoforge: [], fabric: [], optifine: [] })
const loaderSelectedVersions = reactive({ forge: '', neoforge: '', fabric: '', optifine: '' })

const fabricApiMap = reactive({
  list: [],
  selectedVersion: '',
})

const fabricVersionsDetail = ref([])

const activeGroups = ref(['release'])
const loading = ref(false)
const versions = ref([])
const versionError = ref('')
const downloadProgress = ref(0)
const downloadStep = ref('')
const currentTaskId = ref('')
let ws = null

// ================== 图标路径获取（已修复：采用当前文件往上两级的相对路径） ==================
const getStaticIcon = (fileName) => {
  return new URL(`../../assets/icons/minecraft/${fileName}`, import.meta.url).href
}

const getLoaderIcon = (fileName) => {
  return new URL(`../../assets/icons/minecraft/loader/${fileName}`, import.meta.url).href
}
// ===================================================================================

const enterDownloadDetail = async (version) => {
  if (downloadProgress.value > 0 && downloadProgress.value < 100) {
    alert('已有任务在下载中，请稍候')
    return
  }
  selectedVersion.value = version
  selectedLoader.value = 'vanilla'
  expandedLoader.value = ''
  customVersionName.value = version.versionId || version.name || ''

  const loaders = ['forge', 'neoforge', 'fabric', 'optifine']
  loaders.forEach((key) => {
    allLoaderMap[key] = []
    loaderSelectedVersions[key] = ''
  })

  fabricApiMap.list = []
  fabricApiMap.selectedVersion = ''
  fabricVersionsDetail.value = []

  currentView.value = 'detail'

  const mcVersion = version.versionId || version.name

  try {
    const apiResponse = await axios.get(`${API_BASE}/download/fabric-api/versions`, {
      params: { mcVersion: mcVersion },
    })
    if (apiResponse.data && apiResponse.data.length > 0) {
      fabricApiMap.list = apiResponse.data
      fabricApiMap.selectedVersion = apiResponse.data[0]
    }
  } catch (err) {
    console.warn('静默获取 Fabric API 数据失败，应用本地兜底策略:', err)
    fabricApiMap.list = ['0.92.0+1.20.1', '0.91.0+1.20.1']
    fabricApiMap.selectedVersion = fabricApiMap.list[0]
  }

  try {
    const detailResponse = await axios.get(`${API_BASE}/download/fabric/versions/detail`, {
      params: { mcVersion: mcVersion },
    })
    if (detailResponse.data && detailResponse.data.length > 0) {
      fabricVersionsDetail.value = detailResponse.data
    }
  } catch (err) {
    console.warn('静默获取 Fabric 详细版本数据失败:', err)
  }

  loaders.forEach(async (type) => {
    try {
      const response = await axios.get(`${API_BASE}/download/${type}/versions`, {
        params: { mcVersion: mcVersion },
      })
      if (response.data && response.data.length > 0) {
        allLoaderMap[type] = response.data

        if (type === 'fabric' && fabricVersionsDetail.value.length > 0) {
          const recommended = fabricVersionsDetail.value.find(item => item.recommended)
          loaderSelectedVersions[type] = recommended ? recommended.version : response.data[0]
        } else {
          loaderSelectedVersions[type] = response.data[0]
        }
      } else {
        allLoaderMap[type] = ['暂无可用匹配版本']
        loaderSelectedVersions[type] = '暂无可用匹配版本'
      }
    } catch (error) {
      console.warn(`静默匹配 ${type} 失败，启用本地数据兜底:`, error)
      if (type === 'forge') allLoaderMap[type] = ['65.0.3', '65.0.2', '65.0.1']
      else if (type === 'neoforge') allLoaderMap[type] = ['26.1.2.76', '26.1.2.70']
      else if (type === 'fabric') allLoaderMap[type] = ['0.19.3', '0.19.2']
      else allLoaderMap[type] = ['HD_U_Z']

      loaderSelectedVersions[type] = allLoaderMap[type][0]
    }
  })
}

const handleLoaderSelect = (loaderType) => {
  selectedLoader.value = loaderType
  const versionId = selectedVersion.value?.versionId || selectedVersion.value?.name || ''
  if (!customVersionName.value || customVersionName.value.startsWith('Forge-') || 
      customVersionName.value.startsWith('NeoForge-') || 
      customVersionName.value.startsWith('Fabric-') || 
      customVersionName.value.startsWith('OptiFine-') ||
      customVersionName.value === versionId) {
    const loaderPrefixMap = {
      vanilla: '',
      forge: 'Forge-',
      neoforge: 'NeoForge-',
      fabric: 'Fabric-',
      optifine: 'OptiFine-',
    }
    customVersionName.value = `${loaderPrefixMap[loaderType] || ''}${versionId}`
  }
  if (expandedLoader.value === loaderType) {
    expandedLoader.value = ''
  } else {
    expandedLoader.value = loaderType
  }
}

const handleFabricVersionSelect = (version) => {
  loaderSelectedVersions['fabric'] = version
  if (fabricApiMap.list.length > 0 && fabricApiMap.selectedVersion === '') {
    fabricApiMap.selectedVersion = fabricApiMap.list[0]
  }
}

const handleFabricApiAutoMatch = () => {
  if (fabricApiMap.list.length > 0) {
    fabricApiMap.selectedVersion = fabricApiMap.list[0]
  }
}

const executeDownload = async () => {
  const vName = selectedVersion.value.versionId || selectedVersion.value.name
  const chosenLoaderVersion =
    selectedLoader.value !== 'vanilla' ? loaderSelectedVersions[selectedLoader.value] : ''

  const chosenApiVersion =
    selectedLoader.value === 'fabric' && fabricApiMap.selectedVersion !== 'none'
      ? fabricApiMap.selectedVersion
      : ''

  try {
    const response = await axios.post(`${API_BASE}/download/install`, {
      versionId: vName,
      type: selectedVersion.value.type,
      loaderType: selectedLoader.value,
      loaderVersion: chosenLoaderVersion,
      fabricApiVersion: chosenApiVersion,
    })

    downloadProgress.value = 1
    downloadStep.value = '任务已创建'
    currentTaskId.value = response.data?.taskId || ''
    currentView.value = 'list'
  } catch (error) {
    console.error('发起下载任务失败:', error)
    alert('发起下载失败，请检查网络或后端服务')
  }
}

const getVersionIcon = (type) => {
  let fileName = 'Grass.png'
  if (type) {
    const t = type.toLowerCase()
    if (t === 'release' || t === '正式版') fileName = 'Grass.png'
    else if (t === 'snapshot' || t === '快照' || t === 'preview' || t === '预览版')
      fileName = 'CommandBlock.png'
    else if (t === 'old_alpha' || t === 'old_beta' || t === 'old' || t === '远古版')
      fileName = 'CobbleStone.png'
  }
  return getStaticIcon(fileName)
}

const formatDateTime = (rawTime) => {
  if (!rawTime) return '未知时间'
  try {
    const date = new Date(rawTime)
    if (isNaN(date.getTime())) return rawTime
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}/${month}/${day} ${hours}:${minutes}`
  } catch (error) {
    return rawTime
  }
}

const formatFabricReleaseTime = (rawTime) => {
  return formatDateTime(rawTime)
}

const toggleGroup = (id) => {
  const index = activeGroups.value.indexOf(id)
  if (index > -1) activeGroups.value.splice(index, 1)
  else activeGroups.value.push(id)
}

const translateType = (type) => {
  if (!type) return '正式版'
  const t = type.toLowerCase()
  if (t === 'release' || t === '正式版') return '正式版'
  if (t === 'snapshot' || t === '快照' || t === 'preview' || t === '预览版') return '预览版'
  if (t === 'old_alpha' || t === 'old_beta' || t === 'old' || t === '远古版') return '远古版'
  if (t === 'april_fools' || t === '愚人节版') return '愚人节版'
  return type
}

const fetchVersions = async () => {
  loading.value = true
  versionError.value = ''
  try {
    const response = await axios.get(`${API_BASE}/minecraft/versions`)
    versions.value = Array.isArray(response.data) ? response.data : []
    if (versions.value.length === 0) {
      versions.value = fallbackVersions()
      versionError.value = '后端暂未返回版本数据，已显示内置版本列表'
    } else if (versions.value.some((version) => version.fallback)) {
      versionError.value = '无法连接 Mojang 版本清单，当前显示离线兜底列表'
    }
  } catch (error) {
    console.error('获取版本列表失败:', error)
    versions.value = fallbackVersions()
    versionError.value = '后端服务未连接，当前显示离线兜底列表'
  } finally {
    loading.value = false
  }
}

const fallbackVersions = () => [
  createFallbackVersion('1.21.8', 'release', '2025-07-17T12:00:00+00:00'),
  createFallbackVersion('1.21.7', 'release', '2025-06-30T12:00:00+00:00'),
  createFallbackVersion('1.21.6', 'release', '2025-06-17T12:00:00+00:00'),
  createFallbackVersion('1.21.5', 'release', '2025-03-25T12:00:00+00:00'),
  createFallbackVersion('1.21.4', 'release', '2024-12-03T12:00:00+00:00'),
  createFallbackVersion('1.21.1', 'release', '2024-08-08T12:00:00+00:00'),
  createFallbackVersion('1.20.6', 'release', '2024-04-29T12:00:00+00:00'),
  createFallbackVersion('1.20.4', 'release', '2023-12-07T12:00:00+00:00'),
  createFallbackVersion('1.20.1', 'release', '2023-06-12T12:00:00+00:00'),
  createFallbackVersion('1.19.4', 'release', '2023-03-14T12:00:00+00:00'),
  createFallbackVersion('25w31a', 'snapshot', '2025-07-30T12:00:00+00:00'),
  createFallbackVersion('24w14potato', 'april_fools', '2024-04-01T12:00:00+00:00'),
  createFallbackVersion('1.2.5', 'old_alpha', '2012-04-04T12:00:00+00:00'),
]

const createFallbackVersion = (id, type, releaseTime) => ({
  id,
  versionId: id,
  name: id,
  type,
  releaseTime,
  isInstalled: false,
  fallback: true,
})

const latestRelease = computed(() => {
  return versions.value.find((v) => v.type === 'release' || v.type === '正式版')
})
const latestSnapshot = computed(() => {
  return versions.value.find(
    (v) =>
      v.type === 'snapshot' || v.type === '快照' || v.type === 'preview' || v.type === '预览版',
  )
})

const getVersionsByGroup = (groupId) => {
  return versions.value.filter((v) => {
    const type = (v.type || '').toLowerCase()
    if (groupId === 'release') return type === 'release' || type === '正式版'
    if (groupId === 'snapshot')
      return type === 'snapshot' || type === '快照' || type === 'preview' || type === '预览版'
    if (groupId === 'old')
      return type === 'old_alpha' || type === 'old_beta' || type === 'old' || type === '远古版'
    if (groupId === 'april_fools') return type === 'april_fools' || type === '愚人节版'
    return false
  })
}

const initWebSocket = () => {
  ws = new WebSocket(`${WS_BASE}/minecraft/install`)
  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (currentTaskId.value && data.id && data.id !== currentTaskId.value) {
        return
      }
      if (data && typeof data.progress === 'number') {
        downloadProgress.value = data.progress
        downloadStep.value = data.step || data.status || ''
        if (data.progress >= 100) {
          setTimeout(() => {
            downloadProgress.value = 0
            downloadStep.value = ''
            currentTaskId.value = ''
            fetchVersions()
          }, 1500)
        }
        if (data.status === 'FAILED') {
          alert(data.error || '下载失败')
          downloadProgress.value = 0
          downloadStep.value = ''
          currentTaskId.value = ''
        }
      }
    } catch (e) {
      const progress = parseInt(event.data)
      if (!isNaN(progress)) {
        downloadProgress.value = progress
        if (progress >= 100) {
          setTimeout(() => {
            downloadProgress.value = 0
            downloadStep.value = ''
            currentTaskId.value = ''
            fetchVersions()
          }, 1500)
        }
      }
    }
  }
  ws.onclose = () => {
    setTimeout(initWebSocket, 5000)
  }
}

onMounted(() => {
  fetchVersions()
  initWebSocket()
})

onUnmounted(() => {
  if (ws) ws.close()
})
</script>

<style scoped>
button:focus {
  outline: none;
}
input[type='radio'] {
  accent-color: #18181b;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #e4e4e7;
  border-radius: 10px;
}
</style>
