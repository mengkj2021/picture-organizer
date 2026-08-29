---
description: AI 全量开发约定，文档先行，以式样书与路由设计为准
globs: **/*
alwaysApply: true
enabled: true
updatedAt: 2026-08-29
provider: both
---

# AI 全量开发约定

本项目**全部代码由 AI 开发**（Kotlin 业务、Compose UI、Gradle 配置、文档），无人工编写代码。

## 用户职责

- 提需求、确认待定项
- Review diff、决定是否 commit / push

## 默认开发流程（文档先行）

1. **文档**：按 `docs/设计/路由设计.md` 第 4 节新增画面文档，更新路由表与 `docs/式样/项目式样说明书.md` 第 5 章
2. **代码**：按 `docs/画面/` 实现 Kotlin、注册 route
3. **Review**：用户验收

## 权威文档

| 文档 | 用途 |
|------|------|
| `docs/README.md` | docs 分类索引 |
| `docs/式样/项目式样说明书.md` | 功能、技术栈、版本矩阵 |
| `docs/设计/架构设计.md` | MVVM、包结构、Room、数据流 |
| `docs/设计/路由设计.md` | route 命名与导航图 |
| `docs/画面/` | 各画面详细规格 |
| `docs/日志/` | 按日开发日志 |
| `docs/工程/改动清单.md` | 待办 backlog |
| `shared/prompts/` | 拉入对话执行的提示词模板（功能开发、式样问答、文档整理） |

## 强制约定

1. **未确认项**：式样书中标注「待定」的内容，不得擅自假设并实现
2. **新画面**：先 `docs/画面/xxx.md`，再 `app/src/...`
3. **route 命名**：全小写、多词连字符，见路由设计第 2 节
4. **依赖变更**：同步更新 `gradle/libs.versions.toml` 与式样书 3.1 版本矩阵
5. **包结构**：
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
