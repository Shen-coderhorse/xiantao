<#
.SYNOPSIS
    闲淘二手交易平台 - 一键启动脚本 (PowerShell)
.DESCRIPTION
    在当前终端窗口内依次后台启动所有服务，不开新窗口。
    启动顺序: 后端(8080) → 网页端(5173) → 管理后台(5174) → 小程序(5175)
#>

$ErrorActionPreference = "Continue"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# ======================== 路径与端口 ========================
$PROJECT_ROOT = $PSScriptRoot
$BACKEND_DIR  = Join-Path $PROJECT_ROOT "xiantao-server"
$WEB_DIR      = Join-Path $PROJECT_ROOT "xiantao-web"
$ADMIN_DIR    = Join-Path $PROJECT_ROOT "xiantao-admin"
$MINI_DIR     = Join-Path $PROJECT_ROOT "xiantao-miniprogram"

$BACKEND_PORT = 8080
$WEB_PORT     = 5173
$ADMIN_PORT   = 5174
$MINI_PORT    = 5175

$PID_DIR = Join-Path $PROJECT_ROOT ".pids"
$LOG_DIR = Join-Path $PROJECT_ROOT ".logs"

# ======================== 辅助函数 ========================

function Write-Status($Icon, $Msg, $Color) {
    Write-Host "  [$Icon] $Msg" -ForegroundColor $Color
}

function Write-Ok($Msg)   { Write-Status " OK " $Msg "Green" }
function Write-Fail($Msg) { Write-Status "FAIL" $Msg "Red" }
function Write-Warn($Msg) { Write-Status "WARN" $Msg "Yellow" }
function Write-Skip($Msg) { Write-Status "SKIP" $Msg "DarkGray" }

function Test-PortInUse([int]$Port) {
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    return ($null -ne $conn)
}

function Get-PortPid([int]$Port) {
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($conn) { return $conn.OwningProcess | Select-Object -First 1 }
    return $null
}

# ======================== 标题 ========================
Write-Host ""
Write-Host "  ================================================" -ForegroundColor Cyan
Write-Host "       闲淘二手交易平台 - 一键启动脚本" -ForegroundColor Cyan
Write-Host "       XianTao Second-Hand Trading Platform" -ForegroundColor Cyan
Write-Host "  ================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  项目目录: $PROJECT_ROOT" -ForegroundColor DarkCyan
Write-Host ""

# ======================== [1/5] 环境检查 ========================
Write-Host "  [1/5] 检查运行环境..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

$envOk = $true

# JDK
$java = Get-Command java -ErrorAction SilentlyContinue
if ($java) {
    $javaVer = (java -version 2>&1 | Select-Object -First 1) -replace '.*"(.+)".*', '$1'
    Write-Ok "JDK 已安装: $javaVer"
} else {
    Write-Fail "JDK 未安装或未配置到 PATH"
    $envOk = $false
}

# Maven
$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    $mvnVer = (mvn -version 2>&1 | Select-Object -First 1) -replace '.*Apache Maven (\S+).*', '$1'
    Write-Ok "Maven 已安装: $mvnVer"
} else {
    Write-Fail "Maven 未安装或未配置到 PATH"
    $envOk = $false
}

# Node.js
$node = Get-Command node -ErrorAction SilentlyContinue
if ($node) {
    $nodeVer = node -v
    Write-Ok "Node.js 已安装: $nodeVer"
} else {
    Write-Fail "Node.js 未安装或未配置到 PATH"
    $envOk = $false
}

# npm
$npm = Get-Command npm -ErrorAction SilentlyContinue
if ($npm) {
    $npmVer = npm -v
    Write-Ok "npm 已安装: $npmVer"
} else {
    Write-Fail "npm 未安装或未配置到 PATH"
    $envOk = $false
}

# MySQL
$mysql = Get-Command mysql -ErrorAction SilentlyContinue
if ($mysql) {
    Write-Ok "MySQL 客户端可用"
} else {
    Write-Warn "mysql 命令不可用, 请确保 MySQL 已启动且端口 3306 可访问"
}

if (-not $envOk) {
    Write-Host ""
    Write-Fail "环境检查未通过, 请先安装缺失的组件"
    Write-Host ""
    exit 1
}

