#!/bin/bash

# Script برای تحلیل APK و پیدا کردن فایل‌های بزرگ

APK_PATH="${1:-app/build/outputs/apk/release/app-release.apk}"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found: $APK_PATH"
    echo "Usage: ./scripts/analyze-apk.sh [path-to-apk]"
    exit 1
fi

echo "📦 Analyzing APK: $APK_PATH"
echo ""

# Extract APK
TEMP_DIR=$(mktemp -d)
unzip -q "$APK_PATH" -d "$TEMP_DIR"

echo "📊 APK Size Breakdown:"
echo "======================"
echo ""

# Total size
TOTAL_SIZE=$(du -sh "$APK_PATH" | cut -f1)
echo "Total APK Size: $TOTAL_SIZE"
echo ""

# DEX files
echo "📝 DEX Files (Code):"
find "$TEMP_DIR" -name "classes*.dex" -exec du -h {} + | sort -rh
echo ""

# Native libraries
echo "🔧 Native Libraries (.so files):"
if [ -d "$TEMP_DIR/lib" ]; then
    du -sh "$TEMP_DIR/lib"/*
else
    echo "No native libraries found"
fi
echo ""

# Resources
echo "🎨 Resources:"
if [ -d "$TEMP_DIR/res" ]; then
    du -sh "$TEMP_DIR/res"/* | sort -rh | head -10
else
    echo "No resources found"
fi
echo ""

# Assets
echo "📁 Assets:"
if [ -d "$TEMP_DIR/assets" ]; then
    du -sh "$TEMP_DIR/assets"/* | sort -rh
else
    echo "No assets found"
fi
echo ""

# Top 20 largest files
echo "🔝 Top 20 Largest Files:"
find "$TEMP_DIR" -type f -exec du -h {} + | sort -rh | head -20
echo ""

# Cleanup
rm -rf "$TEMP_DIR"

echo "✅ Analysis complete!"
