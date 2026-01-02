#!/bin/bash

# ----------------------
# Einstellungen
# ----------------------

# gradle
GRADLEW_TASK=":demo:app:linkDebugFrameworkIosSimulatorArm64"
GRADLEW_CACHE="/tmp/gradle-project-cache"

# xcode
XCODE_PROJECT="demo/iosApp/iosApp.xcodeproj"
XCODE_SCHEME="iosApp"
XCODE_CONFIGURATION="Debug"

# simulator
SIMULATOR_NAME="iPhone 17 Pro"

# paths
PROJECT_NAME="Lumberjack"
DERIVED_DATA_PATH="demo/iosApp/build/derivedData"
FRAMEWORK_PATH="demo/app/build/bin/iosSimulatorArm64/debugFramework"
APP_PATH="demo/iosApp/build/derivedData/Build/Products/Debug-iphonesimulator/$PROJECT_NAME.app"

# bundle id
BUNDLE_ID="com.michaelflisar.demo.$PROJECT_NAME"

# ----------------------
# Skript
# ----------------------

# 1) go to project root
cd ..

# 2) build framework for iOS simulator
./gradlew $GRADLEW_TASK --project-cache-dir "$GRADLEW_CACHE"

# 3) build iOS wrapper app with xcode
xcodebuild \
  -project "$XCODE_PROJECT" \
  -scheme "$XCODE_SCHEME" \
  -configuration "$XCODE_CONFIGURATION" \
  -destination "platform=iOS Simulator,name=$SIMULATOR_NAME" \
  -derivedDataPath "$DERIVED_DATA_PATH" \
  FRAMEWORK_SEARCH_PATHS="$FRAMEWORK_PATH" \
  build

# 4) run and open simulator
if ! xcrun simctl list devices | grep -F "$SIMULATOR_NAME (" | grep -q Booted; then
  xcrun simctl boot "$SIMULATOR_NAME"
fi
open -a Simulator

# 5) install iOS app +  launch iOS app
xcrun simctl install booted "$APP_PATH"
xcrun simctl launch booted "$BUNDLE_ID"
