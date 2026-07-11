<template>
  <div class="flex gap-6">
    <div class="w-56 flex-shrink-0">
      <div class="bg-white/50 rounded-2xl p-2 border border-gray-100 overflow-hidden">
        <div class="p-3 mb-2">
          <div class="relative">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2"
              stroke="currentColor"
              class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索 Mod..."
              class="w-full pl-9 pr-4 py-2 bg-gray-100 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
            />
          </div>
        </div>

        <div class="space-y-1">
          <div class="overflow-hidden">
            <button
              @click="toggleVersionMenu"
              class="flex items-center justify-between px-4 py-3 rounded-xl text-base font-medium transition-all w-full"
              :class="[
                isVersionMenuOpen
                  ? 'bg-blue-100 text-blue-600'
                  : 'bg-gray-100 text-slate-700 hover:bg-gray-200',
              ]"
            >
              <div class="flex items-center gap-3">
                <img :src="GrassIcon" class="w-5 h-5 object-contain" />
                <span>版本</span>
                <span v-if="selectedVersion" class="text-xs text-gray-400 ml-auto">{{ selectedVersion.versionId }}</span>
              </div>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="2.5"
                stroke="currentColor"
                :class="[
                  'w-5 h-5 transition-transform duration-200',
                  isVersionMenuOpen ? 'rotate-180' : '',
                ]"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                />
              </svg>
            </button>

            <div
              v-show="isVersionMenuOpen"
              class="border-t border-gray-100 bg-gray-50/30"
            >
              <div v-if="versionLoading" class="p-4">
                <div class="space-y-2">
                  <div class="h-10 bg-gray-200/60 animate-pulse rounded-lg"></div>
                  <div class="h-10 bg-gray-200/60 animate-pulse rounded-lg"></div>
                  <div class="h-10 bg-gray-200/60 animate-pulse rounded-lg"></div>
                </div>
              </div>

              <div v-else class="p-2">
                <div
                  @click="selectVersion(null)"
                  class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="[
                    !selectedVersion
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                  </svg>
                  <span class="text-sm font-medium">全部</span>
                </div>
                <div class="max-h-40 overflow-y-auto space-y-1 pr-1 custom-scrollbar mt-1">
                  <div
                    v-for="version in versions"
                    :key="version.versionId || version.id"
                    @click="selectVersion(version)"
                    class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                    :class="[
                      selectedVersion?.versionId === version.versionId
                        ? 'bg-zinc-200/70 text-zinc-900'
                        : 'hover:bg-gray-100 text-gray-600',
                    ]"
                  >
                    <img
                      :src="GrassIcon"
                      class="w-4 h-4 object-contain"
                    />
                    <span class="text-sm font-medium">{{ version.versionId || version.name }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="overflow-hidden">
            <button
              @click="toggleLoaderMenu"
              class="flex items-center justify-between px-4 py-3 rounded-xl text-base font-medium transition-all w-full"
              :class="[
                isLoaderMenuOpen
                  ? 'bg-blue-100 text-blue-600'
                  : 'bg-gray-100 text-slate-700 hover:bg-gray-200',
              ]"
            >
              <div class="flex items-center gap-3">
                <img :src="EndPortalFrameIcon" class="w-5 h-5 object-contain" />
                <span>加载器</span>
                <span v-if="selectedLoader" class="text-xs text-gray-400 ml-auto">{{ selectedLoader.name }}</span>
              </div>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="2.5"
                stroke="currentColor"
                :class="[
                  'w-5 h-5 transition-transform duration-200',
                  isLoaderMenuOpen ? 'rotate-180' : '',
                ]"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                />
              </svg>
            </button>

            <div
              v-show="isLoaderMenuOpen"
              class="border-t border-gray-100 bg-gray-50/30"
            >
              <div class="p-2 space-y-1">
                <div
                  @click="selectLoader(null)"
                  class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="[
                    !selectedLoader
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                  </svg>
                  <span class="text-sm font-medium">全部</span>
                </div>
                <div
                  v-for="loader in loaders"
                  :key="loader.id"
                  @click="selectLoader(loader)"
                  class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="[
                    selectedLoader?.id === loader.id
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <img :src="loader.icon" class="w-4 h-4 object-contain" />
                  <span class="text-sm font-medium">{{ loader.name }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="overflow-hidden">
            <button
              @click="togglePlatformMenu"
              class="flex items-center justify-between px-4 py-3 rounded-xl text-base font-medium transition-all w-full"
              :class="[
                isPlatformMenuOpen
                  ? 'bg-blue-100 text-blue-600'
                  : 'bg-gray-100 text-slate-700 hover:bg-gray-200',
              ]"
            >
              <div class="flex items-center gap-3">
                <img :src="CommandBlockIcon" class="w-5 h-5 object-contain" />
                <span>平台</span>
                <span v-if="selectedPlatform" class="text-xs text-gray-400 ml-auto">{{ selectedPlatform.name }}</span>
              </div>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="2.5"
                stroke="currentColor"
                :class="[
                  'w-5 h-5 transition-transform duration-200',
                  isPlatformMenuOpen ? 'rotate-180' : '',
                ]"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                />
              </svg>
            </button>

            <div
              v-show="isPlatformMenuOpen"
              class="border-t border-gray-100 bg-gray-50/30"
            >
              <div class="p-2 space-y-1">
                <div
                  @click="selectPlatform(null)"
                  class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="[
                    !selectedPlatform
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                  </svg>
                  <span class="text-sm font-medium">全部</span>
                </div>
                <div
                  v-for="platform in platforms"
                  :key="platform.id"
                  @click="selectPlatform(platform)"
                  class="flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="[
                    selectedPlatform?.id === platform.id
                      ? 'bg-zinc-200/70 text-zinc-900'
                      : 'hover:bg-gray-100 text-gray-600',
                  ]"
                >
                  <span class="text-sm font-medium">{{ platform.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-1">
      <template v-if="selectedMod">
        <div class="bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl p-6 mb-6">
          <div class="flex items-start gap-4">
            <div class="w-16 h-16 flex-shrink-0 rounded-xl overflow-hidden bg-white shadow-sm">
              <img
                :src="selectedMod.icon"
                :alt="selectedMod.name"
                class="w-full h-full object-cover"
                @error="(e) => { e.target.src = DefaultModIcon }"
              />
            </div>

            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <h2 class="text-2xl font-bold text-gray-900">{{ selectedMod.name }}</h2>
                <span class="text-xs text-gray-400">by {{ selectedMod.author }}</span>
              </div>

              <div class="flex items-center gap-2 mt-2">
                <span
                  v-for="cat in selectedMod.categories"
                  :key="cat"
                  class="text-xs px-2 py-0.5 rounded-full bg-gray-200/80 text-gray-600"
                >
                  {{ cat }}
                </span>
              </div>

              <p class="text-sm text-gray-600 mt-2 line-clamp-2">
                {{ selectedMod.description }}
              </p>

              <div class="flex items-center gap-4 mt-3 text-sm text-gray-500">
                <span>{{ selectedMod.loaders.join(' / ') }} {{ selectedMod.versionRange }}</span>
                <span class="flex items-center gap-1">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  {{ selectedMod.downloads }}
                </span>
                <span>{{ selectedMod.updatedAt }}</span>
                <span class="text-green-600">Modrinth</span>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-3 mt-4">
            <button
              class="px-4 py-2 bg-blue-500 text-white rounded-lg text-sm font-medium hover:bg-blue-600 transition-colors"
              :onclick="`window.open('https://modrinth.com/mod/${selectedMod.slug}', '_blank')`"
            >
              转到 Modrinth
            </button>
            <button
              class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors"
              @click="openMcWiki(selectedMod.name)"
            >
              转到 MC 百科
            </button>
            <button
              class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors"
              @click="copyModName(selectedMod.name)"
            >
              复制名称
            </button>
          </div>
        </div>

        <div class="bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl overflow-hidden">
          <div class="flex items-center gap-1 p-3 overflow-x-auto">
            <button
              v-for="tab in mcVersionTabs"
              :key="tab"
              @click="selectedMcVersion = tab"
              class="px-4 py-1.5 rounded-lg text-sm font-medium transition-all flex-shrink-0"
              :class="[
                selectedMcVersion === tab
                  ? 'bg-blue-500 text-white shadow-sm'
                  : 'bg-white/60 text-gray-600 hover:bg-white',
              ]"
            >
              {{ tab }}
            </button>
          </div>

          <div v-if="modVersionLoading" class="p-8">
            <div class="space-y-3">
              <div class="h-12 bg-white/60 animate-pulse rounded-lg"></div>
              <div class="h-12 bg-white/60 animate-pulse rounded-lg"></div>
              <div class="h-12 bg-white/60 animate-pulse rounded-lg"></div>
            </div>
          </div>

          <div v-else class="space-y-1">
            <div
              v-for="(versionGroup, groupIndex) in filteredVersionGroups"
              :key="groupIndex"
              class="mx-2 bg-white rounded-lg overflow-hidden"
            >
              <button
                @click="toggleVersionGroup(groupIndex)"
                class="flex items-center justify-between w-full px-4 py-2.5 hover:bg-gray-50/50 transition-colors text-left"
              >
                <span class="font-bold text-gray-800 text-sm">{{ versionGroup.title }}</span>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2"
                  stroke="currentColor"
                  :class="[
                    'w-4 h-4 text-gray-400 transition-transform duration-200',
                    expandedVersionGroups.includes(groupIndex) ? 'rotate-180' : '',
                  ]"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                  />
                </svg>
              </button>

              <div
                v-show="expandedVersionGroups.includes(groupIndex)"
                class="border-t border-gray-100 bg-gray-50/30"
              >
                <div class="space-y-1">
                  <div
                    v-for="(subVersion, subIndex) in versionGroup.subVersions"
                    :key="subIndex"
                    class="mx-2 my-1 bg-white rounded-lg overflow-hidden"
                  >
                    <button
                      @click="toggleSubVersion(groupIndex, subIndex)"
                      class="flex items-center justify-between w-full px-4 py-2.5 hover:bg-gray-50/50 transition-colors text-left"
                    >
                      <span class="font-medium text-gray-800 text-sm">{{ subVersion.title }}</span>
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="2"
                        stroke="currentColor"
                        :class="[
                          'w-4 h-4 text-gray-400 transition-transform duration-200',
                          isSubVersionExpanded(groupIndex, subIndex) ? 'rotate-180' : '',
                        ]"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                        />
                      </svg>
                    </button>

                    <div
                      v-show="isSubVersionExpanded(groupIndex, subIndex)"
                      class="border-t border-gray-100 bg-gray-50/30"
                    >
                      <div class="space-y-1 p-2">
                        <div
                          v-for="(loaderGroup, loaderKey) in subVersion.loaders"
                          :key="loaderKey"
                          class="bg-white rounded-lg overflow-hidden"
                        >
                          <button
                            @click="toggleLoader(groupIndex, subIndex, loaderKey)"
                            class="flex items-center justify-between w-full px-4 py-2.5 hover:bg-gray-50/50 transition-colors text-left"
                          >
                            <div class="flex items-center gap-3">
                              <img :src="getLoaderIcon(loaderKey)" class="w-6 h-6 object-contain" />
                              <div class="flex flex-col">
                                <span class="text-sm font-medium text-gray-800">{{ loaderGroup.title }}</span>
                                <span class="text-xs text-gray-400">
                                  {{ (loaderGroup.release?.length || 0) + (loaderGroup.beta?.length || 0) }} 个版本
                                </span>
                              </div>
                            </div>
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              fill="none"
                              viewBox="0 0 24 24"
                              stroke-width="2"
                              stroke="currentColor"
                              :class="[
                                'w-4 h-4 text-gray-400 transition-transform duration-200',
                                isLoaderExpanded(groupIndex, subIndex, loaderKey) ? 'rotate-180' : '',
                              ]"
                            >
                              <path
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                              />
                            </svg>
                          </button>

                          <div
                            v-show="isLoaderExpanded(groupIndex, subIndex, loaderKey)"
                            class="border-t border-gray-100 bg-gray-50/30"
                          >
                            <div class="p-2">
                              <div v-if="loaderGroup.release && loaderGroup.release.length > 0" class="mb-2">
                                <button
                                  @click="toggleVersionType(groupIndex, subIndex, loaderKey, 'release')"
                                  class="flex items-center justify-between w-full px-3 py-2 bg-white/80 rounded-md hover:bg-white transition-colors"
                                >
                                  <span class="text-xs font-medium text-green-600">正式版 ({{ loaderGroup.release.length }})</span>
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke-width="2"
                                    stroke="currentColor"
                                    :class="[
                                      'w-3 h-3 text-gray-400 transition-transform duration-200',
                                      isVersionTypeExpanded(groupIndex, subIndex, loaderKey, 'release') ? 'rotate-180' : '',
                                    ]"
                                  >
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
                                  </svg>
                                </button>
                                <div
                                  v-show="isVersionTypeExpanded(groupIndex, subIndex, loaderKey, 'release')"
                                  class="mt-1 space-y-1"
                                >
                                  <div
                                    v-for="(variant, vIndex) in loaderGroup.release"
                                    :key="vIndex"
                                    class="flex items-center gap-3 p-2.5 bg-white rounded-md cursor-pointer hover:bg-gray-50 transition-all border border-transparent hover:border-gray-100"
                                    @click="selectModVersion(variant)"
                                  >
                                    <div class="w-7 h-7 flex-shrink-0 rounded-md overflow-hidden bg-gray-100">
                                      <img
                                        :src="getVersionIcon(variant.versionType)"
                                        :alt="variant.name"
                                        class="w-full h-full object-cover"
                                      />
                                    </div>
                                    <div class="flex-1 min-w-0">
                                      <div class="font-medium text-gray-900 text-xs">{{ variant.name }}</div>
                                      <div class="text-xs text-gray-500 mt-0.5">{{ variant.fileName }}</div>
                                    </div>
                                    <div class="flex items-center gap-3 text-xs text-gray-400">
                                      <span class="flex items-center gap-1">
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-3 h-3">
                                          <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                        </svg>
                                        {{ variant.downloads }}
                                      </span>
                                      <span>{{ variant.updatedAt }}</span>
                                    </div>
                                  </div>
                                </div>
                              </div>

                              <div v-if="loaderGroup.beta && loaderGroup.beta.length > 0">
                                <button
                                  @click="toggleVersionType(groupIndex, subIndex, loaderKey, 'beta')"
                                  class="flex items-center justify-between w-full px-3 py-2 bg-white/80 rounded-md hover:bg-white transition-colors"
                                >
                                  <span class="text-xs font-medium text-orange-500">测试版 ({{ loaderGroup.beta.length }})</span>
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke-width="2"
                                    stroke="currentColor"
                                    :class="[
                                      'w-3 h-3 text-gray-400 transition-transform duration-200',
                                      isVersionTypeExpanded(groupIndex, subIndex, loaderKey, 'beta') ? 'rotate-180' : '',
                                    ]"
                                  >
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
                                  </svg>
                                </button>
                                <div
                                  v-show="isVersionTypeExpanded(groupIndex, subIndex, loaderKey, 'beta')"
                                  class="mt-1 space-y-1"
                                >
                                  <div
                                    v-for="(variant, vIndex) in loaderGroup.beta"
                                    :key="vIndex"
                                    class="flex items-center gap-3 p-2.5 bg-white rounded-md cursor-pointer hover:bg-gray-50 transition-all border border-transparent hover:border-gray-100"
                                    @click="selectModVersion(variant)"
                                  >
                                    <div class="w-7 h-7 flex-shrink-0 rounded-md overflow-hidden bg-gray-100">
                                      <img
                                        :src="getVersionIcon(variant.versionType)"
                                        :alt="variant.name"
                                        class="w-full h-full object-cover"
                                      />
                                    </div>
                                    <div class="flex-1 min-w-0">
                                      <div class="font-medium text-gray-900 text-xs">{{ variant.name }}</div>
                                      <div class="text-xs text-gray-500 mt-0.5">{{ variant.fileName }}</div>
                                    </div>
                                    <div class="flex items-center gap-3 text-xs text-gray-400">
                                      <span class="flex items-center gap-1">
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-3 h-3">
                                          <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                                        </svg>
                                        {{ variant.downloads }}
                                      </span>
                                      <span>{{ variant.updatedAt }}</span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="!modVersionLoading && filteredVersionGroups.length === 0" class="p-12 text-center">
            <div class="text-gray-400">暂无匹配的版本</div>
          </div>
        </div>

        <button
          @click="selectedMod = null"
          class="mt-6 flex items-center gap-2 text-gray-500 hover:text-gray-700 transition-colors text-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 15L3 9m0 0l6-6M3 9h12a6 6 0 010 12h-3" />
          </svg>
          返回 Mod 列表
        </button>

        <Teleport to="body">
          <div
            v-if="showVersionModal"
            class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
            @click.self="closeVersionModal"
          >
            <div class="bg-white rounded-xl shadow-xl w-80 p-6 transform transition-all">
              <div class="flex items-center gap-3 mb-4">
                <div class="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center">
                  <img
                    v-if="selectedVariant"
                    :src="getVersionIcon(selectedVariant.versionType)"
                    class="w-8 h-8 object-contain"
                  />
                </div>
                <div>
                  <h3 class="font-semibold text-gray-900 text-sm">{{ selectedVariant?.name }}</h3>
                  <p class="text-xs text-gray-500">{{ selectedVariant?.fileName }}</p>
                </div>
              </div>
              <div class="space-y-3">
                <button
                  @click="downloadToInstance"
                  class="w-full flex items-center justify-center gap-2 px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  下载到游戏实例
                </button>
                <button
                  @click="saveToLocal"
                  class="w-full flex items-center justify-center gap-2 px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  保存到本地
                </button>
              </div>
              <button
                @click="closeVersionModal"
                class="mt-4 w-full px-4 py-2 text-gray-500 hover:text-gray-700 transition-colors text-sm"
              >
                取消
              </button>
            </div>
          </div>
        </Teleport>
      </template>

      <template v-else>
        <header class="mb-6">
          <h2 class="text-xl font-bold text-gray-900">Mod 资源库</h2>
          <p class="text-sm text-gray-500 mt-1">
            从 Modrinth 实时获取模组数据
            <span v-if="searchQuery || selectedVersion || selectedLoader || selectedPlatform" class="ml-2">
              · 已筛选
            </span>
          </p>
        </header>

        <div class="space-y-3">
          <div v-if="modLoading && mods.length === 0" class="space-y-3">
            <div class="h-16 bg-gray-200/60 animate-pulse rounded-xl"></div>
            <div class="h-16 bg-gray-200/60 animate-pulse rounded-xl"></div>
            <div class="h-16 bg-gray-200/60 animate-pulse rounded-xl"></div>
            <div class="h-16 bg-gray-200/60 animate-pulse rounded-xl"></div>
            <div class="h-16 bg-gray-200/60 animate-pulse rounded-xl"></div>
          </div>

          <div
            v-for="mod in filteredMods"
            :key="mod.id"
            @click="selectMod(mod)"
            class="flex items-center gap-4 p-4 bg-white border border-gray-100 rounded-xl hover:border-gray-200 hover:shadow-sm transition-all cursor-pointer"
          >
            <div class="w-12 h-12 flex-shrink-0 rounded-lg overflow-hidden bg-gray-100">
              <img
                :src="mod.icon"
                :alt="mod.name"
                class="w-full h-full object-cover"
                @error="(e) => { e.target.src = DefaultModIcon }"
              />
            </div>

            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span class="font-bold text-gray-900">{{ mod.name }}</span>
                <span class="text-xs text-gray-400">by {{ mod.author }}</span>
              </div>

              <div class="flex items-center gap-2 mt-1 flex-wrap">
                <span
                  v-for="cat in mod.categories"
                  :key="cat"
                  class="text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-600"
                >
                  {{ cat }}
                </span>
              </div>

              <div class="text-sm text-gray-500 mt-1 line-clamp-1">
                {{ mod.description }}
              </div>

              <div class="flex items-center gap-4 mt-2 text-xs text-gray-400">
                <span>{{ mod.loaders.join(' / ') }} {{ mod.versionRange }}</span>
              </div>
            </div>

            <div class="flex items-center gap-6 text-xs">
              <div class="text-center">
                <div class="flex items-center gap-1 text-gray-500">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-3.5 h-3.5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                  <span>{{ mod.downloads }}</span>
                </div>
              </div>

              <div class="text-gray-400">
                {{ mod.updatedAt }}
              </div>

              <div class="flex items-center gap-1 px-2 py-1 rounded-full bg-green-50 text-green-600">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-3.5 h-3.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                </svg>
                <span>Modrinth</span>
              </div>
            </div>
          </div>

          <div v-if="!modLoading && filteredMods.length === 0" class="bg-white rounded-xl border border-gray-100 p-12 text-center">
            <div class="text-4xl mb-4">📦</div>
            <div class="text-gray-500 font-medium">暂无匹配的 Mod</div>
            <div class="text-sm text-gray-400 mt-2">尝试调整筛选条件或搜索关键词</div>
          </div>

          <div v-if="hasMore && filteredMods.length > 0" class="flex justify-center pt-2 pb-4">
            <button
              @click="loadMore"
              :disabled="modLoading"
              class="px-6 py-2 bg-white border border-gray-200 rounded-lg text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors disabled:opacity-50"
            >
              {{ modLoading ? '加载中...' : '加载更多' }}
            </button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import GrassIcon from '../../assets/icons/minecraft/Grass.png'
