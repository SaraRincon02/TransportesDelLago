package co.edu.uptc.presentacion;

import co.edu.uptc.negocio.BusOcupadoException;
import co.edu.uptc.negocio.ContratanteNoEncontradoException;
import co.edu.uptc.negocio.SistemaTransporte;
import co.edu.uptc.modelo.*;

import javax.swing.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuConsola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA_SOLA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TITULO = "Transportes del Lago";
    private static final Path CARPETA_DATOS = Path.of("datos");

    private final Scanner scanner = new Scanner(System.in);
    private SistemaTransporte sistema;

    public MenuConsola() {

    }

    public void iniciar() {
        try {
            this.sistema = new SistemaTransporte(CARPETA_DATOS);
        } catch (DatoInvalidoException e) {
            System.out.println(" No se pudo iniciar el sistema: " + e.getMessage());
            System.out.println("   Revise la carpeta 'datos' y vuelva a intentar.");
            return;
        }

        String opcion = "";
        while (!"0".equals(opcion)) {
            System.out.println("\n=== " + TITULO + " ===");
            System.out.println("1. Ver todas las reservas");
            System.out.println("2. Crear nueva reserva");
            System.out.println("3. Reprogramar reserva");
            System.out.println("4. Cancelar reserva");
            System.out.println("5. Confirmar reserva");
            System.out.println("6. Iniciar reserva");
            System.out.println("7. Cerrar reserva");
            System.out.println("8. Reportes y estadísticas");
            System.out.println("9. Consultar agenda de un bus");
            System.out.println("10. Historial de un cliente");
            System.out.println("11. Ver datos del sistema");
            System.out.println("12. Crear bus");
            System.out.println("13. Crear contratante");
            System.out.println("14. Crear encomienda");
            System.out.println("15. Ver seguro y póliza");
            System.out.println("0. Salir y guardar");
            System.out.print("Elija una opción: ");

            opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> mostrarReservas();
                    case "2" -> crearReserva();
                    case "3" -> reprogramarReserva();
                    case "4" -> cancelarReserva();
                    case "5" -> confirmarReserva();
                    case "6" -> iniciarReserva();
                    case "7" -> cerrarReserva();
                    case "8" -> mostrarReportes();
                    case "9" -> consultarAgenda();
                    case "10" -> historialCliente();
                    case "11" -> mostrarDatosSistema();
                    case "12" -> crearBus();
                    case "13" -> crearContratante();
                    case "14" -> crearEncomienda();
                    case "15" -> verSeguroPoliza();
                    case "0" -> {
                        sistema.guardar();
                        System.out.println("Datos guardados exitosamente. ¡Hasta pronto!");
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (BusOcupadoException | ContratanteNoEncontradoException
                     | TransicionInvalidaException | DatoInvalidoException e) {
                System.out.println("Alerta de negocio: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
    }

    private String preguntar(String mensaje) {
        System.out.print(mensaje + " ");
        return scanner.nextLine().trim();
    }

    private void avisar(String mensaje) {
        System.out.println("Aviso: " + mensaje);
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private void mostrarReservas() {
        if (sistema.getReservas().isEmpty()) {
            avisar("No hay reservas registradas en el sistema.");
            return;
        }
        System.out.println("\n=== LISTA DE RESERVAS ===");
        for (Reserva r : sistema.getReservas()) {
            System.out.println("ID: " + r.getId());
            System.out.println("Cliente: " + r.getContratante().getNombre()
                    + " (Doc: " + r.getContratante().getDocumento() + ")");
            System.out.println("Bus: " + r.getBus().getPlaca());
            System.out.println("Servicio: " + r.getServicio().getNombre());
            System.out.println("Inicio: " + r.getFechaInicio().format(FECHA));
            System.out.println("Fin: " + r.getFechaFin().format(FECHA));
            System.out.println("Estado: " + r.getEstado().getTexto());
            System.out.println("Valor: $" + r.getValorFinal());
            System.out.println("Penalidad: $" + r.getPenalidad());
            System.out.println("Cierre: " + (r.getFechaCierre() == null ? "-" : r.getFechaCierre().format(FECHA)));
            System.out.println("--------------------------------------------------");
        }
    }

    private void crearReserva() throws ContratanteNoEncontradoException {
        String docCliente = preguntar("Ingrese el documento del contratante:");
        if (docCliente.isEmpty()) return;
        Contratante contratante = sistema.buscarContratante(docCliente);
        String placaBus = preguntar("Ingrese la placa del bus:");
        if (placaBus.isEmpty()) return;
        Bus bus = sistema.buscarBus(placaBus);

        System.out.println("Tipo de servicio:");
        System.out.println("1. Paseo turístico");
        System.out.println("2. Excursión escolar");
        System.out.println("3. Traslado empresarial");
        String tipo = preguntar("Elija una opción:");
        Servicio servicio = null;

        try {
            switch (tipo) {
                case "1" -> {
                    int horas = Integer.parseInt(preguntar("Duración en horas (4 a 12):"));
                    double km = Double.parseDouble(preguntar("Kilómetros (ida y vuelta):"));
                    servicio = new PaseoTuristico(horas, km);
                }
                case "2" -> {
                    int horas = Integer.parseInt(preguntar("Duración en horas (4 a 10):"));
                    int estudiantes = Integer.parseInt(preguntar("Cantidad de estudiantes:"));
                    servicio = new ExcursionEscolar(horas, estudiantes);
                }
                case "3" -> {
                    int horas = Integer.parseInt(preguntar("Duración en horas (2 a 8):"));
                    int espera = Integer.parseInt(preguntar("Horas de espera:"));
                    servicio = new TrasladoEmpresarial(horas, espera);
                }
                default -> {
                    avisar("Tipo de servicio inválido.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            avisar("Debe ingresar números válidos.");
            return;
        } catch (DatoInvalidoException e) {
            avisar("Datos del servicio inválidos: " + e.getMessage());
            return;
        }


        String strFecha = preguntar("Ingrese fecha y hora de inicio (dd/MM/yyyy HH:mm):");
        if (strFecha.isEmpty()) return;

        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(strFecha, FECHA);
            LocalDateTime fechaFin = fechaInicio.plusHours(servicio.getDuracionHoras());

            try {
                sistema.verificarCruce(bus, fechaInicio, fechaFin, null);
            } catch (BusOcupadoException e) {
                JOptionPane.showMessageDialog(null,
                        "El bus " + bus.getPlaca() + " ya tiene un viaje en esa franja.\n"
                                + "Elija otra hora u otro bus.",
                        "Bus ocupado", JOptionPane.WARNING_MESSAGE);
                return;
            }


            int nuevoId = sistema.generarIdReserva();
            Reserva nueva = new Reserva(nuevoId, contratante, bus, servicio, fechaInicio);

            int resp = JOptionPane.showConfirmDialog(null,
                    "Valor calculado: $" + nueva.getValorFinal()
                            + "\n\nCliente: " + contratante.getNombre()
                            + "\nBus: " + bus.getPlaca()
                            + "\nServicio: " + servicio.getNombre()
                            + "\nInicio: " + fechaInicio.format(FECHA)
                            + "\nFin: " + fechaFin.format(FECHA)
                            + "\n\n¿Desea guardar la reserva?",
                    "Confirmar reserva", JOptionPane.YES_NO_OPTION);

            if (resp == JOptionPane.YES_OPTION) {
                sistema.agregarReserva(nueva);
                sistema.guardar();
                mostrarMensaje("Reserva creada con ID " + nueva.getId());
            } else {
                avisar("Reserva no guardada.");
            }
        } catch (DateTimeParseException e) {
            avisar("Formato de fecha inválido. Recuerde usar el formato: dd/MM/yyyy HH:mm");
        } catch (BusOcupadoException e) {
            JOptionPane.showMessageDialog(null,
                    "El bus " + bus.getPlaca() + " ya tiene un viaje en esa franja.",
                    "Bus ocupado", JOptionPane.WARNING_MESSAGE);
        } catch (DatoInvalidoException e) {
            avisar("Datos inválidos: " + e.getMessage());
        }
    }
    private void reprogramarReserva() throws BusOcupadoException, TransicionInvalidaException {
        try {
            String idStr = preguntar("Ingrese el ID de la reserva a reprogramar:");
            if (idStr.isEmpty()) return;
            int id = Integer.parseInt(idStr);
            Reserva reserva = sistema.buscarReserva(id);

            System.out.println("¿Qué desea cambiar?");
            System.out.println("1. Solo fecha");
            System.out.println("2. Solo bus");
            System.out.println("3. Ambos");
            String opcion = preguntar("Elija una opción:");
            if (opcion.isEmpty()) return;

            LocalDateTime nuevaFecha = reserva.getFechaInicio();
            Bus nuevoBus = reserva.getBus();

            if (opcion.equals("1") || opcion.equals("3")) {
                String f = preguntar("Nueva fecha (dd/MM/yyyy HH:mm):");
                if (f.isEmpty()) return;
                nuevaFecha = LocalDateTime.parse(f, FECHA);
            }
            if (opcion.equals("2") || opcion.equals("3")) {
                String p = preguntar("Nueva placa:");
                if (p.isEmpty()) return;
                nuevoBus = sistema.buscarBus(p);
            }

            sistema.reprogramarReserva(reserva, nuevaFecha, nuevoBus);
            mostrarMensaje("Reserva reprogramada exitosamente");
        } catch (NumberFormatException e) {
            avisar("El ID debe ser un número entero");
        } catch (DateTimeParseException e) {
            avisar("Formato de fecha inválido. Recuerde usar el formato: dd/MM/yyyy HH:mm");
        }
    }


    private void cancelarReserva() {
        try {
            String idStr = preguntar("Ingrese el ID de la reserva a cancelar:");
            if (idStr.isEmpty()) return;
            int id = Integer.parseInt(idStr);
            Reserva reserva = sistema.buscarReserva(id);
            int resp = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro de cancelar la reserva ID " + reserva.getId()
                            + "?\n" + reserva.getFechaInicio().format(FECHA)
                            + " | " + reserva.getServicio().getNombre(),
                    "Confirmar cancelación", JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) {
                avisar("Cancelación abortada.");
                return;
            }
            sistema.cancelarReserva(id, LocalDateTime.now());
            mostrarMensaje("Reserva cancelada con éxito. Penalidad: $" + reserva.getPenalidad());
        } catch (NumberFormatException e) {
            avisar("El ID debe ser un número entero.");
        } catch (Exception e) {
            avisar("No se pudo cancelar: " + e.getMessage());
        }
    }


    private void confirmarReserva() {
        try {
            String idStr = preguntar("ID a confirmar:");
            if (idStr.isEmpty()) return;
            sistema.confirmarReserva(Integer.parseInt(idStr));
            mostrarMensaje("Reserva confirmada.");
        } catch (NumberFormatException e) {
            avisar("ID inválido.");
        } catch (Exception e) {
            avisar("No se pudo confirmar: " + e.getMessage());
        }
    }


    private void iniciarReserva() {
        try {
            String idStr = preguntar("ID a iniciar:");
            if (idStr.isEmpty()) return;
            sistema.iniciarReserva(Integer.parseInt(idStr), LocalDateTime.now());
            mostrarMensaje("Reserva en curso.");
        } catch (NumberFormatException e) {
            avisar("ID inválido.");
        } catch (Exception e) {
            avisar("No se pudo iniciar: " + e.getMessage());
        }
    }


    private void cerrarReserva() {
        try {
            String idStr = preguntar("ID a cerrar:");
            if (idStr.isEmpty()) return;
            sistema.cerrarReserva(Integer.parseInt(idStr), LocalDateTime.now());
            mostrarMensaje("Reserva cerrada.");
        } catch (NumberFormatException e) {
            avisar("ID inválido.");
        } catch (Exception e) {
            avisar("No se pudo cerrar: " + e.getMessage());
        }
    }

    private void mostrarReportes() {
        System.out.println("\n=== REPORTES FINANCIEROS ===");

        String fi = preguntar("Fecha inicial (dd/MM/yyyy HH:mm):");
        if (fi.isEmpty()) return;
        String ff = preguntar("Fecha final (dd/MM/yyyy HH:mm):");
        if (ff.isEmpty()) return;
        try {
            LocalDateTime ini = LocalDateTime.parse(fi, FECHA);
            LocalDateTime fin = LocalDateTime.parse(ff, FECHA);
            System.out.println("Ingresos entre fechas: $" + sistema.calcularIngresos(ini, fin));
        } catch (DateTimeParseException e) {
            System.out.println("Ingresos: formato de fecha inválido");
        }

        System.out.println("Total Penalidades Acumuladas: $" + sistema.calcularTotalPenalidades());

        Bus busEstrella = sistema.obtenerBusMasUsado();
        System.out.println("Bus más usado: " + (busEstrella != null ? busEstrella.getPlaca() : "Aún no hay registros"));

        System.out.println("--- Conteo por Estado ---");
        int[] conteos = sistema.contarReservasPorEstado();
        EstadoReserva[] estados = EstadoReserva.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println(estados[i].getTexto() + ": " + conteos[i]);
        }
    }


    private void consultarAgenda() {
        String placa = preguntar("Placa del bus:");
        if (placa.isEmpty()) return;
        String f = preguntar("Fecha (dd/MM/yyyy):");
        if (f.isEmpty()) return;
        try {
            LocalDate fecha = LocalDate.parse(f, FECHA_SOLA);
            List<Reserva> agenda = sistema.consultarAgendaBus(placa, fecha);
            if (agenda.isEmpty()) {
                avisar("No hay reservas ese día para el bus " + placa);
                return;
            }
            System.out.println("\nAgenda " + placa + " " + fecha);
            for (Reserva r : agenda) {
                System.out.println(r.getFechaInicio().format(FECHA) + " \n"
                        + r.getFechaFin().format(FECHA) + " \n "
                        + r.getServicio().getNombre() + " \n "
                        + r.getEstado().getTexto());
            }
        } catch (DateTimeParseException e) {
            avisar("Fecha inválida.");
        }
    }


    private void historialCliente() throws ContratanteNoEncontradoException {
        String doc = preguntar("Documento del cliente:");
        if (doc.isEmpty()) return;
        List<Reserva> hist = sistema.historialCliente(doc);
        if (hist.isEmpty()) {
            avisar("El cliente no tiene reservas.");
            return;
        }
        System.out.println("\nHistorial de " + doc);
        for (Reserva r : hist) {
            System.out.println("ID " + r.getId() + "\n"
                    + r.getFechaInicio().format(FECHA) + " \n "
                    + r.getServicio().getNombre() + " \n "
                    + r.getEstado().getTexto() + " \n"
                    + r.getValorFinal());
        }
    }


    private void mostrarDatosSistema() {
        System.out.println("\n=== DATOS ACTUALES DEL SISTEMA ===");

        System.out.println("--- BUSES ---");
        if (sistema.getBuses().isEmpty()) {
            System.out.println("No hay buses registrados.");
        } else {
            for (Bus b : sistema.getBuses()) {
                System.out.println("Placa: " + b.getPlaca() + " | Capacidad: " + b.getCapacidad()
                        + " | Cód: " + b.getCodigo());
            }
        }

        System.out.println("\n--- CLIENTES (CONTRATANTES) ---");
        if (sistema.getContratantes().isEmpty()) {
            System.out.println("No hay clientes registrados.");
        } else {
            for (Contratante c : sistema.getContratantes()) {
                System.out.println("Doc: " + c.getDocumento() + " | Nombre: " + c.getNombre()
                        + " | Tel: " + c.getTelefono());
            }
        }

        System.out.println("\n--- SERVICIOS DISPONIBLES ---");
        if (sistema.getServicios().isEmpty()) {
            System.out.println("No hay servicios registrados.");
        } else {
            for (Servicio s : sistema.getServicios()) {
                System.out.println("Nombre: " + s.getNombre() + " | Duración: "
                        + s.getDuracionHoras() + " horas");
            }
        }

        System.out.println("\n--- ENCOMIENDAS ---");
        if (sistema.getEncomiendas().isEmpty()) {
            System.out.println("No hay encomiendas registradas.");
        } else {
            for (Encomienda e : sistema.getEncomiendas()) {
                System.out.println("Cód: " + e.getCodigo() + " | Remitente: " + e.getRemitente()
                        + " | Dest: " + e.getDestinatario());
            }
        }
    }


    private void crearBus() {
        String cod = preguntar("Código del bus (ej BUS-07):");
        if (cod.isEmpty()) return;
        String placa = preguntar("Placa:");
        if (placa.isEmpty()) return;
        try {
            int cap = Integer.parseInt(preguntar("Capacidad:"));
            sistema.agregarBus(new Bus(cod, placa, cap));
            mostrarMensaje("Bus creado.");
        } catch (NumberFormatException e) {
            avisar("Capacidad inválida.");
        }
    }

    private void crearContratante() {
        String doc = preguntar("Documento/NIT:");
        if (doc.isEmpty()) return;
        String nombre = preguntar("Nombre:");
        if (nombre.isEmpty()) return;
        String telefono = preguntar("Teléfono:");
        if (telefono.isEmpty()) return;
        String fechaStr = preguntar("Fecha desde que contrata (dd/MM/yyyy):");
        if (fechaStr.isEmpty()) return;
        try {
            LocalDate fecha = LocalDate.parse(fechaStr, FECHA_SOLA);
            sistema.agregarContratante(new Contratante(doc, nombre, telefono, fecha));
            mostrarMensaje("Contratante creado.");
        } catch (DateTimeParseException e) {
            avisar("Fecha inválida. Use dd/MM/yyyy");
        }
    }


    private void crearEncomienda() {
        String cod = preguntar("Código de la encomienda:");
        if (cod.isEmpty()) return;
        String rem = preguntar("Remitente:");
        if (rem.isEmpty()) return;
        String dest = preguntar("Destinatario:");
        if (dest.isEmpty()) return;
        try {
            double peso = Double.parseDouble(preguntar("Peso (kg):"));
            double valor = Double.parseDouble(preguntar("Valor declarado ($):"));
            sistema.agregarEncomienda(new Encomienda(cod, rem, dest, peso, valor));
            mostrarMensaje("Encomienda creada.");
        } catch (NumberFormatException e) {
            avisar("Peso o valor inválido.");
        }
    }


    private void verSeguroPoliza() {
        System.out.println("¿Qué desea consultar?");
        System.out.println("1. Excursión escolar (por ID de reserva)");
        System.out.println("2. Encomienda (por código)");
        String tipo = preguntar("Elija una opción:");
        if (tipo.isEmpty()) return;

        if (tipo.equals("1")) {
            try {
                String idStr = preguntar("ID de la reserva de excursión:");
                if (idStr.isEmpty()) return;
                Reserva r = sistema.buscarReserva(Integer.parseInt(idStr));
                if (!(r.getServicio() instanceof Asegurable)) {
                    avisar("Esa reserva no es una excursión escolar.");
                    return;
                }
                Asegurable asegurable = (Asegurable) r.getServicio();
                System.out.println("Seguro: $" + asegurable.calcularValorSeguro());
                System.out.println(asegurable.obtenerDescripcionPoliza());
            } catch (NumberFormatException e) {
                avisar("ID inválido.");
            }
        } else if (tipo.equals("2")) {
            String cod = preguntar("Código de la encomienda:");
            if (cod.isEmpty()) return;
            Encomienda e = sistema.buscarEncomienda(cod);
            System.out.println("Seguro: $" + e.calcularValorSeguro());
            System.out.println(e.obtenerDescripcionPoliza());
        } else {
            avisar("Opción inválida.");
        }
    }
}