package co.edu.uptc.modelo;

import java.util.Locale;

public class TransicionInvalidaException extends Exception {
    public TransicionInvalidaException(EstadoReserva actual, EstadoReserva interno) {

        super("Transición inválida: No se puede cambiar la reserva desde el estado " +
                actual.name() + " hacia el estado " + interno.name() + ".");
    }
}
