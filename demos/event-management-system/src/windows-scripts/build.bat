@echo off
echo Compiling the VDD project...
if not exist ..\..\out mkdir ..\..\out
javac --enable-preview --release 21 ^
    -d ..\..\out ^
    --module-source-path ..\modules ^
    --module com.example.eventmanagement.domain,com.example.eventmanagement.operations,com.example.eventmanagement.cli
if %ERRORLEVEL%==0 (
    echo Build complete.
) else (
    echo Build failed.
    exit /b %ERRORLEVEL%
)
