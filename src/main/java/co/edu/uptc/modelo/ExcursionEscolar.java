package co.edu.uptc.modelo;

import java.time.LocalDateTime;

public class ExcursionEscolar extends ServicioViaje{
    private int cantidadEstudiantes;

    public ExcursionEscolar(String idReserva, String placaBus, String nitContratante, LocalDateTime fechaInicio, LocalDateTime fechaFin, int cantidadEstudiantes){
        super(idReserva, placaBus, nitContratante, fechaInicio, fechaFin);
        if (cantidadEstudiantes <= 0){
            throw new DatoInvalidoException("La cantidad de estudiantes debe de ser mayor a cero.");
        }
        this.cantidadEstudiantes = cantidadEstudiantes;
    }

    @Override
    public double calcularTarifaBase() {
        return 0;
    }
}