import EndPortalFrameIcon from '../../assets/icons/minecraft/loader/EndPortalFrame.png'
import CommandBlockIcon from '../../assets/icons/minecraft/CommandBlock.png'
import CobbleStoneIcon from '../../assets/icons/minecraft/CobbleStone.png'
import AnvilIcon from '../../assets/icons/minecraft/loader/Anvil.png'
import NeoForgeIcon from '../../assets/icons/minecraft/loader/NeoForge.png'
import FabricIcon from '../../assets/icons/minecraft/loader/Fabric.png'
import ReleaseIcon from '../../assets/icons/modversions/Release.png'
import BetaIcon from '../../assets/icons/modversions/Beta.png'
import AlphaIcon from '../../assets/icons/modversions/Alpha.png'

const API_BASE = import.meta.env.PROD ? 'http://localhost:8080/api' : '/api'

const searchQuery = ref('')
const isVersionMenuOpen = ref(false)
const isLoaderMenuOpen = ref(false)
const isPlatformMenuOpen = ref(false)

const versions = ref([])
const selectedVersion = ref(null)
const versionLoading = ref(false)

const loaders = [
  { id: 'forge', name: 'Forge', icon: AnvilIcon },
  { id: 'neoforge', name: 'NeoForge', icon: NeoForgeIcon },
  { id: 'fabric', name: 'Fabric', icon: FabricIcon },
]
const selectedLoader = ref(null)

