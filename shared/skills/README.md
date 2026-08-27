# 技能模板框架（shared/skills）

技能（skills）让 AI 具备领域专用能力。本目录是技能的统一维护入口，先搭建框架，之后逐步补充。

## 如何编写一个技能

每个技能一个子目录，统一 Markdown 格式：

```
shared/skills/<skill-name>/
├── SKILL.md      # 技能说明：用途、触发条件、执行流程（SOP）
└── assets/       # 可选：示例、模板等辅助资源
```

`SKILL.md` 建议包含：

| 区块 | 说明 |
|------|------|
| `name` / `description` | 技能名称与用途描述（frontmatter） |
| 触发条件 | 什么情况下应调用该技能 |
| 执行流程 | 分步骤的操作规范（SOP） |
| 注意事项 | 边界、禁忌、常见错误 |

## 同步到各工具

| 工具 | 技能实际位置 |
|------|--------------|
| CodeBuddy | `.codebuddy/skills/<skill-name>/SKILL.md` |
| Cursor | 通过 `.cursor/rules/` 或 AGENTS.md 声明对应能力 |

## 当前技能

- （暂无，待补充）

### 建议优先补充（功能开发阶段）

| 技能名 | 用途 | 优先级 |
|--------|------|--------|
| `create-screen-doc` | 新增画面时自动走文档 SOP（模板 → 路由表 → 式样书第 5 章） | 高 |
| `implement-screen` | 读画面文档 → 实现 Kotlin + 路由注册 | 高 |
| `sync-docs` | 检查 docs/ 三处交叉引用是否一致 | 中 |
| `update-build-deps` | 添加依赖时同步改 toml + build.gradle + 式样书 3.1 | 中 |

> 本项目全部代码由 AI 开发；上述技能覆盖文档、代码实现与构建配置的重复性流程。
