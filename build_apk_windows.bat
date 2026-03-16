@echo off
echo =============================================
echo   Parent Communication Manager - APK Builder
echo =============================================
echo.

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found.
    echo Download Java 17 from: https://adoptium.net/
    pause
    exit /b 1
)
echo Java found OK

:: Check gradlew.bat
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found.
    echo Run: gradle wrapper
    pause
    exit /b 1
)

:: Check ANDROID_HOME
if "%ANDROID_HOME%"=="" (
    if exist "%USERPROFILE%\AppData\Local\Android\Sdk" (
        set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
        echo Android SDK found at: %ANDROID_HOME%
    ) else (
        echo.
        echo ANDROID_HOME not set and SDK not found.
        echo Please install Android Studio from https://developer.android.com/studio
        echo Then set ANDROID_HOME to your SDK path.
        pause
        exit /b 1
    )
)

echo.
echo Building DEBUG APK...
echo.
call gradlew.bat assembleDebug

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo =============================================
    echo   BUILD SUCCESSFUL!
    echo   APK: app\build\outputs\apk\debug\app-debug.apk
    echo =============================================
) else (
    echo BUILD FAILED. Check output above.
)
pause
