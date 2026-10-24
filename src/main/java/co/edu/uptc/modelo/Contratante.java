package co.edu.uptc.modelo;

import java.time.LocalDate;

public class Contratante {
    private String documento;
    private String nombre;
    private String telefono;
    private LocalDate fechaRegistro;

    public Contratante(String documento, String nombre, String telefono, LocalDate fechaRegistro){
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
}
