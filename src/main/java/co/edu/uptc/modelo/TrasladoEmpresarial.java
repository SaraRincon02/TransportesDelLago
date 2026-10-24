package co.edu.uptc.modelo;

public class TrasladoEmpresarial extends Servicio{
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
        return 250000 + (40000 * getHorasDeEspera());
    }
}
