package negocio.sala.impl;

import negocio.sala.ContratoFachadaSala;
import negocio.sala.IFachadaSala;

/** La misma prueba del CU-01 que pasó el mock, ahora contra el subsistema real. */
class FachadaSalaContratoTest extends ContratoFachadaSala {

    @Override
    protected IFachadaSala crearFachada() {
        return new FachadaSala(new ComunicacionEnMemoria(), PerfilesDePrueba.crear());
    }
}
