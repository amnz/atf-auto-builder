@echo off
if "%OS%" == "Windows_NT" setlocal

rem ------------- please set enviroments -------------------------------
rem 環境変数 JAVA_HOME を設定していない場合は、
rem 9行目の冒頭 rem を削除し、JDKへのパスを記述してください。
rem 例）set JAVA_HOME=c:\Program Files\java\jdk1.6.0_02

rem set JAVA_HOME=jdk\x64\1.7.0_60
set AAB_DIR=%~dp0
set GPSS_LIB_DIRS=%AAB_DIR%\libs
set GPSS_OPT=

rem ------------- check Java Home --------------------------------------
if not "%JAVA_HOME%" == "" goto checkJdk
goto noJdk

:checkJdk
if exist "%JAVA_HOME%\bin\java.exe" goto okHome

:noJdk
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end

:okHome

rem ------------- set GPSS Options -------------------------------------
set GPSS_LIB_DIRS=-Djava.ext.dirs=%GPSS_LIB_DIRS%
if exist "%JAVA_HOME%\jre" goto setjdk

:setjre
set __SERVER_JVM_PATH=%JAVA_HOME%\bin\server
set GPSS_LIB_DIRS=%GPSS_LIB_DIRS%;%JAVA_HOME%\lib\ext
goto okJdk

:setjdk
set __SERVER_JVM_PATH=%JAVA_HOME%\jre\bin\server
set GPSS_LIB_DIRS=%GPSS_LIB_DIRS%;%JAVA_HOME%\jre\lib\ext

:okJdk

set GPSS_OPT=%GPSS_OPT% %GPSS_LIB_DIRS%

%JAVA_HOME%\bin\java %GPSS_OPT% -jar %AAB_DIR%\atf-auto-builder.jar %1 %2 %3 %4 %5

:end
