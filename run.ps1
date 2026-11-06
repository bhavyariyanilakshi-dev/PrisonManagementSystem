# Prison Management System Launcher for PowerShell
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "Starting Prison Management System..." -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Cyan

# Set current directory to where this script is located
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if ($scriptDir) {
    Set-Location $scriptDir
}

java -cp "PrisonManagementSystem.jar;lib/*" com.prison.MainApp
