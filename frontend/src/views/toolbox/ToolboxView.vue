<template>
  <div class="max-w-6xl mx-auto w-full transition-all duration-300">
    <div class="flex gap-10 mb-8 border-b-2 border-gray-100 pb-0.5">
      <button v-for="tab in tabs" :key="tab.id"
              class="pb-4 px-4 text-lg font-bold transition-all duration-300 relative cursor-pointer"
              :class="activeTab === tab.id ? 'text-emerald-600 scale-[1.02]' : 'text-gray-400 hover:text-slate-600'"
              @click="activeTab = tab.id">
        {{ tab.label }}
        <div v-if="activeTab === tab.id" class="absolute bottom-[-2px] left-0 right-0 h-[4px] bg-emerald-500 rounded-full animate-in fade-in duration-200"></div>
      </button>
    </div>

    <div class="flex flex-col gap-4">
      <div v-for="section in visibleSections" :key="section.id"
           class="bg-white border border-gray-100 rounded-2xl shadow-sm overflow-hidden transition-all duration-300">

        <button @click="toggleSection(section.id)"
                class="w-full flex justify-between items-center p-5 px-6 font-bold text-slate-700 hover:bg-slate-50/50 transition-colors cursor-pointer">
          <span>{{ section.title }}</span>
          <span class="transform transition-transform duration-300 text-gray-400 text-xs"
                :class="openSections[section.id] ? 'rotate-180' : ''">▼</span>
        </button>

        <div v-show="openSections[section.id]"
             class="p-4 px-6 pt-0 flex flex-col gap-3 border-t border-gray-50/80 animate-in fade-in slide-in-from-top-2 duration-200">
          <a v-for="item in section.links" :key="item.name" :href="item.url" target="_blank"
             class="group flex items-center bg-gray-50/40 hover:bg-gray-50 p-4 px-5 rounded-xl border border-gray-100/60 transition-all duration-200 text-slate-700 decoration-transparent">

            <div class="w-10 h-10 rounded-xl mr-4 flex items-center justify-center border border-gray-100 shadow-sm flex-shrink-0 overflow-hidden"
                 :class="item.bgColor || 'bg-white'">
              <img v-if="item.icon"
                   :src="item.icon"
                   class="w-full h-full object-cover">
              <span v-else class="font-bold text-xs flex flex-col items-center justify-center leading-none text-center" :class="item.textColor || 'text-white'">
                {{ item.short }}
              </span>
            </div>

            <div class="flex flex-col gap-0.5">
              <span class="text-base font-semibold group-hover:text-emerald-600 transition-colors">{{ item.name }}</span>
              <span class="text-xs text-gray-400">{{ item.desc }}</span>
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface LinkItem {
  name: string
  desc: string
  url: string
  icon?: string
  short?: string
  bgColor?: string
  textColor?: string
}

interface Section {
  id: string
  title: string
  links: LinkItem[]
}

const activeTab = ref('wiki')

// 初始化所有 section 为打开状态
const openSections = ref<Record<string, boolean>>({
  coreWiki: true, playerComm: true, onlineTools: true, skins: true,
  resourceNav: true, modPlat: true, shaders: true, builds: true, servers: true
})

const toggleSection = (id: string) => {
  const key = id as keyof typeof openSections.value;
  openSections.value[key] = !openSections.value[key];
}

