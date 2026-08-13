# AGENTS.md — 闲淘 XianTao 项目 Agent 作业指南

> 本文件是编码 Agent 在本仓库工作的**权威上下文入口**。深度文档见 [README.md](README.md) 与
> `.qoder/repowiki/`（架构、API、数据库、编码规范）。修改前先读本文件，保持在既定范围与约定内。

## 1. 项目意图与模块边界

闲淘是校园二手交易平台，多前端 + 单体后端：

| 模块 | 目录 | 技术栈 | 端口 |
|------|------|--------|------|
| 后端单体 | `xiantao-server` | Spring Boot 3.2.3 / Java 21 / MyBatis-Plus / MySQL 8 / Flyway | 8080 |
| 网页端 | `xiantao-web` | Vue 3 + Element Plus + Vite | 5173 |
| 管理后台 | `xiantao-admin` | Vue 3 + Element Plus + ECharts + Vite | 5174 |
| 小程序端(H5) | `xiantao-miniprogram` | uni-app + Vue 3 + Vite（**唯一入口在 `src/`**） | 5175 |

- 后端分层：`controller → service(impl) → mapper(MyBatis-Plus) → entity`；VO/DTO 严格区分出入参。
- 统一响应体 `common/Result`；统一异常 `common/BusinessException` + 全局异常处理。
- 鉴权：JWT（`utils/JwtUtils` + `config/JwtInterceptor`）；`/api/admin/**` 走 `AdminInterceptor`；
  `/api/upload/**`、登录注册、商品公开查询在 `WebConfig` 的 `excludePathPatterns` 中放行。

## 2. 启动与环境（可复现）

1. **配置本地敏感信息（必需，前置）**：数据源账号与 `jwt.secret` 已从 `application.yml` 外置。
   复制 `xiantao-server/application-local.yml.example` → `application-local.yml`（已 gitignore），
   或设环境变量 `XIANTAO_DB_USERNAME` / `XIANTAO_DB_PASSWORD` / `XIANTAO_JWT_SECRET`。
2. **数据库**：无需手动执行 SQL。应用启动时 Flyway 自动执行 `xiantao-server/src/main/resources/db/migration/`
   下的版本化迁移（`V1__init_schema.sql`），JDBC `createDatabaseIfNotExist=true` 自动建库；
   已有数据的库会被自动**基线化**（不重跑 V1 的建表语句，数据安全）。容器/手动初始化仍可用
   `xiantao-server/sql/init.sql`（docker-compose 经 `docker-entrypoint-initdb.d` 挂载）。
3. **一键启停**：`./start.ps1` / `./stop.ps1`（Windows），`./start.sh` / `./stop.sh`（Linux/macOS）。
   脚本做环境检查、端口检查、后台启动、健康探针、PID/日志落盘（`.pids/`、`.logs/`，均 gitignore）。

## 3. 验收与验证门禁（改动前后必过）

任何后端逻辑或前端改动，交付前必须本地跑通对应门禁：

| 改动范围 | 门禁命令 | 通过标准 |
|----------|----------|----------|
| 后端 `xiantao-server` | `mvn -q test`（在 `xiantao-server` 目录） | 退出码 0、测试全绿 |
| 任一前端 | `npm run build`（在对应目录） | 构建成功、无 error |

- **机械触发点**：CI 工作流 [.github/workflows/ci.yml](.github/workflows/ci.yml) 在 push/PR 上运行同一组命令
  （后端 `mvn -q test` + 三前端 `npm run build`）——它是最终的可复现验收权威。
- **本地快速反馈**：版本化 git hooks 在 `.githooks/`。一次性激活：`git config core.hooksPath .githooks`。
  `pre-push` 运行完整门禁（`mvn test` + 三前端 build）；`pre-commit` 阻止提交 `application-local.yml`
  与明文密钥。也可直接运行 `bash .githooks/pre-push` 手动自检。

## 4. 变更范围与安全约定

- **范围**：改动限定在任务相关模块；跨模块或引入依赖前先说明。提交前 `git status` 自查，勿混入无关变更。
- **禁止入库**：`node_modules/`、`dist/`、`application-local.yml`、密钥/口令。已在 `.gitignore` 覆盖。
- **数据库变更**：新增 schema/数据一律**新增** `V2__*.sql`、`V3__*.sql` 迁移，**禁止**修改已应用的
  `V1__init_schema.sql`；如需同步 docker 引导，一并更新 `sql/init.sql`。
- **敏感配置**：只写占位符/`.example`；真实值走 `application-local.yml` 或环境变量。JWT 密钥泄露需轮换。
- **上传**：`upload.path` 为相对启动目录的 `../uploads`；写文件时须用绝对路径（见 `UploadController`
  已处理 `MultipartFile.transferTo` 的相对路径陷阱）。

## 5. 回滚与恢复

- **代码**：`git revert <sha>`；工作区脏时先 `git stash`。
- **数据库**：Flyway 采用**向前修复**（新增补偿迁移），不手改历史迁移；严重时用 `init.sql` 重建后按序迁移。
- **服务**：`./stop.ps1`（优雅 8s → 强制），再 `./start.ps1` 重启；健康检查失败查 `.logs/backend-err.log`。

## 6. 文档与知识同步

- 接口变更同步 [闲淘平台-API接口文档.md](闲淘平台-API接口文档.md)；架构/规范深度见 `.qoder/repowiki/knowledge/zh/`。
- 完成一个有复用价值的修复/踩坑后，在 `.qoder/repowiki/` 沉淀精简知识条目，避免同一问题重复排查。
