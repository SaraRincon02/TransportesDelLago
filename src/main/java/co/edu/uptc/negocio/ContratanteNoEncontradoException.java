package co.edu.uptc.negocio;
/**
 * Excepción verificada lanzada cuando se busca o reserva a nombre de un contratante inexistente.
 */
public class ContratanteNoEncontradoException extends Exception {

    /**
     * Crea la excepción con el detalle de la búsqueda fallida.
     * @param message descripción del error.
     */
    public ContratanteNoEncontradoException(String message) {

        super(message);
    }
}
