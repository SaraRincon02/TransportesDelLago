package co.edu.uptc.modelo;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Duration;

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


    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio){
        if (id<=0){
            throw new DatoInvalidoException("El ID de la reserva debe ser mayor a 0");
        }
        if (contratante == null || bus == null || servicio == null || fechaInicio == null){
            throw new DatoInvalidoException("Faltan datos obligatorios en la reserva.");
        }
        int hora = fechaInicio.getHour();
        if (hora < 5 || hora > 23 || (hora == 23 && fechaInicio.getMinute() > 0)){
            throw new DatoInvalidoException("Franja fuera del horario: de 5:00 a.m. a 11:00 p.m.");
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
        recalcularValor();
    }

    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, EstadoReserva estado, double valorFinal, double penalidad) {
        this.id = id;
        this.contratante = contratante;
        this.bus = bus;
        this.servicio = servicio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.valorFinal = valorFinal;
        this.penalidad = penalidad;
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
        valorFinal = Math.round(conRecargo * (1 - (descuento / 100.0)));
   }

   public void confirmar() throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CONFIRMADA);
        }
        estado = EstadoReserva.CONFIRMADA;
   }

    public void cancelar(LocalDateTime ahora) throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CANCELADA);
        }
        if (estado == EstadoReserva.CONFIRMADA && ahora.plusHours(24).isAfter(fechaInicio)){
            penalidad = Math.round(valorFinal * 0.30);
        }
        estado = EstadoReserva.CANCELADA;
    }

    public void iniciar(LocalDateTime ahora) throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CONFIRMADA || !ahora.toLocalDate().equals(fechaInicio.toLocalDate())){
            throw new TransicionInvalidaException(estado, EstadoReserva.EN_CURSO);
        }
        estado = EstadoReserva.EN_CURSO;
    }

    public void cerrar() throws TransicionInvalidaException {
        if (estado != EstadoReserva.EN_CURSO){
            throw new TransicionInvalidaException(estado, EstadoReserva.CERRADA);
        }
        estado = EstadoReserva.CERRADA;
    }

    public void reprogramar(LocalDateTime nuevoInicio, Bus nuevobus) throws  TransicionInvalidaException {
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA){
            throw new TransicionInvalidaException("Solo se reprograma reservas CREADA o CONFIRMADA");
        }
        int hora = nuevoInicio.getHour();
        if (hora < 5 || hora > 23 || (hora == 23 && nuevoInicio.getMinute() > 0)) {
            throw new TransicionInvalidaException("Franja de reprogramacion fuera de horario.");
        }
        servicio.validarBus(nuevobus);
        this.bus = nuevobus;
        this.fechaInicio = nuevoInicio;
        this.fechaFin = nuevoInicio.plusHours(servicio.getDuracionHoras());
        recalcularValor();
    }

    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin){
        return fechaInicio.isBefore(otroFin) && otroInicio.isBefore(fechaFin);
    }

    public boolean ocupaBus(){
        return  estado != EstadoReserva.CANCELADA;
    }





}
