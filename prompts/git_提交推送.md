# 提交推送

**用法**：把本文件拉进对话并发送 = **提交并推送当前全部相关变更**（不必再口头说一遍）。  
**约定**：服从 git-convention（UTF-8 `-F`、禁止 `git add .`、禁止擅自 `--force`）。

▶ include `ai-workbench/宪法/include约定.md`

## AI 执行流程（不用改）

▶ include `ai-workbench/细则/prompts/提交推送.md`

---

## 本次提交（可选覆盖；可空）

默认：暂存当前全部应提交变更 → commit → push。  
仅当要改默认时填写：

```
票号：（可空 = 从 diff / 会话推断；推断不到再问）
不纳入本次：（可空 = 无排除）
不推送：（默认推送；仅当写「不推送」才只 commit）
提交说明要点：（可空 = AI 据 diff 起草）
额外说明：
```
