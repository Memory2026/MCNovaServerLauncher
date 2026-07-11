import { app, BrowserWindow, ipcMain } from "electron";
import path from "node:path";
import fs from "node:fs";
import net from "node:net";
import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";

type ChildProcess = ReturnType<typeof spawn>;

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let mainWindow: BrowserWindow | null = null;
let backendProcess: ChildProcess | null = null;

const isDev = !app.isPackaged || process.env.NODE_ENV === 'development';

function getJavaPath(): string {
  console.log("getJavaPath() - isDev:", isDev, "platform:", process.platform);

  if (isDev) {
    return process.platform === "win32" ? "java" : "/usr/bin/java";
  }

  const runtimePaths: Record<string, string[]> = {
    darwin: ["runtime", "mac", "jdk25", "Contents", "Home", "bin", "java"],
    win32: ["runtime", "windows", "jdk25", "bin", "java"],
    linux: ["runtime", "linux", "jdk25", "bin", "java"]
  };

  const paths = runtimePaths[process.platform] || runtimePaths.linux;
  const bundledPath = path.join(process.resourcesPath, ...paths);
  const bundledPathExe = process.platform === "win32" ? bundledPath + ".exe" : bundledPath;

  console.log("getJavaPath() - bundledPath:", bundledPath);
  console.log("getJavaPath() - bundledPathExe:", bundledPathExe);
  console.log("getJavaPath() - fs.existsSync(bundledPath):", fs.existsSync(bundledPath));
  console.log("getJavaPath() - fs.existsSync(bundledPathExe):", fs.existsSync(bundledPathExe));

  if (fs.existsSync(bundledPath)) {
    return bundledPath;
  }
  if (fs.existsSync(bundledPathExe)) {
    return bundledPathExe;
  }

  console.warn("未找到打包的 Java 运行时，尝试使用系统 Java:", bundledPath);

  return "java";
}

function getJarPath(): string {
  if (isDev) {
    return path.resolve(
      __dirname,

      '../../backend/build/libs/backend-1.0.0.jar',
    )
  }

  const backendDir = path.join(
    process.resourcesPath,

    'backend',
  )

  if (!fs.existsSync(backendDir)) {
    throw new Error('找不到 backend 目录：' + backendDir)
  }

  const jar = fs
    .readdirSync(backendDir)

    .find((file) => file.endsWith('.jar'))

  if (!jar) {
    throw new Error('backend 目录没有 jar 文件')
  }

  return path.join(
    backendDir,

    jar,
  )
}

async function waitBackend(port = 8080): Promise<void> {

  return new Promise((resolve, reject) => {

    const timeout = Date.now() + 15000;

    const timer = setInterval(() => {

      const socket = net.connect(port);

      socket.once("connect", () => {

        clearInterval(timer);
        socket.destroy();
        resolve();

      });

      socket.once("error", () => {

        socket.destroy();

        if (Date.now() > timeout) {

          clearInterval(timer);

          reject(
            new Error("Spring Boot 启动超时")
          );

        }

      });

    }, 300);

  });

}

function startBackend() {

  const java = getJavaPath();
  const jar = getJarPath();

  console.log("======================");
  console.log("Java:", java);
  console.log("Jar :", jar);
  console.log("======================");

  if (!fs.existsSync(jar)) {

    throw new Error("找不到Jar：" + jar);

  }

  backendProcess = spawn(

    java,

    [

      "--enable-native-access=ALL-UNNAMED",

      "-jar",

      jar,

      "--server.port=8080"

    ],

    {

      cwd: path.dirname(jar),

      env: process.env,

      stdio: ["ignore", "pipe", "pipe"]

    }

  );

  backendProcess.stdout?.on("data", d => {

    console.log("[Backend]", d.toString());

  });

  backendProcess.stderr?.on("data", d => {

    console.error("[Backend]", d.toString());

  });

  backendProcess.on("exit", code => {

    console.log("Backend Exit:", code);

  });

}

function createWindow() {

  mainWindow = new BrowserWindow({

    width: 1280,

    height: 820,

    minWidth: 1000,

    minHeight: 700,

    title: "MCNova Server Launcher",

    autoHideMenuBar: true,

    show: false,

    webPreferences: {

      preload: path.join(__dirname, "preload.js"),

      nodeIntegration: false,

      contextIsolation: true

    }

  });

  mainWindow.once("ready-to-show", () => {

    mainWindow?.show();

  });

  if (isDev && process.env.VITE_DEV_SERVER_URL) {

    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL);

    mainWindow.webContents.openDevTools();

  } else {

    mainWindow.loadFile(

      path.join(__dirname, "../dist/index.html")

    );

  }

}

app.whenReady().then(async () => {
  try {
    if (isDev) {
      try {
        await waitBackend();
        console.log("后端已在运行");
      } catch {
        console.log("后端未运行，尝试启动...");
        startBackend();
        await waitBackend();
      }
    } else {
      startBackend();
      await waitBackend();
    }

    createWindow();

  } catch (e) {
    console.error(e);
    if (!isDev) {
      const { dialog } = await import('electron');
      dialog.showErrorBox(
        '启动失败',
        '无法启动后端服务。请确保系统已安装 Java 21 或更高版本。\n\n错误信息: ' + (e as Error).message
      );
    }
    app.quit();
  }
});

app.on("before-quit", () => {

  backendProcess?.kill();

});

app.on("window-all-closed", () => {

  if (process.platform !== "darwin") {

    app.quit();

  }

});

app.on("activate", () => {

  if (BrowserWindow.getAllWindows().length === 0) {

    createWindow();

  }

});

ipcMain.handle("ping", () => "pong");