const platforms = [
  { id: 'modrinth', name: 'Modrinth' },
  { id: 'curseforge', name: 'CurseForge' },
]
const selectedPlatform = ref(null)

const selectedMod = ref(null)
const selectedMcVersion = ref('全部')
const expandedVersionGroups = ref([])
const expandedSubVersions = ref({})
const expandedLoaders = ref({})
const expandedVersionTypes = ref({})
const modVersionGroups = ref([])
const modVersionLoading = ref(false)

const mods = ref([])
const modLoading = ref(false)
const searchOffset = ref(0)
const hasMore = ref(true)

const mcVersionTabs = computed(() => {
  const versions = new Set(['全部'])
  modVersionGroups.value.forEach(group => {
    versions.add(group.mcVersion)
  })
  const sorted = Array.from(versions).sort((a, b) => {
    if (a === '全部') return -1
    if (b === '全部') return 1
    const partsA = a.split('.').map(p => parseInt(p) || 0)
    const partsB = b.split('.').map(p => parseInt(p) || 0)
    for (let i = 0; i < Math.max(partsA.length, partsB.length); i++) {
      const diff = (partsA[i] || 0) - (partsB[i] || 0)
      if (diff !== 0) return -diff
    }
    return 0
  })
  return sorted
})

const DefaultModIcon = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIyMCIgY3k9IjIwIiByPSI4IiBmaWxsPSIjZmZmIiBmaWxsLW9wYWNpdHk9IjAuNiIvPjwvc3ZnPg=='

