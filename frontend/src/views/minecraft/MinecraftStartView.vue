<template>
  <div class="flex flex-col items-center justify-center min-h-[700px] px-8">
    <div class="w-full max-w-lg">
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-100 p-6 mb-6">
        <div class="flex items-center gap-4">
          <div class="relative">
            <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-md">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
          <div class="flex-1">
            <div class="text-base font-semibold text-gray-800">
              {{ selectedAccount || (accounts.length > 0 && accounts[0]?.name ? accounts[0].name : '未登录') }}
            </div>
            <div class="text-xs text-gray-400">
              {{ ((accounts.find(a => a.name === selectedAccount) || accounts[0])?.type === 'microsoft') ? '微软账户' : '离线模式' }}
            </div>
          </div>
          <button @click="showAccountManager = true" class="px-4 py-2 text-sm font-semibold text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-all">
            {{ selectedAccount || accounts.length > 0 ? '切换' : '登录' }}
          </button>
        </div>
        <button @click="showAccountManager = true" class="mt-4 w-full py-2.5 text-sm font-medium text-gray-500 hover:text-gray-700 bg-gray-50 rounded-lg hover:bg-gray-100 transition-all flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          账户管理
        </button>
      </div>

      <div class="bg-gray-50 rounded-xl border border-gray-100 overflow-hidden mb-6">
        <div class="flex items-center justify-between px-5 py-4 cursor-pointer" @click="handleVersionSelect">
          <div class="text-sm font-medium text-gray-500">版本选择</div>
          <div class="flex items-center gap-2 text-sm">
            <span :class="selectedVersionId ? 'text-gray-700' : 'text-gray-400'">
              {{ getSelectedVersion()?.instanceName || currentVersion || '无版本' }}
            </span>
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <button v-if="hasVersions" @click="handleStartGame()" :disabled="!selectedVersionId || isLaunching" class="w-full py-5 text-lg font-semibold text-white bg-zinc-900 rounded-xl hover:bg-zinc-800 transition-all cursor-pointer shadow-lg hover:shadow-xl flex items-center justify-center gap-3 disabled:opacity-50 disabled:cursor-not-allowed">
          <svg v-if="!isLaunching" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <svg v-else class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ isLaunching ? '启动中...' : '启动游戏' }}
        </button>

        <div v-if="launchError" class="px-4 py-3 text-sm text-red-500 bg-red-50 rounded-xl">
          {{ launchError }}
        </div>

        <button v-else @click="handleDownloadGame()" class="w-full py-5 text-lg font-semibold text-white bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl hover:from-blue-600 hover:to-purple-700 transition-all cursor-pointer shadow-lg hover:shadow-xl flex items-center justify-center gap-3">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          下载游戏
        </button>

        <div class="flex gap-4">
          <button @click="handleVersionSelect" class="flex-1 py-4 text-base font-medium text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all cursor-pointer shadow-sm">
            版本选择
          </button>
          <button v-if="hasVersions" @click="handleVersionSettings" class="flex-1 py-4 text-base font-medium text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all cursor-pointer shadow-sm">
            版本设置
          </button>
        </div>
      </div>

      <div class="mt-10 pt-6 border-t border-gray-100">
        <div class="flex items-center justify-center gap-6 text-xs text-gray-400">
          <span>MCNova Server Launcher</span>
          <span class="w-1 h-1 bg-gray-300 rounded-full"></span>
          <span>Version 1.0.0</span>
        </div>
      </div>
    </div>

    <div v-if="showAccountManager" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl mx-4 overflow-hidden">
        <div class="flex justify-between items-center px-6 py-4 border-b border-gray-100">
          <h2 class="text-xl font-bold text-gray-900">账户管理</h2>
          <div class="flex items-center gap-2">
            <button @click="handleMicrosoftLogin" class="px-4 py-2 text-sm font-medium text-white bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors flex items-center gap-2">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              正版登录
            </button>
            <button @click="handleOfflineAccount" class="px-4 py-2 text-sm font-medium text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              离线账户
            </button>
            <button @click="showAccountManager = false" class="w-8 h-8 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-all ml-2">
              <span class="text-lg">×</span>
            </button>
          </div>
        </div>
        <div class="p-6">
          <div v-if="accounts.length === 0" class="text-center py-12 text-gray-400">
            <div class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <div class="text-lg font-medium text-gray-500">暂无账户</div>
            <div class="text-sm text-gray-400 mt-2">点击上方按钮添加账户</div>
          </div>
          <div v-else class="space-y-3">
            <div v-for="(account, index) in accounts" :key="index" class="flex items-center justify-between p-4 bg-white rounded-xl border border-gray-200 transition-colors">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
                  <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <div>
                  <div class="text-base font-semibold text-gray-800">{{ account.name }}</div>
                  <div class="text-xs text-gray-500">{{ account.type === 'microsoft' ? '微软账户' : '离线账户' }}</div>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <span v-if="selectedAccount === account.name" class="px-3 py-1.5 text-xs font-medium text-white bg-blue-500 rounded-lg">当前使用</span>
                <button v-else @click="setDefaultAccount(account)" class="px-3 py-1.5 text-xs font-medium text-white bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors">选择</button>
                <button @click="removeAccount(index)" class="px-3 py-1.5 text-xs font-medium text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showLoginModal" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 overflow-hidden">
        <div v-if="newAccountType === 'offline'" class="p-8">
          <div class="flex justify-center gap-4 mb-10">
            <button @click="newAccountType = 'microsoft'" class="px-6 py-2.5 rounded-full font-medium text-sm transition-all flex items-center gap-2 bg-white text-blue-500 border border-blue-300 hover:border-blue-400">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
              正版
            </button>
            <button @click="newAccountType = 'offline'" class="px-6 py-2.5 rounded-full font-medium text-sm transition-all flex items-center gap-2 bg-blue-500 text-white shadow-md">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              离线
            </button>
          </div>
          <div class="flex justify-center mb-8">
            <div class="w-20 h-20 rounded-full bg-gray-50 border-2 border-gray-200 flex items-center justify-center">
              <svg class="w-10 h-10 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
          <div>
            <input v-model="newAccountName" type="text" placeholder="游戏用户名" class="w-full px-4 py-3 text-sm border-2 border-blue-400 rounded-lg focus:outline-none focus:border-blue-500 transition-all pr-10" />
          </div>
          <button @click="addAccount" class="mt-3 w-full py-3 text-sm font-semibold text-blue-600 bg-white border-2 border-blue-400 rounded-lg hover:bg-blue-50 hover:border-blue-500 transition-all">
            创建
          </button>
        </div>
        <div v-else class="p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-bold text-gray-900">微软登录</h3>
            <button @click="cancelMicrosoftLogin" class="w-6 h-6 flex items-center justify-center text-gray-400 hover:text-gray-600">
              <span class="text-lg">×</span>
            </button>
          </div>
          <div v-if="oauthStarted" class="space-y-4">
            <div class="flex flex-col items-center justify-center py-6">
              <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
                <svg class="w-8 h-8 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
              </div>
              <p class="text-sm text-gray-500 text-center">浏览器已打开登录页面，请登录后复制授权码。</p>
            </div>
            <div class="space-y-2">
              <label class="text-xs text-gray-400">授权码:</label>
              <div class="flex items-center gap-2">
                <input v-model="authorizationCode" type="text" placeholder="输入授权码" class="flex-1 px-4 py-2.5 text-sm border-2 border-blue-400 rounded-xl focus:outline-none focus:border-blue-500 transition-all" :disabled="oauthStatus === 'completed'" />
                <button @click="submitAuthorizationCode" :disabled="oauthStatus !== 'pending' || !authorizationCode.trim()" class="px-5 py-2.5 text-sm font-medium text-white bg-blue-500 rounded-xl hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">提交</button>
              </div>
            </div>
            <div class="flex items-center gap-3 px-4 py-3 bg-gray-100 rounded-xl">
              <svg v-if="oauthStatus === 'pending' && authorizationCode.trim()" class="w-4 h-4 text-blue-500 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <svg v-else-if="oauthStatus === 'completed'" class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <svg v-else-if="oauthStatus === 'failed'" class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
              <span class="text-sm text-gray-600">
                {{ oauthStatus === 'pending' && authorizationCode.trim() ? '正在验证...' : oauthStatus === 'completed' ? '登录成功！' : oauthStatus === 'failed' ? oauthErrorMessage || '登录失败' : '请输入授权码' }}
              </span>
            </div>
            <div class="flex justify-end gap-3 pt-2">
              <button @click="openVerificationPage" class="px-5 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors">重新打开网页</button>
              <button @click="cancelMicrosoftLogin" class="px-5 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors">取消</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showVersionSelect" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl mx-4 overflow-hidden">
        <div class="flex justify-between items-center px-5 py-3 border-b border-gray-100 bg-gray-50">
          <div class="flex items-center gap-3">
            <div class="w-7 h-7 bg-gradient-to-br from-green-500 to-emerald-600 rounded-lg flex items-center justify-center shadow-sm">
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-800">版本选择</h2>
          </div>
          <button @click="showVersionSelect = false" class="w-7 h-7 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-200 rounded transition-all">
            <span class="text-lg">×</span>
          </button>
        </div>

        <div class="flex h-[520px]">
          <div class="w-56 border-r border-gray-100 bg-gray-50 p-3">
            <div class="text-xs text-gray-500 mb-2 font-medium px-1">版本列表</div>
            <div class="space-y-1 max-h-[450px] overflow-y-auto">
              <div v-for="folder in versionFolders" :key="folder.instanceId" class="px-3 py-2.5 rounded-lg cursor-pointer text-sm transition-all flex items-center gap-2" :class="selectedVersionId === folder.instanceId ? 'bg-blue-500 text-white shadow-md' : 'hover:bg-gray-200 text-gray-700'" @click="selectVersionById(folder.instanceId)">
                <div class="w-7 h-7 rounded bg-gray-200 flex items-center justify-center flex-shrink-0" :class="selectedVersionId === folder.instanceId ? 'bg-blue-400' : ''">
                  <svg class="w-4 h-4 text-green-600" viewBox="0 0 64 64" fill="none">
                    <rect x="4" y="4" width="56" height="56" rx="4" fill="#567d46"/>
                    <rect x="8" y="8" width="48" height="48" rx="2" fill="#729662"/>
                    <rect x="8" y="8" width="24" height="24" fill="#567d46"/>
                    <rect x="32" y="8" width="24" height="24" fill="#729662"/>
                    <rect x="8" y="32" width="24" height="24" fill="#729662"/>
                    <rect x="32" y="32" width="24" height="24" fill="#567d46"/>
                    <rect x="20" y="20" width="12" height="12" fill="#89a87a"/>
                    <rect x="32" y="20" width="12" height="12" fill="#628554"/>
                    <rect x="20" y="32" width="12" height="12" fill="#628554"/>
                    <rect x="32" y="32" width="12" height="12" fill="#89a87a"/>
                  </svg>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="font-medium truncate">{{ folder.instanceName }}</div>
                  <div class="text-xs opacity-70">{{ getVersionByInstanceId(folder.instanceId)?.version || '' }}</div>
                </div>
              </div>
            </div>
            <div class="mt-4 pt-3 border-t border-gray-200">
              <div class="text-xs text-gray-500 mb-2 font-medium px-1">添加版本</div>
              <button @click="handleDownloadGame()" class="w-full px-3 py-2 text-sm text-gray-600 hover:bg-gray-200 rounded-lg transition-all flex items-center justify-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                下载新版本
              </button>
            </div>
          </div>

          <div class="flex-1 p-5 overflow-y-auto bg-gray-50">
            <div v-if="!selectedVersionId" class="h-full flex flex-col items-center justify-center text-gray-400">
              <div class="w-20 h-20 bg-gray-100 rounded-xl flex items-center justify-center mb-4">
                <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <p class="text-sm">请从左侧列表选择一个版本</p>
            </div>
            <div v-else>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 mb-4">
                <div class="flex items-start gap-4">
                  <div class="w-16 h-16 bg-green-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <svg class="w-10 h-10 text-green-600" viewBox="0 0 64 64" fill="none">
                      <rect x="4" y="4" width="56" height="56" rx="4" fill="#567d46"/>
                      <rect x="8" y="8" width="48" height="48" rx="2" fill="#729662"/>
                      <rect x="8" y="8" width="24" height="24" fill="#567d46"/>
                      <rect x="32" y="8" width="24" height="24" fill="#729662"/>
                      <rect x="8" y="32" width="24" height="24" fill="#729662"/>
                      <rect x="32" y="32" width="24" height="24" fill="#567d46"/>
                      <rect x="20" y="20" width="12" height="12" fill="#89a87a"/>
                      <rect x="32" y="20" width="12" height="12" fill="#628554"/>
                      <rect x="20" y="32" width="12" height="12" fill="#628554"/>
                      <rect x="32" y="32" width="12" height="12" fill="#89a87a"/>
                    </svg>
                  </div>
                  <div class="flex-1">
                    <div class="text-xl font-bold text-gray-800">{{ getSelectedVersion()?.instanceName }}</div>
                    <div class="text-sm text-gray-500 mt-1">Minecraft {{ getSelectedVersion()?.version }}</div>
                    <div class="flex gap-2 mt-3">
                      <span class="px-2 py-0.5 text-xs font-medium text-green-700 bg-green-100 rounded flex items-center gap-1.5">
                      <svg class="w-3 h-3" viewBox="0 0 16 16" fill="none">
                        <rect x="1" y="1" width="14" height="14" rx="1" fill="#729662"/>
                        <rect x="2" y="2" width="6" height="6" fill="#567d46"/>
                        <rect x="8" y="2" width="6" height="6" fill="#729662"/>
                        <rect x="2" y="8" width="6" height="6" fill="#729662"/>
                        <rect x="8" y="8" width="6" height="6" fill="#567d46"/>
                      </svg>
                      发布版
                    </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 mb-4">
                <div class="text-sm font-medium text-gray-700 mb-3">版本信息</div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <div class="text-xs text-gray-400">版本类型</div>
                    <div class="text-sm font-medium text-gray-700">发布版</div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400">游戏目录</div>
                    <div class="text-sm font-medium text-gray-700 truncate">{{ getSelectedVersion()?.gameDir }}</div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400">版本路径</div>
                    <div class="text-sm font-medium text-gray-700 truncate">{{ getSelectedVersion()?.path }}</div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400">实例ID</div>
                    <div class="text-sm font-medium text-gray-700">{{ getSelectedVersion()?.instanceId }}</div>
                  </div>
                </div>
              </div>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-3">快捷操作</div>
                <div class="flex gap-3">
                  <button class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex items-center justify-center gap-2">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" />
                    </svg>
                    打开版本文件夹
                  </button>
                  <button class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex items-center justify-center gap-2">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                    复制版本路径
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="px-5 py-3 border-t border-gray-100 bg-gray-50 flex justify-end gap-3">
          <button @click="showVersionSelect = false" class="px-5 py-2 text-sm font-medium text-gray-600 bg-gray-200 rounded-lg hover:bg-gray-300 transition-colors">取消</button>
          <button @click="openVersionSettings" class="px-5 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">版本设置</button>
          <button @click="confirmVersionSelect" :disabled="!selectedVersionId" class="px-5 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">选择此版本</button>
        </div>
      </div>
    </div>

    <div v-if="showVersionSettings" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl mx-4 overflow-hidden">
        <div class="flex justify-between items-center px-5 py-3 border-b border-gray-100 bg-gray-50">
          <div class="flex items-center gap-3">
            <button @click="showVersionSettings = false" class="w-7 h-7 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-200 rounded transition-all">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h2 class="text-lg font-semibold text-gray-800">{{ getSelectedVersion()?.instanceName || currentVersion }} - 版本设置</h2>
          </div>
          <div class="flex items-center gap-2">
            <button @click="saveVersionSettings" class="px-4 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors">保存设置</button>
          </div>
        </div>

        <div class="flex">
          <div class="w-52 border-r border-gray-100 bg-gray-50 p-2">
            <div class="space-y-1">
              <button @click="settingsTab = 'overview'" class="w-full px-4 py-2.5 text-sm font-medium rounded-lg transition-all flex items-center gap-2" :class="settingsTab === 'overview' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:bg-gray-200'">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
                概览
              </button>
              <button @click="settingsTab = 'java'" class="w-full px-4 py-2.5 text-sm font-medium rounded-lg transition-all flex items-center gap-2" :class="settingsTab === 'java' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:bg-gray-200'">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                Java 设置
              </button>
              <button @click="settingsTab = 'memory'" class="w-full px-4 py-2.5 text-sm font-medium rounded-lg transition-all flex items-center gap-2" :class="settingsTab === 'memory' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:bg-gray-200'">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                内存设置
              </button>
              <button @click="settingsTab = 'jvm'" class="w-full px-4 py-2.5 text-sm font-medium rounded-lg transition-all flex items-center gap-2" :class="settingsTab === 'jvm' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:bg-gray-200'">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
                JVM 参数
              </button>
              <button @click="settingsTab = 'launch'" class="w-full px-4 py-2.5 text-sm font-medium rounded-lg transition-all flex items-center gap-2" :class="settingsTab === 'launch' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:bg-gray-200'">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                启动设置
              </button>
            </div>
          </div>

          <div class="flex-1 p-5 overflow-y-auto bg-gray-50">
            <div v-if="settingsTab === 'overview'" class="space-y-4">
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="flex items-start gap-4">
                  <div class="w-16 h-16 bg-green-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <svg class="w-10 h-10 text-green-600" viewBox="0 0 64 64" fill="none">
                      <rect x="4" y="4" width="56" height="56" rx="4" fill="#567d46"/>
                      <rect x="8" y="8" width="48" height="48" rx="2" fill="#729662"/>
                      <rect x="8" y="8" width="24" height="24" fill="#567d46"/>
                      <rect x="32" y="8" width="24" height="24" fill="#729662"/>
                      <rect x="8" y="32" width="24" height="24" fill="#729662"/>
                      <rect x="32" y="32" width="24" height="24" fill="#567d46"/>
                      <rect x="20" y="20" width="12" height="12" fill="#89a87a"/>
                      <rect x="32" y="20" width="12" height="12" fill="#628554"/>
                      <rect x="20" y="32" width="12" height="12" fill="#628554"/>
                      <rect x="32" y="32" width="12" height="12" fill="#89a87a"/>
                    </svg>
                  </div>
                  <div class="flex-1">
                    <div class="text-xl font-bold text-gray-800">{{ getSelectedVersion()?.instanceName }}</div>
                    <div class="text-sm text-gray-500 mt-1">Minecraft {{ getSelectedVersion()?.version }}</div>
                    <div class="flex gap-2 mt-3">
                      <span class="px-2 py-0.5 text-xs font-medium text-green-700 bg-green-100 rounded flex items-center gap-1.5">
                      <svg class="w-3 h-3" viewBox="0 0 16 16" fill="none">
                        <rect x="1" y="1" width="14" height="14" rx="1" fill="#729662"/>
                        <rect x="2" y="2" width="6" height="6" fill="#567d46"/>
                        <rect x="8" y="2" width="6" height="6" fill="#729662"/>
                        <rect x="2" y="8" width="6" height="6" fill="#729662"/>
                        <rect x="8" y="8" width="6" height="6" fill="#567d46"/>
                      </svg>
                      发布版
                    </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">版本信息</div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <div class="text-xs text-gray-400">游戏目录</div>
                    <div class="text-sm font-medium text-gray-700 truncate">{{ getSelectedVersion()?.gameDir }}</div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400">版本路径</div>
                    <div class="text-sm font-medium text-gray-700 truncate">{{ getSelectedVersion()?.path }}</div>
                  </div>
                </div>
              </div>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">快捷操作</div>
                <div class="grid grid-cols-3 gap-3">
                  <button class="px-4 py-3 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex flex-col items-center gap-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" />
                    </svg>
                    打开版本文件夹
                  </button>
                  <button class="px-4 py-3 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex flex-col items-center gap-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                    复制版本路径
                  </button>
                  <button class="px-4 py-3 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex flex-col items-center gap-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                    导出启动脚本
                  </button>
                </div>
              </div>
            </div>

            <div v-if="settingsTab === 'java'" class="space-y-4">
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">Java 路径</div>
                <div class="flex items-center justify-between">
                  <span class="text-sm text-gray-600">{{ javaPath || '自动检测' }}</span>
                  <div class="flex gap-2">
                    <button class="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex items-center gap-2">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                      浏览
                    </button>
                    <button @click="autoDetectJava" class="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors flex items-center gap-2">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                      </svg>
                      自动检测
                    </button>
                  </div>
                </div>
              </div>
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">已安装的 Java 版本</div>
                <div class="space-y-2">
                  <div class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center">
                        <svg class="w-4 h-4 text-yellow-600" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M13.71 13.37l-.01-.01a3.002 3.002 0 00-5.16-1.69L6.7 15.9l1.77 1.77a3.002 3.002 0 005.14-1.69l.01-.01-.01.01a2.999 2.999 0 005.16-1.69l1.77 1.77-1.42 1.42a2.999 2.999 0 00-5.14-1.69l-.01-.01zM12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z"/>
                        </svg>
                      </div>
                      <div>
                        <div class="text-sm font-medium text-gray-800">Java 21 (推荐)</div>
                        <div class="text-xs text-gray-500">/usr/lib/jvm/java-21-openjdk</div>
                      </div>
                    </div>
                    <input type="radio" name="javaVersion" checked class="w-4 h-4 text-blue-600" />
                  </div>
                  <div class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center">
                        <svg class="w-4 h-4 text-yellow-600" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M13.71 13.37l-.01-.01a3.002 3.002 0 00-5.16-1.69L6.7 15.9l1.77 1.77a3.002 3.002 0 005.14-1.69l.01-.01-.01.01a2.999 2.999 0 005.16-1.69l1.77 1.77-1.42 1.42a2.999 2.999 0 00-5.14-1.69l-.01-.01zM12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z"/>
                        </svg>
                      </div>
                      <div>
                        <div class="text-sm font-medium text-gray-800">Java 17</div>
                        <div class="text-xs text-gray-500">/usr/lib/jvm/java-17-openjdk</div>
                      </div>
                    </div>
                    <input type="radio" name="javaVersion" class="w-4 h-4 text-blue-600" />
                  </div>
                </div>
              </div>
            </div>

            <div v-if="settingsTab === 'memory'" class="space-y-4">
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">内存分配</div>
                <div class="grid grid-cols-2 gap-6">
                  <div>
                    <div class="text-xs text-gray-400 mb-2">最小内存</div>
                    <div class="flex items-center gap-2">
                      <input type="number" v-model="minMemory" class="flex-1 px-4 py-3 text-lg font-semibold border border-gray-200 rounded-xl focus:outline-none focus:border-blue-400" />
                      <span class="text-sm text-gray-500">MB</span>
                    </div>
                    <div class="flex gap-2 mt-3">
                      <button @click="minMemory = 1024" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">1GB</button>
                      <button @click="minMemory = 2048" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">2GB</button>
                      <button @click="minMemory = 4096" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">4GB</button>
                    </div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400 mb-2">最大内存</div>
                    <div class="flex items-center gap-2">
                      <input type="number" v-model="maxMemory" class="flex-1 px-4 py-3 text-lg font-semibold border border-gray-200 rounded-xl focus:outline-none focus:border-blue-400" />
                      <span class="text-sm text-gray-500">MB</span>
                    </div>
                    <div class="flex gap-2 mt-3">
                      <button @click="maxMemory = 2048" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">2GB</button>
                      <button @click="maxMemory = 4096" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">4GB</button>
                      <button @click="maxMemory = 8192" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded hover:bg-gray-200 transition-colors">8GB</button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-blue-50 rounded-xl border border-blue-100 p-4">
                <div class="flex items-start gap-3">
                  <svg class="w-5 h-5 text-blue-500 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <div class="text-sm text-blue-600">
                    <div class="font-medium mb-1">内存建议</div>
                    <div>建议最小内存不低于 1GB，最大内存不超过系统可用内存的 80%。对于大型 Mod 包，建议分配更多内存。</div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="settingsTab === 'jvm'" class="space-y-4">
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="flex items-center justify-between mb-4">
                  <div class="text-sm font-medium text-gray-700">JVM 参数</div>
                  <button @click="resetJvmParameters" class="px-3 py-1 text-xs font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors">重置为默认</button>
                </div>
                <textarea v-model="jvmParameters" rows="6" class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:border-blue-400 resize-none" placeholder="输入 JVM 参数..."></textarea>
              </div>
              <div class="bg-gray-50 rounded-xl border border-gray-100 p-4">
                <div class="text-xs text-gray-500 mb-2">常用参数说明</div>
                <div class="text-xs text-gray-600 space-y-1">
                  <div>-Xms1G: 初始堆内存为1GB</div>
                  <div>-Xmx4G: 最大堆内存为4GB</div>
                  <div>-XX:+UseG1GC: 使用G1垃圾收集器</div>
                  <div>-XX:+ParallelRefProcEnabled: 并行处理引用对象</div>
                </div>
              </div>
            </div>

            <div v-if="settingsTab === 'launch'" class="space-y-4">
              <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div class="text-sm font-medium text-gray-700 mb-4">游戏参数</div>
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <div class="text-sm font-medium text-gray-700">开启快速启动</div>
                      <div class="text-xs text-gray-400">跳过资源包和模组加载检查</div>
                    </div>
                    <div class="relative w-12 h-6 bg-gray-300 rounded-full cursor-pointer transition-colors" :class="fastLaunch ? 'bg-zinc-800' : ''" @click="fastLaunch = !fastLaunch">
                      <div class="absolute top-1 w-4 h-4 bg-white rounded-full shadow-sm transition-all" :class="fastLaunch ? 'left-7' : 'left-1'"></div>
                    </div>
                  </div>
                  <div class="flex items-center justify-between">
                    <div>
                      <div class="text-sm font-medium text-gray-700">开启全屏模式</div>
                      <div class="text-xs text-gray-400">启动时自动全屏</div>
                    </div>
                    <div class="relative w-12 h-6 bg-gray-300 rounded-full cursor-pointer transition-colors" :class="fullScreen ? 'bg-zinc-800' : ''" @click="fullScreen = !fullScreen">
                      <div class="absolute top-1 w-4 h-4 bg-white rounded-full shadow-sm transition-all" :class="fullScreen ? 'left-7' : 'left-1'"></div>
                    </div>
                  </div>
                  <div>
                    <div class="text-xs text-gray-400 mb-2">额外游戏参数</div>
                    <input type="text" v-model="gameArguments" class="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:border-blue-400" placeholder="输入额外的游戏参数..." />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const API_BASE = process.env.NODE_ENV === 'development' ? '/api' : 'http://localhost:8080/api'

