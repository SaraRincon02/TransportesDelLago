package co.edu.uptc.modelo;
/**
 * Representa el despacho de una encomienda independiente de los viajes.
 * Implementa {@link Asegurable} para calcular el seguro sobre el valor declarado.
 */
public class Encomienda implements Asegurable{
    private String codigo;
    private String remitente;
    private String destinatario;
    private double peso;
    private double valorDeclarado;

    /**
     * Construye una nueva encomienda validando todos sus campos obligatorios.
     * @param codigo código único de la encomienda.
     * @param remitente nombre de quien envía.
     * @param destinatario nombre de quien recibe.
     * @param peso peso del paquete en kilogramos.
     * @param valorDeclarado valor comercial declarado en pesos.
     * @throws DatoInvalidoException si algún texto está vacío o peso/valor son menores o iguales a cero.
     */
    public Encomienda(String codigo, String remitente, String destinatario, double peso, double valorDeclarado){
        if (codigo == null || codigo.trim().isEmpty()){
            throw new DatoInvalidoException("El código de la encomienda no puede estar vacío.");
        }

        if (remitente == null || remitente.trim().isEmpty()){
            throw new DatoInvalidoException("El remitente no puede estar vacio.");
        }
        if (destinatario == null || destinatario.trim().isEmpty()){
            throw new DatoInvalidoException("El destinatario no puede estar vacio.");
        }

        if (peso <= 0){
            throw new DatoInvalidoException("El peso de la encomienda debe ser mayor a cero.");
        }

        if(valorDeclarado <= 0){
            throw new DatoInvalidoException("El valor declarado debe ser mayor a cero.");
        }
        this.codigo = codigo;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.peso = peso;
        this.valorDeclarado = valorDeclarado;
    }

    /** @return código único de la encomienda. */
    public String getCodigo() {
        return codigo;
    }

    /** @return nombre del remitente. */
    public String getRemitente() {
        return remitente;
    }

    /** @return nombre del destinatario. */
    public String getDestinatario() {
        return destinatario;
    }

    /** @return peso en kilogramos. */
    public double getPeso() {
        return peso;
    }

    /** @return valor declarado en pesos. */
    public double getValorDeclarado() {
        return valorDeclarado;
    }

    @Override
    public double calcularValorSeguro() {
        double seguro = valorDeclarado * 0.02;
        return (seguro < 5000) ? 5000 : seguro;
    }

    @Override
    public String obtenerDescripcionPoliza() {
        return "Póliza de protección de mercancía " + codigo + " Remitente: " + remitente + " | Destinatario: " + destinatario
                + " | Peso: " + peso + " kg" + " | Valor Declarado: $" + valorDeclarado;
    }
}