Write-Host ""

# ======================== [2/5] 端口检查 ========================
Write-Host "  [2/5] 检查端口占用..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

$portConflict = $false
$ports = @(
    @{ Port = $BACKEND_PORT; Name = "后端服务" },
    @{ Port = $WEB_PORT;     Name = "网页端" },
    @{ Port = $ADMIN_PORT;   Name = "管理后台" },
    @{ Port = $MINI_PORT;    Name = "小程序端" }
)

foreach ($p in $ports) {
    if (Test-PortInUse $p.Port) {
        $procId = Get-PortPid $p.Port
        Write-Fail "端口 $($p.Port) 已被占用 (PID: $procId, $($p.Name))"
        $portConflict = $true
    } else {
        Write-Ok "端口 $($p.Port) 可用 ($($p.Name))"
    }
}

if ($portConflict) {
    Write-Host ""
    Write-Fail "存在端口冲突, 请先运行 .\stop.ps1 关闭已有进程"
    Write-Host ""
    exit 1
}

Write-Host "  所有端口均可用"
Write-Host ""

# ======================== [3/5] 安装依赖 ========================
Write-Host "  [3/5] 检查前端依赖..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

$frontendDirs = @(
    @{ Name = "xiantao-web";         Dir = $WEB_DIR },
    @{ Name = "xiantao-admin";       Dir = $ADMIN_DIR },
    @{ Name = "xiantao-miniprogram"; Dir = $MINI_DIR }
)

foreach ($fd in $frontendDirs) {
    $nm = Join-Path $fd.Dir "node_modules"
    if (-not (Test-Path $nm)) {
        Write-Host "  安装 $($fd.Name) 依赖..." -ForegroundColor DarkGray
        Push-Location $fd.Dir
        npm install --loglevel=error 2>&1 | Out-Null
        Pop-Location
    }
}

Write-Host "  依赖检查完成"
Write-Host ""

# ======================== [4/5] 启动服务 ========================
Write-Host "  [4/5] 启动所有服务..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

# 创建 PID 和日志目录
if (-not (Test-Path $PID_DIR)) { New-Item -ItemType Directory -Path $PID_DIR -Force | Out-Null }
if (-not (Test-Path $LOG_DIR)) { New-Item -ItemType Directory -Path $LOG_DIR -Force | Out-Null }

# --- 启动后端 ---
Write-Host "  启动后端服务 (端口 $BACKEND_PORT)..." -ForegroundColor White

$backendLog = Join-Path $LOG_DIR "backend.log"
$backendProc = Start-Process -FilePath "mvn" `
    -ArgumentList "spring-boot:run" `
    -WorkingDirectory $BACKEND_DIR `
    -RedirectStandardOutput $backendLog `
    -RedirectStandardError (Join-Path $LOG_DIR "backend-err.log") `
    -PassThru -NoNewWindow

$backendProc.Id | Set-Content (Join-Path $PID_DIR "backend.pid")

# 等待后端就绪
Write-Host "  等待后端服务就绪" -ForegroundColor DarkGray -NoNewline
$maxWait = 90
$elapsed = 0
$backendReady = $false

while ($elapsed -lt $maxWait) {
    Start-Sleep -Seconds 2
    $elapsed += 2
    Write-Host "." -ForegroundColor DarkGray -NoNewline

    # 检查进程是否还在
    if ($backendProc.HasExited) {
        Write-Host ""
        Write-Fail "后端进程已退出, 请检查日志: $backendLog"
        exit 1
    }

    try {
        $resp = Invoke-WebRequest -Uri "http://localhost:$BACKEND_PORT/api/category/list" `
            -TimeoutSec 3 -UseBasicParsing -ErrorAction Stop
        if ($resp.StatusCode -eq 200) {
            $backendReady = $true
            break
        }
    } catch { }
}

Write-Host ""
if ($backendReady) {
    Write-Ok "后端服务已就绪 (${elapsed}s, PID: $($backendProc.Id))"
} else {
    Write-Warn "后端服务启动超时(${maxWait}s), 请检查日志: $backendLog"
}

# --- 启动网页端 ---
Write-Host "  启动网页端 (端口 $WEB_PORT)..." -ForegroundColor White

