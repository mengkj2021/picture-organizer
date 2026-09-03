# F16 票：引入单元测试：基础设施与首批覆盖

## 票信息

| 项 | 内容 |
|---|---|
| 票号 | F16 |
| 类型 | 功能 / 体验票 |
| 类型细分 | 工程质量（学习 AI 开发实践） |
| 标题 | 引入单元测试：基础设施与首批易碎纯逻辑覆盖 |
| 状态 | ☐ 待实施 |
| 起票日期 | 2026-09-03 |
| 关联 | `android/app/build.gradle.kts`；`shared/rules/dev-convention.md`；错题本 |

## 用户诉求 / 场景

- 要解决的问题或场景：项目当前**零测试**，且代码全部由 AI 开发——易碎纯逻辑（重命名模板、状态与标签规则、筛选排序）被后续改动波及时缺少回归护栏，改坏了只能靠人工 Review 发现。项目兼作「AI 开发学习场」，本票示范**AI 写的代码如何被单测证明没改坏**，让测试成为 AI 自证的客观手段。
- 预期效果：① `testDebugUnitTest` 一条命令全绿可跑；② 首批测试覆盖项目里最易碎、纯 JVM 友好的几处逻辑；③ 文档记录「测试怎么跑、新测试怎么加」，后续票涉及同类逻辑时可循例补测。
- 是否已有式样依据：否（工程能力，非用户可见功能；不涉画面 / 式样书变更）。

## 需求确认（接票前若为「待定」先在此澄清）

范围界定（**刻意裁剪**，避免把测试铺成全项目重负担）：

- **本票做**：
  - 基础设施：`android/app/build.gradle.kts` 增加 `testImplementation` 依赖（JUnit4 + kotlinx-coroutines-test），新建 `android/app/src/test/java/com/pictureorganizer/` 测试源集
  - 首批测试只覆盖**纯 JVM 友好对象**：
    - `RenamePatternApplier`（`util/file/`）：重命名模板 `{name}` `{date}` `{tag}` 替换——F2 历史反复调整、F14 还会动，最典型易碎点
    - `ImageListItem`（`model/`）：`userTagsOf` / `withStatus` / `STATUS_TAGS`（S1「状态不算标签」规则）
    - `ImageTagFilter`（`model/`）：`matchesFilter` / `sortedByFilter`（主画面 / 打包共用筛选与排序）
- **本票明确不做**（各自有原因，另行评估）：
  - `Converters` / `ImageMappers`：依赖 `org.json`（Android SDK 类），纯 JVM 测不了——需先换解析实现或引入 Robolectric，成本另算
  - Room DAO、ViewModel、Compose UI 测试：依赖 Android 环境 / 模拟器，本票不碰
  - JaCoCo 覆盖率门槛、Git Hook、CI：`先不对应`，不并入本票（效果好后另行起票）

## 方案（接票后填）

- 文档先行（画面文档 / 式样书 / 架构 / 路由，按需）：不涉画面与式样，无
- 代码改动点：`android/gradle/libs.versions.toml`（新增测试依赖版本）、`android/app/build.gradle.kts`（`testImplementation`）、新建 `android/app/src/test/` 下首批测试
- 验证方式：`cd android && ./gradlew testDebugUnitTest` 全绿；`./gradlew ktlintCheck` 仍通过（新测试须过 ktlint 风格）

## 验收标准

- [ ] `cd android && ./gradlew testDebugUnitTest` 全部通过
- [ ] `./gradlew ktlintCheck` 仍通过（含新增测试文件）
- [ ] 首批测试覆盖三处：`RenamePatternApplier`、`ImageListItem`（userTagsOf / withStatus）、`ImageTagFilter`（matchesFilter / sortedByFilter），每处含边界用例（空 pattern / 空标签 / 无扩展名等）
- [ ] 票内「开发记录」写明：测试怎么跑、新测试文件放哪、怎么加
- [ ] 开发日志（文件名带本票号）已写

## 开发记录

| 日期 | 内容 | 关联提交 |
|---|---|---|
|  |  |  |

## 完结复盘（✅ / ❌ 后填；票与台账行保留，不删除）

- **落地效果**（达成了什么 / 与预期差距）：
- **代价与遗留**（新增复杂度、未覆盖场景 → 转新票或备注）：
- **错题本登记**：已登记 [docs/工程/错题本.md](../../../错题本.md) ／ 不适用
