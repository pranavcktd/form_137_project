@echo off
title Stop Nex Server
echo Stopping Nex...

set FOUND=0
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":3000" ^| findstr "LISTENING"') do (
    taskkill /PID %%p /T /F >nul 2>&1
    set FOUND=1
)

timeout /t 1 >nul
netstat -ano | findstr ":3000" | findstr "LISTENING" >nul
if %errorlevel%==0 (
    echo Nex may still be running. Please ask your admin for help.
) else (
    if "%FOUND%"=="1" (
        echo Nex has been stopped. You can close this window.
    ) else (
        echo Nex was not running.
    )
)
pause
