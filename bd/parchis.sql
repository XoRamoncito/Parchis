-- Base de datos del Parchís en red (Hito 2B). Se corre una vez en cada computadora
-- (la persistencia es local: cada una guarda los perfiles de quienes juegan en ella).
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

-- Usuario solo para el juego. Cambia la contraseña y ponla igual en parchis-bd.properties.
CREATE USER IF NOT EXISTS 'parchis'@'localhost' IDENTIFIED BY 'parchis2b';
GRANT SELECT, INSERT, UPDATE ON parchis.* TO 'parchis'@'localhost';
