# F21 票：技能 create-screen-doc（新建画面文档 SOP）

## 票信息

| 项 | 内容 |
|---|---|
| 票号 | F21 |
| 类型 | 功能 / 体验票 |
| 类型细分 | 工程质量（AI 技能落地） |
| 标题 | 落地技能 `create-screen-doc`：新增画面自动走文档 SOP |
| 状态 | ☐ 待实施 |
| 起票日期 | 2026-09-03 |
| 关联 | `shared/skills/`（框架已建、0 实际技能）；docs/README「新增画面流程」 |

## 用户诉求 / 场景

- 要解决的问题或场景：`shared/skills/README.md` 框架自建起就标注"高优先级建议补充 create-screen-doc / implement-screen"，至今 0 技能落地。业界共识：把重复 SOP 编码为可复用技能，让每个 agent 同一套打法，不靠个人纪律与每次重述提示词。新增画面的流程（模板 → 路由表 → 式样书第 5 章）目前依赖 prompt 每次重复，容易漏步（Bug1 教训：壳层约束没随模板带出）。
- 预期效果：调用 `create-screen-doc` 技能 → 按固定 SOP 一步不落新建画面文档（复制 `_模板/` → 登记路由 → 更新式样书画面清单 → 填 10 节模板）。
- 是否已有式样依据：否（工程资产）。

## 需求确认（接票前若为「待定」先在此澄清）

- 范围：只做 `create-screen-doc` 一个技能（`implement-screen` 见 F22，拆票便于各自验收）。
- 落地形态：按 `shared/skills/README.md` 的 SKILL.md 规范（name/description frontmatter + 触发条件 + SOP + 注意事项）；同步到工具技能目录（CodeBuddy `.codebuddy/skills/`；Cursor 声明方式实现时按工具能力落地）。
- 技能内容须覆盖 Bug1 教训：SOP 里强制含"insets/edge-to-edge 壳层约束从模板带出"检查（可引画面 `_模板` 或错题本）。
- 与本项目"新画面流程"（docs/README 摘要）保持一致；流程文档有变更时技能需同步（可备注）。

## 方案（接票后填）

- 文档先行（画面文档 / 式样书 / 架构 / 路由，按需）：无
- 代码改动点：新建 `shared/skills/create-screen-doc/SKILL.md`（+assets 按需）；同步工具技能目录；`shared/skills/README.md`「当前技能」表补一行
- 验证方式：用技能走一遍新增画面演练（可造一个一次性演示画面或复用待办画面规格），核对四步齐全

## 验收标准

- [ ] `create-screen-doc` SKILL.md 就位（触发条件 + SOP + 注意事项，含 insets 检查）
- [ ] `shared/skills/README.md` 登记；工具目录同步可用
- [ ] 演练一次：SOP 四步（模板→路由→式样书→填写）无漏，日志记录
- [ ] 开发日志（文件名带本票号）已写

## 开发记录

| 日期 | 内容 | 关联提交 |
|---|---|---|
|  |  |  |

## 完结复盘（✅ / ❌ 后填；票与台账行保留，不删除）

- **落地效果**（达成了什么 / 与预期差距）：
- **代价与遗留**（新增复杂度、未覆盖场景 → 转新票或备注）：
- **错题本登记**：已登记 [docs/工程/错题本.md](../../../错题本.md) ／ 不适用
