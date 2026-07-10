import { contextBridge as e, ipcRenderer as n } from "electron";
e.exposeInMainWorld(
  "electronAPI",
  {
    ping() {
      return n.invoke(
        "ping"
      );
    }
  }
);
