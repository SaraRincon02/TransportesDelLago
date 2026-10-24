package co.edu.uptc.modelo;

import java.time.LocalDateTime;

public class Reserva {
    private Contratante contratante;
    private Bus bus;
    private Servicio servicio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private double valorFinal;
    private double penalidad;

    public Reserva(Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio, double valorFinal){
        this.contratante = contratante;
        this.bus = bus;
        this.servicio = servicio;
        this.fechaInicio = fechaInicio;

        this.fechaFin = fechaInicio.plusHours(servicio.getDuracionHoras());
        this.valorFinal = valorFinal;
        this.estado = EstadoReserva.CREADA;
        this.penalidad = 0.0;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public Bus getBus() {
        return bus;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public double getPenalidad() {
        return penalidad;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusHours(this.servicio.getDuracionHoras());
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public void setPenalidad(double penalidad) {
        this.penalidad = penalidad;
    }
}
