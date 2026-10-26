package co.edu.uptc.modelo;

import java.util.Locale;

public class TransicionInvalidaException extends Exception {
    public TransicionInvalidaException(EstadoReserva actual, String accion) {

        super("No se puede " + accion + " una reserva " + actual.getTexto().toLowerCase() + ".");
    }
}
