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

    public abstract String getNombre();

    public abstract String getTipoPersistencia();

    public abstract double getParametro();

    public void validarBus(Bus bus) {
        if (bus == null) {
            throw new DatoInvalidoException("El bus es obligatorio.");
        }
    }
}
