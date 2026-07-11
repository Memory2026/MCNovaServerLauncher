import { app, BrowserWindow, ipcMain } from "electron";
import path from "node:path";
import fs from "node:fs";
import net from "node:net";
import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";
const __filename$1 = fileURLToPath(import.meta.url);
const __dirname$1 = path.dirname(__filename$1);
let mainWindow = null;
let backendProcess = null;
const isDev = !app.isPackaged || process.env.NODE_ENV === "development";
function getJavaPath() {
  if (isDev) {
    return process.platform === "win32" ? "java" : "/usr/bin/java";
  }
  switch (process.platform) {
    case "darwin":
      return path.join(
        process.resourcesPath,
        "runtime",
        "mac",
        "jdk25",
        "Contents",
        "Home",
        "bin",
        "java"
      );
    case "win32":
      return path.join(
        process.resourcesPath,
        "runtime",
        "windows",
        "jdk25",
        "bin",
        "java.exe"
      );
    default:
      return path.join(
        process.resourcesPath,
        "runtime",
        "linux",
        "jdk25",
        "bin",
        "java"
      );
  }
}
function getJarPath() {
  if (isDev) {
    return path.resolve(
      __dirname$1,
      "../../backend/build/libs/backend-1.0.0.jar"
    );
  }
  const backendDir = path.join(
    process.resourcesPath,
    "backend"
  );
  if (!fs.existsSync(backendDir)) {
    throw new Error("找不到 backend 目录：" + backendDir);
  }
  const jar = fs.readdirSync(backendDir).find((file) => file.endsWith(".jar"));
  if (!jar) {
    throw new Error("backend 目录没有 jar 文件");
  }
  return path.join(
    backendDir,
    jar
  );
}
async function waitBackend(port = 8080) {
  return new Promise((resolve, reject) => {
    const timeout = Date.now() + 15e3;
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
  backendProcess.stdout?.on("data", (d) => {
    console.log("[Backend]", d.toString());
  });
  backendProcess.stderr?.on("data", (d) => {
    console.error("[Backend]", d.toString());
  });
  backendProcess.on("exit", (code) => {
    console.log("Backend Exit:", code);
  });
}
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 820,
    minWidth: 1e3,
    minHeight: 700,
    title: "MCNova Server Launcher",
    autoHideMenuBar: true,
    show: false,
    webPreferences: {
      preload: path.join(__dirname$1, "preload.js"),
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
      path.join(__dirname$1, "../dist/index.html")
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
