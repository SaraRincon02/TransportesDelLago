package co.edu.uptc.vista;

import co.edu.uptc.controlador.BusOcupadoException;
import co.edu.uptc.controlador.ContratanteNoEncontradoException;
import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.modelo.Bus;
import co.edu.uptc.modelo.DatoInvalidoException;
import co.edu.uptc.modelo.Reserva;
import co.edu.uptc.modelo.TransicionInvalidaException;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MenuConsola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA_SOLA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TITULO = "Transportes del Lago";

    private SistemaTransporte sistema;

    public  MenuConsola(SistemaTransporte sistema){
        if (sistema == null) {
            throw new DatoInvalidoException("El sistema de transporte es obligatorio");
        }
        this.sistema = sistema;
    }

    public void iniciar(){
        try {
            sistema.cargar();
        }catch (DatoInvalidoException e){
            avisar("Iniciando sistema" + e.getMessage());
        }

        String opcion = "";
        while (!"0".equals(opcion)) {
            String menu = "=== " + TITULO + " ===\n"
                    + "1. Ver todas las reservas\n"
                    + "2. Crear nueva reserva\n"
                    + "3. Reprogramar reserva\n"
                    + "4. Cancelar reserva\n"
                    + "5. Confirmar reserva\n"
                    + "6. Iniciar reserva\n"
                    + "7. Cerrar reserva\n"
                    + "8. Reportes y estadísticas\n"
                    + "9. Consultar agenda de un bus\n"
                    + "10. Historial de un cliente\n"
                    + "11. Ver datos del sistema\n"
                    + "12. Crear bus\n"
                    + "13. Crear contratante\n"
                    + "14. Crear encomienda\n"
                    + "15. Ver seguro y póliza (excursión/encomienda)\n"
                    + "0. Salir y guardar\n"
                    + "Elija una opción:";

            String input = JOptionPane.showInputDialog(null, menu, TITULO, JOptionPane.PLAIN_MESSAGE);

            if (input == null) {
                opcion = "0";
            } else if (input.trim().isEmpty()) {
                avisar("Debe elegir una opción del menú");
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
                        confirmarReserva();
                        break;
                    case "6":
                        iniciarReserva();
                        break;
                    case "7":
                        cerrarReserva();
                        break;
                    case "8":
                        mostrarReportes();
                        break;
                    case "9":
                        consultarAgenda();
                        break;
                    case "10":
                        historialCliente();
                        break;
                    case "11":
                        mostrarDatosSistema();
                        break;
                    case "12":
                        crearBus();
                        break;
                    case "13":
                        crearContratante();
                        break;
                    case "14":
                        crearEncomienda();
                        break;
                    case "15":
                        verSeguroPoliza();
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
                e.printStackTrace();
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
                    + "Cliente: " + r.getContratante().getNombre()
                    + " (Doc: " + r.getContratante().getDocumento() + ")\n"
                    + "Bus: " + r.getBus().getPlaca() + "\n"
                    + "Servicio: " + r.getServicio().getNombre() + "\n"
                    + "Inicio: " + r.getFechaInicio().format(FECHA) + "\n"
                    + "Fin: " + r.getFechaFin().format(FECHA)+ "\n"
                    + "Estado: " + r.getEstado().getTexto() + "\n"
                    + "Valor: $" + r.getValorFinal() + "\n"
                    + "Penalidad: $" + r.getPenalidad()+ "\n"
                    + "Cierre: " + r.getFechaCierre().format(FECHA) +"\n"
                    + "--------------------------------------------------\n";
        }


        JTextArea area = new JTextArea(texto, 25, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane desplazamiento = new JScrollPane(area);

        JOptionPane.showMessageDialog(
                null,
                desplazamiento,
                "Lista de reservas",
                JOptionPane.INFORMATION_MESSAGE
        );
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

            int nuevoId = sistema.getReservas().size() + 1;

            Reserva nueva = new Reserva(nuevoId, contratante, bus, servicio, fechaInicio);
            int resp = JOptionPane.showConfirmDialog(null,
                    "Valor calculado: $" + nueva.getValorFinal() + "\n¿Desea guardar la reserva?",
                    TITULO, JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                sistema.agregarReserva(nueva);
                mostrarMensaje("Reserva creada con ID " + nueva.getId(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                avisar("Reserva no guardada.");
            }

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

            reserva.cancelar(LocalDateTime.now());
            mostrarMensaje("Reserva cancelada con éxito.", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            avisar("El ID debe ser un número entero.");
        } catch (Exception e) { // Atrapa la TransicionInvalida si intentan cancelar una reserva que ya está cancelada o cerrada
            avisar("No se pudo cancelar: " + e.getMessage());
        }
    }

    private void confirmarReserva() {
        try {
            int id = Integer.parseInt(preguntar("ID a confirmar:"));
            sistema.confirmarReserva(id);
            mostrarMensaje("Reserva confirmada.", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) { avisar("ID inválido."); }
        catch (Exception e) { avisar("No se pudo confirmar: " + e.getMessage()); }
    }
    private void iniciarReserva() {
        try {
            int id = Integer.parseInt(preguntar("ID a iniciar:"));
            sistema.iniciarReserva(id, LocalDateTime.now());
            mostrarMensaje("Reserva en curso.", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) { avisar("ID inválido."); }
        catch (Exception e) { avisar("No se pudo iniciar: " + e.getMessage()); }
    }

    private void cerrarReserva() {
        try {
            int id = Integer.parseInt(preguntar("ID a cerrar:"));
            sistema.cerrarReserva(id, LocalDateTime.now());
            mostrarMensaje("Reserva cerrada.", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) { avisar("ID inválido."); }
        catch (Exception e) { avisar("No se pudo cerrar: " + e.getMessage()); }
    }



    private void mostrarReportes() {
        String texto = "=== REPORTES FINANCIEROS ===\n\n";

        String pr = preguntar("Reporte de ingresos.\nFecha inicial (dd/MM/yyyy HH:mm):");
        if (pr == null) return;
        String pre = preguntar("Fecha final (dd/MM/yyyy HH:mm):");
        if (pre == null) return;

        try {
            LocalDateTime ini = LocalDateTime.parse(pr, FECHA);
            LocalDateTime fin = LocalDateTime.parse(pre, FECHA);
            texto += "Ingresos entre fechas: $" + sistema.calcularIngresos(ini, fin) + "\n";
        } catch (DateTimeParseException e) {
            texto += "Ingresos: Formato de fecha invalido";
        }

        texto += "Total Penalidades Acumuladas: $" + sistema.calcularTotalPenalidades() + "\n\n";

        var busEstrella = sistema.obtenerBusMasUsado();
        texto += "Bus más usado: " + (busEstrella != null ? busEstrella.getPlaca() : "Aún no hay registros") + "\n\n";

        texto += "--- Conteo por Estado ---\n";
        int[] conteos = sistema.contarReservasPorEstado();
        var estados = co.edu.uptc.modelo.EstadoReserva.values();
        for (int i = 0; i < estados.length; i++) {
            texto += estados[i].getTexto() + ": " + conteos[i] + "\n";
        }

        mostrarMensaje(texto, JOptionPane.INFORMATION_MESSAGE);
    }

    private void consultarAgenda() {
        String texto = "=== AGENDA ===\n\n";

        String placa = preguntar("Placa del bus:");
        if (placa == null) return;
        String f = preguntar("Fecha (dd/MM/yyyy):");
        if (f == null) return;
        try {
            LocalDate fecha = LocalDate.parse(f, FECHA_SOLA);
            List<Reserva> agenda = sistema.consultarAgendaBus(placa, fecha);
            if (agenda.isEmpty()) { avisar("No hay reservas ese día para el bus " + placa); return; }
            for (Reserva r : agenda) {
                texto+= r.getFechaInicio().format(FECHA) + "\n"
                        +r.getFechaFin().format(FECHA) + "\n"
                        +r.getServicio().getNombre()  + "\n"
                        +r.getEstado().getTexto()  + "\n";
            }
        } catch (DateTimeParseException e) { avisar("Fecha inválida."); }
    }

    private void historialCliente() throws ContratanteNoEncontradoException {
        String texto = "=== HISTORIAL ===\n\n";
        String doc = preguntar("Documento del cliente:");
        if (doc == null) return;
        List<Reserva> hist = sistema.historialCliente(doc);
        if (hist.isEmpty()) {
            avisar("El cliente no tiene reservas.");
            return;
        }
        for (Reserva r : hist) {
            texto+= "ID " +r.getId() + "\n"
                    +r.getFechaInicio().format(FECHA) + "\n"
                    +r.getServicio().getNombre() + "\n"
                    +r.getEstado().getTexto() + "\n"
                    +r.getValorFinal() + "\n";
        }
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
    private void crearBus() {
        String cod = preguntar("Código del bus (ej BUS-07):");
        if (cod == null) return;
        String placa = preguntar("Placa:");
        if (placa == null) return;
        try {
            int cap = Integer.parseInt(preguntar("Capacidad:"));
            sistema.agregarBus(new Bus(cod, placa, cap));
            mostrarMensaje("Bus creado.", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) { avisar("Capacidad inválida."); }
    }

    private void crearContratante() {
        String doc = preguntar("Documento/NIT:");
        if (doc == null) return;



}
