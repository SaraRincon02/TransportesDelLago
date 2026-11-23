package co.edu.uptc.negocio;

public class BusOcupadoException extends Exception {
    public BusOcupadoException(String placa) {

        super("El bus con placa " + placa + " ya tiene un viaje programado en esa franja horaria.");
    }
}
