@echo off
rem CU-01 en red (Hito 2B).
rem   En la computadora que tendra el bus:   ejecutar-parchis.bat
rem   En las demas:                         ejecutar-parchis.bat <ip-de-la-computadora-del-bus>
rem Antes, una sola vez en cada computadora: correr bd\parchis.sql en MySQL y copiar
rem parchis-bd.properties.ejemplo como parchis-bd.properties con sus datos.
cd /d "%~dp0"
set JAR=parchis-presentacion\target\parchis-presentacion-1.0-SNAPSHOT.jar
if not exist "%JAR%" (
  echo Falta compilar: en NetBeans abre el proyecto parchis y haz Clean and Build.
  pause & exit /b 1
)
if not exist parchis-bd.properties (
  echo Falta parchis-bd.properties: copia parchis-bd.properties.ejemplo y pon tus datos de MySQL.
  pause & exit /b 1
)
java -jar "%JAR%" %*
