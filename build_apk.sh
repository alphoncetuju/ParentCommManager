#!/bin/bash
# ================================================================
# Parent Communication Manager - APK Build Script
# ================================================================
# Run this script on your Linux/macOS machine (or Windows WSL/Git Bash)
# Requirements: Java 11+ must be installed
# ================================================================

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "=============================================="
echo "  Parent Communication Manager - APK Builder"
echo "=============================================="
echo ""

# --- Check Java ---
if ! command -v java &>/dev/null; then
  echo "ERROR: Java not found. Please install Java 11 or higher."
  echo "  Ubuntu/Debian: sudo apt install openjdk-17-jdk"
  echo "  macOS:         brew install openjdk@17"
  exit 1
fi
JAVA_VER=$(java -version 2>&1 | head -1)
echo "✓ Java found: $JAVA_VER"

# --- Download Gradle wrapper jar if missing ---
GRADLE_JAR="gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$GRADLE_JAR" ]; then
  echo ""
  echo "Downloading Gradle wrapper jar..."
  mkdir -p gradle/wrapper
  curl -fsSL "https://raw.githubusercontent.com/nicowillis/gradle-wrapper/main/gradle-wrapper.jar" \
       -o "$GRADLE_JAR" 2>/dev/null || \
  wget -q "https://raw.githubusercontent.com/nicowillis/gradle-wrapper/main/gradle-wrapper.jar" \
       -O "$GRADLE_JAR" 2>/dev/null || true

  # Fallback: use installed gradle
  if [ ! -f "$GRADLE_JAR" ]; then
    if command -v gradle &>/dev/null; then
      echo "Using system Gradle..."
      gradle wrapper --gradle-version 8.4
    else
      echo ""
      echo "Could not get gradle-wrapper.jar automatically."
      echo "Please run ONE of these:"
      echo "  Option A (if gradle installed): gradle wrapper"
      echo "  Option B: Download from https://services.gradle.org/distributions/gradle-8.4-bin.zip"
      echo "            then extract and run:  /path/to/gradle-8.4/bin/gradle wrapper"
      echo ""
      echo "Then re-run this script."
      exit 1
    fi
  fi
fi

# --- Set up Android SDK ---
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
  # Try common locations
  for candidate in \
    "$HOME/Android/Sdk" \
    "$HOME/android-sdk" \
    "/opt/android-sdk" \
    "$HOME/Library/Android/sdk"; do
    if [ -d "$candidate" ]; then
      export ANDROID_HOME="$candidate"
      export ANDROID_SDK_ROOT="$candidate"
      echo "✓ Android SDK found at: $candidate"
      break
    fi
  done
fi

if [ -z "$ANDROID_HOME" ]; then
  echo ""
  echo "Android SDK not found. Attempting auto-install via sdkmanager..."
  echo ""
  SDK_DIR="$HOME/android-sdk"
  mkdir -p "$SDK_DIR/cmdline-tools"
  TOOLS_ZIP="commandlinetools-linux-11076708_latest.zip"
  if [[ "$OSTYPE" == "darwin"* ]]; then
    TOOLS_ZIP="commandlinetools-mac-11076708_latest.zip"
  fi
  TOOLS_URL="https://dl.google.com/android/repository/$TOOLS_ZIP"
  echo "Downloading Android command-line tools from:"
  echo "  $TOOLS_URL"
  curl -fsSL "$TOOLS_URL" -o /tmp/cmdtools.zip || wget -q "$TOOLS_URL" -O /tmp/cmdtools.zip
  unzip -q /tmp/cmdtools.zip -d "$SDK_DIR/cmdline-tools/"
  mv "$SDK_DIR/cmdline-tools/cmdline-tools" "$SDK_DIR/cmdline-tools/latest" 2>/dev/null || true
  export ANDROID_HOME="$SDK_DIR"
  export ANDROID_SDK_ROOT="$SDK_DIR"
  export PATH="$PATH:$SDK_DIR/cmdline-tools/latest/bin"

  echo "Accepting licenses and installing build tools..."
  yes | sdkmanager --licenses >/dev/null 2>&1
  sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
  echo "✓ Android SDK installed"
fi

export ANDROID_HOME="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
export PATH="$PATH:$ANDROID_HOME/platform-tools"

echo ""
echo "Building DEBUG APK..."
echo ""

chmod +x gradlew
./gradlew assembleDebug --stacktrace

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
  echo ""
  echo "================================================"
  echo "  ✅ BUILD SUCCESSFUL!"
  echo "================================================"
  echo "  APK: $SCRIPT_DIR/$APK_PATH"
  SIZE=$(du -sh "$APK_PATH" | cut -f1)
  echo "  Size: $SIZE"
  echo ""
  echo "  Transfer to your Android phone and install."
  echo "  Enable 'Install from unknown sources' first."
  echo "================================================"
else
  echo "ERROR: APK not found after build."
  exit 1
fi
