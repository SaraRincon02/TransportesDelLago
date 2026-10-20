package co.edu.uptc.modelo;

import java.time.LocalDateTime;

public abstract class ServicioViaje {
    protected String idReserva;
    protected String placaBus;
    protected String nitContratante;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;

    public ServicioViaje(String idReserva, String placaBus, String nitContratante, LocalDateTime fechaInicio, LocalDateTime fechaFin){
        this.idReserva = idReserva;
        this.placaBus = placaBus;
        this.nitContratante = nitContratante;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public String getPlacaBus() {
        return placaBus;
    }

    public String getNitContratante() {
        return nitContratante;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public abstract double calcularTarifaBase();
}
