package co.edu.uptc.modelo;

public class Bus {
    private String codigo;
    private String placa;
    private int capacidad;

    public Bus(String codigo, String placa, int capacidad){
        if (capacidad <= 0){
            throw new DatoInvalidoException("La capacidad del bus debe ser mayor a cero.");
        }
        this.codigo = codigo;
        this.placa = placa;
        this.capacidad = capacidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getPlaca() {
        return placa;
    }

    public int getCapacidad() {
        return capacidad;
    }

}
