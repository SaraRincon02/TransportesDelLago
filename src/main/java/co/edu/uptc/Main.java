package co.edu.uptc;

import co.edu.uptc.presentacion.MenuConsola;

/**
 * Punto de entrada principal de la aplicación Transportes del Lago.
 * Inicia la interfaz de usuario por consola.
 */
public class Main {
    /**
     * Método principal que arranca el menú de consola del sistema.
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        MenuConsola menu = new MenuConsola();
        menu.iniciar();
    }
}