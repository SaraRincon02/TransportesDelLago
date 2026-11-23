package co.edu.uptc.modelo;
/**
 * Clase abstracta base que modela los distintos tipos de servicios de transporte especial.
 */
public abstract class Servicio {

    protected int duracionHoras;

    /**
     * Inicializa la duración base del servicio.
     * @param duracionHoras duración del viaje en horas.
     * @throws DatoInvalidoException si la duración es menor o igual a cero.
     */
    public Servicio(int duracionHoras){
        if (duracionHoras <= 0){
            throw new DatoInvalidoException("La duracion debe ser mayor a cero.");
        }
        this.duracionHoras = duracionHoras;
    }

    /** @return duración del servicio en horas. */
    public int getDuracionHoras() {
        return duracionHoras;
    }

    /**
     * Calcula la tarifa base del servicio según su propia fórmula de negocio.
     * @return valor base del servicio en pesos.
     */
    public abstract double calcularTarifaBase();

    /** @return nombre legible del tipo de servicio. */
    public abstract String getNombre();

    /** @return identificador textual utilizado para guardar el servicio en archivo. */
    public abstract String getTipoPersistencia();

    /** @return parámetro numérico específico del servicio (kilómetros, estudiantes u horas de espera). */
    public abstract double getParametro();

    /**
     * Valida que el bus asignado cumpla las restricciones del servicio.
     * @param bus vehículo a asignar.
     * @throws DatoInvalidoException si el bus es nulo o no cumple los requisitos del servicio.
     */
    public void validarBus(Bus bus) {
        if (bus == null) {
            throw new DatoInvalidoException("El bus es obligatorio.");
        }
    }
}
