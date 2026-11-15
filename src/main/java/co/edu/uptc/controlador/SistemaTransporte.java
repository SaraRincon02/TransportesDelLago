package co.edu.uptc.controlador;

import co.edu.uptc.modelo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SistemaTransporte {
    private List<Reserva> reservas;
    private ArchivoTransporte archivo;
    private List<Contratante> contratantes;
    private List<Bus> buses;
    private List<Encomienda> encomiendas;

    private List<Servicio> servicios;

    public SistemaTransporte(){
        this.reservas = new ArrayList<>();
        this.archivo = new ArchivoTransporte();
        this.contratantes = new ArrayList<>();
        this.buses = new ArrayList<>();
        this.encomiendas = new ArrayList<>();
        this.servicios = new ArrayList<>();

        precargarDatos();
    }

    public void cargar() {
        this.reservas = new ArrayList<>(archivo.cargar(this.contratantes, this.buses, this.servicios));
    }

    public void guardar() {
        archivo.guardar(reservas);
    }

    public void agregarReserva(Reserva nueva) throws  BusOcupadoException{
        verificarCruce(nueva.getBus(), nueva.getFechaInicio(), nueva.getFechaFin(), null);
        reservas.add(nueva);
    }

    public void reprogramarReserva(Reserva r, LocalDateTime nuevoInicio, Bus nuevoBus) throws TransicionInvalidaException, BusOcupadoException{
        LocalDateTime nuevoFin = nuevoInicio.plusHours(r.getServicio().getDuracionHoras());
        verificarCruce(nuevoBus, nuevoInicio, nuevoFin, r);
        r.reprogramar(nuevoInicio,nuevoBus);
    }

    public void verificarCruce(Bus bus, LocalDateTime inicio, LocalDateTime fin, Reserva excepcion) throws  BusOcupadoException{
        for (Reserva r : reservas){
            if (r != excepcion && r.getBus().equals(bus) && r.ocupaBus()){
                if (r.seCruzaCon(inicio,fin)){
                    throw new BusOcupadoException(bus.getPlaca());
                }
            }
        }
    }

    public double calcularIngresos(LocalDateTime f1, LocalDateTime f2){
        double total = 0;
        for (Reserva r : reservas){
            if (r.getEstado() == EstadoReserva.CERRADA && !r.getFechaInicio().isBefore(f1) &&
                !r.getFechaInicio().isAfter(f2)){
                total += r.getValorFinal();
            }
        }
        return total;
    }

    public double calcularTotalPenalidades(){
        double total = 0;
        for (Reserva r : reservas){
            total += r.getPenalidad();
        }
        return total;
    }

    public int[] contarReservasPorEstado(){
        int[] conteos = new int[EstadoReserva.values().length];
        for (Reserva r : reservas){
            conteos[r.getEstado().ordinal()]++;
        }
        return conteos;
    }

    public Bus obtenerBusMasUsado(){
        Bus masUsado = null;
        int maxUso = -1;
        for (Bus b : buses){
            int contador = 0;
            for (Reserva r : reservas){
                if (r.getBus().equals(b) && r.getEstado() != EstadoReserva.CANCELADA){
                    contador++;
                }
            }
            if (contador > maxUso){
                maxUso = contador;
                masUsado = b;
            }
        }
        return masUsado;
    }

    public Contratante buscarContratante(String documento) throws ContratanteNoEncontradoException{
        for (Contratante c : contratantes){
            if (c.getDocumento().equals(documento)){
                return c;
            }
        }
        throw new ContratanteNoEncontradoException("No existe un contratante con el documento:  " + documento);
    }


    private void precargarDatos(){
        buses.add(new Bus("BUS-01", "AAA001", 40));
        buses.add(new Bus("BUS-02", "AAA002", 40));
        buses.add(new Bus("BUS-03", "AAA003", 30));
        buses.add(new Bus("BUS-04", "AAA004", 30));
        buses.add(new Bus("BUS-05", "AAA005", 19));
        buses.add(new Bus("BUS-06", "AAA006", 19));

        contratantes.add(new Contratante("123", "Colegio Nuestra Señora del Rosario", "555-1111", LocalDate.now().minusYears(9)));
        contratantes.add(new Contratante("456", "Empresa JRS", "555-2222", LocalDate.now().minusYears(2)));
        contratantes.add(new Contratante("789", "Familia Rincon", "555-3333", LocalDate.now()));
        contratantes.add(new Contratante("208", "Turismos el Manantial", "555-4444", LocalDate.now().minusYears(5)));
    }
}
