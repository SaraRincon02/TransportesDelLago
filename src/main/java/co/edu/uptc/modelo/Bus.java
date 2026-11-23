package co.edu.uptc.modelo;
/**
 * Representa un vehículo de la flota de Transportes del Lago.
 */
public class Bus {
    private String codigo;
    private String placa;
    private int capacidad;

    /**
     * Crea un nuevo bus validando que sus datos sean obligatorios y su capacidad positiva.
     * @param codigo identificador interno del bus (ej. BUS-01).
     * @param placa placa única del vehículo.
     * @param capacidad número máximo de pasajeros permitidos.
     * @throws DatoInvalidoException si el código o placa están vacíos, o la capacidad es menor o igual a cero.
     */
    public Bus(String codigo, String placa, int capacidad){
        if (codigo == null || codigo.trim().isEmpty()){
            throw new DatoInvalidoException("El codigo del bus no puede estar vacio.");
        }
        if (placa == null || placa.trim().isEmpty()){
            throw new DatoInvalidoException("La placa del bus no puede estar vacia.");
        }
        if (capacidad <= 0){
            throw new DatoInvalidoException("La capacidad del bus debe ser mayor a cero.");
        }
        this.codigo = codigo;
        this.placa = placa;
        this.capacidad = capacidad;
    }

    /** @return código identificador del bus. */
    public String getCodigo() {
        return codigo;
    }

    /** @return placa del vehículo. */
    public String getPlaca() {
        return placa;
    }

    /** @return capacidad máxima de pasajeros del bus. */
    public int getCapacidad() {
        return capacidad;
    }

}
