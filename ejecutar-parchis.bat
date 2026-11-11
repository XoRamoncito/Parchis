@echo off
rem Compila todos los modulos y abre la pantalla de registro del CU-01.
cd /d "%~dp0"
if not exist out mkdir out
(for /d %%m in (parchis-*) do @dir /s /b "%%m\src\main\java\*.java") > out\fuentes.txt
javac -encoding UTF-8 -d out @out\fuentes.txt || (pause & exit /b 1)
start "" javaw -cp out presentacion.AppParchis