const accounts = ref<any[]>([])
const selectedAccount = ref('')
const showAccountManager = ref(false)
const showLoginModal = ref(false)
const newAccountType = ref('offline')
const newAccountName = ref('')
const authorizationCode = ref('')
const oauthStarted = ref(false)
const oauthStatus = ref('')
const oauthErrorMessage = ref('')

const currentVersion = ref('')
const showVersionSelect = ref(false)
const showVersionSettings = ref(false)
const selectedVersionId = ref('')
const versionFolders = ref<any[]>([])
const settingsTab = ref('overview')
const javaPath = ref('')
const minMemory = ref(1024)
const maxMemory = ref(4096)
const jvmParameters = ref('-Xms1G -Xmx4G')
const fastLaunch = ref(false)
const fullScreen = ref(false)
const gameArguments = ref('')

const isLaunching = ref(false)
const launchError = ref('')

const hasVersions = computed(() => versionFolders.value.length > 0)

const getSelectedVersion = () => {
  return versionFolders.value.find(v => v.instanceId === selectedVersionId.value)
}

const getVersionByInstanceId = (instanceId: string) => {
  return versionFolders.value.find(v => v.instanceId === instanceId)
}

const loadInstalledVersions = async () => {
  try {
    const response = await axios.get(`${API_BASE}/minecraft/versions/installed`)
    versionFolders.value = response.data || []
    
    const seenNames = new Set<string>()
    versionFolders.value = versionFolders.value.filter(folder => {
      if (seenNames.has(folder.instanceId)) return false
      seenNames.add(folder.instanceId)
      return true
    })

    if (versionFolders.value.length > 0 && !selectedVersionId.value) {
      selectedVersionId.value = versionFolders.value[0].instanceId
    }
  } catch (error) {
    console.error('加载已安装版本失败:', error)
  }
}

