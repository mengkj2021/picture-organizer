# 图片整理（PictureOrganizer）

本地运行的 Android 图片管理 App（Kotlin + Jetpack Compose），用于导入、压缩、打标签并按标签导出 zip。

## 文档入口

| 文档 | 说明 |
|------|------|
| [shared/README.md](shared/README.md) | AI 双工具维护约定与项目说明（**唯一维护入口**） |
| [docs/项目式样说明书.md](docs/项目式样说明书.md) | 功能、技术栈、页面架构式样 |
| [docs/架构设计.md](docs/架构设计.md) | MVVM、包结构、Room 设计、数据流 |
| [docs/路由设计.md](docs/路由设计.md) | Navigation Compose 路由命名与导航图 |
| [docs/画面/](docs/画面/) | 各画面详细规格 |
| [docs/代码优化/](docs/代码优化/) | 代码改动清单（优化项与实施依据） |
| [docs/开发日志-2026-08-27.md](docs/开发日志-2026-08-27.md) | 主画面与数据层架子开发记录 |
| [提交注意事项.md](提交注意事项.md) | Git 账号、编码、提交范围约定 |
| [shared/常用语句.md](shared/常用语句.md) | AI 协作提示词模板（复制粘贴用） |

## 当前进度（v0.7）

- `splash` → `main` 自动导航
- 主画面三 Tab（待处理 / 已确认 / 不修改）+ 假数据列表 + 编辑批量改状态
- MVVM + Repository 模式（`MainViewModel` + `MockImageRepository`）
- Room 数据库架子已就绪（Mock 仍为数据源）
- 详见 [架构设计](docs/架构设计.md)、[开发日志 · 2026-08-27](docs/开发日志-2026-08-27.md)

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

- **全部代码**：由 AI 开发（文档先行，见 [shared/常用语句.md](shared/常用语句.md)）
- **构建配置与文档**：由 AI 维护
- **用户**：提需求、确认待定项、Review 与验收
