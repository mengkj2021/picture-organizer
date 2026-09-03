# F8 票：删除标签时联动清理引用图片

## 票信息

| 项 | 内容 |
|---|---|
| 票号 | F8 |
| 类型 | 功能 / 体验票 |
| 类型细分 | 体验增强 |
| 标题 | 删除标签库标签前判断图片引用：无引用直接删；有引用弹窗确认，继续则批量去掉该标签（库 + EXIF） |
| 状态 | ☐ 待实施 |
| 起票日期 | 2026-09-03 |
| 关联 | [标签管理画面 README](../../../画面/标签管理画面/README.md) §8「删库标签不改 `images.tagsJson`」（本票要改写的行为）；[TagManageViewModel.kt](../../../../android/app/src/main/java/com/pictureorganizer/ui/tagmanage/TagManageViewModel.kt)（`confirmDeleteTag`）；[TagDao.kt](../../../../android/app/src/main/java/com/pictureorganizer/data/local/dao/TagDao.kt)（`deleteById`）；[ImageDao.kt](../../../../android/app/src/main/java/com/pictureorganizer/data/local/dao/ImageDao.kt)；[RoomImageRepository.updateTags](../../../../android/app/src/main/java/com/pictureorganizer/data/repository/RoomImageRepository.kt)（复用去标写路径：tagsJson + EXIF `UserComment`） |

## 用户诉求 / 场景

- **现状**：标签管理 → 删标签（已有一层确认框 `confirmDeleteTagId`）→ `TagDao.deleteById` 直接删 `tags` 词表项；**不改任何图片的 `images.tagsJson`** → 图片上残留孤儿标签名（词表没了但图还挂着），标签管理画面看不到、详情画面照常显示可删。
- **要解决的问题**：删除一个还在被图片使用的标签，应给用户知情与选择，而不是静默造成孤儿引用。
- **预期效果**：
  1. 请求删除时先判断该标签是否还被图片使用（`images.tagsJson` 含该名）；
  2. **无引用 → 直接删除**；
  3. **有引用 → 弹框提示**「该标签被 N 张图片使用，继续将一并移除」→ **继续**：删词表 + 把引用图片的该标签去掉（数据库 `tagsJson` 删除 **并同步图片文件 EXIF `UserComment`**）；**取消**：中止，词表与图片均不动。
- **是否已有式样依据**：否（现状文档是反向约定 §8），行为属新增强，需求以本票为准。

## 需求确认（接票前若为「待定」先在此澄清）

- **沿用现有第一层确认**（建议）：保留现状「点删除 → 确认框 → 确认」；判断引用放在确认之后执行，避免推翻现有交互。无引用路径与现状一致（直接删），有引用路径多一次「联动影响」提示。
- **引用判定口径**：`tagsJson` 精确匹配标签名（JSON 数组元素）；实现建议在 `TagRepository` 增加按名查引用图片的接口（ImageDao 现只有 `getAll` 全表，小规模可内存过滤，或加 SQL `LIKE` 查询——接票时定）。
- **EXIF 失败策略**（建议对齐 `updateTags` 现状）：数据库为主，EXIF 写入失败不阻塞删除，失败张数汇总提示（与现有「Exif 失败时 DB 仍成功，Snackbar 提示」一致）。
- **范围边界**：仅处理**图片引用**；`tag_templates` 模板内同名文本与默认标签模板不在联动范围（模板 tagNames 是名字快照，删除后模板/默认模板仍可能把旧名带给新导入图——如需一并清理或防再生，另行起票）。

## 方案（接票后填）

- 文档先行（画面文档 / 式样书 / 架构 / 路由，按需）：
- 代码改动点（预计：TagRepository / ImageDao 查询接口 → TagManageViewModel 删除流程分支 → UI 弹框文案与状态）：
- 验证方式：

## 验收标准

- [ ] 删除无引用标签：确认后直接删除成功（行为同现状）
- [ ] 删除有引用标签：弹框提示影响张数；继续 → 词表删除 + 引用图片 `tagsJson` 移除 + 文件 EXIF `UserComment` 同步更新；取消 → 全部不动
- [ ] 删除后全库无任何图片 `tagsJson` / EXIF 含该标签名（抽样验证）
- [ ] 文档与代码对齐：标签管理 README §8 改写为联动行为；开发日志（文件名带 F8）已写

## 开发记录

| 日期 | 内容 | 关联提交 |
|---|---|---|
|  |  |  |

## 完结复盘（✅ / ❌ 后填；票与台账行保留，不删除）

- **落地效果**（达成了什么 / 与预期差距）：
- **代价与遗留**（新增复杂度、未覆盖场景 → 转新票或备注）：
- **错题本登记**：已登记 [docs/工程/错题本.md](../../../错题本.md) ／ 不适用
