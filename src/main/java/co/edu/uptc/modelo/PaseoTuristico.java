package co.edu.uptc.modelo;

public class PaseoTuristico extends Servicio{
    private double kilometros;

    public PaseoTuristico(int cantidadHoras, double kilometros){
        super(cantidadHoras);
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
