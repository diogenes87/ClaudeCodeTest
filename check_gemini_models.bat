@echo off
REM Script to check available Gemini models with your API key
REM Usage: check_gemini_models.bat YOUR_API_KEY

if "%1"=="" (
    echo Usage: check_gemini_models.bat YOUR_API_KEY
    echo Example: check_gemini_models.bat AIzaSy...
    exit /b 1
)

set API_KEY=%1

echo Checking available Gemini models...
echo ==================================
echo.

curl -s "https://generativelanguage.googleapis.com/v1beta/models?key=%API_KEY%" | findstr /C:"name" /C:"displayName"

echo.
echo ==================================
echo Look for model names like: gemini-1.5-flash, gemini-1.0-pro, etc.
