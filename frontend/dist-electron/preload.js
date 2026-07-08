import { contextBridge, ipcRenderer } from "electron";
contextBridge.exposeInMainWorld(
  "electronAPI",
  {
    ping() {
      return ipcRenderer.invoke(
        "ping"
      );
    }
  }
);
