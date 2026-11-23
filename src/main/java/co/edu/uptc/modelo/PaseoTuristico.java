package co.edu.uptc.modelo;
/**
 * Servicio de paseo turístico cobrado por kilómetro recorrido (ida y vuelta).
 */
public class PaseoTuristico extends Servicio{

    public static final double TARIFA_KM = 4500;

    private double kilometros;

    /**
     * Crea un paseo turístico validando duración (4 a 12 horas) y kilómetros positivos.
     * @param cantidadHoras duración en horas.
     * @param kilometros distancia total ida y vuelta en kilómetros.
     */
    public PaseoTuristico(int cantidadHoras, double kilometros){
        super(cantidadHoras);

        if (duracionHoras < 4 || duracionHoras > 12){
            throw new DatoInvalidoException("El paseo turístico debe de durar entre 4 y 12 horas");
        }
        if (kilometros <= 0){
            throw new DatoInvalidoException("Los kilómetros deben ser mayores a cero.");
        }

        this.kilometros = kilometros;
    }

    /** @return kilómetros totales del recorrido. */
    public double getKilometros() {
        return kilometros;
    }

    @Override
    public double calcularTarifaBase() {
        return kilometros * TARIFA_KM;
    }

    @Override
    public String getNombre() {
        return "Paseo turistico";
    }

    @Override
    public String getTipoPersistencia() {
        return "PASEO_TURISTICO";
    }

    @Override
    public double getParametro() {
        return kilometros;
    }
}
