@echo off
:: 闲淘二手交易平台 - 启动脚本 (CMD 包装器)
:: 实际逻辑在 start.ps1 中，此文件仅用于双击启动
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1"
pause
