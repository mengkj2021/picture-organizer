# 图片整理（PictureOrganizer）

本地运行的 Android 图片管理 App（Kotlin + Jetpack Compose），用于导入、压缩、打标签并按标签导出 zip。

## 仓库结构

```
picture-organizer/
├── android/                            # ★ Android 工程（Gradle root：app/ + 构建配置）
├── docs/                               # 需求 / 设计 / 画面规格 / 日志 / 工程（票库）文档
├── shared/                             # ★ AI 双工具配置唯一维护入口（项目说明 / prompts / rules）
├── .codebuddy/  .cursor/               # 规则实际读取目录（从 shared/rules 同步）
└── README.md                            # 本文档（仓库手册：入口 / 构建 / 账号 / 维护约定）
```

仓库根是「文档 + AI 配置」层；**Android 工程整体位于 `android/`**（可独立打开与构建）。文档与代码的路径引用一律带 `android/` 前缀。

## 文档入口

| 文档 | 说明 |
|------|------|
| [shared/README.md](shared/README.md) | AI 双工具维护约定与项目说明（**唯一维护入口**） |
| [docs/README.md](docs/README.md) | **docs 分类索引**（式样 / 设计 / 画面 / 日志 / 工程） |
| [docs/式样/项目式样说明书.md](docs/式样/项目式样说明书.md) | 功能、技术栈、页面架构式样 |
| [docs/设计/架构设计.md](docs/设计/架构设计.md) | MVVM、包结构、Room 设计、数据流 |
| [docs/设计/路由设计.md](docs/设计/路由设计.md) | Navigation Compose 路由命名与导航图 |
| [docs/画面/](docs/画面/) | 各画面规格（每画面一目录，见 [画面/README.md](docs/画面/README.md)） |
| [docs/工程/票/](docs/工程/票/) | **票库**：Bug / 式样变更 / 调查 / 功能体验四类票（起票模板与台账） |
| [docs/工程/票/改动清单.md](docs/工程/票/改动清单.md) | 总台账：已完成票 + 待办一览 + 建议实施顺序 |
| [docs/工程/AI协作复盘-阶段一.md](docs/工程/AI协作复盘-阶段一.md) | AI 开发协作阶段复盘（成效 / 缺陷 / 起票衔接） |
| [docs/日志/](docs/日志/) | 按日开发日志 |
| [shared/prompts/](shared/prompts/) | 拉入对话执行的提示词（功能开发 / 式样问答 / 文档整理） |

## 当前进度

- 产品功能 **C1–C10** 已落地：导入（相册 / 文件、默认标签、压缩开关）、主画面三 Tab + Room、编辑批量移动、详情缩放浏览 / 重命名 / 标签库、筛选分页、标签打包导出（zip / FileProvider）、教程、设置
- 工程质量：**B3/B5/B6**（R8 / 图标 / ktlint）；结构债务 **D1**；体验 **P6**
- **Bug1**（教程画面系统栏裁切）已修复；**S1**（状态不算标签）已落地
- 式样书 v0.9；App 版本号当前 `0.0.1`（versionCode 1）
- 开发模式：已切换为**起票制**（改动经 Bug / 式样变更 / 调查 / 功能体验票驱动，见 [docs/工程/票/](docs/工程/票/)）

## 仓库与账号配置

本项目托管于 GitHub：**mengkj2021/picture-organizer**，推荐使用专用 SSH 别名（与笔记库等其它仓库隔离）。若已为其它仓库生成过专用 key，可复用，无需重复生成。

### 克隆

```
git clone git@github-mengkj2021:mengkj2021/picture-organizer.git
```

### 首次配置（仅需一次）

1. 若无专用 key，生成（无 passphrase）：
   ```
   ssh-keygen -t ed25519 -C "mengkj2021-github" -f ~/.ssh/id_ed25519_mengkj2021 -N ""
   ```
2. 将公钥 `~/.ssh/id_ed25519_mengkj2021.pub` 添加到 GitHub 账号 → Settings → SSH and GPG keys
3. 在 `~/.ssh/config` 追加（Windows 路径为 `C:\Users\<用户名>\.ssh\config`）：
   ```
   Host github-mengkj2021
     HostName github.com
     User git
     IdentityFile ~/.ssh/id_ed25519_mengkj2021
     IdentitiesOnly yes
   ```
4. 验证：`ssh -T git@github-mengkj2021` 应返回 `Hi mengkj2021!`

### git 身份（仓库级，勿设全局）

在仓库目录内执行：

```
git config user.name "mengkj2021"
git config user.email "mengkj2021@163.com"
```

## 首次构建

### 环境要求

- Android Studio（支持 AGP 9.2 + Gradle 9.7）
- JDK 17+
- Android SDK 37（compileSdk / targetSdk）

### 打开工程

Android 工程位于 `android/`：Android Studio 直接 **Open `android/` 目录**（仓库根为文档层，不识别 Gradle 工程）。

### Gradle 发行包

本项目 [`android/gradle/wrapper/gradle-wrapper.properties`](android/gradle/wrapper/gradle-wrapper.properties) 使用**本地相对路径** `gradle-9.7.1-bin.zip`（不提交至 Git，勿提交明细见下方「开发约定」）。

克隆后任选其一：

1. **放置本地 zip**：将 `gradle-9.7.1-bin.zip` 放入 `android/gradle/wrapper/` 目录（与 `gradle-wrapper.properties` 同级），再执行 Sync / 构建。
2. **改用官方 URL**：临时修改 `android/gradle/wrapper/gradle-wrapper.properties` 中的 `distributionUrl` 为：
   ```
   https://services.gradle.org/distributions/gradle-9.7.1-bin.zip
   ```

### 中文路径说明

若项目路径含非 ASCII 字符（如 `项目列表`），[`android/gradle.properties`](android/gradle.properties) 中已启用 `android.overridePathCheck=true`（experimental）。若迁移至纯 ASCII 路径，可移除此项。

### 构建命令

```bash
cd android

# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

## 开发约定

- **全部代码**：由 AI 开发（**起票 → 接票**，见 [docs/工程/票/README.md](docs/工程/票/README.md) 与 [shared/prompts/功能开发.md](shared/prompts/功能开发.md)）
- **构建配置与文档**：由 AI 维护
- **用户**：起票、确认待定项、Review 与验收
- **AI 提交纪律**：已规则化，见 [shared/rules/git-convention.md](shared/rules/git-convention.md)（中文提交 `-F` 文件、禁 `git add .`、禁 `--force`、push 前自检）
- **本地质量门禁（F23）**：克隆后执行一次 `git config core.hooksPath .githooks`；此后 `git push` 前自动跑 `ktlintCheck`，不过则拒推（详见 git-convention「push 前自动门禁」）

### 勿提交内容（已在 .gitignore）

**构建与 IDE 本地产物**（可随时删除，下次构建会重建）：

| 目录 / 文件 | 说明 |
|-------------|------|
| `android/` 下 `build/`、`app/build/` | Gradle / Android 构建输出（含 APK、中间文件） |
| `.gradle/` | Gradle 缓存 |
| `.kotlin/` | Kotlin 编译插件本地缓存 |
| `.idea/`、`*.iml` | Android Studio 配置 |
| `local.properties` | 本机 SDK 路径 |

**其它不提交项**：

- `android/gradle/wrapper/*.zip`（本地 Gradle 发行包，约 130MB，超过 GitHub 单文件 100MB 限制）
- `.codebuddy/` 只提交 `rules/`，其余（`plans/` 等）已忽略
- `.DS_Store`、临时文件、签名文件（`*.jks` / `*.keystore`）、日志（`*.log`）
