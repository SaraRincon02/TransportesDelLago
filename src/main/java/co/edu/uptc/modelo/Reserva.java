package co.edu.uptc.modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public void confirmar() throws TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CONFIRMADA);
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar(LocalDateTime fechaActual) throws TransicionInvalidaException{
        if (estado == EstadoReserva.CREADA){
            estado = EstadoReserva.CANCELADA;
        } else if (estado == EstadoReserva.CONFIRMADA) {
            long horasFaltantes = ChronoUnit.HOURS.between(fechaActual, fechaInicio);

            if (horasFaltantes < 24 && horasFaltantes >= 0){
                penalidad = valorFinal * 0.30;
            }
            estado = EstadoReserva.CANCELADA;
        }else{
            throw new TransicionInvalidaException(estado, EstadoReserva.CANCELADA);
        }
    }

    public void iniciar(LocalDateTime fechaActual) throws TransicionInvalidaException{
        if (estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.EN_CURSO);
        }

        if (!fechaActual.toLocalDate().equals(fechaInicio.toLocalDate())){
            throw new IllegalStateException("Solo se puede iniciar la reserva el mismo día del viaje.");
        }
        estado = EstadoReserva.EN_CURSO;
    }

    public void cerrar() throws TransicionInvalidaException{
        if (estado != EstadoReserva.EN_CURSO){
            throw new TransicionInvalidaException(estado, EstadoReserva.CERRADA);
        }
        estado = EstadoReserva.CERRADA;
    }

    public void validarReprogramacion() throws  IllegalStateException{
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA ){
            throw new IllegalStateException("Solo se puede reprogramar una reserva CREADA o CONFIRMADA.");
        }
    }



}
