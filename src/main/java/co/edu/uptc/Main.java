package co.edu.uptc;

import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.vista.MenuConsola;

public class Main {
    public static void main(String[] args) {
        SistemaTransporte sistema = new SistemaTransporte();
        MenuConsola menu = new MenuConsola(sistema);
        menu.iniciar();
    }
}