// ========== 从 Modrinth/CurseForge API 获取模组 ==========

const fetchMods = async (reset = false) => {
  if (modLoading.value) return
  if (reset) {
    searchOffset.value = 0
    hasMore.value = true
  }
  if (!hasMore.value) return

  modLoading.value = true
  try {
    const params = {
      offset: searchOffset.value,
      limit: 20,
    }
    if (searchQuery.value) params.query = searchQuery.value
    if (selectedLoader.value) params.loader = selectedLoader.value.id
    if (selectedVersion.value) params.version = selectedVersion.value.versionId

    const platform = selectedPlatform.value?.id || 'modrinth'
    let response, newMods

    if (platform === 'curseforge') {
      response = await axios.get(`${API_BASE}/curseforge/search`, { params })
      newMods = response.data || []
    } else {
      response = await axios.get(`${API_BASE}/modrinth/search`, { params })
      newMods = response.data || []
    }

    if (reset || searchOffset.value === 0) {
      mods.value = newMods
    } else {
      mods.value = [...mods.value, ...newMods]
    }

    hasMore.value = newMods.length >= 20
    searchOffset.value += newMods.length
  } catch (error) {
    console.error('获取模组失败:', error)
    if (reset) mods.value = []
  } finally {
    modLoading.value = false
  }
}

