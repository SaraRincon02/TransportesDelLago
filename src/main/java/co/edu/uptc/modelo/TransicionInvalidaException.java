package co.edu.uptc.modelo;

import java.util.Locale;
/**
 * Excepción verificada lanzada cuando se intenta realizar un cambio de estado no permitido en una reserva.
 */
public class TransicionInvalidaException extends Exception {

    /**
     * Crea la excepción indicando el estado actual y el estado destino inválido.
     * @param actual estado actual de la reserva.
     * @param interno estado al que se intentó cambiar.
     */
    public TransicionInvalidaException(EstadoReserva actual, EstadoReserva interno) {
        super("Transición inválida: no se puede cambiar la reserva desde el estado "
                + actual.name() + " hacia el estado " + interno.name() + ". ");
    }

    /**
     * Crea la excepción con un mensaje personalizado.
     * @param mensaje descripción del error de transición.
     */
    public TransicionInvalidaException(String mensaje){
        super(mensaje);
    }
}
