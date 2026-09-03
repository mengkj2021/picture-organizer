---
description: AI 全量开发约定，起票制、文档先行，以式样书与票库为准
globs: **/*
alwaysApply: true
enabled: true
updatedAt: 2026-09-02
provider: both
---

# AI 全量开发约定

本项目**全部代码由 AI 开发**（Kotlin 业务、Compose UI、Gradle 配置、文档），无人工编写代码。Android 工程整体位于仓库 `android/` 子目录。

## 用户职责

- **起票**：任何改动先起票（Bug / 式样变更 / 调查 / 功能体验四类，规则见 `docs/工程/票/README.md`），并确认待定项
- Review diff、决定是否 commit / push

## 默认开发流程（起票 → 接票，文档先行）

1. **票**：任何改动先有票。接票时先读票（类型 / 状态 / 现象或诉求 / 验收标准），涉及式样「待定」先回票澄清
2. **文档**：涉及画面按 `docs/设计/路由设计.md` 第 4 节新增画面目录（复制 `docs/画面/_模板/`），更新路由表与 `docs/式样/项目式样说明书.md` 第 5 章
3. **代码**：按 `docs/画面/<画面>/` 实现 Kotlin（位于 `android/app/src/`）、注册 route
4. **日志**：写开发日志（文件名带票号），回写票状态与分区台账；完成项进 `docs/工程/票/改动清单.md` 总台账
5. **Review**：用户验收 → 票状态 ✅ / ❌，再提交

## 权威文档

| 文档 | 用途 |
|------|------|
| `docs/README.md` | docs 分类索引 |
| `docs/式样/项目式样说明书.md` | 功能、技术栈、版本矩阵 |
| `docs/设计/架构设计.md` | MVVM、包结构、Room、数据流 |
| `docs/设计/路由设计.md` | route 命名与导航图 |
| `docs/画面/` | 各画面规格（每画面一目录 + 区域文件；见 `docs/画面/README.md`） |
| `docs/工程/票/` | **票库**：起票制规则、四类票模板与分区台账（入口 `docs/工程/票/README.md`） |
| `docs/工程/票/改动清单.md` | 总台账：已完成归档 + 待办一览 + 建议顺序（与分区台账配套） |
| `docs/日志/` | 按日开发日志（文件名带票号 / 功能号） |
| `shared/prompts/` | 拉入对话执行的提示词模板（功能开发、式样问答、文档整理） |

## 强制约定

1. **票驱动**：没有票的改动不进代码；票上「待定」或规格不清，先确认再实施
2. **未确认项**：式样书中标注「待定」的内容，不得擅自假设并实现
3. **新画面**：先 `docs/画面/<画面名>/`（`README.md` + 按需区域文件），再 `android/app/src/...`
4. **route 命名**：全小写、多词连字符，见路由设计第 2 节
5. **依赖变更**：同步更新 `android/gradle/libs.versions.toml` 与式样书 3.1 版本矩阵
6. **票号贯穿**：开发日志文件名与 commit 信息带票号（如 `Bug2 …`、`S2 …`、`F1 …`）
7. **包结构**（`android/app/src/main/java/com/pictureorganizer/`）：
   - 画面 Composable 放 `ui/<screen>/`（纯 UI，无业务逻辑）
   - 画面逻辑放 `ui/<screen>/<Screen>ViewModel.kt` + `*UiState` / `*UiEvent`
   - 领域 / UI 模型放 `model/`（无 Android 框架依赖为佳）
   - 数据访问放 `data/repository/`
   - Room 持久化放 `data/local/`（Entity、DAO、Database、Converter）
   - Entity ↔ UI 映射放 `data/mapper/`
   - 假数据种子放 `data/mock/`（开发期保留，Preview / 测试）
   - 文件 / 图片等无 UI 工具类放 `util/`
   - route 常量集中定义于 `navigation/Routes.kt`

## 维护入口

项目说明与 AI 配置见 `shared/README.md`。
