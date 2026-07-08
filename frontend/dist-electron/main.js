import { app as t, BrowserWindow as u, ipcMain as f } from "electron";
import o from "node:path";
import c from "node:fs";
import h from "node:net";
import { spawn as j } from "node:child_process";
import { fileURLToPath as v } from "node:url";
const g = v(import.meta.url), l = o.dirname(g);
let a = null, i = null;
const d = !t.isPackaged;
function k() {
  if (d)
    return process.platform === "win32" ? "java" : "/usr/bin/java";
  switch (process.platform) {
    case "darwin":
      return o.join(
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
      return o.join(
        process.resourcesPath,
        "runtime",
        "windows",
        "jdk25",
        "bin",
        "java.exe"
      );
    default:
      return o.join(
        process.resourcesPath,
        "runtime",
        "linux",
        "jdk25",
        "bin",
        "java"
      );
  }
}
function b() {
  if (d)
    return o.resolve(
      l,
      "../../backend/build/libs/backend-1.0.0.jar"
    );
  const e = o.join(
    process.resourcesPath,
    "backend"
  );
  if (!c.existsSync(e))
    throw new Error("找不到 backend 目录：" + e);
  const n = c.readdirSync(e).find((r) => r.endsWith(".jar"));
  if (!n)
    throw new Error("backend 目录没有 jar 文件");
  return o.join(
    e,
    n
  );
}
async function E(e = 8080) {
  return new Promise((n, r) => {
    const m = Date.now() + 15e3, p = setInterval(() => {
      const s = h.connect(e);
      s.once("connect", () => {
        clearInterval(p), s.destroy(), n();
      }), s.once("error", () => {
        s.destroy(), Date.now() > m && (clearInterval(p), r(
          new Error("Spring Boot 启动超时")
        ));
      });
    }, 300);
  });
}
function P() {
  const e = k(), n = b();
  if (console.log("======================"), console.log("Java:", e), console.log("Jar :", n), console.log("======================"), !c.existsSync(n))
    throw new Error("找不到Jar：" + n);
  i = j(
    e,
    [
      "--enable-native-access=ALL-UNNAMED",
      "-jar",
      n,
      "--server.port=8080"
    ],
    {
      cwd: o.dirname(n),
      env: process.env,
      stdio: ["ignore", "pipe", "pipe"]
    }
  ), i.stdout?.on("data", (r) => {
    console.log("[Backend]", r.toString());
  }), i.stderr?.on("data", (r) => {
    console.error("[Backend]", r.toString());
  }), i.on("exit", (r) => {
    console.log("Backend Exit:", r);
  });
}
function w() {
  a = new u({
    width: 1280,
    height: 820,
    minWidth: 1e3,
    minHeight: 700,
    title: "MCNova Server Launcher",
    autoHideMenuBar: !0,
    show: !1,
    webPreferences: {
      preload: o.join(l, "preload.js"),
      nodeIntegration: !1,
      contextIsolation: !0
    }
  }), a.once("ready-to-show", () => {
    a?.show();
  }), d && process.env.VITE_DEV_SERVER_URL ? (a.loadURL(process.env.VITE_DEV_SERVER_URL), a.webContents.openDevTools()) : a.loadFile(
    o.join(l, "../dist/index.html")
  );
}
t.whenReady().then(async () => {
  try {
    P(), await E(), w();
  } catch (e) {
    console.error(e), t.quit();
  }
});
t.on("before-quit", () => {
  i?.kill();
});
t.on("window-all-closed", () => {
  process.platform !== "darwin" && t.quit();
});
t.on("activate", () => {
  u.getAllWindows().length === 0 && w();
});
f.handle("ping", () => "pong");