// 搜索防抖
let searchTimer = null
watch(searchQuery, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchMods(true)
  }, 500)
})

watch(selectedLoader, () => fetchMods(true))
watch(selectedVersion, () => fetchMods(true))
watch(selectedPlatform, () => fetchMods(true))

const filteredMods = computed(() => {
  return mods.value
})

const filteredVersionGroups = computed(() => {
  if (selectedMcVersion.value === '全部') {
    return modVersionGroups.value
  }
  return modVersionGroups.value.filter(g => g.mcVersion === selectedMcVersion.value)
})

// ========== 菜单切换 ==========

const toggleVersionMenu = () => {
  isVersionMenuOpen.value = !isVersionMenuOpen.value
  isLoaderMenuOpen.value = false
  isPlatformMenuOpen.value = false
  if (isVersionMenuOpen.value && versions.value.length === 0) {
    fetchVersions()
  }
}

const toggleLoaderMenu = () => {
  isLoaderMenuOpen.value = !isLoaderMenuOpen.value
  isVersionMenuOpen.value = false
  isPlatformMenuOpen.value = false
}

const togglePlatformMenu = () => {
  isPlatformMenuOpen.value = !isPlatformMenuOpen.value
  isVersionMenuOpen.value = false
  isLoaderMenuOpen.value = false
}

