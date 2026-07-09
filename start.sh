#!/usr/bin/env bash
# ============================================================
# 闲淘二手交易平台 - 一键启动脚本 (Linux/macOS)
# 按顺序启动: 后端(8080) → 网页端(5173) → 管理后台(5174) → 小程序(5175)
# ============================================================

set -e

# 获取脚本所在目录（项目根目录）
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$PROJECT_ROOT/.pids"
LOG_DIR="$PROJECT_ROOT/.logs"

# 端口定义
BACKEND_PORT=8080
WEB_PORT=5173
ADMIN_PORT=5174
MINI_PORT=5175

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║     闲淘二手交易平台 - 一键启动脚本             ║${NC}"
echo -e "${CYAN}║     XianTao Second-Hand Trading Platform         ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "  项目目录: ${BLUE}$PROJECT_ROOT${NC}"
echo ""

# 创建 PID 和日志目录
mkdir -p "$PID_DIR" "$LOG_DIR"

# ======================== 辅助函数 ========================

# 检查端口是否被占用
check_port() {
    local port=$1
    local name=$2
    if command -v lsof &>/dev/null; then
        if lsof -i ":$port" -sTCP:LISTEN &>/dev/null; then
            local pid=$(lsof -t -i ":$port" -sTCP:LISTEN 2>/dev/null | head -1)
            echo -e "  ${RED}[FAIL]${NC} 端口 $port 已被占用 (PID: $pid, $name)"
            return 1
        fi
    elif command -v ss &>/dev/null; then
        if ss -tlnp | grep -q ":$port "; then
            echo -e "  ${RED}[FAIL]${NC} 端口 $port 已被占用 ($name)"
            return 1
        fi
    elif command -v netstat &>/dev/null; then
        if netstat -tlnp 2>/dev/null | grep -q ":$port "; then
            echo -e "  ${RED}[FAIL]${NC} 端口 $port 已被占用 ($name)"
            return 1
        fi
    fi
    echo -e "  ${GREEN}[ OK ]${NC} 端口 $port 可用 ($name)"
    return 0
}

# 等待后端就绪
wait_for_backend() {
    local max_wait=90
    local elapsed=0
    echo -n "  等待后端服务就绪"
    while [ $elapsed -lt $max_wait ]; do
        if curl -s -o /dev/null -w "%{http_code}" "http://localhost:$BACKEND_PORT/api/category/list" 2>/dev/null | grep -q "200"; then
            echo ""
            echo -e "  ${GREEN}[ OK ]${NC} 后端服务已就绪 (${elapsed}s)"
            return 0
        fi
        sleep 2
        elapsed=$((elapsed + 2))
        echo -n "."
    done
    echo ""
    echo -e "  ${YELLOW}[WARN]${NC} 后端服务启动超时(${max_wait}s), 请检查日志: $LOG_DIR/backend.log"
    return 1
}

# ======================== 环境检查 ========================
echo -e "  ${CYAN}[1/5]${NC} 检查运行环境..."
echo "  ─────────────────────────────────────────────────"

ENV_OK=1

# 检查 JDK
if command -v java &>/dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -1 | awk -F '"' '{print $2}')
    echo -e "  ${GREEN}[ OK ]${NC} JDK 已安装: $JAVA_VER"
else
    echo -e "  ${RED}[FAIL]${NC} JDK 未安装或未配置到 PATH"
    ENV_OK=0
fi

# 检查 Maven
if command -v mvn &>/dev/null; then
    MVN_VER=$(mvn -version 2>&1 | head -1 | awk '{print $3}')
    echo -e "  ${GREEN}[ OK ]${NC} Maven 已安装: $MVN_VER"
else
    echo -e "  ${RED}[FAIL]${NC} Maven 未安装或未配置到 PATH"
    ENV_OK=0
fi

# 检查 Node.js
if command -v node &>/dev/null; then
    NODE_VER=$(node -v)
    echo -e "  ${GREEN}[ OK ]${NC} Node.js 已安装: $NODE_VER"
else
    echo -e "  ${RED}[FAIL]${NC} Node.js 未安装或未配置到 PATH"
    ENV_OK=0
fi

# 检查 npm
if command -v npm &>/dev/null; then
    NPM_VER=$(npm -v)
    echo -e "  ${GREEN}[ OK ]${NC} npm 已安装: $NPM_VER"
else
    echo -e "  ${RED}[FAIL]${NC} npm 未安装或未配置到 PATH"
    ENV_OK=0
fi

# 检查 curl (用于健康检查)
if ! command -v curl &>/dev/null; then
    echo -e "  ${YELLOW}[WARN]${NC} curl 未安装, 将跳过健康检查"
fi

# 检查 MySQL
if command -v mysql &>/dev/null; then
    echo -e "  ${GREEN}[ OK ]${NC} MySQL 客户端可用"
else
    echo -e "  ${YELLOW}[WARN]${NC} mysql 命令不可用, 请确保 MySQL 已启动且端口 3306 可访问"
fi

if [ "$ENV_OK" -eq 0 ]; then
    echo ""
    echo -e "  ${RED}[ERROR]${NC} 环境检查未通过, 请先安装缺失的组件"
    echo ""
    exit 1
fi

echo ""

