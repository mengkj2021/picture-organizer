# 教程画面

## 1. 画面概述

| 项 | 内容 |
|---|---|
| 画面名称 | 教程画面 |
| route | `tutorial?fromSettings={fromSettings}` |
| 职责 | 首次启动引导（3 页）；设置内可再次查看 |

## 2. 路由定义

- 路由字符串：`tutorial?fromSettings={fromSettings}`（见 `Routes.TUTORIAL`）
- 可选参数：`fromSettings`（默认 `false`）
- 参数说明：
  - `false`（首次）：完成或跳过 → 写入 DataStore `tutorial_completed` → 导航 `main`（替换栈，不回 splash）
  - `true`（设置重看）：完成或跳过 → **不**改写完成标记 → `popBackStack` 回设置

## 3. 功能清单（总览）

- [x] 3 页横向滑动（`HorizontalPager`）+ 页指示
- [x] 「下一步」/ 末页「开始使用」
- [x] 「跳过」任意页可跳过
- [x] 首次完成/跳过写入 `tutorial_completed`（`fromSettings=false`）
- [x] 设置内再次查看（`fromSettings=true`）
- [x] 首次流程不留在 back stack（与 splash 一致）

## 4. 用户操作（总览）

1. 首次：`splash` → 本画面 → 浏览 / 跳过 → `main`
2. 设置：「查看教程」→ 本画面 → 返回设置

## 5. UI 壳层

- 布局：整页包 M3 `Scaffold`，内容消费系统栏 inset（修复 Bug1）
- 顶部右侧：「跳过」
- 主体：当前页标题 + 说明文案
- 底部：页点指示 +「下一步」或「开始使用」

### 三页文案（与 `strings.xml` 一致）

| 页 | 标题 | 说明 |
|---|---|---|
| 1 | 导入图片 | 从相册或文件将图片导入「待处理」。 |
| 2 | 整理分类 | 移动到已确认或不修改，并为图片打标签。 |
| 3 | 筛选与打包 | 按标签筛选列表；已确认图片可按标签打包导出。 |

## 6. 系统返回动作

| 场景 | 行为 |
|---|---|
| 首次教程（根引导） | 返回键退出应用（不回 splash） |
| 从设置进入 | 返回键 / 完成 → `popBackStack` 回设置 |

## 7. 进入与退出

| 方向 | 说明 |
|---|---|
| 从哪里进入 | [启动画面](../启动画面/README.md)（未完成教程）；[设置画面](../设置画面/README.md)「查看教程」 |
| 可前往 | [主画面](../主画面/README.md)（首次）；或返回设置 |
| 退出去向 | 见 §2 |

## 8. 数据依赖

- `UserPreferencesRepository`（键 `tutorial_completed`；仅首次流程写入）
- UI：`TutorialScreen`；完成回调由 `PictureOrganizerNavHost` 按 `fromSettings` 分支处理

## 9. 待定事项

- [ ] 配图（可选）

## 10. 开发记录

| 日期 | 变更 | 关联提交 |
|---|---|---|
| 2026-08-31 | C8：首次引导 3 页 + DataStore | 见开发日志 |
| 2026-08-31 | C9：设置重看 `fromSettings`；文档同步 | 见开发日志 |
| 2026-09-01 | P6：第 3 页文案更新（打包已落地措辞） | 见开发日志 |
| 2026-09-02 | Bug1：Scaffold 消费系统栏 inset，修复高版本上下被裁切 | 见开发日志 |
