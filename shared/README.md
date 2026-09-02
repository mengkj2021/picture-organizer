# 图片整理 · 项目说明（唯一维护入口）

本项目由 **CodeBuddy** 与 **Cursor** 共同维护。`shared/` 目录是 AI 开发配置的**唯一维护入口**，统一存放项目说明、rules 与 skills，再手动同步到两个工具的实际读取目录。

## 目录结构

```
图片整理/
├── android/                           # ★ Android 工程（Gradle root，独立构建 / Android Studio 打开此目录）
│   ├── app/                           # app 模块（Kotlin 业务、Compose UI）
│   ├── build.gradle.kts               # 工程构建配置
│   ├── settings.gradle.kts / gradle.properties / gradlew
│   └── gradle/                        # wrapper（+ 本地 zip 不提交）与 libs.versions.toml
├── shared/                            # ★ AI 配置唯一维护入口（改动都从这里开始）
│   ├── README.md                      # 项目说明（CodeBuddy / Cursor 共用一份）
│   ├── prompts/                       # 拉入对话执行的提示词模板
│   │   ├── README.md
│   │   ├── 功能开发.md
│   │   ├── 式样问答.md
│   │   └── 文档整理.md
│   ├── rules/
│   │   ├── sync-convention.md         # 双工具规则同步约定
│   │   ├── dev-convention.md          # AI 全量开发、文档先行约定
│   │   └── git-convention.md          # git 提交纪律（-F 编码、禁 add .、自检）
│   └── skills/
│       └── README.md                  # 技能模板框架说明
├── .codebuddy/rules/                  # CodeBuddy 实际读取（从 shared 同步）
│   ├── sync-convention/RULE.mdc
│   ├── dev-convention/RULE.mdc
│   └── git-convention/RULE.mdc
└── .cursor/rules/                     # Cursor 实际读取（从 shared 同步）
    ├── sync-convention.mdc
    ├── dev-convention.mdc
    └── git-convention.mdc
```

## 维护流程

所有改动先改 `shared/` 下的源文件，再手动同步到工具目录：

| 改动内容 | 维护入口 | 同步目标 |
|----------|----------|----------|
| 项目说明 | `shared/README.md` | 无需同步（规则文件已引用本文件） |
| AI 提示词 | `shared/prompts/` | 无需同步（拉入对话并填写模板填写区） |
| 规则 | `shared/rules/<name>.md` | `.codebuddy/rules/<name>/RULE.mdc` + `.cursor/rules/<name>.mdc` |
| 技能 | `shared/skills/<name>/` | 各工具技能目录 |

### 新增 / 修改 / 删除规则

1. 编辑 `shared/rules/<name>.md`（统一 Markdown + frontmatter）
2. 复制到 CodeBuddy：`.codebuddy/rules/<name>/RULE.mdc`
3. 复制到 Cursor：`.cursor/rules/<name>.mdc`

> 统一 frontmatter 同时包含两个工具所需的全部字段，各工具自动忽略不认识的字段，因此**同一份文件可直接复制到两处，无需转换**。

### 新增 / 修改 / 删除技能

1. 在 `shared/skills/<skill-name>/` 下创建 `SKILL.md`（含用途、触发条件、执行流程）
2. 同步到各工具的实际技能目录（详见 `shared/skills/README.md`）

## frontmatter 字段对照

| 字段 | shared（统一格式） | CodeBuddy | Cursor |
|------|:----------:|:---------:|:------:|
| `description` | 必填 | 必填 | 必填 |
| `alwaysApply` | 支持 | 支持 | 支持 |
| `globs` | 支持（如 `**/*`） | — | 支持 |
| `enabled` | 支持 | 支持 | — |
| `updatedAt` | 支持 | 支持 | — |
| `provider` | 可选 | 可选 | — |

## 注意事项

- 规则在会话开始时注入，**修改后需新建对话才能生效**
- 规则正文使用 UTF-8 编码保存，避免中文乱码
- 工具通过规则文件获知项目说明位置，实际内容以本文件为准

## 首次构建（Android 工程位于 `android/`）

Gradle Wrapper 使用本地 `gradle-9.7.1-bin.zip`（相对路径，不提交 Git）。克隆后需自行放置该 zip 到 `android/gradle/wrapper/` 目录（与 `gradle-wrapper.properties` 同级），或临时将 `android/gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 改为官方 URL：

```
https://services.gradle.org/distributions/gradle-9.7.1-bin.zip
```

项目路径含中文时，`android/gradle.properties` 中 `android.overridePathCheck=true` 为必需项；迁移至纯 ASCII 路径后可移除。详见根目录 [README.md](../README.md)。
