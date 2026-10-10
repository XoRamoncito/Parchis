@echo off
rem Compila el proyecto y abre la ventana de un jugador.
rem Uso: ejecutar-cliente.bat [nombre] [direccion-del-servidor] [puerto]
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\main\java\*.java > out\fuentes.txt
javac -encoding UTF-8 -d out @out\fuentes.txt || (pause & exit /b 1)
start "" javaw -cp out cliente.AppCliente %*
