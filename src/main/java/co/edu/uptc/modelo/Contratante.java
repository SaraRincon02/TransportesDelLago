package co.edu.uptc.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Contratante {

    public static final int DESCUENTO_POR_ANIO = 2;
    public static final int DESCUENTO_MAXIMO = 15;

    private String documento;
    private String nombre;
    private String telefono;
    private LocalDate fechaRegistro;

    public Contratante(String documento, String nombre, String telefono, LocalDate fechaRegistro){
        if (documento == null || documento.trim().isEmpty()){
            throw new DatoInvalidoException("El documento del contratante no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()){
            throw new DatoInvalidoException("El nombre no puede ir vacío.");
        }
        if (fechaRegistro == null){
            throw new DatoInvalidoException("La fecha desde la que contrata no puede ser nula.");
        }

        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public int calcularAntiguedad(LocalDate fecha){
        long anios = ChronoUnit.YEARS.between(fechaRegistro, fecha);
        return (int) Math.max(0, anios);
    }

    public int calcularPorccentajeDescuento(LocalDate fecha){
        return Math.max(DESCUENTO_MAXIMO, calcularAntiguedad(fecha) * DESCUENTO_POR_ANIO);
    }
}
