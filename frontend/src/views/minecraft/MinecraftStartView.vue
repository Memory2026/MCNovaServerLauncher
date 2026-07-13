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
          <button
            @click="showAccountManager = true"
            class="px-4 py-2 text-sm font-semibold text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-all"
          >
            {{ selectedAccount || accounts.length > 0 ? '切换' : '登录' }}
          </button>
        </div>
        <button
          @click="showAccountManager = true"
          class="mt-4 w-full py-2.5 text-sm font-medium text-gray-500 hover:text-gray-700 bg-gray-50 rounded-lg hover:bg-gray-100 transition-all flex items-center justify-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          账户管理
        </button>
      </div>

      <div class="bg-gray-50 rounded-xl border border-gray-100 overflow-hidden mb-6">
        <div class="flex items-center justify-between px-5 py-4 cursor-pointer" @click="handleVersionSelect">
          <div class="text-sm font-medium text-gray-500">版本选择</div>
          <div class="flex items-center gap-2 text-sm">
            <span :class="currentVersion ? 'text-gray-700' : 'text-gray-400'">
              {{ currentVersion || '无版本' }}
            </span>
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <button
          v-if="hasVersions"
          @click="handleStartGame()"
          :disabled="!currentVersion || isLaunching"
          class="w-full py-5 text-lg font-semibold text-white bg-zinc-900 rounded-xl hover:bg-zinc-800 transition-all cursor-pointer shadow-lg hover:shadow-xl flex items-center justify-center gap-3 disabled:opacity-50 disabled:cursor-not-allowed"
        >
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

        <button
          v-else
          @click="handleDownloadGame()"
          class="w-full py-5 text-lg font-semibold text-white bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl hover:from-blue-600 hover:to-purple-700 transition-all cursor-pointer shadow-lg hover:shadow-xl flex items-center justify-center gap-3"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          下载游戏
        </button>

        <div class="flex gap-4">
          <button
            @click="handleVersionSelect"
            class="flex-1 py-4 text-base font-medium text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all cursor-pointer shadow-sm"
          >
            版本选择
          </button>
          <button
            v-if="hasVersions"
            @click="handleVersionSettings"
            class="flex-1 py-4 text-base font-medium text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all cursor-pointer shadow-sm"
          >
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
            <button
              @click="handleMicrosoftLogin"
              class="px-4 py-2 text-sm font-medium text-white bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors flex items-center gap-2"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              正版登录
            </button>
            <button
              @click="handleOfflineAccount"
              class="px-4 py-2 text-sm font-medium text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center gap-2"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              离线账户
            </button>
            <button
              @click="showAccountManager = false"
              class="w-8 h-8 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-all ml-2"
            >
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
            <div
              v-for="(account, index) in accounts"
              :key="index"
              class="flex items-center justify-between p-4 bg-white rounded-xl border border-gray-200 transition-colors"
            >
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
                <button
                  v-else
                  @click="setDefaultAccount(account)"
                  class="px-3 py-1.5 text-xs font-medium text-white bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors"
                >选择</button>
                <button
                  @click="removeAccount(index)"
                  class="px-3 py-1.5 text-xs font-medium text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors"
                >删除</button>
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
            <button
              @click="newAccountType = 'microsoft'"
              class="px-6 py-2.5 rounded-full font-medium text-sm transition-all flex items-center gap-2 bg-white text-blue-500 border border-blue-300 hover:border-blue-400"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
              正版
            </button>
            <button
              @click="newAccountType = 'offline'"
              class="px-6 py-2.5 rounded-full font-medium text-sm transition-all flex items-center gap-2 bg-blue-500 text-white shadow-md"
            >
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
            <input
              v-model="newAccountName"
              type="text"
              placeholder="游戏用户名"
              class="w-full px-4 py-3 text-sm border-2 border-blue-400 rounded-lg focus:outline-none focus:border-blue-500 transition-all pr-10"
            />
          </div>

          <button
            @click="addAccount"
            class="mt-3 w-full py-3 text-sm font-semibold text-blue-600 bg-white border-2 border-blue-400 rounded-lg hover:bg-blue-50 hover:border-blue-500 transition-all"
          >
            创建
          </button>
        </div>

        <div v-else class="p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-bold text-gray-900">微软登录</h3>
            <button
              @click="cancelMicrosoftLogin"
              class="w-6 h-6 flex items-center justify-center text-gray-400 hover:text-gray-600"
            >
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
                <input
                  v-model="authorizationCode"
                  type="text"
                  placeholder="输入授权码"
                  class="flex-1 px-4 py-2.5 text-sm border-2 border-blue-400 rounded-xl focus:outline-none focus:border-blue-500 transition-all"
                  :disabled="oauthStatus === 'completed'"
                />
                <button
                  @click="submitAuthorizationCode"
                  :disabled="oauthStatus !== 'pending' || !authorizationCode.trim()"
                  class="px-5 py-2.5 text-sm font-medium text-white bg-blue-500 rounded-xl hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >提交</button>
              </div>
            </div>

            <div class="flex items-center gap-3 px-4 py-3 bg-gray-100 rounded-xl">
              <svg
                v-if="oauthStatus === 'pending' && authorizationCode.trim()"
                class="w-4 h-4 text-blue-500 animate-spin"
                fill="none"
                viewBox="0 0 24 24"
              >
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
              <button
                @click="openVerificationPage"
                class="px-5 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
              >重新打开网页</button>
              <button
                @click="cancelMicrosoftLogin"
                class="px-5 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
              >取消</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showVersionSelect" class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden">
        <div class="flex justify-between items-center px-6 py-4 border-b border-gray-100">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 bg-gradient-to-br from-green-500 to-emerald-600 rounded-lg flex items-center justify-center shadow-md">
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="text-xl font-bold text-gray-900">版本选择</h2>
          </div>
          <button
            @click="showVersionSelect = false"
            class="w-8 h-8 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-all"
          >
            <span class="text-lg">×</span>
          </button>
        </div>

        <div class="p-6 max-h-[50vh] overflow-y-auto">
          <div v-if="installedVersions.length === 0" class="text-center py-12 text-gray-400">
            <div class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p class="text-sm">暂无已安装的版本</p>
            <button
              @click="showVersionSelect = false; handleDownloadGame()"
              class="mt-4 px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
            >去下载</button>
          </div>

          <div v-else class="space-y-2">
            <button
              v-for="item in installedVersions"
              :key="`${item.instanceId}-${item.version}`"
              @click="selectVersion(item.version)"
              :class="[
                'w-full p-4 text-left rounded-xl border-2 transition-all flex items-center justify-between',
                currentVersion === item.version
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-100 bg-white hover:border-gray-200 hover:bg-gray-50'
              ]"
            >
              <div>
                <div class="font-semibold text-gray-800">{{ item.version }}</div>
                <div class="text-xs text-gray-400">实例: {{ item.instanceId }}</div>
              </div>
              <svg
                v-if="currentVersion === item.version"
                class="w-5 h-5 text-blue-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
