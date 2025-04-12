@echo off
echo Compiling the OOAD project...
if not exist ..\..\out mkdir ..\..\out
javac --enable-preview --release 21 ^
    -d ..\..\out ^
    --module-source-path ..\modules ^
    --module com.example.eventmanagement.oop
if %ERRORLEVEL%==0 (
    echo Build complete.
) else (
    echo Build failed.
    exit /b %ERRORLEVEL%
)
