package co.edu.uptc.modelo;
/**
 * Define el contrato para los elementos del sistema que requieren cálculo de seguro
 * y generación de póliza (excursiones escolares y encomiendas).
 */
public interface Asegurable {
    /**
     * Calcula el valor monetario del seguro aplicable.
     * @return costo del seguro en pesos.
     */
    double calcularValorSeguro();

    /**
     * Genera la descripción detallada de la cobertura de la póliza.
     * @return texto descriptivo de la póliza.
     */
    String obtenerDescripcionPoliza();
}
