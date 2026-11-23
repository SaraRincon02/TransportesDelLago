package co.edu.uptc.modelo;
/**
 * Excepción no verificada lanzada cuando se ingresan datos fuera de las reglas de negocio.
 */
public class DatoInvalidoException extends RuntimeException {

    /**
     * Crea la excepción con el detalle del dato inválido.
     * @param message descripción del error.
     */
    public DatoInvalidoException(String message) {

        super(message);
    }
}
