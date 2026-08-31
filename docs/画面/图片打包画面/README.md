# 图片打包画面

## 1. 画面概述

| 项 | 内容 |
|---|---|
| 画面名称 | 图片打包画面 |
| route | `export-zip` |
| 职责 | 对**已确认**图片按标签筛选后打包为 zip（每标签一包），存私有目录并支持分享 |

## 2. 路由定义

- 路由字符串：`export-zip`
- 参数：无（筛选条件本地状态；可 navigate `filter` 回传）

## 3. 功能清单

- [x] 展示当前筛选摘要；可进 `filter` / 清除
- [x] 「开始打包」：按筛选标签各生成 zip（「未打标签」单独包）
- [x] 无筛选时：已确认中每个出现过的用户标签各一包 + 有未打标签则一包
- [x] 进度；结果列表；系统分享（FileProvider）
- [x] 输出目录 `filesDir/exports/`

## 4. 用户操作

1. 已确认菜单「打包导出」进入
2. （可选）筛选 → 开始打包 → 分享或返回

## 5. UI 壳层

- TopAppBar「打包导出」+ 返回
- 筛选行 + 开始按钮 + 进度 / 结果列表

## 6. 系统返回动作

| 场景 | 行为 |
|---|---|
| Idle | `popBackStack()` |
| 打包中 | `BackHandler` 拦截 |

## 7. 进入与退出

| 方向 | 说明 |
|---|---|
| 从哪里进入 | [已确认一览 · 菜单](../主画面/已确认一览/菜单.md) |
| 可前往 | [筛选画面](../筛选画面/README.md) |
| 退出去向 | 回 `main` |

## 8. 数据依赖

- `ExportZipViewModel`；`ImageRepository.observeItems(Confirmed)`；`ZipExporter`；`TagFilterCriteria`
- FileProvider：`@xml/file_paths` → `exports/`

## 9. 待定事项

- 无

## 10. 开发记录

| 日期 | 变更 | 关联提交 |
|---|---|---|
| 2026-08-31 | C7 新建 | （待提交） |
