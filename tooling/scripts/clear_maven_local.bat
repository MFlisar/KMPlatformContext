@echo off
setlocal
set "M2_REPO=%USERPROFILE%\.m2\repository"
if exist "%M2_REPO%" (
    rmdir /s /q "%M2_REPO%"
    echo Maven-Repository wurde gelöscht.
) else (
    echo Maven-Repository existiert nicht.
)
endlocal