package co.edu.uptc.modelo;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
/**
 * Representa la reserva de un bus para un contratante y un servicio en una franja horaria.
 * Controla las transiciones de estado, el cálculo tarifario y el cruce de horarios.
 */
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

    /**
     * Crea una nueva reserva en estado CREADA, validando horario de operación (5:00 a.m. a 11:00 p.m.)
     * y calculando su valor final.
     * @param id identificador numérico único de la reserva.
     * @param contratante cliente que solicita el servicio.
     * @param bus vehículo asignado.
     * @param servicio tipo de viaje contratado.
     * @param fechaInicio fecha y hora de salida.
     * @throws DatoInvalidoException si algún parámetro es nulo o está fuera de las reglas de negocio.
     */
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

    /**
     * Reconstruye una reserva existente desde persistencia sin fecha de cierre.
     * @param id identificador de la reserva.
     * @param contratante cliente asociado.
     * @param bus vehículo asignado.
     * @param servicio servicio contratado.
     * @param fechaInicio fecha y hora de inicio.
     * @param fechaFin fecha y hora de finalización.
     * @param estado estado actual de la reserva.
     * @param valorFinal valor total calculado.
     * @param penalidad valor de penalidad registrada.
     */
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

    /**
     * Reconstruye una reserva existente desde persistencia incluyendo su fecha de cierre.
     * @param id identificador de la reserva.
     * @param contratante cliente asociado.
     * @param bus vehículo asignado.
     * @param servicio servicio contratado.
     * @param fechaInicio fecha y hora de inicio.
     * @param fechaFin fecha y hora de finalización.
     * @param estado estado actual de la reserva.
     * @param valorFinal valor total calculado.
     * @param penalidad valor de penalidad registrada.
     * @param fechaCierre fecha y hora en que se cerró el viaje (puede ser nula).
     */
    public Reserva(int id, Contratante contratante, Bus bus, Servicio servicio,
                   LocalDateTime fechaInicio, LocalDateTime fechaFin,
                   EstadoReserva estado, double valorFinal, double penalidad,
                   LocalDateTime fechaCierre) {

        this(id, contratante, bus, servicio, fechaInicio, fechaFin,
                estado, valorFinal, penalidad);

        this.fechaCierre = fechaCierre;
    }

    /** @return contratante de la reserva. */
    public Contratante getContratante() {
        return contratante;
    }

    /** @return bus asignado a la reserva. */
    public Bus getBus() {
        return bus;
    }

    /** @return servicio contratado. */
    public Servicio getServicio() {
        return servicio;
    }

    /** @return fecha y hora de salida. */
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    /** @return fecha y hora estimada de llegada. */
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    /** @return estado actual de la reserva. */
    public EstadoReserva getEstado() {
        return estado;
    }

    /** @return valor final calculado en pesos. */
    public double getValorFinal() {
        return valorFinal;
    }

    /** @return penalidad cobrada en caso de cancelación tardía. */
    public double getPenalidad() {
        return penalidad;
    }

    /** @return identificador único de la reserva. */
    public int getId() {
        return id;
    }

    /** @return fecha y hora de cierre del viaje, o {@code null} si no está cerrada. */
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

        double RECARGO = 1.15;

    /**
     * Calcula el valor final aplicando recargo único del 15% (nocturno o fin de semana)
     * y descuento por antigüedad del contratante (máximo 15%).
     */
   public void recalcularValor(){
        double base = servicio.calcularTarifaBase();
        DayOfWeek dia = fechaInicio.getDayOfWeek();

        boolean aplicarRecargo = (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY || fechaInicio.getHour() >= 18);

       double conRecargo = aplicarRecargo ? base * RECARGO : base;

        int descuento = contratante.calcularPorccentajeDescuento(fechaInicio.toLocalDate());
       double valorCalculado = conRecargo * (1 - descuento / 100.0);
       valorFinal = Math.round(Math.max(0, Math.min(valorCalculado, conRecargo)));
   }

    /**
     * Cambia el estado de la reserva de CREADA a CONFIRMADA.
     * @throws TransicionInvalidaException si la reserva no está en estado CREADA.
     */
   public void confirmar() throws  TransicionInvalidaException{
        if (estado != EstadoReserva.CREADA){
            throw new TransicionInvalidaException(estado, EstadoReserva.CONFIRMADA);
        }
        estado = EstadoReserva.CONFIRMADA;
   }

    /**
     * Cancela una reserva CREADA o CONFIRMADA antes de su salida.
     * Si estaba CONFIRMADA y faltan menos de 24 horas, aplica penalidad del 30%.
     * @param ahora fecha y hora en que se realiza la cancelación.
     * @throws TransicionInvalidaException si el estado actual no permite cancelar.
     */
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
        double Penalidad=0.30;

        if (estado == EstadoReserva.CONFIRMADA && Duration.between(ahora, fechaInicio).compareTo(Duration.ofHours(24)) < 0) {
            penalidad = Math.round(valorFinal * Penalidad);
        }
        estado = EstadoReserva.CANCELADA;
    }

    /**
     * Inicia el viaje pasando de CONFIRMADA a EN_CURSO el mismo día programado.
     * @param ahora fecha y hora actual de salida.
     * @throws TransicionInvalidaException si el estado no es CONFIRMADA o no es el mismo día.
     */
    public void iniciar(LocalDateTime ahora) throws TransicionInvalidaException {
        if (ahora == null) {
            throw new DatoInvalidoException("Debe indicar la fecha y hora de inicio");
        }
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new TransicionInvalidaException(estado, EstadoReserva.EN_CURSO);
        }
        if (!ahora.toLocalDate().equals(fechaInicio.toLocalDate())) {

            throw new TransicionInvalidaException("Solo se puede iniciar el mismo día de la reserva");
        }
        estado = EstadoReserva.EN_CURSO;
    }

    /**
     * Finaliza el viaje pasando de EN_CURSO a CERRADA y registra la fecha de cierre.
     * @param ahora fecha y hora de finalización.
     * @throws TransicionInvalidaException si la reserva no está EN_CURSO.
     */
    public void cerrar(LocalDateTime ahora) throws TransicionInvalidaException {
        if (ahora == null) {
            throw new DatoInvalidoException("Debe indicar la fecha y hora de cierre");
        }
        if (estado != EstadoReserva.EN_CURSO) {
            throw new TransicionInvalidaException(estado, EstadoReserva.CERRADA);
        }

        fechaCierre = ahora;
        estado = EstadoReserva.CERRADA;
    }


    /**
     * Reprograma la fecha de salida y/o el bus asignado sin cambiar el estado actual.
     * @param nuevoInicio nueva fecha y hora de salida.
     * @param nuevoBus nuevo vehículo asignado.
     * @throws TransicionInvalidaException si la reserva no está CREADA o CONFIRMADA.
     */
    public void reprogramar(LocalDateTime nuevoInicio, Bus nuevoBus) throws TransicionInvalidaException {
        if (estado != EstadoReserva.CREADA && estado != EstadoReserva.CONFIRMADA) {
            throw new TransicionInvalidaException("Solo se reprograma reservas CREADA o CONFIRMADA");
        }
        if (nuevoInicio == null || nuevoBus == null) {
            throw new DatoInvalidoException("La nueva fecha y el nuevo bus son obligatorios");
        }
        if (nuevoInicio.isBefore(LocalDateTime.now())) {
            throw new DatoInvalidoException("No se puede reprogramar a una fecha pasada");
        }
        validarHorario(nuevoInicio);
        servicio.validarBus(nuevoBus);
        this.bus = nuevoBus;
        this.fechaInicio = nuevoInicio;
        this.fechaFin = nuevoInicio.plusHours(servicio.getDuracionHoras());
        recalcularValor();
    }

    /**
     * Determina si la franja horaria de esta reserva se cruza con otro intervalo.
     * Las reservas contiguas no se consideran cruzadas.
     * @param otroInicio inicio del otro intervalo.
     * @param otroFin fin del otro intervalo.
     * @return {@code true} si existe solapamiento horario; {@code false} en caso contrario.
     */
    public boolean seCruzaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
        if (otroInicio == null || otroFin == null || !otroFin.isAfter(otroInicio)) {
            throw new DatoInvalidoException("El intervalo de fechas no es válido");
        }

        return fechaInicio.isBefore(otroFin) && otroInicio.isBefore(fechaFin);
    }

    /**
     * Indica si la reserva mantiene ocupado el bus (toda reserva no cancelada).
     * @return {@code true} si el estado es distinto de CANCELADA.
     */
    public boolean ocupaBus(){
        return  estado != EstadoReserva.CANCELADA;
    }


}
