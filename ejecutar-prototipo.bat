@echo off
rem Prototipo del estilo basado en eventos: dos computadoras se mandan un mensaje.
rem En una computadora:   ejecutar-prototipo.bat bus
rem En cada computadora:  ejecutar-prototipo.bat <nombre> <ip-de-la-computadora-del-bus>
cd /d "%~dp0"
if not exist out mkdir out
(for /d %%m in (parchis-*) do @dir /s /b "%%m\src\main\java\*.java") > out\fuentes.txt
javac -encoding UTF-8 -d out @out\fuentes.txt || (pause & exit /b 1)
java -cp out negocio.comunicacion.eventos.PrototipoEventos %*
pause
