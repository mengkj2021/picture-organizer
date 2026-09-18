# ai-workbench · AI 开发工作台

编排壳在 [`prompts/`](../prompts/)、[`shared/`](../shared/)；**本目录**放宪法、细则、模板与使用指南。

**给人看的整体系整合**（全面、短精确）：[`指南/AI开发体系总览.md`](指南/AI开发体系总览.md)  
**实践定位与边界**（单人 / 不够什么）：[`指南/AI开发实践-定位与现状.md`](指南/AI开发实践-定位与现状.md)

▶ include 语义：[`宪法/include约定.md`](宪法/include约定.md)  
整仓地图（非 android 包树）：[`指南/仓库结构总览.md`](指南/仓库结构总览.md)

```
prompts/ + shared/  ──▶ include──▶  ai-workbench/
        ↓ 读 project-status / project-docs
        ↓ 改 android/
        ↓ 写回 status / docs（过程写票复盘）
```

## 分区

| 分区 | 用途 |
|---|---|
| [指南/](指南/) | **人读**：[体系总览](指南/AI开发体系总览.md) · [定位与现状](指南/AI开发实践-定位与现状.md) · 仓库结构 · docs/status 用法 |
| [宪法/](宪法/) | 文档分层 · include · 落位 |
| [细则/](细则/) | 接票 / prompts / rules / skills 正文（被 ▶ include） |
| [模板/](模板/) | 画面文档（含区域模板）· 票 |
| [错题本.md](错题本.md) | 跨票教训 |

## 旁系

| 目录 | 关系 |
|---|---|
| [`project-docs/`](../project-docs/) · [`project-status/`](../project-status/) | 读/写的项目真相（过程在票内） |
| [`android/`](../android/) | 代码改动目标 |
| [`prompts/`](../prompts/) · [`shared/`](../shared/) | 编排壳 |
