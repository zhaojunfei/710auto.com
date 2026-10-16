@echo off
chcp 936 >nul
setlocal EnableExtensions EnableDelayedExpansion
pushd "%~dp0"
title 710号汽车之家 - 一键启动

set "ROOT=%CD%"
set "JAR=%ROOT%\backend\target\auto710-backend.jar"

echo ============================================================
echo    710号汽车之家   一键启动 - 开发模式
echo    项目目录: %ROOT%
echo ============================================================
echo.

where java >nul 2>nul
if errorlevel 1 goto :nojava
where node >nul 2>nul
if errorlevel 1 goto :nonode

if /i "%~1"=="build" if exist "%JAR%" del /q "%JAR%"
if exist "%JAR%" goto :builded
echo [准备] 未发现后端 jar, 正在构建, 首次较慢 ...
call :buildBackend
if errorlevel 1 goto :done
:builded

echo.
echo [1/4] 后端 API .............. 端口 8080
call :portbusy 8080
if defined BUSY goto :skipbe
start "710-houduan-8080" /D "%ROOT%\backend" cmd /k java -jar target\auto710-backend.jar
goto :afterbe
:skipbe
echo       端口 8080 已被占用, 跳过. 后端可能已在运行.
:afterbe

echo [2/4] PC 统一后台 ........... 端口 5173
call :startFront "710-houtai-5173" "web-admin" 5173

echo [3/4] PC 品牌官网 ........... 端口 5174
call :startFront "710-guanwang-5174" "web-www" 5174

echo [4/4] H5 移动端 ............. 端口 5175
call :startFront "710-h5-5175" "h5" 5175

echo.
echo 正在等待服务就绪, 约 25 秒 ...
ping -n 26 127.0.0.1 >nul
start "" http://localhost:5173

echo.
echo ============================================================
echo    启动完成. 每个服务一个独立窗口, 关闭窗口即停止该服务.
echo ------------------------------------------------------------
echo    后台管理   http://localhost:5173         admin / admin123
echo    品牌官网   http://localhost:5174
echo    H5 移动端  http://localhost:5175
echo    技师端     http://localhost:5175/tech/login       tech01 / admin123
echo    供应商端   http://localhost:5175/supplier/login   supplier01 / admin123
echo    接口文档   http://localhost:8080/swagger-ui.html
echo ------------------------------------------------------------
echo    停止全部     : 双击 stop.bat
echo    重新构建后端 : start.bat build
echo    前端首次运行会先 npm install, 请稍等片刻
echo ============================================================
echo.
pause
popd
endlocal
exit /b 0

:buildBackend
pushd "%ROOT%\backend"
call mvn -DskipTests package
set "RC=%errorlevel%"
popd
if not "%RC%"=="0" echo [错误] 后端构建失败, 请检查上面的 Maven 输出.
exit /b %RC%

:startFront
set "FTITLE=%~1"
set "FDIR=%~2"
set "FPORT=%~3"
call :portbusy %FPORT%
if defined BUSY goto :skipfront
if not exist "%ROOT%\%FDIR%" goto :skipfront
if not exist "%ROOT%\%FDIR%\node_modules" (
  echo       %FDIR% 首次运行, 正在安装依赖 npm install ...
  pushd "%ROOT%\%FDIR%"
  call npm install
  popd
)
start "%FTITLE%" /D "%ROOT%\%FDIR%" cmd /k npm run dev
exit /b 0
:skipfront
echo       端口 %FPORT% 已被占用或无此目录, 跳过 %FDIR%.
exit /b 0

:portbusy
set "BUSY="
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /c:":%~1" ^| findstr /c:"LISTENING"') do set "BUSY=1"
exit /b 0

:nojava
echo [错误] 未找到 java. 请安装 JDK 17 并将 bin 目录加入 PATH 后重试.
goto :done

:nonode
echo [错误] 未找到 node. 请安装 Node 18+ 并将 bin 目录加入 PATH 后重试.
goto :done

:done
echo.
pause
popd
endlocal
exit /b 1
