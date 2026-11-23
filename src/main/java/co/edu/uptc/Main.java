package co.edu.uptc;

import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.vista.MenuConsola;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path archivo = Path.of("datos", "reservas.txt");
        SistemaTransporte sistema = new SistemaTransporte(archivo);
        MenuConsola menu = new MenuConsola(sistema);
        menu.iniciar();
    }
}
