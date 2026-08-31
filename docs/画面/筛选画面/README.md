# 筛选画面

## 1. 画面概述

| 项 | 内容 |
|---|---|
| 画面名称 | 筛选画面 |
| route | `filter?tags={tags}&untagged={untagged}` |
| 职责 | 配置标签筛选条件（库标签多选 OR +「未打标签」）；供主画面一览与打包画面复用 |

## 2. 路由定义

- 路由字符串：`filter?tags={tags}&untagged={untagged}`（见 `Routes.FILTER`）
- 必选参数：无
- 可选参数：`tags`（逗号分隔标签名，URL 编码）、`untagged`（`true`/`false`，默认 `false`）
- 参数说明：进入时带入当前条件以便回显；应用后经上一页 `SavedStateHandle`（`FilterResultKeys`）回传

## 3. 功能清单（总览）

- [x] TopAppBar「筛选」+ 返回（不应用）
- [x] 「未打标签」Checkbox
- [x] 库内标签多选 Checkbox（OR）
- [x] 「应用」回传条件并 `popBackStack`
- [x] 「清除」回传空条件并返回

## 4. 用户操作（总览）

1. 从主画面或打包画面带入当前条件进入
2. 勾选标签 / 未打标签 →「应用」返回上一画面
3. 「清除」清空条件并返回；返回箭头不改动原条件

## 5. UI 壳层

- 顶部：`TopAppBar` + 返回
- 主体：可滚动 Checkbox 列表
- 底部：清除 / 应用

## 6. 系统返回动作

| 场景 | 返回键 / 返回箭头行为 |
|---|---|
| 有上级画面 | 返回上一画面（**不**回传条件） |

## 7. 进入与退出

| 方向 | 说明 |
|---|---|
| 从哪里进入 | [主画面](../主画面/README.md) 三一览菜单「筛选」；[图片打包画面](../图片打包画面/README.md) |
| 可前往 | 无 |
| 退出去向 | `popBackStack` 回调用方 |

## 8. 数据依赖

- UI：`FilterScreen`；逻辑：`FilterViewModel`（`FilterUiState` / 事件）
- 数据：`TagRepository.observeTags()`；结果模型 `TagFilterCriteria`
- 回传键：`FilterResultKeys`（`filter_result_tags` / `filter_result_untagged` / `filter_result_applied`）

## 9. 待定事项

- 无（文件名 / 日期筛选不在本期）

## 10. 开发记录

| 日期 | 变更 | 关联提交 |
|---|---|---|
| 2026-08-31 | 新建；主画面 Dialog 改为 navigate | 见开发日志 |
| 2026-08-31 | C7 打包复用；文档去掉「后续 C7」措辞 | 见开发日志 |
