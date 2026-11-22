package co.edu.uptc.modelo;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;


public class Reserva {
    private int id;
    private Contratante contratante;
    private Servicio servicio;
    private Bus bus;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private double valorFinal;
    private double penalidad;
    private LocalDateTime fechaCierre;


    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio){
        if (id<=0){
            throw new DatoInvalidoException("El ID de la reserva debe ser mayor a 0");
        }
        if (contratante == null || bus == null || servicio == null || fechaInicio == null){
            throw new DatoInvalidoException("Faltan datos obligatorios en la reserva.");
        }

        validarHorario(fechaInicio);
        servicio.validarBus(bus);
        this.id = id;
        this.contratante = contratante;
        this.bus = bus;
        this.servicio = servicio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusHours(servicio.getDuracionHoras());
        this.estado = EstadoReserva.CREADA;
        this.penalidad = 0.0;
        this.fechaCierre= null;
        recalcularValor();
    }

    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, EstadoReserva estado, double valorFinal, double penalidad) {
        if (id <= 0 || contratante == null || bus == null || servicio == null || fechaInicio == null || fechaFin == null || estado == null) {
            throw new DatoInvalidoException("Los datos de la reserva no son válidos");
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new DatoInvalidoException("La fecha final debe ser posterior a la inicial");
        }

        if (valorFinal < 0 || penalidad < 0) {
            throw new DatoInvalidoException("Los valores no pueden ser negativos");
        }
        this.id = id;
        this.contratante = contratante;
        this.bus = bus;
        this.servicio = servicio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.valorFinal = valorFinal;
        this.penalidad = penalidad;
        this.fechaCierre = null;
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

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    private void validarHorario(LocalDateTime fecha){
            if (fecha == null) {
                throw new DatoInvalidoException("La fecha de inicio es obligatoria");
            }
            int hora = fecha.getHour();
            if (hora < 5 || hora > 23 || (hora == 23 && fecha.getMinute() > 0)) {
                throw new DatoInvalidoException("La salida debe programarse entre las 5:00 a.m. y las 11:00 p.m");
            }
        }

   public void recalcularValor(){
        double base = servicio.calcularTarifaBase();
        DayOfWeek dia = fechaInicio.getDayOfWeek();

        boolean aplicarRecargo = (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY || fechaInicio.getHour() >= 18);
        double conRecargo = aplicarRecargo ? base * 1.15 : base;

        int descuento = contratante.calcularPorccentajeDescuento(fechaInicio.toLocalDate());
       double valorCalculado = conRecargo * (1 - descuento / 100.0);
       valorFinal = Math.round(Math.max(0, Math.min(valorCalculado, conRecargo)));
   }

   public void confirmar() throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CONFIRMADA);
        }
        estado = EstadoReserva.CONFIRMADA;
   }

    public void cancelar(LocalDateTime ahora) throws  TransicionInvalidaException {
        if (ahora == null) {
            throw new DatoInvalidoException("Debe indicar la fecha y hora de cancelación");
        }
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA) {
            throw new TransicionInvalidaException(estado, EstadoReserva.CANCELADA);
        }
        if (!ahora.isBefore(fechaInicio)) {
            throw new DatoInvalidoException("No se puede cancelar una reserva después de su hora de salida");
        }

        penalidad = 0.0;

        if (estado == EstadoReserva.CONFIRMADA && Duration.between(ahora, fechaInicio).compareTo(Duration.ofHours(24)) < 0) {
            penalidad = Math.round(valorFinal * 0.30);
        }
        estado = EstadoReserva.CANCELADA;
    }

    public void iniciar(LocalDateTime ahora) throws  TransicionInvalidaException {
            if (ahora == null) {
                throw new DatoInvalidoException("Debe indicar la fecha y hora de inicio");
            }

            if (estado != EstadoReserva.CONFIRMADA || !ahora.toLocalDate().equals(fechaInicio.toLocalDate())) {
                throw new TransicionInvalidaException(estado, EstadoReserva.EN_CURSO);
            }

            if (ahora.isBefore(fechaInicio)) {
                throw new DatoInvalidoException("No se puede iniciar el viaje antes de la hora programada");
            }
            estado = EstadoReserva.EN_CURSO;
        }


    public void cerrar(LocalDateTime ahora) throws TransicionInvalidaException {

        if (ahora == null) {
            throw new DatoInvalidoException("Debe indicar la fecha y hora de cierre");
        }
        if (ahora.isBefore(fechaInicio)) {
            throw new DatoInvalidoException("La fecha de cierre no puede ser anterior al inicio del viaje");
        }

        if (estado != EstadoReserva.EN_CURSO) {
            throw new TransicionInvalidaException(estado, EstadoReserva.CERRADA);
        }

        fechaCierre = ahora;
        estado = EstadoReserva.CERRADA;
    }


    public void reprogramar(LocalDateTime nuevoInicio, Bus nuevobus) throws  TransicionInvalidaException {
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException("Solo se reprograma reservas CREADA o CONFIRMADA");
        }

        if (nuevoInicio == null || nuevobus == null) {
            throw new DatoInvalidoException("La nueva fecha y el nuevo bus son obligatorios");
        }

        validarHorario(nuevoInicio);
        servicio.validarBus(nuevobus);
        this.bus = nuevobus;
        this.fechaInicio = nuevoInicio;
        this.fechaFin = nuevoInicio.plusHours(servicio.getDuracionHoras());
        recalcularValor();
    }

    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
        if (otroInicio == null || otroFin == null || !otroFin.isAfter(otroInicio)) {
            throw new DatoInvalidoException("El intervalo de fechas no es válido");
        }

        return fechaInicio.isBefore(otroFin) && otroInicio.isBefore(fechaFin);
    }


    public boolean ocupaBus(){
        return  estado != EstadoReserva.CANCELADA;
    }
}
