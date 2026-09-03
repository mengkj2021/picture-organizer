# F23 票：本地 Git Hook（pre-push 自动 ktlint）

## 票信息

| 项 | 内容 |
|---|---|
| 票号 | F23 |
| 类型 | 功能 / 体验票 |
| 类型细分 | 工程质量（质量门禁） |
| 标题 | 本地 Git Hook：push 前自动跑 `ktlintCheck`，不过则拒推 |
| 状态 | ☐ 待实施 |
| 起票日期 | 2026-09-03 |
| 关联 | `shared/rules/git-convention.md`；android 工程（AGP/Gradle）；F16（后续可并入 test 任务） |

## 用户诉求 / 场景

- 要解决的问题或场景：ktlint 已配置且 `ignoreFailures=false`，但**验证靠人记得跑**——没有机器强制，漏跑即可能把不过关代码推上去（历史 git-convention 也自陈"push 前自检"靠人）。业界质量门禁第一步就是"把验证从人记得变成机器强制"。
- 预期效果：本地 `git push` 前 hook 自动执行 `cd android && ./gradlew ktlintCheck`（增量，先于 push），不通过则拒绝推送并提示修复；通过才推。
- 是否已有式样依据：否（工程能力）。

## 需求确认（接票前若为「待定」先在此澄清）

- 范围：只做**本地 hook（pre-push）**；云端 CI 不进本票（项目计划 W3 的可选 GitHub Actions 另排）；单测任务不并入（依赖 F16，落地后可起一张后续票把 `testDebugUnitTest` 并入同一 hook）。
- 落地形态考虑：hook 文件入仓（`.githooks/pre-push` + `git config core.hooksPath .githooks` 或 android 侧脚本）——需权衡"hook 入仓便于多人/展示" vs "core.hooksPath 需每克隆者执行一次"，实施时拍板并在 git-convention 写明启用命令。
- 遵守 git-convention：hook 内脚本不产生提交、不改历史；失败仅拒绝并给提示。
- 性能：只跑 ktlint（比全量 `build` 快）；跑失败退出非 0。

## 方案（接票后填）

- 文档先行（画面文档 / 式样书 / 架构 / 路由，按需）：无（git-convention 文档需补一段）
- 代码改动点：新增 hook 脚本（位置按落地形态拍板）；`shared/rules/git-convention.md` 补「push 前自动门禁」说明并三处同步
- 验证方式：故意制造一处 ktlint 违规 → push 被拒；修复后 push 通过（可临时验证后还原）

## 验收标准

- [ ] push 前自动跑 ktlintCheck；违规时 push 被拒并给出可读提示
- [ ] 正常代码 push 不受影响（门禁通过）
- [ ] 启用方式已写入 git-convention（三处同步）；克隆后一条命令可启用
- [ ] 开发日志（文件名带本票号）已写

## 开发记录

| 日期 | 内容 | 关联提交 |
|---|---|---|
|  |  |  |

## 完结复盘（✅ / ❌ 后填；票与台账行保留，不删除）

- **落地效果**（达成了什么 / 与预期差距）：
- **代价与遗留**（新增复杂度、未覆盖场景 → 转新票或备注）：
- **错题本登记**：已登记 [docs/工程/错题本.md](../../../错题本.md) ／ 不适用
