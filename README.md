# 图片整理（PictureOrganizer）

本地运行的 Android 图片管理 App（Kotlin + Jetpack Compose），用于导入、压缩、打标签并按标签导出 zip。

## 文档入口

| 文档 | 说明 |
|------|------|
| [shared/README.md](shared/README.md) | AI 双工具维护约定与项目说明（**唯一维护入口**） |
| [docs/README.md](docs/README.md) | **docs 分类索引**（式样 / 设计 / 画面 / 日志 / 工程） |
| [docs/式样/项目式样说明书.md](docs/式样/项目式样说明书.md) | 功能、技术栈、页面架构式样 |
| [docs/设计/架构设计.md](docs/设计/架构设计.md) | MVVM、包结构、Room 设计、数据流 |
| [docs/设计/路由设计.md](docs/设计/路由设计.md) | Navigation Compose 路由命名与导航图 |
| [docs/画面/](docs/画面/) | 各画面详细规格 |
| [docs/工程/改动清单.md](docs/工程/改动清单.md) | 待办 backlog |
| [docs/日志/](docs/日志/) | 按日开发日志 |
| [提交注意事项.md](提交注意事项.md) | Git 账号、编码、提交范围约定 |
| [shared/prompts/](shared/prompts/) | 拉入对话执行的提示词（功能开发 / 式样问答 / 文档整理） |

## 当前进度（v0.8）

- `splash` → `main` ↔ `import-images` / `image-detail/{imageId}`
- 主画面三 Tab + Room；编辑批量「移动到」（改库 + 搬文件）；列表真实缩略图（Coil）
- 导入：相册 / 文件 → 压缩 → `images/pending/` + Room
- 详情：缩放浏览、重命名、标签 Room + Exif 双写；底部同状态缩略条
- 不修改 Tab：真实删除（文件 + DB）
- 详见 [架构设计](docs/设计/架构设计.md)、[开发日志 · 2026-08-29](docs/日志/开发日志-2026-08-29.md)

## 首次构建

### 环境要求

- Android Studio（支持 AGP 9.2 + Gradle 9.7）
- JDK 17+
- Android SDK 37（compileSdk / targetSdk）

### Gradle 发行包

本项目 [`gradle/wrapper/gradle-wrapper.properties`](gradle/wrapper/gradle-wrapper.properties) 使用**本地相对路径** `gradle-9.7.1-bin.zip`（不提交至 Git，见 [提交注意事项.md](提交注意事项.md)）。

克隆后任选其一：

1. **放置本地 zip**：将 `gradle-9.7.1-bin.zip` 放入 `gradle/wrapper/` 目录（与 `gradle-wrapper.properties` 同级），再执行 Sync / 构建。
2. **改用官方 URL**：临时修改 `gradle/wrapper/gradle-wrapper.properties` 中的 `distributionUrl` 为：
   ```
   https://services.gradle.org/distributions/gradle-9.7.1-bin.zip
   ```

### 中文路径说明

若项目路径含非 ASCII 字符（如 `项目列表`），[`gradle.properties`](gradle.properties) 中已启用 `android.overridePathCheck=true`（experimental）。若迁移至纯 ASCII 路径，可移除此项。

### 构建命令

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

## 开发约定

- **全部代码**：由 AI 开发（文档先行，见 [shared/prompts/功能开发.md](shared/prompts/功能开发.md)）
- **构建配置与文档**：由 AI 维护
- **用户**：提需求、确认待定项、Review 与验收
