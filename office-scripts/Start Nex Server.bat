@echo off
title Nex - Tax Filing App (keep this window open)
cd /d "D:\Form_137_Project"

echo ============================================
echo   Starting Nex...
echo   This can take up to 30 seconds the first time.
echo.
echo   DO NOT CLOSE this window while you are using Nex.
echo   To stop Nex: close this window, or double-click
echo   "Stop Nex Server" on the Desktop.
echo ============================================
echo.

rem If it's already running, just open it instead of starting a second copy.
netstat -ano | findstr ":3000" | findstr "LISTENING" >nul
if %errorlevel%==0 (
    echo Nex is already running - opening it in your browser...
    start "" http://localhost:3000
    pause
    exit /b
)

rem Open the browser automatically once the server has had time to start.
start "" cmd /c "timeout /t 10 >nul && start http://localhost:3000"

call npm run dev

echo.
echo Nex has stopped.
pause
