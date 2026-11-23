package co.edu.uptc.modelo;
/**
 * Representa los posibles estados del ciclo de vida de una reserva.
 */
public enum EstadoReserva {

    CREADA("Viaje creado"),
    CONFIRMADA("Confirmado"),
    EN_CURSO("En curso"),
    CERRADA("Cerrado"),
    CANCELADA("Cancelado");

    private final String texto;

    EstadoReserva(String texto){
        this.texto = texto;
    }

    // ...
    /**
     * Obtiene la descripción legible del estado.
     * @return texto descriptivo del estado.
     */
    public String getTexto() {
        return texto;
    }
}