const API_BASE = import.meta.env.PROD ? 'http://localhost:8080/api' : '/api'

interface Account {
  name: string
  type: string
  id?: string
}

interface OAuthInfo {
  deviceCode: string
  userCode: string
  verificationUri: string
  expiresIn: number
  interval: number
}

const accounts = ref<Account[]>([])
const selectedAccount = ref('')
const hasVersions = ref(false)
const currentVersion = ref('')
const installedVersions = ref<{instanceId: string, version: string, path: string}[]>([])
const showAccountManager = ref(false)
const showAddAccount = ref(false)
const newAccountType = ref<'microsoft' | 'offline'>('microsoft')
const newAccountName = ref('')
const autoLogin = ref(false)
const showLoginModal = ref(false)
const showVersionSelect = ref(false)
const isLaunching = ref(false)
const launchError = ref('')

const oauthStarted = ref(false)
const oauthInfo = ref<OAuthInfo>({
  deviceCode: '',
  userCode: '',
  verificationUri: '',
  expiresIn: 0,
  interval: 0
})
const oauthStatus = ref<'pending' | 'completed' | 'failed'>('pending')
const oauthErrorMessage = ref('')
const authorizationCode = ref('')
let oauthPollingInterval: number | null = null

async function loadInstalledVersions() {
  try {
    const response = await axios.get(`${API_BASE}/minecraft/versions/installed`)
    installedVersions.value = response.data
    hasVersions.value = installedVersions.value.length > 0
    if (hasVersions.value && !currentVersion.value && installedVersions.value[0]) {
      currentVersion.value = installedVersions.value[0].version
    }
  } catch (error) {
    console.error('获取已安装版本失败:', error)
    hasVersions.value = false
  }
}

function handleLogin() {
  if (selectedAccount.value) {
    console.log('登录账号:', selectedAccount.value)
  }
}

async function handleStartGame() {
  console.log('handleStartGame called')
  console.log('currentVersion:', currentVersion.value)
  console.log('installedVersions:', installedVersions.value)

  if (!currentVersion.value || !installedVersions.value.length) {
    console.log('No version selected or no installed versions')
    return
  }

  const versionInfo = installedVersions.value.find(v => v.version === currentVersion.value)
  if (!versionInfo) {
    launchError.value = '未找到对应的版本'
    return
  }

  const account = accounts.value.find(a => a.name === selectedAccount.value) || accounts.value[0]
  const username = account?.name || 'Player'
  
  isLaunching.value = true
  launchError.value = ''

  try {
    const body: Record<string, string> = {
      username
    }

    if (account?.type === 'offline' || !account) {
      const uuidResponse = await axios.get(`${API_BASE}/minecraft/uuid/offline`, {
        params: { username }
      })
      body.uuid = uuidResponse.data.uuid
      body.accessToken = '0'
      body.userType = 'legacy'
    } else if (account?.id) {
      body.uuid = account.id
    }

    console.log('Starting game with:', body)
    const response = await axios.post(`${API_BASE}/minecraft/instance/${versionInfo.instanceId}/start`, body)
    console.log('Start response:', response.data)
    
    setTimeout(() => {
      isLaunching.value = false
    }, 2000)
  } catch (error: any) {
    console.error('启动游戏失败:', error)
    launchError.value = error.response?.data?.error || '启动游戏失败'
    isLaunching.value = false
  }
}

