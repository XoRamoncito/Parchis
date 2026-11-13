-- Base de datos del Parchís en red (Hito 2B). Se corre una vez en cada computadora
-- (cada aplicación guarda en el MySQL de su computadora el perfil de quien juega en ella).
-- En MySQL Workbench: File > Open SQL Script, y el rayo para ejecutar.

CREATE DATABASE IF NOT EXISTS parchis;
USE parchis;

CREATE TABLE IF NOT EXISTS perfil (
    nombre            VARCHAR(15) NOT NULL PRIMARY KEY,
    color_preferido   VARCHAR(10) NOT NULL,
    avatar            INT         NOT NULL,
    partidas_jugadas  INT         NOT NULL DEFAULT 0,
    partidas_ganadas  INT         NOT NULL DEFAULT 0
);

-- Usuario solo para el juego. Antes de correrlo, cambia CAMBIA_ESTA_CONTRASENA por tu contraseña
-- y pon la misma en parchis-bd.properties.
CREATE USER IF NOT EXISTS 'parchis'@'localhost' IDENTIFIED BY 'CAMBIA_ESTA_CONTRASENA';
GRANT SELECT, INSERT, UPDATE ON parchis.* TO 'parchis'@'localhost';
