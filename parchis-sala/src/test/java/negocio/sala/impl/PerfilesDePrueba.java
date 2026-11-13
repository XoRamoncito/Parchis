package negocio.sala.impl;

import datos.PerfilDAOMemoria;
import negocio.perfiles.PerfilBO;

/** PerfilBO con el DAO en memoria, para probar la sala sin MySQL. */
final class PerfilesDePrueba {

    private PerfilesDePrueba() {
    }

    static PerfilBO crear() {
        return new PerfilBO(new PerfilDAOMemoria());
    }
}
