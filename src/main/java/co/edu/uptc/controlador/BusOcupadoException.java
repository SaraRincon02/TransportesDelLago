package co.edu.uptc.controlador;

public class BusOcupadoException extends Exception {
    public BusOcupadoException(String placa) {

        super("El bus con placa " + placa + " ya tiene un viaje programado en esa franja horaria.");
    }
}
