# ▶ include 约定

> **权威**。`prompts/`、`shared/rules/`、`shared/skills/` 等编排壳一律按本文调用分册。  
> 类似 Android XML 的 `<include>`：壳里只写步骤骨架，细则在被 include 的文件里。

## 语法

编排壳中出现一行：

```text
▶ include `相对仓库根的路径`
```

（反引号可选；路径相对仓库根，如 `ai-workbench/细则/接票/先测后写.md`。）

## 语义（必须遵守）

1. 执行到该行时，AI **立刻打开目标文件全文**
2. **视同嵌在当前文件中**执行；禁止只贴链接 / 只扫标题而不读正文
3. 同一会话内同一路径已完整读过且未改文件，可不再重复打开；若用户或工具刚改过该文件，须重读
4. 分册有更新时只改分册；编排壳步骤编号尽量稳定

## 谁放分册

| 编排壳所在 | 细则目录（示例） |
|---|---|
| `prompts/`（扁平：`{用途}_{名}.md`，如 `票_接票开发.md`、`git_拉取最新代码.md`） | `ai-workbench/细则/prompts/`、`ai-workbench/细则/接票/` |
| `shared/rules/` | `ai-workbench/细则/rules/` |
| `shared/skills/` | `ai-workbench/细则/skills/` |

## 壳文件写法

1. 文首可一行：`▶ include \`ai-workbench/宪法/include约定.md\``（首次进入该壳时建立约定）
2. 步骤里按需 `▶ include` 各分册
3. 文末保留用户填写区（prompts）或 frontmatter（rules/skills）

`ai-workbench/细则/接票/README.md` 只列接票分册索引，**不再重复**本文正文。