const sectionsData: Record<string, Section[]> = {
  wiki: [
    { id: 'coreWiki', title: '核心百科', links: [
        { name: 'Minecraft Wiki', desc: '最权威的官方百科', url: 'https://minecraft.fandom.com/zh/wiki/Minecraft_Wiki', icon: 'https://minecraft.fandom.com/zh/wiki/Special:FilePath/Wiki.png' },
        { name: 'MC百科', desc: '中文 Mod 百科数据库', url: 'https://www.mcmod.cn/', icon:'https://www.google.com/s2/favicons?domain=mcmod.cn&sz=128' },
        { name: 'DigMinecraft', desc: '全物品/方块 ID 查询', url: 'https://www.digminecraft.com/', icon:'https://www.google.com/s2/favicons?domain=digminecraft.com&sz=128' }
      ]},
    { id: 'playerComm', title: '玩家社区', links: [
        { name: 'MCBBS', desc: '国内最大 MC 中文论坛', url: 'https://www.mcbbs.net/', short: '📦', bgColor: 'bg-amber-800' },
        { name: 'MineBBS', desc: '国内 MC 中文论坛', url: 'https://www.minebbs.com/', short: 'MB', bgColor: 'bg-blue-600' },
        { name: '苦力怕论坛', desc: '资源丰富的中文社区', url: 'https://www.klpbbs.com/', icon:'https://www.google.com/s2/favicons?domain=klpbbs.com&sz=128' },
        { name: 'Bilibili MC', desc: '视频教程 / 实况 / 攻略', url: 'https://www.bilibili.com/', icon:'https://www.google.com/s2/favicons?domain=bilibili.com&sz=128' }
      ]},
    { id: 'onlineTools', title: '在线工具', links: [
        { name: 'Chunkbase', desc: '种子地图 / 结构定位', url: 'https://www.chunkbase.com/', icon:'https://www.google.com/s2/favicons?domain=chunkbase.com&sz=128' },
        { name: 'MCStacker', desc: '命令在线生成器', url: 'https://mcstacker.net/', icon:'https://www.google.com/s2/favicons?domain=mcstacker.net&sz=128' },
        { name: 'MC Tools', desc: '合成/烟花/药水/旗帜', url: 'https://minecraft.tools/zh/', icon:'https://www.google.com/s2/favicons?domain=minecraft.tools&sz=128' },
        { name: 'Misode', desc: '数据包 / 世界编辑器', url: 'https://misode.github.io/', icon:'https://www.google.com/s2/favicons?domain=misode.github.io&sz=128' },
        { name: 'mclo.gs', desc: '游戏日志分析工具', url: 'https://mclo.gs/', icon:'https://www.google.com/s2/favicons?domain=mclo.gs&sz=128' },
        { name: 'Minecraft Heads', desc: '头颅数据库/give指令', url: 'https://minecraft-heads.com/', icon:'https://www.google.com/s2/favicons?domain=minecraft-heads.com&sz=128' },
        { name: 'Colorize FUN', desc: 'MC 彩色文本生成器', url: 'https://colorize.fun/', icon:'https://www.google.com/s2/favicons?domain=colorize.fun&sz=128' },
        { name: 'Textcraft', desc: 'MC 风格 Logo 生成', url: 'https://textcraft.net/', icon:'https://www.google.com/s2/favicons?domain=textcraft.net&sz=128' },
        { name: 'CraftMC Tools', desc: '红石模拟/圆形生成', url: 'https://www.craftmc.net/', icon:'https://www.google.com/s2/favicons?domain=craftmc.net&sz=128' }
      ]},
    { id: 'skins', title: '皮肤资源', links: [
        { name: 'NameMC', desc: '正版皮肤 / UUID 查询', url: 'https://namemc.com/', icon:'https://www.google.com/s2/favicons?domain=namemc.com&sz=128' },
        { name: 'LittleSkin', desc: '国内皮肤站', url: 'https://littleskin.cn/', icon:'https://www.google.com/s2/favicons?domain=littleskin.cn&sz=128' },
        { name: 'Nova Skin', desc: '3D 皮肤编辑器', url: 'https://minecraft.novaskin.me/', icon:'https://www.google.com/s2/favicons?domain=minecraft.novaskin.me&sz=128' },
        { name: 'The Skindex', desc: '百万皮肤分享社区', url: 'https://www.minecraftskins.com/', icon:'https://www.google.com/s2/favicons?domain=minecraftskins.com&sz=128' },
        { name: 'SkinMC', desc: '3D 皮肤查看器', url: 'https://skinmc.net/zh', icon:'https://www.google.com/s2/favicons?domain=skinmc.net&sz=128' }
      ]},
    { id: 'resourceNav', title: '资源导航', links: [
        { name: 'MCNav', desc: 'MC 资源大全一站导航', url: 'https://www.mcnav.net/', icon:'https://www.google.com/s2/favicons?domain=mcnav.net&sz=128' },
        { name: 'MinecraftXZ', desc: '中文资源下载站', url: 'https://www.minecraftxz.com/', icon:'https://www.google.com/s2/favicons?domain=minecraftxz.com&sz=128' }
      ]}
  ],
  mods: [
    { id: 'modPlat', title: '模组与整合包', links: [
        { name: 'CurseForge', desc: '全球最大 Mod 整合包平台', url: 'https://www.curseforge.com/minecraft', icon:'https://www.google.com/s2/favicons?domain=curseforge.com&sz=128' },
        { name: 'Modrinth', desc: '新兴开源 Mod 平台', url: 'https://modrinth.com/', icon:'https://www.google.com/s2/favicons?domain=modrinth.com&sz=128' },
        { name: 'Planet Minecraft', desc: '全球最大 MC 社区', url: 'https://www.planetminecraft.com/', icon:'https://www.google.com/s2/favicons?domain=planetminecraft.com&sz=128' },
        { name: 'MCPEDL', desc: '基岩版资源大全', url: 'https://mcpedl.com/', icon:'https://www.google.com/s2/favicons?domain=mcpedl.com&sz=128' }
      ]},
    { id: 'shaders', title: '材质与光影', links: [
        { name: 'ResourcePack', desc: '海量材质包下载', url: 'https://resourcepack.net/', icon:'https://www.google.com/s2/favicons?domain=resourcepack.net&sz=128' },
        { name: 'ShadersMods', desc: '光影资源合集', url: 'https://shadersmods.com/', icon:'https://www.google.com/s2/favicons?domain=shadersmods.com&sz=128' },
        { name: 'Vanilla Tweaks', desc: '原版微调增强包', url: 'https://vanillatweaks.net/', icon:'https://www.google.com/s2/favicons?domain=vanillatweaks.net&sz=128' }
      ]},
    { id: 'builds', title: '地图与建筑', links: [
        { name: 'Plotz', desc: '圆形/球形建造蓝图', url: 'https://www.plotz.co.uk/', icon:'https://www.google.com/s2/favicons?domain=plotz.co.uk&sz=128' },
        { name: 'Block Palettes', desc: '方块配色方案库', url: 'https://www.blockpalettes.com/', icon:'https://www.google.com/s2/favicons?domain=blockpalettes.com&sz=128' },
        { name: 'MinecraftShapes', desc: '几何形状建造指南', url: 'https://minecraftshapes.com/', icon:'https://www.google.com/s2/favicons?domain=minecraftshapes.com&sz=128' },
        { name: 'Minecraft Maps', desc: '冒险/解谜地图下载', url: 'https://www.minecraftmaps.com/', icon:'https://www.google.com/s2/favicons?domain=minecraftmaps.com&sz=128' }
      ]},
    { id: 'servers', title: '服务器与插件', links: [
        { name: 'FindMCServer', desc: 'Mojang 官方服务器列表', url: 'https://findmcserver.com/', icon:'https://www.google.com/s2/favicons?domain=findmcserver.com&sz=128' },
        { name: 'MC Servers', desc: '国际服务器列表', url: 'https://minecraftservers.org/', icon:'https://www.google.com/s2/favicons?domain=minecraftservers.org&sz=128' },
        { name: 'SpigotMC', desc: '服务器插件/核心资源', url: 'https://www.spigotmc.org/', icon:'https://www.google.com/s2/favicons?domain=spigotmc.org&sz=128' },
        { name: '找服网', desc: '国内服务器大全', url: 'https://www.mczfw.com/, icon:', icon:'https://www.google.com/s2/favicons?domain=mczfw.com&sz=128' }
      ]}
  ],
  serverhearts:[
    {id: 'vanillaserverrhearts', title: '原版服务器核心', links: [
        {name: 'Vanilla Server', desc:'Vanilla 服务器核心', url: 'https://www.minecraft.net/zh-hans/download/server', icon: 'https://www.google.com/s2/favicons?domain=minecraft.net&sz=128'}
      ]},
    {id: 'modsserverrhearts', title: '模组服务器核心', links: [
        {name: 'Fabric Server', desc:'Fabric 服务器核心', url: 'https://fabricmc.net/use/server/', icon: 'https://www.google.com/s2/favicons?domain=fabricmc.net&sz=128'},
        {name: 'Forge Server', desc:'Forge 服务器核心', url: 'https://files.minecraftforge.net/net/minecraftforge/forge/', icon: 'https://www.google.com/s2/favicons?domain=files.minecraftforge.net&sz=128'},
        {name: 'NeoForge Server', desc:'NeoForge 服务器核心', url: 'https://neoforged.net/', icon: 'https://www.google.com/s2/favicons?domain=neoforged.net&sz=128'}
      ]},
    {id: 'bukkitsserverrhearts', title: '插件服务器核心', links: [
        {name: 'Paper Server', desc:'Paper 服务器核心', url: 'https://papermc.io/downloads/paper', icon: 'https://www.google.com/s2/favicons?domain=papermc.io&sz=128'},
        {name: 'Purpur Server', desc:'Purpur 服务器核心', url: 'https://purpurmc.org/download/purpur', icon: 'https://www.google.com/s2/favicons?domain=purpurmc.org&sz=128'},
        {name: 'Folia Server', desc:'Folia 服务器核心', url: 'https://papermc.io/downloads/folia', icon: 'https://www.google.com/s2/favicons?domain=papermc.io&sz=128'},
        {name: 'Spigot Server', desc:'Spigot 服务器核心', url: 'https://hub.spigotmc.org/jenkins/job/BuildTools/', icon: 'https://www.google.com/s2/favicons?domain=hub.spigotmc.org&sz=128'},
        {name: 'Pufferfish Server', desc:'Pufferfish 服务器核心', url: 'https://pufferfish.host/downloads', icon: 'https://www.google.com/s2/favicons?domain=pufferfish.host&sz=128'}
      ]},
    {id: 'agentserverrhearts', title: '代理服务器核心', links: [
        {name: 'Velocity Server', desc:'Velocity 服务器核心', url: 'https://papermc.io/downloads/velocity', icon: 'https://www.google.com/s2/favicons?domain=papermc.io&sz=128'},
        {name: 'Waterfall Server', desc:'Waterfall 服务器核心', url: 'https://papermc.io/downloads/waterfall', icon: 'https://www.google.com/s2/favicons?domain=papermc.io&sz=128'}
      ]},
    {id: 'mixserverrhearts', title: '混合服务器核心', links: [
        {name: 'CatServer Server', desc:'CatServer 服务器核心', url: 'https://catmc.org/', icon: 'https://www.google.com/s2/favicons?domain=catmc.org&sz=128'},
        {name: 'SpongeVanilla Server', desc:'SpongeVanilla 服务器核心', url: 'https://dl.spongepowered.org/spongevanilla', icon: 'https://www.google.com/s2/favicons?domain=dl.spongepowered.org&sz=128'},
        {name: 'SpongeForge Server', desc:'SpongeForge 服务器核心', url: 'https://dl.spongepowered.org/spongeforge', icon: 'https://www.google.com/s2/favicons?domain=dl.spongepowered.org&sz=128'},
        {name: 'SpongeNeo Server', desc:'SpongeNeo 服务器核心', url: 'https://dl.spongepowered.org/spongeneo', icon: 'https://www.google.com/s2/favicons?domain=dl.spongepowered.org&sz=128'}
      ]}
  ],
  supplementtheofficialwebsite:[
    {id: 'moddevelop', title: 'Mod 开发与使用', links: [
        {name: 'Fabric', desc: 'Fabric 官网', url: 'https://fabricmc.net/', icon: 'https://www.google.com/s2/favicons?domain=fabricmc.net&sz=128'},
        {name: 'Fabric Mod Develop', desc: 'Fabric Mod 开发 MDK 下载', url: 'https://fabricmc.net/develop/template/', icon: 'https://www.google.com/s2/favicons?domain=fabricmc.net&sz=128'},
        {name: 'Fabric Documentation Develop Mod', desc: 'Fabric Mod 开发文档指南', url: '', icon: 'https://www.google.com/s2/favicons?domain=fabricmc.net&sz=128'},
        {name: 'Fabric Documentation Installing Mods ', desc: 'Fabric Mod 模组安装文档指南', url: 'https://docs.fabricmc.net/players/installing-mods', icon: 'https://www.google.com/s2/favicons?domain=fabricmc.net&sz=128'},
        {name: 'Forge', desc: 'Forge 官网', url: 'https://files.minecraftforge.net/net/minecraftforge/forge/', icon: 'https://www.google.com/s2/favicons?domain=files.minecraftforge.net&sz=128'},
        {name: 'NeoForge', desc: 'NeoForge 官网', url: 'https://neoforged.net/', icon: 'https://www.google.com/s2/favicons?domain=neoforged.net&sz=128'},
        {name: 'NeoForge', desc: 'NeoForge Mod Develop', url: 'https://neoforged.net/mod-generator/', icon: 'https://www.google.com/s2/favicons?domain=neoforged.net&sz=128'},
      ]},
    {id: 'moddevelopidea', title: 'Mod 开发工具', links: [
        {name: 'JetBrains', desc: '一个专注于集成开发环境的公司', url: 'https://www.jetbrains.com/', icon: 'https://www.google.com/s2/favicons?domain=jetbrains.com&sz=128'},
        {name: 'IntelliJ IDEA', desc: '众大开发者都喜欢的一款 IDE', url: 'https://www.jetbrains.com/idea/download/', icon: 'https://www.google.com/s2/favicons?domain=jetbrains.com&sz=128'},
        {name: 'Visual Studio Code', desc: '一款还不错的多元集成环境，个人感觉不错，对新手不是很友好', url: 'https://code.visualstudio.com/', icon: 'https://www.google.com/s2/favicons?domain=code.visualstudio.com&sz=128'},
        {name: 'Eclipse', desc: '一款老牌的 Java IDE，不过现在已经不怎么用了', url: 'https://www.eclipse.org/', icon: 'https://www.google.com/s2/favicons?domain=eclipse.org&sz=128'},
        {name: 'Eclipse IDE', desc: '这是他的下载链接', url: 'https://eclipseide.org/', icon: 'https://www.google.com/s2/favicons?domain=eclipseide.org&sz=128'},
        {name: 'Eclipse IDE Other Develop Versions', desc: '这是他的其他开发环境的下载链接', url: 'https://www.eclipse.org/downloads/packages/', icon: 'https://www.google.com/s2/favicons?domain=eclipseide.org&sz=128'}
      ]},
    {id: 'network accelerator', title: '网络"加速器"', links: [
        {name: 'Clash Verge', desc: '一款网络加速器，可以用来构建特殊环境,但是需要一定的技术，需要搭配 Magic 或 七根木棍来使用', url: 'https://clashverge.net/downloads/', icon: 'https://www.google.com/s2/favicons?domain=clashverge.net&sz=128'}
      ]}
  ]
}

const tabs = [{ id: 'wiki', label: '百科攻略、玩家社区、资源与工具' }, { id: 'mods', label: '模组、整合包、材质光影、服务器与资源生成' }, {id: 'serverhearts', label: '服务器核心'}, {id: 'supplementtheofficialwebsite', label: '补充官网'}]
const visibleSections = computed(() => sectionsData[activeTab.value] || [])
</script>
