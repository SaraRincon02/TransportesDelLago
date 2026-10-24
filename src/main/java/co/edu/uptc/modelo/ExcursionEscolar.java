package co.edu.uptc.modelo;

public class ExcursionEscolar extends Servicio implements Asegurable{
    private int cantidadEstudiantes;

    public ExcursionEscolar(int duracionHoras, int cantidadEstudiantes){
        super(duracionHoras);
        this.cantidadEstudiantes = cantidadEstudiantes;
    }

    public int getCantidadEstudiantes() {
        return cantidadEstudiantes;
    }

    @Override
    public double calcularTarifaBase() {
        return getCantidadEstudiantes() * 18000;
    }

    @Override
    public double calcularValorSeguro() {
        return getCantidadEstudiantes() * 3500;
    }

    @Override
    public String obtenerDescripcionPoliza() {
        return "Póliza estudiantil para " + cantidadEstudiantes + " pasajeros.";
    }
}
