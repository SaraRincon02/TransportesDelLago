package co.edu.uptc.modelo;

public class Bus {
    private String placa;
    private int capacidadPuestos;
    private String modelo;

    public Bus(String placa, int capacidadPuestos, String modelo){
        if (capacidadPuestos <= 0){
            throw new DatoInvalidoException("La Capacidad del bus debe ser mayor a cero.");
        }
        this.placa = placa;
        this.capacidadPuestos = capacidadPuestos;
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public int getCapacidadPuestos() {
        return capacidadPuestos;
    }

    public String getModelo() {
        return modelo;
    }
}
