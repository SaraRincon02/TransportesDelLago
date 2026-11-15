package co.edu.uptc.controlador;

import co.edu.uptc.modelo.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArchivoTransporte {
    private final Path archivo = Path.of("datos", "reservas.txt");
    private final String SEP = ";";

    public boolean existe(){
        return Files.exists(archivo);
    }

    public void guardar(List<Reserva> lista){
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (Reserva r : lista){
                String linea = r.getId() + SEP +
                        r.getContratante().getDocumento() + SEP +
                        r.getBus().getPlaca() + SEP +
                        r.getServicio().getNombre() + SEP +
                        r.getFechaInicio().toString() + SEP +
                        r.getFechaFin().toString() + SEP +
                        r.getEstado().name() + SEP +
                        r.getValorFinal() + SEP +
                        r.getPenalidad();
                lineas.add(linea);
            }
            Files.write(archivo, lineas);
        }catch (IOException e){
            throw new RuntimeException("Error al guardar: " + e.getMessage());
        }
    }

    public List<Reserva> cargar(List<Contratante> contratantes, List<Bus> buses, List<Servicio> servicios){
        List<Reserva> reservas = new ArrayList<>();
        if (!existe()) return reservas;

        try {
            List<String> lineas = Files.readAllLines(archivo);
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.isEmpty()) continue;

                String[] d = linea.split(SEP);
                if (d.length != 9){
                    throw new DatoInvalidoException("La linea " + (i + 1) + " esta dañada: " + linea);
                }
                try {
                    int id = Integer.parseInt(d[0].trim());
                    String idContratante = d[1].trim();
                    String idBus = d[2].trim();
                    String idServicio = d[3].trim();
                    LocalDateTime fechaInicio = LocalDateTime.parse(d[4].trim());
                    LocalDateTime fechaFin = LocalDateTime.parse(d[5].trim());
                    EstadoReserva estado = EstadoReserva.valueOf(d[6].trim());
                    double valorFinal = Double.parseDouble(d[7].trim());
                    double penalidad = Double.parseDouble(d[8].trim());

                    Contratante contratante = buscarContratante(contratantes, idContratante);
                    Bus bus = buscarBus(buses, idBus);
                    Servicio servicio = buscarServicio(servicios, idServicio);

                    if (contratante == null || bus == null || servicio == null) {
                        throw new DatoInvalidoException("Faltan datos vinculados en la línea " + (i + 1));
                    }

                    reservas.add(new Reserva(id, contratante, bus, servicio, fechaInicio, fechaFin, estado, valorFinal, penalidad));
                }catch (IllegalArgumentException e){
                    throw new DatoInvalidoException("La linea " + (i + 1) + " esta danada: " + linea);
                }
            }
        }catch (IOException e){
            throw new DatoInvalidoException("No se pudo leer el archivo: " + e.getMessage());
        }
        return reservas;
    }
    private Contratante buscarContratante(List<Contratante> lista, String id) {
        for (Contratante c : lista) { if (c.getDocumento().equals(id)) return c; }
        return null;
    }

    private Bus buscarBus(List<Bus> lista, String id) {
        for (Bus b : lista) { if (b.getPlaca().equals(id)) return b; }
        return null;
    }

    private Servicio buscarServicio(List<Servicio> lista, String id) {
        for (Servicio s : lista) { if (s.getNombre().equals(id)) return s; }
        return null;
    }
}

