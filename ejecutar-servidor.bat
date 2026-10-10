@echo off
rem Compila el proyecto y arranca el servidor de Parchis (puerto 5000).
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\main\java\*.java > out\fuentes.txt
javac -encoding UTF-8 -d out @out\fuentes.txt || (pause & exit /b 1)
java -cp out servidor.AppServidor %*
pause
