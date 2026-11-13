@echo off
rem Prototipo del estilo basado en eventos: dos computadoras se mandan un mensaje.
rem En una computadora:   ejecutar-prototipo.bat bus
rem En cada computadora:  ejecutar-prototipo.bat <nombre> <ip-de-la-computadora-del-bus>
cd /d "%~dp0"
java -cp "parchis-presentacion\target\lib\*" negocio.comunicacion.eventos.PrototipoEventos %*
pause
