package co.edu.uptc.modelo;

public abstract class Servicio {

    protected int duracionHoras;

    public Servicio(int duracionHoras){
        this.duracionHoras = duracionHoras;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public abstract double calcularTarifaBase();
}
