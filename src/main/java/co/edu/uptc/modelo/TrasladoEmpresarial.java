package co.edu.uptc.modelo;

public class TrasladoEmpresarial extends Servicio{
    private int horasDeEspera;

    public TrasladoEmpresarial(int cantidadHoras, int horasDeEspera){
        super(cantidadHoras);
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
