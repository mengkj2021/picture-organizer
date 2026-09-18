# AI 如何使用 project-docs

> `project-docs/` = **`android/` 已落地实现的文档镜像**。  
> 入口：[`project-docs/README.md`](../../project-docs/README.md)。仓库总览：[仓库结构总览.md](仓库结构总览.md)。

## 定位（先判对）

| 是 | 不是 |
|---|---|
| 当前 App 行为、包结构、route、画面规格、Room… | Roadmap / 未决 / 「打算做」（→ `project-status/`） |
| 与代码冲突时以 **`android/app/src/main` 为准**，再回写本文 | AI 纪律 / prompts（→ `shared/` · `prompts/` · 本 workbench） |

## 何时打开

| 场景 | 先读 |
|---|---|
| 接票改功能 / 修 Bug | 票「关联画面」→ 对应 `画面/<名>/`；按需核心流程 / Room / 架构 §2 |
| 新画面 | [路由设计.md](../../project-docs/路由设计.md) §4 → 复制 [`模板/画面文档/`](../模板/画面文档/) → 填十节 |
| 改包结构 | **只** [架构设计.md](../../project-docs/架构设计.md) **§2**（先改文档再改代码） |
| 依赖版本 | [技术栈.md](../../project-docs/技术栈.md) ↔ `android/gradle/libs.versions.toml` |
| 只问不改（规格 / 现状） | `prompts/问答_式样问答.md`（先 docs/status，不足再定点 `android/`） |

## 推荐阅读顺序（按需停）

1. [项目概述.md](../../project-docs/项目概述.md) — 产品一句话  
2. [架构设计.md](../../project-docs/架构设计.md) **§2** — 包落点  
3. [路由设计.md](../../project-docs/路由设计.md) — route 表  
4. [画面/README.md](../../project-docs/画面/README.md) — 清单 → 下钻单画面  
5. [核心流程.md](../../project-docs/核心流程.md) · [图片存储管理.md](../../project-docs/图片存储管理.md)  
6. [数据流.md](../../project-docs/数据流.md) · [Room.md](../../project-docs/Room.md) · [工具类.md](../../project-docs/工具类.md)  

**禁止**：无票、无关联路径时通读全目录。

## 读写纪律

1. **文档先行**：新画面 / 改交互先改 `画面/<名>/`，再写 Kotlin  
2. **冲突**：代码为准 → 改文档对齐，或回票澄清；禁止为迁就代码擅自改规格且不说明  
3. **写回**：实现后更新相关画面 README / 区域文件、路由表行状态；依赖变更同步技术栈  
4. **开发记录**：画面 README §10 可留跳转；过程写在**相关票**复盘，不另建日志目录
5. **未决**：不要把 `project-status` 愿望写进「已确认」式样表述  

## 与 project-status 的分工

- **docs**：什么样（规格与现状）  
- **status**：做到哪（票与是否实现）  
接票两者都要：先 status 定边界，再 docs 定规格。详见 [如何使用-project-status.md](如何使用-project-status.md)。
