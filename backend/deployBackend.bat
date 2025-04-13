@echo off
set SERVER=root@lan.rtast.cn
set REMOTE_DIR=/root/reposilite
set LOCAL_FILE=build\bin\linuxX64\releaseExecutable\backend.kexe

echo [1/4] Checking artifacts...
if not exist %LOCAL_FILE% (
    echo Artifact not found: %LOCAL_FILE%
    exit /b 1
)

echo [2/4] Uploading...
scp %LOCAL_FILE% %SERVER%:/tmp/backend.kexe
if errorlevel 1 (
    echo Upload failure
    exit /b 1
)

echo [3/4] Moving file and restart services...
ssh %SERVER% "systemctl stop reposilite && rm %REMOTE_DIR%/backend.kexe && mv /tmp/backend.kexe %REMOTE_DIR%/backend.kexe && chmod +x %REMOTE_DIR%/backend.kexe && systemctl restart reposilite"

if errorlevel 1 (
    echo execution failure
    exit /b 1
)

echo [4/4] Deployment success！
