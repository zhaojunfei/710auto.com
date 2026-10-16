@echo off
chcp 936 >nul
setlocal EnableExtensions EnableDelayedExpansion
title 710号汽车之家 - 停止服务

echo ============================================================
echo    710号汽车之家   停止服务
echo    释放端口: 8080-后端 5173-后台 5174-官网 5175-H5
echo ============================================================
echo.

set "N=0"
for %%P in (8080 5173 5174 5175) do call :kill %%P

echo.
echo 已停止 %N% 个进程.
echo 若仍有 710-xxx 命令行窗口残留, 直接关闭即可.
echo.
pause
exit /b 0

:kill
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /c:":%~1" ^| findstr /c:"LISTENING"') do call :killpid %%A %~1
exit /b 0

:killpid
if defined K_%1 exit /b 0
set "K_%1=1"
echo   端口 %2 -^> 结束进程 PID %1
taskkill /F /PID %1 >nul 2>nul
set /a N+=1
exit /b 0
