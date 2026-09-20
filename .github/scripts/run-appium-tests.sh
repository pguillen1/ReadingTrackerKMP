#!/usr/bin/env bash

set -e

adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk

appium > appium.log 2>&1 &
APPIUM_PID=$!

cleanup() {
  kill "$APPIUM_PID" 2>/dev/null || true
}

trap cleanup EXIT

echo "Waiting for Appium..."

for i in {1..30}; do
  if curl --silent --fail http://127.0.0.1:4723/status > /dev/null; then
    echo "Appium is ready"
    break
  fi

  if [ "$i" -eq 30 ]; then
    echo "Appium did not start"
    exit 1
  fi

  sleep 1
done

./gradlew :appiumTests:test