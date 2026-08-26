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
