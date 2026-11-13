package negocio.sala.impl;

import datos.IPerfilDAO;
import datos.PerfilEntidad;
import negocio.perfiles.PerfilBO;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** PerfilBO con un DAO en un mapa, para probar la sala sin MySQL. */
final class PerfilesDePrueba {

    private PerfilesDePrueba() {
    }

    static PerfilBO crear() {
        Map<String, PerfilEntidad> tabla = new ConcurrentHashMap<>();
        return new PerfilBO(new IPerfilDAO() {
            @Override
            public void guardar(PerfilEntidad perfil) {
                tabla.put(perfil.nombre(), perfil);
            }

            @Override
            public Optional<PerfilEntidad> buscarPorNombre(String nombre) {
                return Optional.ofNullable(tabla.get(nombre));
            }
        });
    }
}
