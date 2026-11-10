package co.edu.uptc.modelo;

public abstract class Servicio {

    protected int duracionHoras;

    public Servicio(int duracionHoras){
        if (duracionHoras <= 0){
            throw new DatoInvalidoException("La duracion debe ser mayor a cero.");
        }
        this.duracionHoras = duracionHoras;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public abstract double calcularTarifaBase();
}