const selectVersionById = (instanceId: string) => {
  selectedVersionId.value = instanceId
}

const confirmVersionSelect = () => {
  showVersionSelect.value = false
}

const openVersionSettings = () => {
  showVersionSelect.value = false
  showVersionSettings.value = true
}

const handleVersionSelect = () => {
  showVersionSelect.value = true
}

const handleVersionSettings = () => {
  if (!selectedVersionId.value && versionFolders.value.length > 0) {
    selectedVersionId.value = versionFolders.value[0].instanceId
  }
  showVersionSettings.value = true
}

const handleDownloadGame = () => {
  router.push('/app/downloads')
}

const handleStartGame = async () => {
  if (!selectedVersionId.value) {
    launchError.value = '请先选择一个版本'
    return
  }

  launchError.value = ''
  isLaunching.value = true

  try {
    const account = accounts.value.find(a => a.name === selectedAccount.value) || accounts.value[0]
    let uuid = ''
    let accessToken = ''
    let userType = 'offline'

    if (account?.type === 'microsoft') {
      uuid = account.uuid || ''
      accessToken = account.accessToken || ''
      userType = 'microsoft'
    } else {
      const username = account?.name || 'Player'
      const uuidResponse = await axios.get(`${API_BASE}/minecraft/uuid/offline`, {
        params: { username }
      })
      uuid = uuidResponse.data.uuid
    }

    await axios.post(`${API_BASE}/minecraft/instance/${selectedVersionId.value}/start`, {
      username: account?.name || 'Player',
      uuid,
      accessToken,
      userType,
      minMemory: minMemory.value,
      maxMemory: maxMemory.value
    })

    router.push('/app/gamelog')
  } catch (error: any) {
    launchError.value = error?.response?.data?.message || '启动游戏失败，请检查网络或后端服务'
    isLaunching.value = false
  }
}

