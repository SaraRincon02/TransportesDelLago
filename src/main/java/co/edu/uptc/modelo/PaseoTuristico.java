package co.edu.uptc.modelo;

import java.time.LocalDateTime;

public class PaseoTuristico extends ServicioViaje{

    private double kilometrosRecorridos;

    public PaseoTuristico(String idReserva, String placaBus, String nitContratante, LocalDateTime fechaInicio, LocalDateTime fechaFin, double kilometrosRecorridos){
        super(idReserva, placaBus, nitContratante, fechaInicio, fechaFin);
        if (kilometrosRecorridos <= 0){
            throw new DatoInvalidoException("Los kilometros deben ser mayores a cero.");
        }
        this.kilometrosRecorridos = kilometrosRecorridos;
    }

    public double getKilometrosRecorridos() {
        return kilometrosRecorridos;
    }

    @Override
    public double calcularTarifaBase() {
        return getKilometrosRecorridos() * 4500;
    }


}
