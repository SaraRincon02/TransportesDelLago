package co.edu.uptc.negocio;
/**
 * Excepción verificada lanzada cuando se intenta asignar un bus ya ocupado en esa franja horaria.
 */
public class BusOcupadoException extends Exception {

    /**
     * Crea la excepción indicando la placa del bus ocupado.
     * @param placa placa del vehículo en conflicto.
     */
    public BusOcupadoException(String placa) {

        super("El bus con placa " + placa + " ya tiene un viaje programado en esa franja horaria.");
    }
}
