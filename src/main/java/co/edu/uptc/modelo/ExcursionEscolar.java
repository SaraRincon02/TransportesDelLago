package co.edu.uptc.modelo;

public class ExcursionEscolar extends Servicio implements Asegurable{
    private int cantidadEstudiantes;

    public ExcursionEscolar(int duracionHoras, int cantidadEstudiantes, Bus busAsignado){
        super(duracionHoras);

        if (duracionHoras < 4 || duracionHoras > 10){
            throw new DatoInvalidoException("La excursión escolar debe durar entre 4 y 10 horas.");
        }
        if (cantidadEstudiantes <= 0){
            throw new DatoInvalidoException("La cantidad de estudiantes debe ser mayor a cero.");
        }
        if (cantidadEstudiantes > busAsignado.getCapacidad()){
            throw new DatoInvalidoException("La cantidad de estudiantes supera la capacidad del bus.");
        }

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
