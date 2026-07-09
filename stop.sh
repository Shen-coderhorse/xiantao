#!/usr/bin/env bash
# ============================================================
# 闲淘二手交易平台 - 一键关闭脚本 (Linux/macOS)
# 按顺序关闭: 小程序(5175) → 管理后台(5174) → 网页端(5173) → 后端(8080)
# ============================================================

# 获取脚本所在目录（项目根目录）
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$PROJECT_ROOT/.pids"

# 端口定义
BACKEND_PORT=8080
WEB_PORT=5173
ADMIN_PORT=5174
MINI_PORT=5175

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║     闲淘二手交易平台 - 一键关闭脚本             ║${NC}"
echo -e "${CYAN}║     XianTao Second-Hand Trading Platform         ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════╝${NC}"
echo ""

TOTAL_KILLED=0
REMAINING=0

# ======================== 辅助函数 ========================

# 通过 PID 文件关闭服务
stop_by_pid() {
    local pid_file="$1"
    local name="$2"
    local port="$3"
    local service_name="$pid_file"

    if [ ! -f "$PID_DIR/$pid_file.pid" ]; then
        # 尝试通过端口查找
        local port_pid=""
        if command -v lsof &>/dev/null; then
            port_pid=$(lsof -t -i ":$port" -sTCP:LISTEN 2>/dev/null | head -1)
        elif command -v fuser &>/dev/null; then
            port_pid=$(fuser "$port/tcp" 2>/dev/null | awk '{print $1}')
        fi

        if [ -z "$port_pid" ]; then
            echo -e "  ${YELLOW}[SKIP]${NC} $name (端口 $port) - 未运行"
            return 0
        fi

        _kill_process "$port_pid" "$name" "$port"
        return $?
    fi

    local pid=$(cat "$PID_DIR/$pid_file.pid" 2>/dev/null)

    if [ -z "$pid" ]; then
        echo -e "  ${YELLOW}[SKIP]${NC} $name - PID 文件为空"
        rm -f "$PID_DIR/$pid_file.pid"
        return 0
    fi

    # 检查进程是否存在
    if ! kill -0 "$pid" 2>/dev/null; then
        echo -e "  ${YELLOW}[SKIP]${NC} $name (PID: $pid) - 进程已不存在"
        rm -f "$PID_DIR/$pid_file.pid"
        return 0
    fi

    _kill_process "$pid" "$name" "$port"
    local result=$?

    # 清理 PID 文件
    rm -f "$PID_DIR/$pid_file.pid"

    return $result
}

# 杀死进程（优雅 → 强制）
_kill_process() {
    local pid=$1
    local name=$2
    local port=$3

    echo -n "  关闭 $name (PID: $pid, 端口 $port)..."

    # 第一步：发送 SIGTERM（优雅关闭）
    kill "$pid" 2>/dev/null

    # 等待进程退出（最多 8 秒）
    local wait=0
    while [ $wait -lt 8 ]; do
        if ! kill -0 "$pid" 2>/dev/null; then
            echo -e " ${GREEN}[ OK ]${NC} 已关闭 (${wait}s)"
            TOTAL_KILLED=$((TOTAL_KILLED + 1))
            return 0
        fi
        sleep 1
        wait=$((wait + 1))
    done

    # 第二步：发送 SIGKILL（强制杀死）
    echo -n " 强制终止..."
    kill -9 "$pid" 2>/dev/null
    sleep 1

    if ! kill -0 "$pid" 2>/dev/null; then
        echo -e " ${GREEN}[ OK ]${NC} 已强制关闭"
        TOTAL_KILLED=$((TOTAL_KILLED + 1))
        return 0
    else
        echo -e " ${RED}[FAIL]${NC} 无法关闭 (PID: $pid)"
        REMAINING=$((REMAINING + 1))
        return 1
    fi
}

# 通过端口关闭服务（备用方案）
stop_by_port() {
    local port=$1
    local name=$2
    local pid=""

    if command -v lsof &>/dev/null; then
        pid=$(lsof -t -i ":$port" -sTCP:LISTEN 2>/dev/null | head -1)
    elif command -v fuser &>/dev/null; then
        pid=$(fuser "$port/tcp" 2>/dev/null | awk '{print $1}')
    elif command -v ss &>/dev/null; then
        pid=$(ss -tlnp 2>/dev/null | grep ":$port " | grep -oP 'pid=\K[0-9]+' | head -1)
    fi

    if [ -z "$pid" ]; then
        return 1
    fi

    _kill_process "$pid" "$name" "$port"
}

# 验证端口已释放
verify_port() {
    local port=$1
    local name=$2

    if command -v lsof &>/dev/null; then
        if lsof -i ":$port" -sTCP:LISTEN &>/dev/null; then
            echo -e "  ${YELLOW}[WARN]${NC} 端口 $port 仍被占用 ($name)"
            REMAINING=$((REMAINING + 1))
            return 1
        fi
    elif command -v ss &>/dev/null; then
        if ss -tlnp 2>/dev/null | grep -q ":$port "; then
            echo -e "  ${YELLOW}[WARN]${NC} 端口 $port 仍被占用 ($name)"
            REMAINING=$((REMAINING + 1))
            return 1
        fi
    fi

    echo -e "  ${GREEN}[ OK ]${NC} 端口 $port 已释放 ($name)"
    return 0
}

# ======================== 关闭前端服务 ========================
echo -e "  ${CYAN}[1/3]${NC} 关闭前端服务..."
echo "  ─────────────────────────────────────────────────"

stop_by_pid "mini" "小程序端" "$MINI_PORT" || stop_by_port 5175 "小程序端"
stop_by_pid "admin" "管理后台" "$ADMIN_PORT" || stop_by_port 5174 "管理后台"
stop_by_pid "web" "网页端" "$WEB_PORT" || stop_by_port 5173 "网页端"

echo ""

# ======================== 关闭后端服务 ========================
echo -e "  ${CYAN}[2/3]${NC} 关闭后端服务..."
echo "  ─────────────────────────────────────────────────"

stop_by_pid "backend" "后端服务" "$BACKEND_PORT" || stop_by_port 8080 "后端服务"

echo ""

# ======================== 验证清理 ========================
echo -e "  ${CYAN}[3/3]${NC} 验证进程清理..."
echo "  ─────────────────────────────────────────────────"

verify_port 5175 "小程序端"
verify_port 5174 "管理后台"
verify_port 5173 "网页端"
verify_port 8080 "后端服务"

# 清理 PID 目录
if [ -d "$PID_DIR" ] && [ -z "$(ls -A "$PID_DIR" 2>/dev/null)" ]; then
    rmdir "$PID_DIR" 2>/dev/null
fi

echo ""

# ======================== 结果汇总 ========================
if [ "$REMAINING" -eq 0 ]; then
    echo -e "  ${CYAN}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "  ${CYAN}║  所有服务已成功关闭                             ║${NC}"
    echo -e "  ${CYAN}╚══════════════════════════════════════════════════╝${NC}"
else
    echo -e "  ${YELLOW}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "  ${YELLOW}║  [WARN] 仍有 $REMAINING 个端口未释放                         ║${NC}"
    echo -e "  ${YELLOW}║  请使用 ps aux 和 kill 手动关闭残留进程          ║${NC}"
    echo -e "  ${YELLOW}╚══════════════════════════════════════════════════╝${NC}"
fi

echo ""
echo "  共关闭 $TOTAL_KILLED 个进程"
echo ""
