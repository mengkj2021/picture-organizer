# android · Gradle 工程

本目录是仓库**唯一业务代码根**。用 Android Studio **Open 本目录**（不要 Open 仓库根）。

## 版号

| 项 | 值 | 出处 |
|---|---|---|
| versionName | `1.0.0` | `app/build.gradle.kts` |
| versionCode | `2` | 同上（F51；对外发包必增） |

仓库目前**不上应用商店**。对外安装包走 [GitHub Releases](https://github.com/mengkj2021/picture-organizer/releases)：**debug 签名** APK（本机/CI 自动调试证书，非正式商店密钥）。也可按下方自行 `assembleDebug` /（有 keystore 时）`assembleRelease`。

### 回家补发 v1.0.0 Release（若页面上还没有 APK）

远程 **tag `v1.0.0` 已推**；APK 不入库（体积大 + `.tmp/` ignore）。在能编 Android 的机器上、仓库根执行：

```bash
git pull
gh auth login          # 本机第一次
./scripts/publish-v1.0.0-github-release.sh
```

脚本会：`assembleDebug` → 创建或更新 GitHub Release 并挂上 `picture-organizer-1.0.0-debug.apk`。

## 环境

- Android Studio（支持 AGP 9.2 + Gradle 9.7）
- JDK 17+
- Android SDK 37（compileSdk / targetSdk）

## 构建

```bash
cd android
./gradlew assembleDebug   # Windows: gradlew.bat assembleDebug
```

产物一般在 `app/build/outputs/apk/`。首次 Sync 会按 Wrapper 下载 Gradle；**不要**把 `gradle/wrapper/*.zip` 提交进仓。

路径含非 ASCII 时已启用 `android.overridePathCheck=true`。

## 与文档的关系

- 规格镜像：[project-docs/](../project-docs/)（冲突以本目录源码为准）
- 产品概述：[项目概述](../project-docs/项目概述.md) · [技术栈](../project-docs/技术栈.md)
- 整仓门面（含 AI 开发主线）：[根 README](../README.md)