function handleDownloadGame() {
  console.log('下载游戏')
}

function handleVersionSelect() {
  loadInstalledVersions()
  showVersionSelect.value = true
}

function selectVersion(version: string) {
  currentVersion.value = version
  showVersionSelect.value = false
}

function handleVersionSettings() {
  console.log('版本设置')
}

loadInstalledVersions()

function addAccount() {
  if (!newAccountName.value.trim()) {
    return
  }
  accounts.value.push({
    name: newAccountName.value.trim(),
    type: newAccountType.value
  })
  newAccountName.value = ''
  newAccountType.value = 'microsoft'
  autoLogin.value = false
  showLoginModal.value = false
}

function removeAccount(index: number) {
  accounts.value.splice(index, 1)
}

function setDefaultAccount(account: Account) {
  selectedAccount.value = account.name
  console.log('设为默认账户:', account.name)
}

function handleAccountSettings() {
  console.log('账户设置')
}

async function handleMicrosoftLogin() {
  showLoginModal.value = true
  newAccountType.value = 'microsoft'
  oauthStarted.value = true
  oauthStatus.value = 'pending'
  oauthErrorMessage.value = ''

  try {
    const response = await axios.get(`${API_BASE}/oauth/microsoft/authorize`)
    const data = response.data

    oauthInfo.value = {
      deviceCode: '',
      userCode: '',
      verificationUri: data.url,
      expiresIn: 300,
      interval: 5
    }

    window.open(data.url, '_blank', 'width=800,height=600')
  } catch (error: any) {
    console.error('获取授权URL失败:', error)
    oauthErrorMessage.value = error.response?.data?.error || '获取授权链接失败，请重试'
    oauthStatus.value = 'failed'
  }
}

function handleOfflineAccount() {
  newAccountType.value = 'offline'
  showLoginModal.value = true
}

async function submitAuthorizationCode() {
  if (!authorizationCode.value.trim()) {
    return
  }

  oauthStatus.value = 'pending'
  oauthErrorMessage.value = ''

  try {
    const response = await axios.post(`${API_BASE}/oauth/microsoft/exchange`, {
      code: authorizationCode.value.trim()
    })
    const data = response.data

    oauthStatus.value = 'completed'

    accounts.value.push({
      name: data.user?.name || data.user?.username || 'Microsoft Account',
      type: 'microsoft',
      id: data.user?.id
    })

    authorizationCode.value = ''

    setTimeout(() => {
      showLoginModal.value = false
      resetOAuthState()
    }, 1500)
  } catch (error: any) {
    console.error('兑换授权码失败:', error)
    oauthErrorMessage.value = error.response?.data?.error || '兑换授权码失败，请重试'
    oauthStatus.value = 'failed'
  }
}

async function checkOAuthStatus() {
  if (oauthPollingInterval) {
    clearInterval(oauthPollingInterval)
  }

  oauthPollingInterval = window.setInterval(async () => {
    try {
      const response = await axios.get(`/api/oauth/microsoft/status/${oauthInfo.value.deviceCode}`)
      const data = response.data

      if (data.status === 'completed') {
        if (oauthPollingInterval) {
          clearInterval(oauthPollingInterval)
        }
        oauthStatus.value = 'completed'

        accounts.value.push({
          name: data.user?.name || data.user?.username || 'Microsoft Account',
          type: 'microsoft',
          id: data.user?.id
        })

        setTimeout(() => {
          showLoginModal.value = false
          resetOAuthState()
        }, 1500)
      } else if (data.status === 'failed') {
        if (oauthPollingInterval) {
          clearInterval(oauthPollingInterval)
        }
        oauthStatus.value = 'failed'
        oauthErrorMessage.value = data.error_message || '登录失败'
      }
    } catch (error) {
      console.error('检查授权状态失败:', error)
    }
  }, oauthInfo.value.interval * 1000)
}

function copyVerificationUri() {
  navigator.clipboard.writeText(oauthInfo.value.verificationUri)
}

function copyUserCode() {
  navigator.clipboard.writeText(oauthInfo.value.userCode)
}

function openVerificationPage() {
  window.open(oauthInfo.value.verificationUri, '_blank')
}

async function completeMicrosoftLogin() {
  try {
    await axios.post(`/api/oauth/microsoft/complete/${oauthInfo.value.deviceCode}`)
  } catch (error) {
    console.error('完成验证失败:', error)
  }
}

function cancelMicrosoftLogin() {
  if (oauthPollingInterval) {
    clearInterval(oauthPollingInterval)
  }
  showLoginModal.value = false
  resetOAuthState()
}

function resetOAuthState() {
  oauthStarted.value = false
  oauthInfo.value = {
    deviceCode: '',
    userCode: '',
    verificationUri: '',
    expiresIn: 0,
    interval: 0
  }
  oauthStatus.value = 'pending'
  oauthErrorMessage.value = ''
}

onMounted(() => {
  loadInstalledVersions()
})
</script>
