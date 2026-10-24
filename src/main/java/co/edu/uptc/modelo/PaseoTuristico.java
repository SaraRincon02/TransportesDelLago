package co.edu.uptc.modelo;

public class PaseoTuristico extends Servicio{
    private double kilometros;

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

    public double getKilometros() {
        return kilometros;
    }

    @Override
    public double calcularTarifaBase() {
        return getKilometros() * 4500;
    }
}
