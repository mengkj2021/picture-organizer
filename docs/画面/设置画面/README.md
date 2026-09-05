# 设置画面

## 1. 画面概述

| 项 | 内容 |
|---|---|
| 画面名称 | 设置画面 |
| route | `settings` |
| 职责 | App 设置入口：标签管理、重命名模板、默认标签、导入重名询问开关、查看教程、开源许可 |

## 2. 路由定义

- 路由字符串：`settings`
- 必选 / 可选参数：无

## 3. 功能清单

- [x] TopAppBar + 返回
- [x] 「标签管理」→ `tag-manage`
- [x] 「重命名模板」→ `rename-template-manage`
- [x] 「默认标签」→ `default-tags`
- [x] **F11**「导入重名询问」Switch（默认开；关则导入不询问）
- [x] 「查看教程」→ `tutorial`（`fromSettings=true`，返回设置）
- [x] **F26**「开源许可」→ `oss-licenses`

## 4. 用户操作

1. 主画面齿轮进入
2. 点各列表项进入对应画面；切换「导入重名询问」即时写入 DataStore
3. 返回主画面

## 5. UI 描述

- `TopAppBar`「设置」+ 返回
- `LazyColumn`：导航行 `ListItem` + **F11** Switch 行（标题「导入重名询问」，说明「原图名与库中相同的，导入前逐张确认」）

## 6. 系统返回动作

| 场景 | 行为 |
|---|---|
| 有上级 | `popBackStack()` |

## 7. 进入与退出

| 方向 | 说明 |
|---|---|
| 从哪里进入 | [主画面](../主画面/README.md) TopAppBar 设置 |
| 可前往 | 标签管理 / 重命名模板管理 / 默认标签 / 教程 / 开源许可 |
| 退出去向 | 回 `main` |

## 8. 数据依赖

- `SettingsViewModel` + `UserPreferencesRepository.importDuplicateAskEnabled`（key `import_duplicate_ask_enabled`，默认 true）

## 9. 待定事项

- 无

## 10. 开发记录

| 日期 | 变更 | 关联提交 |
|---|---|---|
| 2026-08-30 | 极简设置（C5 入口） | （待提交） |
| 2026-08-31 | C9：重命名模板 / 默认标签 / 教程入口 | （待提交） |
| 2026-09-05 | F11：导入重名询问 Switch 行 | 见开发日志-2026-09-05-F11 |
| 2026-09-05 | F26：开源许可入口（教程下方） | 见开发日志-2026-09-05-F26 |
