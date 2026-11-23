package co.edu.uptc.controlador;

import co.edu.uptc.modelo.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArchivoTransporte {
    private final Path carpeta;
    private final String SEP = ";";

    private final Path archivoReservas;
    private final Path archivoContratantes;
    private final Path archivoBuses;
    private final Path archivoEncomiendas;

    public ArchivoTransporte(Path carpeta) {
        if (carpeta == null) {
            throw new DatoInvalidoException("El directorio de datos es obligatorio");
        }
        this.carpeta = carpeta;
        this.archivoReservas = carpeta.resolve("reservas.txt");
        this.archivoContratantes = carpeta.resolve("contratantes.txt");
        this.archivoBuses = carpeta.resolve("buses.txt");
        this.archivoEncomiendas = carpeta.resolve("encomiendas.txt");
    }

    public boolean existe() {
        return Files.exists(archivoReservas);
    }

    public void guardar(List<Reserva> reservas, List<Contratante> contratantes,
                        List<Bus> buses, List<Encomienda> encomiendas) {
        try {
            Files.createDirectories(carpeta);
            guardarReservas(reservas);
            guardarContratantes(contratantes);
            guardarBuses(buses);
            guardarEncomiendas(encomiendas);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar: " + e.getMessage());
        }
    }

    private void guardarReservas(List<Reserva> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Reserva r : lista) {
            lineas.add(r.getId() + SEP +
                    r.getContratante().getDocumento() + SEP +
                    r.getBus().getPlaca() + SEP +
                    r.getServicio().getTipoPersistencia() + SEP +
                    r.getServicio().getDuracionHoras() + SEP +
                    r.getServicio().getParametro() + SEP +
                    r.getFechaInicio() + SEP +
                    r.getFechaFin() + SEP +
                    r.getEstado().name() + SEP +
                    r.getValorFinal() + SEP +
                    r.getPenalidad() + SEP +
                    (r.getFechaCierre() == null ? "" : r.getFechaCierre()));
        }
        Files.write(archivoReservas, lineas);
    }


    private void guardarContratantes(List<Contratante> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Contratante c : lista) {
            lineas.add(c.getDocumento() + SEP + c.getNombre() + SEP +
                    c.getTelefono() + SEP + c.getFechaRegistro());
        }
        Files.write(archivoContratantes, lineas);
    }

    private void guardarBuses(List<Bus> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Bus b : lista) {
            lineas.add(b.getCodigo() + SEP + b.getPlaca() + SEP + b.getCapacidad());
        }
        Files.write(archivoBuses, lineas);
    }

    private void guardarEncomiendas(List<Encomienda> lista) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Encomienda e : lista) {
            lineas.add(e.getCodigo() + SEP + e.getRemitente() + SEP +
                    e.getDestinatario() + SEP + e.getPeso() + SEP + e.getValorDeclarado());
        }
        Files.write(archivoEncomiendas, lineas);
    }

    public List<Contratante> cargarContratantes() {
        List<Contratante> lista = new ArrayList<>();
        if (!Files.exists(archivoContratantes)) return lista;
        try {
            for (String linea : Files.readAllLines(archivoContratantes)) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split(SEP, -1);
                lista.add(new Contratante(d[0], d[1], d[2], LocalDate.parse(d[3])));
            }
        } catch (IOException e) {
            throw new DatoInvalidoException("Error al leer contratantes: " + e.getMessage());
        }
        return lista;
    }

    public List<Bus> cargarBuses() {
        List<Bus> lista = new ArrayList<>();
        if (!Files.exists(archivoBuses)) return lista;
        try {
            for (String linea : Files.readAllLines(archivoBuses)) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split(SEP, -1);
                lista.add(new Bus(d[0], d[1], Integer.parseInt(d[2])));
            }
        } catch (IOException e) {
            throw new DatoInvalidoException("Error al leer buses: " + e.getMessage());
        }
        return lista;
    }

    public List<Encomienda> cargarEncomiendas() {
        List<Encomienda> lista = new ArrayList<>();
        if (!Files.exists(archivoEncomiendas)) return lista;
        try {
            for (String linea : Files.readAllLines(archivoEncomiendas)) {
                if (linea.trim().isEmpty()) continue;
                String[] d = linea.split(SEP, -1);
                lista.add(new Encomienda(d[0], d[1], d[2],
                        Double.parseDouble(d[3]), Double.parseDouble(d[4])));
            }
        } catch (IOException e) {
            throw new DatoInvalidoException("Error al leer encomiendas: " + e.getMessage());
        }
        return lista;
    }

    public List<Reserva> cargarReservas(List<Contratante> contratantes,
                                        List<Bus> buses) {
        List<Reserva> reservas = new ArrayList<>();
        if (!Files.exists(archivoReservas)) return reservas;
        try {
            List<String> lineas = Files.readAllLines(archivoReservas);
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.isEmpty()) continue;
                String[] d = linea.split(SEP, -1);
                if (d.length != 12) {
                    throw new DatoInvalidoException("Línea " + (i + 1) + " dañada: " + linea);
                }
                try {
                    int id = Integer.parseInt(d[0].trim());
                    Contratante c = buscarContratante(contratantes, d[1].trim());
                    Bus b = buscarBus(buses, d[2].trim());
                    Servicio s = crearServicio(d[3].trim(),
                            Integer.parseInt(d[4].trim()),
                            Double.parseDouble(d[5].trim()));
                    if (c == null || b == null || s == null) {
                        throw new DatoInvalidoException("Faltan datos vinculados en línea " + (i + 1));
                    }
                    LocalDateTime inicio = LocalDateTime.parse(d[6].trim());
                    LocalDateTime fin = LocalDateTime.parse(d[7].trim());
                    EstadoReserva estado = EstadoReserva.valueOf(d[8].trim());
                    double valor = Double.parseDouble(d[9].trim());
                    double penalidad = Double.parseDouble(d[10].trim());
                    LocalDateTime cierre = d[11].trim().isEmpty() ? null : LocalDateTime.parse(d[11].trim());
                    reservas.add(new Reserva(id, c, b, s, inicio, fin, estado, valor, penalidad, cierre));
                } catch (IllegalArgumentException e) {
                    throw new DatoInvalidoException("Línea " + (i + 1) + " dañada: " + linea);
                }
            }
        } catch (IOException e) {
            throw new DatoInvalidoException("No se pudo leer reservas: " + e.getMessage());
        }
        return reservas;
    }


    private Contratante buscarContratante(List<Contratante> lista, String id) {
        for (Contratante c : lista) {
            if (c.getDocumento().equals(id)) {
                return c;
            }
        }

        return null;
    }

    private Bus buscarBus(List<Bus> lista, String id) {
        for (Bus b : lista) {
            if (b.getPlaca().equals(id)) {
                return b;
            }
        }

        return null;
    }


    private Servicio crearServicio(String tipo, int duracion, double parametro) {
        return switch (tipo) {
            case "PASEO_TURISTICO" -> new PaseoTuristico(duracion, parametro);
            case "EXCURSION_ESCOLAR" -> new ExcursionEscolar(duracion, (int) parametro);
            case "TRASLADO_EMPRESARIAL" -> new TrasladoEmpresarial(duracion, (int) parametro);
            default -> throw new DatoInvalidoException("El tipo de servicio guardado no existe");
        };
    }
}

