package co.edu.uptc.vista;

import co.edu.uptc.controlador.BusOcupadoException;
import co.edu.uptc.controlador.ContratanteNoEncontradoException;
import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.modelo.DatoInvalidoException;
import co.edu.uptc.modelo.Reserva;
import co.edu.uptc.modelo.TransicionInvalidaException;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MenuConsola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TITULO = "Transportes del Lago";

    private SistemaTransporte sistema;

    public  MenuConsola(SistemaTransporte sistema){
        this.sistema = sistema;
    }

    public void iniciar(){
        try {
            sistema.cargar();
        }catch (DatoInvalidoException e){
            avisar("Iniciando sistema." + e.getMessage());
        }

        String opcion = "";
        while (!"0".equals(opcion)) {
            String menu = "=== " + TITULO + " ===\n"
                    + "1. Ver todas las reservas\n"
                    + "2. Crear nueva reserva\n"
                    + "3. Reprogramar reserva\n"
                    + "4. Cancelar reserva\n"
                    + "5. Reportes y Estadísticas\n"
                    + "6. Precargar datos del Sistema\n"
                    + "7. Ver datos del sistema (Buses, Clientes, etc.)\n"
                    + "0. Salir y Guardar\n"
                    + "Elija una opción:";

            String input = JOptionPane.showInputDialog(null, menu, TITULO, JOptionPane.PLAIN_MESSAGE);

            if (input == null) {
                opcion = "0";
            } else if (input.trim().isEmpty()) {
                avisar("Debe elegir una opción del menú.");
                opcion = "";
                continue;
            } else {
                opcion = input.trim();
            }

            try {
                switch (opcion) {
                    case "1":
                        mostrarReservas();
                        break;
                    case "2":
                        crearReserva();
                        break;
                    case "3":
                        reprogramarReserva();
                        break;
                    case "4":
                        cancelarReserva();
                        break;
                    case "5":
                        mostrarReportes();
                        break;
                    case "6":
                        sistema.precargarDatos();
                        mostrarMensaje("Buses, clientes y servicios precargados con exito.", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case "7":
                        mostrarDatosSistema();
                        break;
                    case "0":
                        sistema.guardar();
                        mostrarMensaje("Datos guardados exitosamente. ¡Hasta pronto!", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    default:
                        avisar("Opción inválida.");
                }
            } catch (BusOcupadoException | ContratanteNoEncontradoException | TransicionInvalidaException | DatoInvalidoException e) {
                avisar("Alerta de negocio: " + e.getMessage());
            } catch (Exception e) {
                avisar("Error inesperaado: " + e.getMessage());
            }
        }
    }

    private String preguntar(String mensaje) {
        String input = JOptionPane.showInputDialog(null, mensaje, TITULO, JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarMensaje(String mensaje, int tipoMensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, tipoMensaje);
    }

    private void mostrarReservas() {
        if (sistema.getReservas().isEmpty()) {
            avisar("No hay reservas registradas en el sistema.");
            return;
        }

        String texto = "=== LISTA DE RESERVAS ===\n\n";

        for (Reserva r : sistema.getReservas()) {
            texto += "ID: " + r.getId() + "\n"
                    + "Cliente: " + r.getContratante().getNombre() + " (Doc: " + r.getContratante().getDocumento() + ")\n"
                    + "Bus: " + r.getBus().getPlaca() + "\n"
                    + "Servicio: " + r.getServicio().getNombre() + "\n"
                    + "Inicio: " + r.getFechaInicio().format(FECHA) + "\n"
                    + "Estado: " + r.getEstado().getTexto() + "\n"
                    + "--------------------------------------------------\n";
        }

        mostrarMensaje(texto, JOptionPane.INFORMATION_MESSAGE);
    }

    private void crearReserva() throws BusOcupadoException, ContratanteNoEncontradoException {
        String docCliente = preguntar("Ingrese el documento del contratante:");
        if (docCliente == null) return;
        var contratante = sistema.buscarContratante(docCliente);

        String placaBus = preguntar("Ingrese la placa del bus:");
        if (placaBus == null) return;
        var bus = sistema.buscarBus(placaBus);
        if (bus == null) {
            avisar("Bus no encontrado en el sistema.");
            return;
        }

        String nomServicio = preguntar("Ingrese el tipo de servicio\n(Ej: Paseo turistico, Traslado empresarial, Excursion escolar):");
        if (nomServicio == null) return;
        var servicio = sistema.buscarServicio(nomServicio);
        if (servicio == null) {
            avisar("Servicio no encontrado.");
            return;
        }

        String strFecha = preguntar("Ingrese fecha y hora de inicio (dd/MM/yyyy HH:mm):");
        if (strFecha == null) return;

        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(strFecha, FECHA);

            // Generamos un ID autoincremental sencillo
            int nuevoId = sistema.getReservas().size() + 1;

            Reserva nueva = new Reserva(nuevoId, contratante, bus, servicio, fechaInicio);
            sistema.agregarReserva(nueva);

            mostrarMensaje("¡Reserva ID " + nuevoId + " creada con éxito!", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            avisar("Formato de fecha inválido. Recuerde usar el formato: dd/MM/yyyy HH:mm");
        }
    }

    private void reprogramarReserva() throws BusOcupadoException, TransicionInvalidaException {
        try {
            String idStr = preguntar("Ingrese el ID de la reserva a reprogramar:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            Reserva reserva = sistema.buscarReserva(id);
            if (reserva == null) {
                avisar("No existe una reserva con ese ID.");
                return;
            }

            String nuevaPlaca = preguntar("Ingrese la nueva placa del bus (o la misma si no cambia):");
            if (nuevaPlaca == null) return;
            var nuevoBus = sistema.buscarBus(nuevaPlaca);
            if (nuevoBus == null) {
                avisar("Bus no encontrado.");
                return;
            }

            String nuevaFechaStr = preguntar("Ingrese la nueva fecha de inicio (dd/MM/yyyy HH:mm):");
            if (nuevaFechaStr == null) return;
            LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr, FECHA);

            sistema.reprogramarReserva(reserva, nuevaFecha, nuevoBus);
            mostrarMensaje("Reserva reprogramada exitosamente.", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            avisar("El ID debe ser un número entero.");
        } catch (DateTimeParseException e) {
            avisar("Formato de fecha inválido. Recuerde usar el formato: dd/MM/yyyy HH:mm");
        }
    }

    private void cancelarReserva() {
        try {
            String idStr = preguntar("Ingrese el ID de la reserva a cancelar:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            Reserva reserva = sistema.buscarReserva(id);
            if (reserva == null) {
                avisar("No existe una reserva con ese ID.");
                return;
            }

            // Llamamos al método de la clase Reserva
            reserva.cancelar(LocalDateTime.now());
            mostrarMensaje("Reserva cancelada con éxito.", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            avisar("El ID debe ser un número entero.");
        } catch (Exception e) { // Atrapa la TransicionInvalida si intentan cancelar una reserva que ya está cancelada o cerrada
            avisar("No se pudo cancelar: " + e.getMessage());
        }
    }

    private void mostrarReportes() {
        String texto = "=== REPORTES FINANCIEROS ===\n\n";

        texto += "Total Penalidades Acumuladas: $" + sistema.calcularTotalPenalidades() + "\n\n";

        var busEstrella = sistema.obtenerBusMasUsado();
        texto += "Bus más usado: " + (busEstrella != null ? busEstrella.getPlaca() : "Aún no hay registros") + "\n\n";

        texto += "--- Conteo por Estado ---\n";
        int[] conteos = sistema.contarReservasPorEstado();
        // Usamos el Enum para imprimir dinámicamente los estados y sus contadores
        var estados = co.edu.uptc.modelo.EstadoReserva.values();
        for (int i = 0; i < estados.length; i++) {
            texto += estados[i].getTexto() + ": " + conteos[i] + "\n";
        }

        mostrarMensaje(texto, JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDatosSistema() {
        String texto = "=== DATOS ACTUALES DEL SISTEMA ===\n\n";

        texto += "--- BUSES ---\n";
        if (sistema.getBuses().isEmpty()) {
            texto += "No hay buses registrados.\n";
        } else {
            for (co.edu.uptc.modelo.Bus b : sistema.getBuses()) {
                texto += "Placa: " + b.getPlaca() + " | Capacidad: " + b.getCapacidad() + " | Cód: " + b.getCodigo() + "\n";
            }
        }
        texto += "\n";

        texto += "--- CLIENTES (CONTRATANTES) ---\n";
        if (sistema.getContratantes().isEmpty()) {
            texto += "No hay clientes registrados.\n";
        } else {
            for (co.edu.uptc.modelo.Contratante c : sistema.getContratantes()) {
                texto += "Doc: " + c.getDocumento() + " | Nombre: " + c.getNombre() + " | Tel: " + c.getTelefono() + "\n";
            }
        }
        texto += "\n";

        texto += "--- SERVICIOS DISPONIBLES ---\n";
        if (sistema.getServicios().isEmpty()) {
            texto += "No hay servicios registrados.\n";
        } else {
            for (co.edu.uptc.modelo.Servicio s : sistema.getServicios()) {
                texto += "Nombre: " + s.getNombre() + " | Duración: " + s.getDuracionHoras() + " horas\n";
            }
        }
        texto += "\n";

        texto += "--- ENCOMIENDAS ---\n";
        if (sistema.getEncomiendas().isEmpty()) {
            texto += "No hay encomiendas registradas.\n";
        } else {
            for (co.edu.uptc.modelo.Encomienda e : sistema.getEncomiendas()) {
                texto += "Cód: " + e.getCodigo() + " | Remitente: " + e.getRemitente() + " | Dest: " + e.getDestinatario() + "\n";
            }
        }
        mostrarMensaje(texto, JOptionPane.INFORMATION_MESSAGE);
    }

}