$webLog = Join-Path $LOG_DIR "web.log"
$webProc = Start-Process -FilePath "npm" `
    -ArgumentList "run", "dev" `
    -WorkingDirectory $WEB_DIR `
    -RedirectStandardOutput $webLog `
    -RedirectStandardError (Join-Path $LOG_DIR "web-err.log") `
    -PassThru -NoNewWindow

$webProc.Id | Set-Content (Join-Path $PID_DIR "web.pid")
Write-Ok "网页端已启动 (PID: $($webProc.Id))"

# --- 启动管理后台 ---
Write-Host "  启动管理后台 (端口 $ADMIN_PORT)..." -ForegroundColor White

$adminLog = Join-Path $LOG_DIR "admin.log"
$adminProc = Start-Process -FilePath "npm" `
    -ArgumentList "run", "dev" `
    -WorkingDirectory $ADMIN_DIR `
    -RedirectStandardOutput $adminLog `
    -RedirectStandardError (Join-Path $LOG_DIR "admin-err.log") `
    -PassThru -NoNewWindow

$adminProc.Id | Set-Content (Join-Path $PID_DIR "admin.pid")
Write-Ok "管理后台已启动 (PID: $($adminProc.Id))"

# --- 启动小程序端 ---
Write-Host "  启动小程序端 (端口 $MINI_PORT)..." -ForegroundColor White

$miniLog = Join-Path $LOG_DIR "mini.log"
$miniProc = Start-Process -FilePath "npm" `
    -ArgumentList "run", "dev:h5" `
    -WorkingDirectory $MINI_DIR `
    -RedirectStandardOutput $miniLog `
    -RedirectStandardError (Join-Path $LOG_DIR "mini-err.log") `
    -PassThru -NoNewWindow

$miniProc.Id | Set-Content (Join-Path $PID_DIR "mini.pid")
Write-Ok "小程序端已启动 (PID: $($miniProc.Id))"

# 等待前端就绪
Start-Sleep -Seconds 3

# ======================== [5/5] 状态汇总 ========================
Write-Host ""
Write-Host "  [5/5] 服务状态汇总" -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"
Write-Host ""
Write-Host "  ┌────────────────────────────────────────────────────────────────┐" -ForegroundColor Cyan
Write-Host "  │  服务            端口    PID       日志文件                    │" -ForegroundColor Cyan
Write-Host "  ├────────────────────────────────────────────────────────────────┤" -ForegroundColor Cyan
Write-Host ("  │  后端服务        {0,-5}   {1,-8}  .logs/backend.log          │" -f $BACKEND_PORT, $backendProc.Id) -ForegroundColor Cyan
Write-Host ("  │  网页端          {0,-5}   {1,-8}  .logs/web.log              │" -f $WEB_PORT, $webProc.Id) -ForegroundColor Cyan
Write-Host ("  │  管理后台        {0,-5}   {1,-8}  .logs/admin.log            │" -f $ADMIN_PORT, $adminProc.Id) -ForegroundColor Cyan
Write-Host ("  │  小程序端        {0,-5}   {1,-8}  .logs/mini.log             │" -f $MINI_PORT, $miniProc.Id) -ForegroundColor Cyan
Write-Host "  └────────────────────────────────────────────────────────────────┘" -ForegroundColor Cyan
Write-Host ""
Write-Host "  访问地址:" -ForegroundColor White
Write-Host "    网页端:    http://localhost:$WEB_PORT" -ForegroundColor Green
Write-Host "    管理后台:  http://localhost:$ADMIN_PORT" -ForegroundColor Green
Write-Host "    小程序端:  http://localhost:$MINI_PORT" -ForegroundColor Green
Write-Host ""
Write-Host "  测试账号: test001/123456 (用户)  admin/123456 (管理员)" -ForegroundColor White
Write-Host ""
Write-Host "  查看实时日志:" -ForegroundColor White
Write-Host "    Get-Content .logs\backend.log -Tail 20 -Wait" -ForegroundColor DarkGray
Write-Host "    Get-Content .logs\web.log -Tail 20 -Wait" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  关闭所有服务: .\stop.ps1" -ForegroundColor Yellow
Write-Host ""