const toggleVersionGroup = (index) => {
  const idx = expandedVersionGroups.value.indexOf(index)
  if (idx > -1) {
    expandedVersionGroups.value.splice(idx, 1)
    delete expandedSubVersions.value[index]
    delete expandedLoaders.value[index]
  } else {
    expandedVersionGroups.value.push(index)
    expandedSubVersions.value[index] = {}
    expandedLoaders.value[index] = {}
  }
}

const toggleSubVersion = (groupIndex, subIndex) => {
  if (!expandedSubVersions.value[groupIndex]) {
    expandedSubVersions.value[groupIndex] = {}
  }
  const key = String(subIndex)
  if (expandedSubVersions.value[groupIndex][key]) {
    expandedSubVersions.value[groupIndex][key] = false
    if (expandedLoaders.value[groupIndex]) {
      delete expandedLoaders.value[groupIndex][key]
    }
  } else {
    expandedSubVersions.value[groupIndex][key] = true
    if (!expandedLoaders.value[groupIndex]) {
      expandedLoaders.value[groupIndex] = {}
    }
    expandedLoaders.value[groupIndex][key] = {}
  }
}

const isSubVersionExpanded = (groupIndex, subIndex) => {
  return expandedSubVersions.value[groupIndex]?.[String(subIndex)] || false
}