const saveVersionSettings = async () => {
  if (!selectedVersionId.value) return

  try {
    await axios.put(`${API_BASE}/minecraft/instance/${selectedVersionId.value}/settings`, {
      javaPath: javaPath.value,
      minMemory: minMemory.value,
      maxMemory: maxMemory.value,
      jvmParameters: jvmParameters.value,
      username: accounts.value.find(a => a.name === selectedAccount.value)?.name || 'Player'
    })
    alert('设置已保存')
    showVersionSettings.value = false
  } catch (error) {
    console.error('保存设置失败:', error)
    alert('保存设置失败')
  }
}

const resetJvmParameters = () => {
  jvmParameters.value = '-Xms1G -Xmx4G'
}

const autoDetectJava = () => {
  javaPath.value = '/usr/lib/jvm/java-21-openjdk/bin/java'
}

const handleOfflineAccount = () => {
  newAccountType.value = 'offline'
  showLoginModal.value = true
}

const handleMicrosoftLogin = async () => {
  newAccountType.value = 'microsoft'
  showLoginModal.value = true
  oauthStarted.value = true
  oauthStatus.value = ''
  authorizationCode.value = ''

  try {
    const response = await axios.get(`${API_BASE}/oauth/microsoft/authorize`)
    if (response.data && response.data.url) {
      window.open(response.data.url, '_blank')
      oauthStatus.value = 'pending'
    }
  } catch (error) {
    oauthStatus.value = 'failed'
    oauthErrorMessage.value = '获取授权URL失败'
  }
}

