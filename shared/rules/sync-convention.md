---
description: 手动双目录维护约定，CodeBuddy 与 Cursor 规则需同时更新保持一致
globs: **/*
alwaysApply: true
enabled: true
updatedAt: 2026-08-25
provider: both
---

# 手动双目录维护约定

本项目由 CodeBuddy 与 Cursor 共同维护，规则文件分别存在于两个目录中，需手动保持同步。

项目说明与维护流程见 `shared/README.md`。

## 规则文件位置

| 工具 | 文件位置 |
|------|----------|
| CodeBuddy | `.codebuddy/rules/<name>/RULE.mdc` |
| Cursor | `.cursor/rules/<name>.mdc` |

## 强制约定

1. **新增规则时**：先在 `shared/rules/<name>.md` 创建统一源文件，再复制到两个工具目录。
2. **修改规则时**：先改 `shared/rules/<name>.md`，必须同时更新 CodeBuddy 与 Cursor 两份文件，禁止只改一处。
3. **删除规则时**：`shared/rules/` 与两个工具目录中的对应文件一并删除。
4. **frontmatter 字段差异**：统一 frontmatter 含两工具全部所需字段，各工具自动忽略不认识的字段，无需转换。
5. **CodeBuddy 规则生效时机**：规则在会话开始时注入，创建或修改规则后需新建对话才生效。
