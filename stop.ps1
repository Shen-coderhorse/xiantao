<#
.SYNOPSIS
    闲淘二手交易平台 - 一键关闭脚本 (PowerShell)
.DESCRIPTION
    在当前终端窗口内关闭所有服务，使用进程树杀确保子进程全部清理。
    关闭顺序: 小程序(5175) → 管理后台(5174) → 网页端(5173) → 后端(8080)
#>

$ErrorActionPreference = "Continue"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# ======================== 路径与端口 ========================
$PROJECT_ROOT = $PSScriptRoot
$PID_DIR      = Join-Path $PROJECT_ROOT ".pids"

$BACKEND_PORT = 8080
$WEB_PORT     = 5173
$ADMIN_PORT   = 5174
$MINI_PORT    = 5175

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

function Stop-ServiceByPid([int]$TargetPid, [string]$Name) {
    # 检查进程是否存在
    $proc = Get-Process -Id $TargetPid -ErrorAction SilentlyContinue
    if (-not $proc) {
        return "not_found"
    }

    # 第一步：尝试优雅关闭（taskkill 不带 /F 发送 WM_CLOSE）
    & taskkill /PID $TargetPid /T 2>$null | Out-Null

    # 等待进程退出（最多 8 秒）
    for ($i = 0; $i -lt 8; $i++) {
        Start-Sleep -Seconds 1
        $proc = Get-Process -Id $TargetPid -ErrorAction SilentlyContinue
        if (-not $proc) {
            return "graceful"
        }
    }

    # 第二步：强制杀死进程树
    & taskkill /PID $TargetPid /T /F 2>$null | Out-Null
    Start-Sleep -Seconds 1

    $proc = Get-Process -Id $TargetPid -ErrorAction SilentlyContinue
    if (-not $proc) {
        return "forced"
    }

    return "failed"
}

function Stop-Service([string]$PidFile, [string]$Name, [int]$Port) {
    $pidPath = Join-Path $PID_DIR "$PidFile.pid"
    $procId = $null

    # 尝试从 PID 文件读取
    if (Test-Path $pidPath) {
        $procId = Get-Content $pidPath -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($procId) { $procId = [int]$procId }
    }

    # 如果 PID 文件不存在或进程已死，尝试通过端口查找
    if (-not $procId) {
        $procId = Get-PortPid $Port
    }

    if (-not $procId) {
        Write-Skip "$Name (端口 $Port) - 未运行"
        # 清理可能残留的 PID 文件
        if (Test-Path $pidPath) { Remove-Item $pidPath -Force }
        return
    }

    Write-Host "  关闭 $Name (PID: $procId, 端口 $Port)..." -ForegroundColor White -NoNewline

    $result = Stop-ServiceByPid $procId $Name

    switch ($result) {
        "graceful" {
            Write-Host " 已关闭" -ForegroundColor Green
            $script:totalKilled++
        }
        "forced" {
            Write-Host " 已强制关闭" -ForegroundColor Yellow
            $script:totalKilled++
        }
        "failed" {
            Write-Host " 无法关闭!" -ForegroundColor Red
            $script:remaining++
        }
        "not_found" {
            Write-Host " 进程已不存在" -ForegroundColor DarkGray
            $script:totalKilled++
        }
    }

    # 清理 PID 文件
    if (Test-Path $pidPath) { Remove-Item $pidPath -Force }
}

# ======================== 标题 ========================
Write-Host ""
Write-Host "  ================================================" -ForegroundColor Cyan
Write-Host "       闲淘二手交易平台 - 一键关闭脚本" -ForegroundColor Cyan
Write-Host "       XianTao Second-Hand Trading Platform" -ForegroundColor Cyan
Write-Host "  ================================================" -ForegroundColor Cyan
Write-Host ""

$script:totalKilled = 0
$script:remaining = 0

# ======================== [1/3] 关闭前端服务 ========================
Write-Host "  [1/3] 关闭前端服务..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

Stop-Service "mini"  "小程序端"  $MINI_PORT
Stop-Service "admin" "管理后台"  $ADMIN_PORT
Stop-Service "web"   "网页端"    $WEB_PORT

Write-Host ""

# ======================== [2/3] 关闭后端服务 ========================
Write-Host "  [2/3] 关闭后端服务..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

Stop-Service "backend" "后端服务" $BACKEND_PORT

Write-Host ""

# ======================== [3/3] 验证清理 ========================
Write-Host "  [3/3] 验证端口释放..." -ForegroundColor Cyan
Write-Host "  ─────────────────────────────────────────────────"

# 等待端口释放
Start-Sleep -Seconds 2

$verifyPorts = @(
    @{ Port = $MINI_PORT;    Name = "小程序端" },
    @{ Port = $ADMIN_PORT;   Name = "管理后台" },
    @{ Port = $WEB_PORT;     Name = "网页端" },
    @{ Port = $BACKEND_PORT; Name = "后端服务" }
)

foreach ($vp in $verifyPorts) {
    if (Test-PortInUse $vp.Port) {
        $retryId = Get-PortPid $vp.Port
        Write-Warn "端口 $($vp.Port) 仍被占用 ($($vp.Name), PID: $retryId)"

        # 最后尝试直接杀
        if ($retryId) {
            Write-Host "    尝试再次关闭 PID $retryId..." -ForegroundColor DarkGray -NoNewline
            & taskkill /PID $retryId /T /F 2>$null | Out-Null
            Start-Sleep -Seconds 2
            if (-not (Test-PortInUse $vp.Port)) {
                Write-Host " 已关闭" -ForegroundColor Green
                $script:remaining--
            } else {
                Write-Host " 仍无法关闭" -ForegroundColor Red
            }
        }
    } else {
        Write-Ok "端口 $($vp.Port) 已释放 ($($vp.Name))"
    }
}

# 清理空的 PID 目录
if ((Test-Path $PID_DIR) -and ((Get-ChildItem $PID_DIR -ErrorAction SilentlyContinue).Count -eq 0)) {
    Remove-Item $PID_DIR -Force -ErrorAction SilentlyContinue
}

Write-Host ""

# ======================== 结果汇总 ========================
if ($script:remaining -le 0) {
    Write-Host "  ================================================" -ForegroundColor Green
    Write-Host "       所有服务已成功关闭" -ForegroundColor Green
    Write-Host "  ================================================" -ForegroundColor Green
} else {
    Write-Host "  ================================================" -ForegroundColor Yellow
    Write-Host "  [WARN] 仍有 $($script:remaining) 个端口未释放" -ForegroundColor Yellow
    Write-Host "  请使用 Get-Process 和 Stop-Process 手动关闭残留进程" -ForegroundColor Yellow
    Write-Host "  ================================================" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "  共关闭 $($script:totalKilled) 个服务" -ForegroundColor White
Write-Host ""
