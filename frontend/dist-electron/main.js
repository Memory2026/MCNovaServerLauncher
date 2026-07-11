import { app as r, BrowserWindow as h, ipcMain as v } from "electron";
import t from "node:path";
import a from "node:fs";
import g from "node:net";
import { spawn as b } from "node:child_process";
import { fileURLToPath as j } from "node:url";
const P = j(import.meta.url), u = t.dirname(P);
let i = null, l = null;
const c = !r.isPackaged || process.env.NODE_ENV === "development";
function x() {
  if (console.log("getJavaPath() - isDev:", c, "platform:", process.platform), c)
    return process.platform === "win32" ? "java" : "/usr/bin/java";
  const o = {
    darwin: ["runtime", "mac", "jdk25", "Contents", "Home", "bin", "java"],
    win32: ["runtime", "windows", "jdk25", "bin", "java"],
    linux: ["runtime", "linux", "jdk25", "bin", "java"]
  }, n = o[process.platform] || o.linux, e = t.join(process.resourcesPath, ...n), s = process.platform === "win32" ? e + ".exe" : e;
  return console.log("getJavaPath() - bundledPath:", e), console.log("getJavaPath() - bundledPathExe:", s), console.log("getJavaPath() - fs.existsSync(bundledPath):", a.existsSync(e)), console.log("getJavaPath() - fs.existsSync(bundledPathExe):", a.existsSync(s)), a.existsSync(e) ? e : a.existsSync(s) ? s : (console.warn("未找到打包的 Java 运行时，尝试使用系统 Java:", e), "java");
}
function E() {
  if (c)
    return t.resolve(
      u,
      "../../backend/build/libs/backend-1.0.0.jar"
    );
  const o = t.join(
    process.resourcesPath,
    "backend"
  );
  if (!a.existsSync(o))
    throw new Error("找不到 backend 目录：" + o);
  const n = a.readdirSync(o).find((e) => e.endsWith(".jar"));
  if (!n)
    throw new Error("backend 目录没有 jar 文件");
  return t.join(
    o,
    n
  );
}
async function p(o = 8080) {
  return new Promise((n, e) => {
    const s = Date.now() + 15e3, w = setInterval(() => {
      const d = g.connect(o);
      d.once("connect", () => {
        clearInterval(w), d.destroy(), n();
      }), d.once("error", () => {
        d.destroy(), Date.now() > s && (clearInterval(w), e(
          new Error("Spring Boot 启动超时")
        ));
      });
    }, 300);
  });
}
function f() {
  const o = x(), n = E();
  if (console.log("======================"), console.log("Java:", o), console.log("Jar :", n), console.log("======================"), !a.existsSync(n))
    throw new Error("找不到Jar：" + n);
  l = b(
    o,
    [
      "--enable-native-access=ALL-UNNAMED",
      "-jar",
      n,
      "--server.port=8080"
    ],
    {
      cwd: t.dirname(n),
      env: process.env,
      stdio: ["ignore", "pipe", "pipe"]
    }
  ), l.stdout?.on("data", (e) => {
    console.log("[Backend]", e.toString());
  }), l.stderr?.on("data", (e) => {
    console.error("[Backend]", e.toString());
  }), l.on("exit", (e) => {
    console.log("Backend Exit:", e);
  });
}
function m() {
  i = new h({
    width: 1280,
    height: 820,
    minWidth: 1e3,
    minHeight: 700,
    title: "MCNova Server Launcher",
    autoHideMenuBar: !0,
    show: !1,
    webPreferences: {
      preload: t.join(u, "preload.js"),
      nodeIntegration: !1,
      contextIsolation: !0
    }
  }), i.once("ready-to-show", () => {
    i?.show();
  }), c && process.env.VITE_DEV_SERVER_URL ? (i.loadURL(process.env.VITE_DEV_SERVER_URL), i.webContents.openDevTools()) : i.loadFile(
    t.join(u, "../dist/index.html")
  );
}
r.whenReady().then(async () => {
  try {
    if (c)
      try {
        await p(), console.log("后端已在运行");
      } catch {
        console.log("后端未运行，尝试启动..."), f(), await p();
      }
    else
      f(), await p();
    m();
  } catch (o) {
    if (console.error(o), !c) {
      const { dialog: n } = await import("electron");
      n.showErrorBox(
        "启动失败",
        `无法启动后端服务。请确保系统已安装 Java 21 或更高版本。

错误信息: ` + o.message
      );
    }
    r.quit();
  }
});
r.on("before-quit", () => {
  l?.kill();
});
r.on("window-all-closed", () => {
  process.platform !== "darwin" && r.quit();
});
r.on("activate", () => {
  h.getAllWindows().length === 0 && m();
});
v.handle("ping", () => "pong");
