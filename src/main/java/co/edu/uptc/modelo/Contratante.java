package co.edu.uptc.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
/**
 * Representa un cliente (colegio, empresa o familia) que contrata servicios de transporte.
 * Gestiona el cálculo de antigüedad y porcentaje de descuento aplicable.
 */
public class Contratante {

    public static final int DESCUENTO_POR_ANIO = 2;
    public static final int DESCUENTO_MAXIMO = 15;

    private String documento;
    private String nombre;
    private String telefono;
    private LocalDate fechaRegistro;

    /**
     * Construye un nuevo contratante validando sus datos básicos.
     * @param documento documento de identidad o NIT único.
     * @param nombre nombre o razón social del contratante.
     * @param telefono número telefónico de contacto.
     * @param fechaRegistro fecha desde la cual trabaja con la empresa.
     * @throws DatoInvalidoException si el documento, nombre o fecha de registro son nulos o vacíos.
     */
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

    /** @return documento o NIT del contratante. */
    public String getDocumento() {
        return documento;
    }

    /** @return nombre del contratante. */
    public String getNombre() {
        return nombre;
    }

    /** @return teléfono de contacto. */
    public String getTelefono() {
        return telefono;
    }

    /** @return fecha desde la cual contrata con la empresa. */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Calcula los años completos de antigüedad del contratante hasta una fecha dada.
     * @param fecha fecha de referencia (normalmente la fecha del viaje).
     * @return cantidad de años cumplidos (mínimo 0).
     * @throws DatoInvalidoException si la fecha de referencia es nula.
     */
    public int calcularAntiguedad(LocalDate fecha){
        if (fecha ==null ){
            throw new DatoInvalidoException("La fecha para calcular antigüedad es obligatoria");
        }
        long anios = ChronoUnit.YEARS.between(fechaRegistro, fecha);
        return (int) Math.max(0, anios);
    }

    /**
     * Calcula el porcentaje de descuento (2% por año cumplido, con tope máximo del 15%).
     * @param fecha fecha de referencia para evaluar la antigüedad.
     * @return porcentaje entero de descuento entre 0 y 15.
     */
    public int calcularPorccentajeDescuento(LocalDate fecha){
        return Math.min(DESCUENTO_MAXIMO, calcularAntiguedad(fecha) * DESCUENTO_POR_ANIO);
    }
}
