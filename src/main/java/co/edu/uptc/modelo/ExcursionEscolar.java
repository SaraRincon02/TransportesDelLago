package co.edu.uptc.modelo;
/**
 * Servicio de excursión escolar cobrado por estudiante. Implementa {@link Asegurable}.
 */
public class ExcursionEscolar extends Servicio implements Asegurable{

    public static final double TARIFA_ESTUDIANTE = 18000;
    public static final double SEGURO_ESTUDIANTE = 3500;

    private int cantidadEstudiantes;

    /**
     * Crea una excursión escolar validando duración (4 a 10 horas) y cantidad de estudiantes.
     * @param duracionHoras duración en horas.
     * @param cantidadEstudiantes número de estudiantes asistentes.
     */
    public ExcursionEscolar(int duracionHoras, int cantidadEstudiantes){
        super(duracionHoras);

        if (duracionHoras < 4 || duracionHoras > 10){
            throw new DatoInvalidoException("La excursión escolar debe durar entre 4 y 10 horas.");
        }
        if (cantidadEstudiantes <= 0){
            throw new DatoInvalidoException("La cantidad de estudiantes debe ser mayor a cero.");
        }

        this.cantidadEstudiantes = cantidadEstudiantes;
    }

    /** @return cantidad de estudiantes inscritos en la excursión. */
    public int getCantidadEstudiantes() {
        return cantidadEstudiantes;
    }

    @Override
    public double calcularTarifaBase() {
        return cantidadEstudiantes * TARIFA_ESTUDIANTE;
    }

    @Override
    public String getNombre() {
        return "Excursion escolar";
    }

    @Override
    public String getTipoPersistencia() {
        return "EXCURSION_ESCOLAR";
    }


    @Override
    public double getParametro() {
        return cantidadEstudiantes;
    }

    @Override
    public void validarBus(Bus bus) {
        super.validarBus(bus);
        if (cantidadEstudiantes > bus.getCapacidad()) {
            throw new DatoInvalidoException("La cantidad de estudiantes supera la capacidad del bus.");
        }
    }

    @Override
    public double calcularValorSeguro() {
        return cantidadEstudiantes * SEGURO_ESTUDIANTE;
    }

    @Override
    public String obtenerDescripcionPoliza() {
        return "Póliza estudiantil para " + cantidadEstudiantes + " estudiantes.";
    }
}
