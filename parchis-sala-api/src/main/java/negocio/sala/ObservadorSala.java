package negocio.sala;

import dtos.SalaDTO;

/** Avisa a la presentación cuando la sala cambia (alguien llega o se marca listo). */
public interface ObservadorSala {

    void salaActualizada(SalaDTO sala);
}
