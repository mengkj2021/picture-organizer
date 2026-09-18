#!/usr/bin/env bash
# 在已登录 gh 的机器上：编 debug APK → 挂到已有 tag v1.0.0 的 GitHub Release。
# 用法（仓库根）：
#   ./scripts/publish-v1.0.0-github-release.sh
# 前置：git pull；gh auth login（一次）；Android SDK / JDK 可用。
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
REPO="${GITHUB_REPO:-mengkj2021/picture-organizer}"
TAG="v1.0.0"
APK_NAME="picture-organizer-1.0.0-debug.apk"
OUT_DIR="$ROOT/.tmp"
BUILT="$ROOT/android/app/build/outputs/apk/debug/app-debug.apk"
STAGED="$OUT_DIR/$APK_NAME"

cd "$ROOT"

if ! command -v gh >/dev/null 2>&1; then
  echo "缺少 gh。安装后执行: brew install gh && gh auth login" >&2
  exit 1
fi

if ! gh auth status >/dev/null 2>&1; then
  echo "gh 未登录。先执行: gh auth login" >&2
  echo "（推代码用 SSH 不够；建 Release 需要 gh 的 HTTPS token。）" >&2
  exit 1
fi

# 选 JDK：优先 Android Studio JBR 21，否则沿用环境
if [[ -z "${JAVA_HOME:-}" ]]; then
  for cand in \
    "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
    "/Applications/Android Studio 2.app/Contents/jbr/Contents/Home"
  do
    if [[ -x "$cand/bin/java" ]]; then
      export JAVA_HOME="$cand"
      break
    fi
  done
fi
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}$PATH"

echo "==> assembleDebug (JAVA_HOME=${JAVA_HOME:-system})"
(
  cd "$ROOT/android"
  ./gradlew assembleDebug
)

mkdir -p "$OUT_DIR"
cp -f "$BUILT" "$STAGED"
echo "==> APK: $STAGED ($(wc -c <"$STAGED" | tr -d ' ') bytes)"

if gh release view "$TAG" --repo "$REPO" >/dev/null 2>&1; then
  echo "==> Release $TAG 已存在，上传/覆盖附件 $APK_NAME"
  gh release upload "$TAG" "$STAGED" --repo "$REPO" --clobber
else
  echo "==> 创建 Release $TAG 并附带 APK"
  gh release create "$TAG" "$STAGED" \
    --repo "$REPO" \
    --title "v1.0.0 · 图片整理首版" \
    --notes "$(cat <<'EOF'
## 说明

- **不上应用商店**；本包供侧载 / 随意试用。
- 附件为 **debug 签名** APK（Android 调试证书自动签名，非正式商店密钥）。
- 版号：`versionName` 1.0.0 / `versionCode` 2。
- 也可自行从源码构建：Open `android/` → `./gradlew assembleDebug`。

## 安装注意

- 需允许「未知来源」安装。
- 本机调试包与日后正式签名包 **不能** 互相覆盖升级（签名不同）。

AI 开发搭法见仓库根 README。
EOF
)"
fi

echo "==> 完成"
gh release view "$TAG" --repo "$REPO" --web 2>/dev/null || gh release view "$TAG" --repo "$REPO"
