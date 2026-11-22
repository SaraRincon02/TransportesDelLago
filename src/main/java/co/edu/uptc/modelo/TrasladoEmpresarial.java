package co.edu.uptc.modelo;

public class TrasladoEmpresarial extends Servicio{

    public static final double TARIFA_FIJA = 250000;
    public static final double TARIFA_HORA_ESPERA = 40000;

    private int horasDeEspera;

    public TrasladoEmpresarial(int cantidadHoras, int horasDeEspera){
        super(cantidadHoras);

        if (duracionHoras < 2 || duracionHoras > 8){
            throw new DatoInvalidoException("El traslado empresarial debe de durar entre 2 y 8 horas.");
        }
        if (horasDeEspera < 0){
            throw new DatoInvalidoException("Las horas de espera no pueden ser negativas.");
        }
        this.horasDeEspera = horasDeEspera;
    }

    public int getHorasDeEspera() {
        return horasDeEspera;
    }

    @Override
    public double calcularTarifaBase() {
        return TARIFA_FIJA + (TARIFA_HORA_ESPERA * horasDeEspera);
    }

    @Override
    public String getNombre() {
        return "Traslado empresarial";
    }

    @Override
    public String getTipoPersistencia() {
        return "TRASLADO_EMPRESARIAL";
    }

    @Override
    public double getParametro() {
        return horasDeEspera;
    }
}