# ======================== 端口检查 ========================
echo -e "  ${CYAN}[2/5]${NC} 检查端口占用..."
echo "  ─────────────────────────────────────────────────"

PORT_CONFLICT=0
check_port $BACKEND_PORT "后端服务" || PORT_CONFLICT=1
check_port $WEB_PORT "网页端" || PORT_CONFLICT=1
check_port $ADMIN_PORT "管理后台" || PORT_CONFLICT=1
check_port $MINI_PORT "小程序端" || PORT_CONFLICT=1

if [ "$PORT_CONFLICT" -eq 1 ]; then
    echo ""
    echo -e "  ${RED}[ERROR]${NC} 存在端口冲突, 请先运行 ./stop.sh 关闭已有进程"
    echo ""
    exit 1
fi

echo -e "  所有端口均可用"
echo ""

# ======================== 安装依赖 ========================
echo -e "  ${CYAN}[3/5]${NC} 检查前端依赖..."
echo "  ─────────────────────────────────────────────────"

for dir_name in "xiantao-web" "xiantao-admin" "xiantao-miniprogram"; do
    dir="$PROJECT_ROOT/$dir_name"
    if [ ! -d "$dir/node_modules" ]; then
        echo "  安装 $dir_name 依赖..."
        (cd "$dir" && npm install --loglevel=error)
    fi
done

echo "  依赖检查完成"
echo ""

# ======================== 启动服务 ========================
echo -e "  ${CYAN}[4/5]${NC} 启动所有服务..."
echo "  ─────────────────────────────────────────────────"

# 启动后端
echo "  启动后端服务 (端口 $BACKEND_PORT)..."
(cd "$PROJECT_ROOT/xiantao-server" && nohup mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &)
BACKEND_PID=$!
echo $BACKEND_PID > "$PID_DIR/backend.pid"

# 等待后端就绪
if wait_for_backend; then
    echo -e "  ${GREEN}[ OK ]${NC} 后端服务已启动 (PID: $BACKEND_PID)"
else
    echo -e "  ${YELLOW}[WARN]${NC} 后端可能未完全就绪, 请检查日志"
fi

# 启动网页端
echo "  启动网页端 (端口 $WEB_PORT)..."
(cd "$PROJECT_ROOT/xiantao-web" && nohup npm run dev > "$LOG_DIR/web.log" 2>&1 &)
WEB_PID=$!
echo $WEB_PID > "$PID_DIR/web.pid"
echo -e "  ${GREEN}[ OK ]${NC} 网页端已启动 (PID: $WEB_PID)"

# 启动管理后台
echo "  启动管理后台 (端口 $ADMIN_PORT)..."
(cd "$PROJECT_ROOT/xiantao-admin" && nohup npm run dev > "$LOG_DIR/admin.log" 2>&1 &)
ADMIN_PID=$!
echo $ADMIN_PID > "$PID_DIR/admin.pid"
echo -e "  ${GREEN}[ OK ]${NC} 管理后台已启动 (PID: $ADMIN_PID)"

# 启动小程序端
echo "  启动小程序端 (端口 $MINI_PORT)..."
(cd "$PROJECT_ROOT/xiantao-miniprogram" && nohup npm run dev:h5 > "$LOG_DIR/mini.log" 2>&1 &)
MINI_PID=$!
echo $MINI_PID > "$PID_DIR/mini.pid"
echo -e "  ${GREEN}[ OK ]${NC} 小程序端已启动 (PID: $MINI_PID)"

# 等待前端启动
sleep 3

# ======================== 状态汇总 ========================
echo ""
echo -e "  ${CYAN}[5/5]${NC} 服务状态汇总"
echo "  ─────────────────────────────────────────────────"
echo ""
echo -e "  ${CYAN}╔══════════════════════════════════════════════════╗${NC}"
echo -e "  ${CYAN}║  服务            端口    PID      日志文件       ║${NC}"
echo -e "  ${CYAN}╠══════════════════════════════════════════════════╣${NC}"
printf "  ${CYAN}║${NC}  后端服务        8080    %-8s backend.log       ${CYAN}║${NC}\n" "$BACKEND_PID"
printf "  ${CYAN}║${NC}  网页端          5173    %-8s web.log           ${CYAN}║${NC}\n" "$WEB_PID"
printf "  ${CYAN}║${NC}  管理后台        5174    %-8s admin.log         ${CYAN}║${NC}\n" "$ADMIN_PID"
printf "  ${CYAN}║${NC}  小程序端        5175    %-8s mini.log          ${CYAN}║${NC}\n" "$MINI_PID"
echo -e "  ${CYAN}╚══════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "  访问地址:"
echo -e "    网页端:    ${GREEN}http://localhost:$WEB_PORT${NC}"
echo -e "    管理后台:  ${GREEN}http://localhost:$ADMIN_PORT${NC}"
echo -e "    小程序端:  ${GREEN}http://localhost:$MINI_PORT${NC}"
echo ""
echo -e "  测试账号: test001/123456 (用户)  admin/123456 (管理员)"
echo ""
echo -e "  查看日志:  tail -f $LOG_DIR/*.log"
echo -e "  关闭服务:  ${YELLOW}./stop.sh${NC}"
echo ""
