package co.edu.uptc.vista;

import co.edu.uptc.controlador.SistemaTransporte;

import java.time.format.DateTimeFormatter;

public class MenuConsola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TITULO = "Transportes del Lago";

    private SistemaTransporte sistema;

    public  MenuConsola(SistemaTransporte sistema){
        this.sistema = sistema;
    }


}
