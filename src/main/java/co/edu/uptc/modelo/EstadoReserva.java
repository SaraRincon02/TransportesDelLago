package co.edu.uptc.modelo;

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

    public String getTexto() {
        return texto;
    }
}
