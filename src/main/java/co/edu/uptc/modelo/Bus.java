package co.edu.uptc.modelo;

public class Bus {
    private String codigo;
    private String placa;
    private int capacidad;

    public Bus(String codigo, String placa, int capacidad){
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
