# 重命名模板管理画面

## 1. 画面概述

| 项 | 内容 |
|---|---|
| 画面名称 | 重命名模板管理 |
| route | `rename-template-manage` |
| 职责 | CRUD 重命名模板；设默认；供详情套用 |

## 2. 路由定义

- 路由字符串：`rename-template-manage`
- 参数：无

## 3. 功能清单

- [x] 列表：名称、pattern、默认徽标
- [x] 新增 / 编辑（名称 + pattern）
- [x] 删除确认；设为默认（互斥）
- [x] pattern 占位符说明：`{name}` `{date}` `{tag}`

## 4. 用户操作

1. 设置 → 本画面
2. 添加/编辑/删除/设默认
3. 返回设置

## 5. UI 壳层

- TopAppBar + FAB 添加
- 列表 + 编辑 Dialog

## 6. 系统返回动作

| 场景 | 行为 |
|---|---|
| 有上级 | `popBackStack()` |

## 7. 进入与退出

| 方向 | 说明 |
|---|---|
| 从哪里进入 | [设置画面](../设置画面/README.md) |
| 可前往 | 无 |
| 退出去向 | 回 `settings` |

## 8. 数据依赖

- `RenameTemplateRepository` ← Room `rename_templates`（DB v3）

## 9. 待定事项

- 无

## 10. 开发记录

| 日期 | 变更 | 关联提交 |
|---|---|---|
| 2026-08-31 | C9 新建 | （待提交） |
