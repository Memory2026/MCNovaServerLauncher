import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:25565/api',

  timeout: 10000,
})

/**
 * Minecraft实例
 */
export interface MinecraftInstance {
  id: string

  name: string

  version: string

  loader: string

  path: string
}

/**
 * 启动参数
 */
export interface LaunchRequest {
  id: string

  username: string

  minMemory: number

  maxMemory: number
}

/**
 * 下载任务
 */
export interface DownloadTask {
  id: string

  fileName: string

  current: number

  total: number

  speed: number

  status: string
}

/**
 * 获取实例列表
 */
export function getInstances() {
  return api.get<MinecraftInstance[]>('/minecraft/instance/list')
}

/**
 * 启动Minecraft
 */
export function launchMinecraft(data: LaunchRequest) {
  return api.post(
    '/minecraft/launch',

    data,
  )
}

/**
 * 停止Minecraft
 */
export function stopMinecraft(id: string) {
  return api.post(`/minecraft/process/stop/${id}`)
}

/**
 * 安装 Vanilla
 */
export function installVanilla(
  version: string,

  gameDir: string,
) {
  return api.post(
    '/minecraft/install/vanilla',

    null,

    {
      params: {
        version,

        gameDir,
      },
    },
  )
}

/**
 * 安装 Fabric
 */
export function installFabric(
  version: string,

  loader: string,

  gameDir: string,
) {
  return api.post(
    '/minecraft/install/fabric',

    null,

    {
      params: {
        version,

        loader,

        gameDir,
      },
    },
  )
}

/**
 * 获取下载任务
 */
export function getDownloadTasks() {
  return api.get<DownloadTask[]>('/minecraft/install/tasks')
}

/**
 * 获取单个下载任务
 */
export function getDownloadTask(id: string) {
  return api.get<DownloadTask>(`/minecraft/install/task/${id}`)
}

/**
 * 取消下载
 */
export function cancelDownload(id: string) {
  return api.post(`/minecraft/install/cancel/${id}`)
}
