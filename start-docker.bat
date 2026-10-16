@echo off
chcp 936 >nul
setlocal EnableExtensions
pushd "%~dp0"
title 710号汽车之家 - Docker 一键部署

echo ============================================================
echo    710号汽车之家   Docker 一键部署
echo ============================================================
echo.

where docker >nul 2>nul
if errorlevel 1 goto :nodocker

docker info >nul 2>nul
if errorlevel 1 goto :nodockerrun

if not exist ".env" if exist "deploy\.env.example" (
  echo 未发现 .env, 正在从 deploy\.env.example 生成 ...
  copy /y "deploy\.env.example" ".env" >nul
)

echo 正在构建并启动, 首次较慢, 请耐心等待 ...
docker compose up -d --build
if errorlevel 1 (
  echo.
  echo [错误] 启动失败, 请查看上面的输出.
  goto :done
)

echo.
echo ============================================================
echo    部署完成
echo ------------------------------------------------------------
echo    后台管理   http://localhost:8081
echo    品牌官网   http://localhost:8082
echo    H5 移动端  http://localhost:8083
echo    后端 API   http://localhost:8080
echo    RabbitMQ   http://localhost:15672
echo    MinIO      http://localhost:9001
echo ------------------------------------------------------------
echo    查看日志: docker compose logs -f
echo    停止服务: docker compose down
echo ============================================================
goto :done

:nodocker
echo [错误] 未检测到 docker 命令. 请先安装并启动 Docker Desktop 后重试.
goto :done

:nodockerrun
echo [错误] Docker 未运行. 请先启动 Docker Desktop, 待其就绪后重试.
goto :done

:done
echo.
pause
popd
endlocal
exit /b 0
