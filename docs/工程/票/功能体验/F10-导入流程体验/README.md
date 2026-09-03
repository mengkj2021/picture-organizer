# F10 票：导入流程体验（期间返回全程拦截 + 完成后停留可续导）

## 票信息

| 项 | 内容 |
|---|---|
| 票号 | F10 |
| 类型 | 功能 / 体验票 |
| 类型细分 | 体验增强 |
| 标题 | 导入过程中系统返回键不生效；导入结束后不自动返回画面，可继续导入 |
| 状态 | ☐ 待实施 |
| 起票日期 | 2026-09-03 |
| 关联 | [导入画面 README](../../../画面/导入画面/README.md)、[进度与结果.md](../../../画面/导入画面/进度与结果.md)；[ImportViewModel.kt](../../../../android/app/src/main/java/com/pictureorganizer/ui/importimages/ImportViewModel.kt)（`importUris` / `ImportUiEffect.ImportFinished`）；[ImportScreen.kt](../../../../android/app/src/main/java/com/pictureorganizer/ui/importimages/ImportScreen.kt)（BackHandler 83-85、顶栏返回 108-118、全屏遮罩 195-217）；[PictureOrganizerNavHost.kt](../../../../android/app/src/main/java/com/pictureorganizer/navigation/PictureOrganizerNavHost.kt)（176-181 导入后 popBackStack）；[Bug4 票](../bug/Bug4-导入返回不可点/README.md)（Bug4-导入返回不可点，已完成：导入中顶栏返回禁用） |

## 用户诉求 / 场景

- **诉求 1**：图片导入过程中（导入中），系统返回键 / 返回手势**不生效**，用户无法中断退出。
- **诉求 2**：导入结束后**不自动返回**上一画面，停留在导入画面并提示完成，可**继续导入**下一批；想走时再自己返回。
- **现状落差（写实）**：`BackHandler(enabled = !isImporting)` + 顶栏返回按钮 `if (!isImporting)` 已拦截 **Compose 层 UI 返回**（Bug4 成果）；但 `enabled=false` 时**系统返回手势 / Android 13+ 预测性返回由 Navigation 接管，仍可能 pop 回主画面**；且导入为「先落盘（`prepareForImport` 写 `images/pending/`）后写库（`imageDao.insert`）」，中途画面销毁会中断单张导入，**残留 DB 无记录的孤儿文件**。当前导入全部成功即 `ImportFinished` → `popBackStack` 自动回主画面（重试清空失败后同样自动返回）。
- **预期效果**：
  1. 导入中：UI 返回、系统返回键、返回手势、预测性返回**全部不响应**（画面不可退出）；
  2. 导入完成后：不自动 pop，画面复位为空闲可继续选图导入，并给出完成反馈（如「导入完成」提示）；
  3. 空闲态返回键 / 顶栏返回行为与现状一致（正常返回）。
- **是否已有式样依据**：画面文档现状为自动返回；本票为新增强，以本票为准。

## 需求确认（接票前若为「待定」先在此澄清）

- **拦截实现**：预测性返回需在 Compose 层之外处理（Navigation 的 predictive back / `NavBackHandler` 或对返回事件在导入中吞掉）——接票时定具体 API，保证手势与键均不可退。
- **孤儿文件兜底**（建议本票顺带做）：导入入口（或启动 / 进入 pending 列表时）扫描 `images/pending/`，清理 DB 无记录的文件——防历史半途退出与强杀堆积；或仅保证本票后不再产生新孤儿（删旧不动）。接票前定范围。
- **完成反馈形态**（建议）：Snackbar「N 张导入完成」+ 状态复位（`isImporting=false`、清 current/total、清选图 Uri 残留），回到空闲态；失败明细维持现状（停留 + 可重试 + 可返回）。
- **重试完成**：`retryAll` / 单张重试成功后是否也不再自动返回（建议：与全量导入一致，不返回、提示完成）——确认后一并实现。

## 方案（接票后填）

- 文档先行（画面文档 / 式样书 / 架构 / 路由，按需）：
- 代码改动点（预计：ImportScreen 返回拦截升级、移除成功自动 pop、完成反馈与状态复位；NavHost 回调行为调整）：
- 验证方式：

## 验收标准

- [ ] 导入中：UI 返回按钮、系统返回键、返回手势均无响应，画面保持
- [ ] 导入完成后不自动返回；可再次选图导入下一批
- [ ] 完成有明确反馈；空闲态可正常返回
- [ ] （若纳入兜底）pending 目录无 DB 记录孤儿文件被清理
- [ ] 文档与代码对齐：[进度与结果.md](../../../画面/导入画面/进度与结果.md) 同步；开发日志（文件名带 F10）已写

## 开发记录

| 日期 | 内容 | 关联提交 |
|---|---|---|
|  |  |  |

## 完结复盘（✅ / ❌ 后填；票与台账行保留，不删除）

- **落地效果**（达成了什么 / 与预期差距）：
- **代价与遗留**（新增复杂度、未覆盖场景 → 转新票或备注）：
- **错题本登记**：已登记 [docs/工程/错题本.md](../../../错题本.md) ／ 不适用
