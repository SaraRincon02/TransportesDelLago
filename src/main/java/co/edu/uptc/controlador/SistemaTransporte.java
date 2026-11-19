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

    public void agregarBus(Bus nuevoBus){
        if(nuevoBus == null){
            throw new DatoInvalidoException("El bus no puede ser nulo");
        }
        for (Bus b: buses){
            if(b.getCodigo().equalsIgnoreCase(nuevoBus.getCodigo()) || b.getPlaca().equalsIgnoreCase(nuevoBus.getPlaca())){
                throw new DatoInvalidoException("Ya existe un bus con ese codigo o placa");
            }
        }
        buses.add(nuevoBus);
    }


    public void agregarContratante(Contratante nuevoContratante) {
        if (nuevoContratante == null) {
            throw new DatoInvalidoException("El contratante no puede ser nulo");
        }

        for (Contratante c : contratantes) {
            if (c.getDocumento().equalsIgnoreCase(nuevoContratante.getDocumento())) {
                throw new DatoInvalidoException("Ya existe un contratante con ese documento");
            }
        }

        contratantes.add(nuevoContratante);
    }

    public void agregarEncomienda(Encomienda nuevaEncomienda) {
        if (nuevaEncomienda == null) {
            throw new DatoInvalidoException("La encomienda no puede ser nula");
        }

        for (Encomienda encomienda : encomiendas) {
            if (encomienda.getCodigo().equalsIgnoreCase(nuevaEncomienda.getCodigo())) {
                throw new DatoInvalidoException("Ya existe una encomienda con ese código");
            }
        }
        encomiendas.add(nuevaEncomienda);
    }



    public void agregarReserva(Reserva nueva) throws BusOcupadoException{
        if (nueva ==null){
            throw new DatoInvalidoException("La reserva no puede ser nula");
        }

        for (Reserva r : reservas){
            if (r.getId() == nueva.getId()){
                throw new DatoInvalidoException("Ya existe una reserva con ese iD");
            }
        }
        verificarCruce(nueva.getBus(), nueva.getFechaInicio(), nueva.getFechaFin(), null);
        reservas.add(nueva);
    }

    public void reprogramarReserva(Reserva r, LocalDateTime nuevoInicio, Bus nuevoBus) throws TransicionInvalidaException, BusOcupadoException{
        if (r == null || nuevoInicio == null || nuevoBus == null){
            throw new DatoInvalidoException("Faltan datos para reprogramar la reserva");
        }

        if (!reservas.contains(r)) {
            throw new DatoInvalidoException("La reserva no pertenece al sistema");
        }
        LocalDateTime nuevoFin = nuevoInicio.plusHours(r.getServicio().getDuracionHoras());
        verificarCruce(nuevoBus, nuevoInicio, nuevoFin, r);
        r.reprogramar(nuevoInicio,nuevoBus);
    }

    public void verificarCruce(Bus bus, LocalDateTime inicio, LocalDateTime fin, Reserva excepcion) throws  BusOcupadoException{
        if (bus == null || inicio == null || fin == null) {
            throw new DatoInvalidoException("Los datos para verificar la disponibilidad son obligatorios");
        }

        if (!fin.isAfter(inicio)) {
            throw new DatoInvalidoException("La fecha final debe ser posterior a la inicial");
        }

        for (Reserva reserva : reservas) {
            boolean mismoBus = reserva.getBus().getCodigo().equalsIgnoreCase(bus.getCodigo()
            );

            if (reserva != excepcion && mismoBus && reserva.ocupaBus()) {
                if (reserva.seCruzaCon(inicio, fin)) {
                    throw new BusOcupadoException(bus.getPlaca());
                }
            }
        }
    }




    public List<Reserva> getReservas(){
        return this.reservas;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public List<Contratante> getContratantes() {
        return contratantes;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public List<Encomienda> getEncomiendas() {
        return encomiendas;
    }

    public Reserva buscarReserva(int id){
        for (Reserva r : reservas){
            if (r.getId() == id){
                return r;
            }
        }
        throw new DatoInvalidoException("No existe una reserva con el ID:  " + id);
    }

    public double calcularIngresos(LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        if (fechaInicial == null || fechaFinal == null || fechaFinal.isBefore(fechaInicial)) {
            throw new DatoInvalidoException("El rango de fechas no es válido.");
        }

        double total = 0;

        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == EstadoReserva.CERRADA && !reserva.getFechaInicio().isBefore(fechaInicial) && !reserva.getFechaInicio().isAfter(fechaFinal)) {
                total += reserva.getValorFinal();
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

        if (documento == null || documento.trim().isEmpty()){
            throw new DatoInvalidoException("Debe ingresar el documento del contratante");
        }
        for (Contratante c : contratantes){
            if (c.getDocumento().equals(documento)){
                return c;
            }
        }
        throw new ContratanteNoEncontradoException("No existe un contratante con el documento:  " + documento);
    }

    public  Bus buscarBus(String placa){
        if (placa == null || placa.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar la placa del bus");
        }
        for (Bus b : buses){
            if (b.getPlaca().equalsIgnoreCase(placa)){
                return  b;
            }
        }
        throw new DatoInvalidoException("No existe un bus con la placa " + placa);
    }

    public Servicio buscarServicio(String nombre){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar el servicio");
        }
        for (Servicio s : servicios){
            if (s.getNombre().equalsIgnoreCase(nombre.trim())){
                return s;
            }
        }
        throw new DatoInvalidoException("No existe un servicio con el nombre " + nombre);
    }


    public void precargarDatos(){

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

        servicios.add(new PaseoTuristico(8, 120.5));
        servicios.add(new TrasladoEmpresarial(4, 1));
        servicios.add(new ExcursionEscolar(6, 30));

        encomiendas.add(new Encomienda("ENC-001", "Juan Perez", "Maria Lopez", 15.5, 150000));
    }
}
