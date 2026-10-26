package co.edu.uptc.modelo;

public class Encomienda implements Asegurable{
    private String codigo;
    private String remitente;
    private String destinatario;
    private double peso;
    private double valorDeclarado;

    public Encomienda(String codigo, String remitente, String destinatario, double peso, double valorDeclarado){
        if (codigo == null || codigo.trim().isEmpty()){
            throw new DatoInvalidoException("El código de la encomienda no puede estar vacío.");
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

    public String getCodigo() {
        return codigo;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public double getPeso() {
        return peso;
    }

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
        return "Póliza de protección de mercancía. Remitente: " + remitente + " | Destinatario: " + destinatario
                + " | Valor Declarado: $" + valorDeclarado;
    }
}
