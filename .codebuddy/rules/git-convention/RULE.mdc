---
description: git 提交纪律，中文提交 -F 文件、禁 git add .、禁 --force，push 前自检
globs: **/*
alwaysApply: true
enabled: true
updatedAt: 2026-09-02
provider: both
---

# git 操作约定（提交纪律）

本仓库文档与提交信息均为中文。历史教训：Windows 上编码错误会造成中文**真乱码且不可逆**。任何 commit / push 遵循以下纪律。

> 面向人的 onboarding 事实（账号配置、克隆、勿提交明细、Gradle 发行包）见仓库根 [`提交注意事项.md`](../../提交注意事项.md)，不在此重复。

## 提交编码（防乱码）

1. 提交信息一律 **UTF-8（无 BOM）**，用文件方式提交，**禁止** `git commit -m "中文"`（PowerShell 按 ANSI/GBK 传参会损坏中文，2026-08-27 实测）：
   ```
   git commit -F 提交说明.txt
   ```
   `提交说明.txt` 用 UTF-8 无 BOM 保存，提交后删除该临时文件。
2. 中文文件名不能直接作为 git 参数（如 `git add docs/工程/功能开发.md` 会报 pathspec 不匹配）。改为由 git 内部处理：
   - 指定目录：`git add docs/工程/`、`git add shared/`
   - 用 git 通配符：`git add "*.md"`（仅匹配根目录同名文件，不递归）
3. 提交前 `git log -1` 检查中文显示正常再 push；已乱码且未 push 时用 `git commit --amend -F 提交说明.txt` 修复，验证后再 push。

## 提交流程

1. `git status` 查看改动
2. `git add <文件>`（**禁止** `git add .`，避免误提本地产物）
3. `git commit -F 提交说明.txt`——说明改了什么、为什么；commit 信息带票号（如 `Bug2 …`、`S2 …`、`F1 …`）
4. `git push`（正常推送即可；**禁止** `--force`，除非用户明确确认要改写历史）
5. 是否 commit / push 由用户决定，不主动越权执行

## 勿提交内容（摘要）

`build/`、`.gradle/`、`.kotlin/`、`.idea/`、`*.iml`、`local.properties`、`android/gradle/wrapper/*.zip`、`.DS_Store`、`*.jks` / `*.keystore`、`*.log`、`.codebuddy/plans/` 等本地产物一律不提交（完整明细见仓库根 `提交注意事项.md`）。

## push 前自检

- [ ] `git status` 无预期外文件（build/、.gradle/、.kotlin/、.idea/、zip 等）
- [ ] `git log -1` 中文显示正常
- [ ] 提交信息说明改了什么、为什么
- [ ] 规则改动已从 `shared/rules/` 同步到 `.codebuddy/` 与 `.cursor/`（见 sync-convention）
