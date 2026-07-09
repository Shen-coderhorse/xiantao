@echo off
:: 闲淘二手交易平台 - 关闭脚本 (CMD 包装器)
:: 实际逻辑在 stop.ps1 中，此文件仅用于双击启动
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop.ps1"
pause