const toggleLoader = (groupIndex, subIndex, loaderKey) => {
  if (!expandedLoaders.value[groupIndex]) {
    expandedLoaders.value[groupIndex] = {}
  }
  if (!expandedLoaders.value[groupIndex][subIndex]) {
    expandedLoaders.value[groupIndex][subIndex] = {}
  }
  const key = String(subIndex)
  const loaderMap = expandedLoaders.value[groupIndex][key] || {}
  if (loaderMap[loaderKey]) {
    loaderMap[loaderKey] = false
  } else {
    loaderMap[loaderKey] = true
  }
  expandedLoaders.value[groupIndex][key] = { ...loaderMap }
}

const isLoaderExpanded = (groupIndex, subIndex, loaderKey) => {
  return expandedLoaders.value[groupIndex]?.[String(subIndex)]?.[loaderKey] || false
}

const showVersionModal = ref(false)
const selectedVariant = ref(null)

const toggleVersionType = (groupIndex, subIndex, loaderKey, versionType) => {
  if (!expandedVersionTypes.value[groupIndex]) {
    expandedVersionTypes.value[groupIndex] = {}
  }
  if (!expandedVersionTypes.value[groupIndex][subIndex]) {
    expandedVersionTypes.value[groupIndex][subIndex] = {}
  }
  if (!expandedVersionTypes.value[groupIndex][subIndex][loaderKey]) {
    expandedVersionTypes.value[groupIndex][subIndex][loaderKey] = {}
  }
  const key = String(subIndex)
  const typeMap = expandedVersionTypes.value[groupIndex][key]?.[loaderKey] || {}
  if (typeMap[versionType]) {
    typeMap[versionType] = false
  } else {
    typeMap[versionType] = true
  }
  if (!expandedVersionTypes.value[groupIndex][key]) {
    expandedVersionTypes.value[groupIndex][key] = {}
  }
  expandedVersionTypes.value[groupIndex][key][loaderKey] = { ...typeMap }
}

const isVersionTypeExpanded = (groupIndex, subIndex, loaderKey, versionType) => {
  return expandedVersionTypes.value[groupIndex]?.[String(subIndex)]?.[loaderKey]?.[versionType] || false
}

const getVersionIcon = (versionType) => {
  if (!versionType) return ReleaseIcon
  const type = versionType.toLowerCase()
  if (type === 'beta') return BetaIcon
  if (type === 'alpha') return AlphaIcon
  return ReleaseIcon
}

// ========== 选择操作 ==========

const selectVersion = (version) => {
  selectedVersion.value = version
}

const selectLoader = (loader) => {
  selectedLoader.value = loader
}

const selectPlatform = (platform) => {
  selectedPlatform.value = platform
}

