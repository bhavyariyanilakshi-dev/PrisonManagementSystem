@echo off
title Prison Management System
echo ====================================================
echo Starting Prison Management System...
echo ====================================================

java -cp "PrisonManagementSystem.jar;lib/*" com.prison.MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with an error or Java is not installed in PATH.
    pause
)
