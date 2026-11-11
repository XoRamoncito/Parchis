package negocio.sala.mock;

import negocio.sala.ContratoFachadaSala;
import negocio.sala.IFachadaSala;

/** El mock cumple el contrato de la fachada. */
class FachadaSalaMockTest extends ContratoFachadaSala {

    @Override
    protected IFachadaSala crearFachada() {
        return new FachadaSalaMock();
    }
}