const selectMod = async (mod) => {
  selectedMod.value = mod
  selectedMcVersion.value = '全部'
  expandedVersionGroups.value = []
  expandedSubVersions.value = {}
  expandedLoaders.value = {}
  expandedVersionTypes.value = {}
  modVersionGroups.value = []
  const projectId = mod.slug || mod.id
  const platform = mod.source || mod.platform || 'modrinth'
  console.log('selectMod:', projectId, platform)
  await fetchModVersions(projectId, platform)
}

const selectModVersion = (variant) => {
  selectedVariant.value = variant
  showVersionModal.value = true
}

const closeVersionModal = () => {
  showVersionModal.value = false
  selectedVariant.value = null
}

const downloadToInstance = async () => {
  if (!selectedVariant.value) return
  console.log('下载到游戏实例:', selectedVariant.value)
  closeVersionModal()
}

const saveToLocal = async () => {
  if (!selectedVariant.value) return
  const url = selectedVariant.value.fileUrl
  const fileName = selectedVariant.value.fileName

  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  closeVersionModal()
}

// ========== 获取模组版本 ==========

const fetchModVersions = async (projectId, platform) => {
  modVersionLoading.value = true
  try {
    let response
    if (platform === 'curseforge') {
      response = await axios.get(`${API_BASE}/curseforge/project/${projectId}/versions`)
    } else {
      response = await axios.get(`${API_BASE}/modrinth/project/${projectId}/versions`)
    }
    console.log('版本数据:', response.data)
    modVersionGroups.value = response.data || []
    console.log('modVersionGroups:', modVersionGroups.value.length)
  } catch (error) {
    console.error('获取模组版本失败:', error)
    modVersionGroups.value = []
  } finally {
    modVersionLoading.value = false
  }
}

// ========== 工具方法 ==========

const getLoaderIcon = (loaderId) => {
  if (loaderId === 'forge') return AnvilIcon
  if (loaderId === 'neoforge') return NeoForgeIcon
  if (loaderId === 'fabric') return FabricIcon
  return GrassIcon
}

const getLoaderDisplayName = (loaderId) => {
  if (loaderId === 'forge') return 'Forge'
  if (loaderId === 'neoforge') return 'NeoForge'
  if (loaderId === 'fabric') return 'Fabric'
  if (loaderId === 'quilt') return 'Quilt'
  return loaderId || '其他'
}

const openMcWiki = (modName) => {
  window.open(`https://mcwiki.cn/${encodeURIComponent(modName)}`, '_blank')
}

const copyModName = (name) => {
  navigator.clipboard.writeText(name)
  alert('已复制到剪贴板')
}

const loadMore = () => {
  fetchMods(false)
}

const fetchVersions = async () => {
  versionLoading.value = true
  try {
    const response = await axios.get(`${API_BASE}/minecraft/versions`)
    let rawVersions = Array.isArray(response.data) ? response.data : []
    versions.value = rawVersions.filter(v => {
      const type = (v.type || '').toLowerCase()
      return type === 'release' || type === '正式版'
    })
    if (versions.value.length === 0) {
      versions.value = fallbackVersions()
    }
  } catch (error) {
    console.error('获取版本列表失败:', error)
    versions.value = fallbackVersions()
  } finally {
    versionLoading.value = false
  }
}

const fallbackVersions = () => [
  { versionId: '1.21.8', name: '1.21.8', type: 'release', releaseTime: '2025-07-17T12:00:00+00:00' },
  { versionId: '1.21.7', name: '1.21.7', type: 'release', releaseTime: '2025-06-30T12:00:00+00:00' },
  { versionId: '1.21.6', name: '1.21.6', type: 'release', releaseTime: '2025-06-17T12:00:00+00:00' },
  { versionId: '1.21.5', name: '1.21.5', type: 'release', releaseTime: '2025-06-17T12:00:00+00:00' },
  { versionId: '1.21.4', name: '1.21.4', type: 'release', releaseTime: '2024-12-03T12:00:00+00:00' },
  { versionId: '1.21.1', name: '1.21.1', type: 'release', releaseTime: '2024-08-08T12:00:00+00:00' },
  { versionId: '1.20.6', name: '1.20.6', type: 'release', releaseTime: '2024-04-29T12:00:00+00:00' },
  { versionId: '1.20.4', name: '1.20.4', type: 'release', releaseTime: '2023-12-07T12:00:00+00:00' },
  { versionId: '1.20.1', name: '1.20.1', type: 'release', releaseTime: '2023-06-12T12:00:00+00:00' },
]

onMounted(() => {
  fetchMods(true)
})
</script>
