@echo off
setlocal enabledelayedexpansion
title Nex - Authorize Hosting Accounts
cd /d "D:\Form_137_Project"

echo ============================================
echo   Nex - Authorize Hosting Accounts
echo   Sets up CLI logins for GitHub and Railway
echo   on this machine. Cloudinary has no CLI
echo   login - instructions are shown at the end.
echo ============================================
echo.

rem --- GitHub CLI ---
where gh >nul 2>nul
if errorlevel 1 (
    echo [1/2] GitHub CLI not found - installing via winget...
    where winget >nul 2>nul
    if errorlevel 1 (
        echo winget is not available on this machine.
        echo Install the GitHub CLI manually from https://cli.github.com, then run this script again.
        pause
        exit /b
    )
    winget install --id GitHub.cli -e --scope user --accept-source-agreements --accept-package-agreements
    if errorlevel 1 (
        echo.
        echo The GitHub CLI install failed ^(winget exit code %errorlevel%^).
        echo This is usually a permissions issue. Try one of these, then run this script again:
        echo   - Right-click this .bat file and choose "Run as administrator"
        echo   - Or install it yourself from https://cli.github.com and re-run this script
        pause
        exit /b
    )
    echo.
    echo GitHub CLI was just installed. Close this window, reopen it, and run this script again
    echo so Windows picks up the new PATH entry.
    pause
    exit /b
)
echo [1/2] GitHub CLI found.

echo.
echo Logging in to GitHub - follow the prompts; a browser window will open...
gh auth login
if errorlevel 1 (
    echo GitHub login failed or was cancelled.
    pause
    exit /b
)
echo.
echo GitHub status:
gh auth status

echo.
echo ============================================
echo.

rem --- Railway CLI ---
where railway >nul 2>nul
if errorlevel 1 (
    echo [2/2] Railway CLI not found - installing via npm...
    call npm install -g @railway/cli
    if errorlevel 1 (
        echo Could not install the Railway CLI. Check your npm setup and try again.
        pause
        exit /b
    )
)
echo [2/2] Railway CLI found.

echo.
echo Logging in to Railway - a browser window will open...
railway login
if errorlevel 1 (
    echo Railway login failed or was cancelled.
    pause
    exit /b
)
echo.
echo Railway status:
railway whoami

echo.
echo ============================================
echo   GitHub and Railway are now authorized on
echo   this machine.
echo.
echo   Cloudinary has no CLI login. Instead:
echo     1. Go to https://cloudinary.com and sign up
echo        or log in (the free tier is fine to start).
echo     2. On your Cloudinary Dashboard home page,
echo        copy the "API Environment variable" line -
echo        it looks like:
echo        CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
echo     3. Send that value to Claude, or paste it into
echo        .env yourself, so file storage can be wired
echo        up to it.
echo.
echo   Next: once GitHub, Railway, and Cloudinary are
echo   sorted, tell Claude and it will create the
echo   Railway project(s) for dev/production, write the
echo   Dockerfile (Node + Java), connect this GitHub
echo   repo to Railway for auto-deploy on push, and wire
echo   Cloudinary into the file-storage code.
echo ============================================
pause
