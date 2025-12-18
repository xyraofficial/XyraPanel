#!/bin/bash
echo "========================================"
echo "       XyraPanel - Android Project"
echo "========================================"
echo ""
echo "Project Type: Android Native (Java)"
echo "Package: com.xyra.panel"
echo ""
echo "Build Status:"
if [ -f "XyraPanel/gradlew" ]; then
    echo "  Gradle wrapper: Available"
else
    echo "  Gradle wrapper: Not found"
fi
echo ""
echo "Project Structure:"
echo "  - Main Activity: XyraPanel/app/src/main/java/com/xyra/panel/MainActivity.java"
echo "  - Resources: XyraPanel/app/src/main/res/"
echo "  - Manifest: XyraPanel/app/src/main/AndroidManifest.xml"
echo ""
echo "Note: This is a native Android project."
echo "To build an APK, use Android Studio or AIDE."
echo ""
echo "Java Version:"
java -version 2>&1 | head -1
echo ""
echo "========================================"
