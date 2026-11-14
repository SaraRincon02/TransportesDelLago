package co.edu.uptc.modelo;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Reserva {

    public static final LocalTime HORA_MINIMA = LocalTime.of(5,0);
    public static final LocalTime HORA_MAXIMA = LocalTime.of(23,0);
    public static final LocalTime HORA_RECARGO = LocalTime.of(18,0);
    public static final int PORCENTAJE_RECARGO = 15;
    public static final int PORCENTAJE_PENALIDAD = 30;
    public static final int HORAS_SIN_PENALIDAD = 24;

    private int id;
    private Contratante contratante;
    private Servicio servicio;
    private Bus bus;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private double valorFinal;
    private double penalidad;

    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio){
        if (contratante == null || bus == null || servicio == null || fechaInicio == null){
            throw new DatoInvalidoException("La reserva necesita contratante, bus, servicio y fecha de salida.");
        }

        this.id = id;
        this.contratante = contratante;
        this.bus = bus;
        this.servicio = servicio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusHours(servicio.getDuracionHoras());
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

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public void setPenalidad(double penalidad) {
        this.penalidad = penalidad;
    }

    public static void validarFranja(LocalDateTime inicio) {
        if (inicio == null) {
            throw new DatoInvalidoException("La fecha de salida no puede ser nula.");
        }
        LocalTime hora = inicio.toLocalTime();
        if (hora.isBefore(HORA_MINIMA) || hora.isAfter(HORA_MAXIMA)) {
            throw new DatoInvalidoException(
                    "Franja fuera del horario: las salidas son entre las 5:00 a.m. y las 11:00 p.m.");
        }
    }

    public boolean ocupaBus(){
        return  estado != EstadoReserva.CANCELADA;
    }

    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin){
        return fechaInicio.isBefore(otroFin) && otroInicio.isBefore(fechaFin);
    }

    public boolean aplicaRecargo(){
        DayOfWeek dia = fechaInicio.getDayOfWeek();
        boolean finDeSemana = (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY);
        boolean nocturno = !fechaInicio.toLocalTime().isBefore(HORA_RECARGO);
        return finDeSemana || nocturno;
    }

    public void recalcularValor(){
        double base = servicio.calcularTarifaBase();
        double conRecargo = aplicaRecargo() ? base * (100 + PORCENTAJE_RECARGO) / 100.0 : base;
        int descuento = contratante.calcularPorccentajeDescuento(fechaInicio.toLocalDate());
        double resultado = Math.round(conRecargo * (100 - descuento) / 100.0);
        double tope = Math.round(conRecargo);
        valorFinal = Math.max(0, Math.min(resultado, tope));
    }

    public double calcularPenalidad(LocalDateTime ahora){
        boolean tarde = ahora.plusHours(HORAS_SIN_PENALIDAD).isAfter(fechaInicio);
        if (estado == EstadoReserva.CONFIRMADA && tarde){
            return Math.round(valorFinal * PORCENTAJE_PENALIDAD / 100.0);
        }
        return 0;
    }

    public void confirmar() throws TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CONFIRMADA, ".");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar(LocalDateTime fechaActual) throws TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CANCELADA, ".");
        }
        penalidad = calcularPenalidad(fechaActual);
        estado = EstadoReserva.CANCELADA;
    }

    public void iniciar(LocalDateTime fechaActual) throws TransicionInvalidaException{
        if (estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.EN_CURSO, ".");
        }

        if (!fechaActual.toLocalDate().equals(fechaInicio.toLocalDate())){
            estado, EstadoReserva.EN_CURSO,
                    "Solo se puede iniciar el mismo día de la reserva (" + fechaInicio.toLocalDate() + ").");
        }
        estado = EstadoReserva.EN_CURSO;
    }

    public void cerrar() throws TransicionInvalidaException{
        if (estado != EstadoReserva.EN_CURSO){
            throw new TransicionInvalidaException(estado, EstadoReserva.CERRADA, ".");
        }
        estado = EstadoReserva.CERRADA;
    }

    public void validarReprogramacion() throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA ){
            throw new TransicionInvalidaException("Transicion invalida: no se puede reprogramar una reserva en estado "
                                                    + estado.name() + "; solo se pueden reprogramar reservas CREADA o CONFIRMADA");
        }
    }





}