const submitAuthorizationCode = async () => {
  if (!authorizationCode.value.trim()) return

  try {
    const response = await axios.post(`${API_BASE}/oauth/microsoft/exchange`, {
      code: authorizationCode.value.trim()
    })

    if (response.data && response.data.success) {
      const account = {
        name: response.data.name,
        uuid: response.data.uuid,
        accessToken: response.data.accessToken,
        type: 'microsoft'
      }
      accounts.value.push(account)
      selectedAccount.value = account.name
      saveAccounts()
      oauthStatus.value = 'completed'
      setTimeout(() => {
        showLoginModal.value = false
        showAccountManager.value = false
      }, 1500)
    } else {
      oauthStatus.value = 'failed'
      oauthErrorMessage.value = response.data?.message || '授权失败'
    }
  } catch (error: any) {
    oauthStatus.value = 'failed'
    oauthErrorMessage.value = error?.response?.data?.message || '授权失败'
  }
}

const openVerificationPage = () => {
  window.open('https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=df007240-5a27-494f-bf7e-9fcf3794384b&response_type=code&redirect_uri=https://login.microsoftonline.com/common/oauth2/nativeclient&scope=XboxLive.signin%20XboxLive.offline_access', '_blank')
}

const cancelMicrosoftLogin = () => {
  showLoginModal.value = false
  oauthStarted.value = false
  oauthStatus.value = ''
  authorizationCode.value = ''
}

