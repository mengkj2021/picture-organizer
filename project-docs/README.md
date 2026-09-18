# project-docs · android 现状文档

本目录是 [`android/`](../android/) **已落地实现**的文档镜像。与代码冲突时以 `android/app/src/main` 为准，再回写本文。

| 是 | 不是 |
|---|---|
| 功能、包结构、route、Room、画面规格 | Roadmap / 未决（→ [`project-status/`](../project-status/)） |
| 对照实现的产品描述 | AI 规范（→ [`shared/`](../shared/) · [`ai-workbench/`](../ai-workbench/)） |

根包：`android/app/src/main/java/com/pictureorganizer/`。用法：[如何使用-project-docs](../ai-workbench/指南/如何使用-project-docs.md)。

## 文档地图（唯一索引）

| 分区 | 文档 | 维护什么 | 代码落点 |
|---|---|---|---|
| 总览 | [项目概述.md](项目概述.md) | 产品定位与形态 | — |
|  | [技术栈.md](技术栈.md) | Gradle / BOM / 版号 | `libs.versions.toml`、`app/build.gradle.kts` |
| 架构 · 数据 | [架构设计.md](架构设计.md) | 分层 + **§2 包树（权威）** | 根包目录树 |
|  | [数据流.md](数据流.md) | 画面 → VM → Repo → 文件/DB | `ui/` · `data/` · `util/` |
|  | [Room.md](Room.md) | 表、DAO、Repository 接口 | `data/local/` · `data/repository/` |
|  | [工具类.md](工具类.md) | util / common / model 小工具 | `util/` · `ui/common/` · `model/` |
| 导航 · 画面 | [路由设计.md](路由设计.md) | route 表与返回约定 | `navigation/` |
|  | [画面/README.md](画面/README.md) | **画面清单（唯一全表）** + 单画面规格 | `ui/<screen>/` |
| 主路径 | [核心流程.md](核心流程.md) | 导入→分类→标签→单包导出 | 相关 `ui/` |
|  | [图片存储管理.md](图片存储管理.md) | 私有目录布局 | `filesDir/images/…`、`exports/` |

勿在本 README 再抄画面表；改清单只改 `画面/README.md`，再视情况同步路由 §3 一行。

## 推荐阅读顺序

1. [项目概述.md](项目概述.md) → 产品一句话  
2. [架构设计.md](架构设计.md) **§2** → 包落点（改布局先改本节）  
3. [路由设计.md](路由设计.md) ↔ `navigation/`  
4. [画面/README.md](画面/) ↔ `ui/<screen>/`  
5. [核心流程.md](核心流程.md) · [图片存储管理.md](图片存储管理.md)  
6. [数据流.md](数据流.md) · [Room.md](Room.md) · [工具类.md](工具类.md)  
7. [技术栈.md](技术栈.md) ↔ `libs.versions.toml`  

进度 / 未决属 `project-status/`，不列入本目录默认序列。分层见 [文档分层约定](../ai-workbench/宪法/文档分层约定.md)。