const addAccount = () => {
  if (!newAccountName.value.trim()) {
    alert('请输入用户名')
    return
  }

  accounts.value.push({
    name: newAccountName.value.trim(),
    type: 'offline'
  })
  selectedAccount.value = newAccountName.value.trim()
  saveAccounts()
  showLoginModal.value = false
  newAccountName.value = ''
}

const setDefaultAccount = (account: any) => {
  selectedAccount.value = account.name
  saveAccounts()
}

const removeAccount = (index: number) => {
  accounts.value.splice(index, 1)
  if (selectedAccount.value && !accounts.value.find(a => a.name === selectedAccount.value)) {
    selectedAccount.value = accounts.value[0]?.name || ''
  }
  saveAccounts()
}

const saveAccounts = () => {
  localStorage.setItem('mcnsl_accounts', JSON.stringify(accounts.value))
  localStorage.setItem('mcnsl_selected_account', selectedAccount.value)
}

const loadAccounts = () => {
  const saved = localStorage.getItem('mcnsl_accounts')
  if (saved) {
    accounts.value = JSON.parse(saved)
  }
  const selected = localStorage.getItem('mcnsl_selected_account')
  if (selected) {
    selectedAccount.value = selected
  }
}

onMounted(() => {
  loadAccounts()
  loadInstalledVersions()
})
</script>
