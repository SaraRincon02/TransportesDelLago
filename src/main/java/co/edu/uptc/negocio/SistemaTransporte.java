package co.edu.uptc.negocio;

import co.edu.uptc.modelo.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Controlador principal de negocio de Transportes del Lago.
 * Centraliza la gestión de buses, contratantes, encomiendas, reservas y reportes.
 */
public class SistemaTransporte {
    private List<Reserva> reservas;
    private ArchivoTransporte archivo;
    private List<Contratante> contratantes;
    private List<Bus> buses;
    private List<Encomienda> encomiendas;
    private List<Servicio> servicios;

    /**
     * Inicializa el sistema cargando datos desde disco o precargando datos de fábrica si no existen.
     * @param carpeta directorio donde se almacenan los archivos de datos.
     */
    public SistemaTransporte(Path carpeta) {
        if (carpeta == null) {
            throw new DatoInvalidoException("El directorio de datos es obligatorio");
        }
        this.archivo = new ArchivoTransporte(carpeta);
        this.reservas = new ArrayList<>();
        this.contratantes = new ArrayList<>();
        this.buses = new ArrayList<>();
        this.encomiendas = new ArrayList<>();
        this.servicios = new ArrayList<>();

        if (archivo.existe()) {
            try {
                cargar();
            } catch (DatoInvalidoException e) {
                throw new DatoInvalidoException("No se pudo cargar el archivo de datos. Revise el archivo y vuelva a intentar.");
            }
        } else {
            precargarDatos();
            guardar();
        }
    }

    /** Carga todas las entidades desde los archivos de texto. */
    public void cargar() {
        this.contratantes = new ArrayList<>(archivo.cargarContratantes());
        this.buses = new ArrayList<>(archivo.cargarBuses());
        this.encomiendas = new ArrayList<>(archivo.cargarEncomiendas());
        this.reservas = new ArrayList<>(archivo.cargarReservas(contratantes, buses));
        // Reconstruir lista de servicios a partir de las reservas (o precargar)
        this.servicios = new ArrayList<>();
        for (Reserva r : reservas) {
            if (!servicios.contains(r.getServicio())) {
                servicios.add(r.getServicio());
            }
        }
    }

    /** Guarda el estado actual del sistema en los archivos de texto. */
    public void guardar() {
        archivo.guardarTodo(reservas, contratantes, buses, encomiendas);   // ✅
    }

    /**
     * Registra un nuevo bus verificando que código y placa no estén repetidos.
     * @param nuevoBus bus a registrar.
     */
    public void agregarBus(Bus nuevoBus) {
        if (nuevoBus == null) {
            throw new DatoInvalidoException("El bus no puede ser nulo");
        }
        for (Bus b : buses) {
            if (b.getCodigo().equalsIgnoreCase(nuevoBus.getCodigo()) || b.getPlaca().equalsIgnoreCase(nuevoBus.getPlaca())) {
                throw new DatoInvalidoException("Ya existe un bus con ese codigo o placa");
            }
        }
        buses.add(nuevoBus);
    }

    /**
     * Registra un nuevo contratante verificando que su documento no esté repetido.
     * @param nuevoContratante contratante a registrar.
     */
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

    /**
     * Registra una nueva encomienda verificando que su código no esté repetido.
     * @param nuevaEncomienda encomienda a registrar.
     */
    public void agregarEncomienda(Encomienda nuevaEncomienda) {
        if (nuevaEncomienda == null) {
            throw new DatoInvalidoException("La encomienda no puede ser nula");
        }

        for (Encomienda e : encomiendas) {
            if (e.getCodigo().equalsIgnoreCase(nuevaEncomienda.getCodigo())) {
                throw new DatoInvalidoException("Ya existe una encomienda con ese código");
            }
        }
        encomiendas.add(nuevaEncomienda);
    }

    /**
     * Registra una nueva reserva validando que el ID sea único y el bus esté disponible.
     * @param nueva reserva a registrar.
     * @throws BusOcupadoException si el bus tiene cruce de horario en esa franja.
     */
    public void agregarReserva(Reserva nueva) throws BusOcupadoException {
        if (nueva == null) {
            throw new DatoInvalidoException("La reserva no puede ser nula");
        }

        for (Reserva r : reservas) {
            if (r.getId() == nueva.getId()) {
                throw new DatoInvalidoException("Ya existe una reserva con ese iD");
            }
        }
        verificarCruce(nueva.getBus(), nueva.getFechaInicio(), nueva.getFechaFin(), null);
        reservas.add(nueva);
    }

    /**
     * Cancela una reserva existente por su identificador.
     * @param id identificador de la reserva.
     * @param ahora fecha y hora de cancelación.
     * @throws TransicionInvalidaException si la reserva no puede cancelarse en su estado actual.
     */
    public void cancelarReserva(int id, LocalDateTime ahora) throws TransicionInvalidaException {
        buscarReserva(id).cancelar(ahora);
    }

    /**
     * Reprograma la fecha y/o el bus de una reserva verificando disponibilidad sin contarse a sí misma.
     * @param r reserva a modificar.
     * @param nuevoInicio nueva fecha y hora de inicio.
     * @param nuevoBus nuevo vehículo asignado.
     * @throws TransicionInvalidaException si el estado de la reserva no permite reprogramar.
     * @throws BusOcupadoException si el bus elegido se cruza con otra reserva activa.
     */
    public void reprogramarReserva(Reserva r, LocalDateTime nuevoInicio, Bus nuevoBus) throws TransicionInvalidaException, BusOcupadoException {
        if (r == null || nuevoInicio == null || nuevoBus == null) {
            throw new DatoInvalidoException("Faltan datos para reprogramar la reserva");
        }

        if (!reservas.contains(r)) {
            throw new DatoInvalidoException("La reserva no pertenece al sistema");
        }
        LocalDateTime nuevoFin = nuevoInicio.plusHours(r.getServicio().getDuracionHoras());
        verificarCruce(nuevoBus, nuevoInicio, nuevoFin, r);
        r.reprogramar(nuevoInicio, nuevoBus);
    }

    /**
     * Verifica que un bus no tenga reservas activas cruzadas en un intervalo de tiempo.
     * @param bus vehículo a evaluar.
     * @param inicio fecha y hora de inicio.
     * @param fin fecha y hora de fin.
     * @param excepcion reserva a ignorar durante la validación (puede ser {@code null}).
     * @throws BusOcupadoException si existe un cruce de horarios.
     */
    public void verificarCruce(Bus bus, LocalDateTime inicio, LocalDateTime fin, Reserva excepcion) throws BusOcupadoException {
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

    /** @return lista de reservas registradas. */
    public List<Reserva> getReservas() {
        return this.reservas;
    }

    /** @return lista de servicios de referencia. */
    public List<Servicio> getServicios() {
        return servicios;
    }

    /** @return lista de contratantes registrados. */
    public List<Contratante> getContratantes() {
        return contratantes;
    }

    /** @return lista de buses registrados. */
    public List<Bus> getBuses() {
        return buses;
    }

    /** @return lista de encomiendas registradas. */
    public List<Encomienda> getEncomiendas() {
        return encomiendas;
    }

    /**
     * Busca una reserva por su ID.
     * @param id identificador de la reserva.
     * @return la reserva encontrada.
     */
    public Reserva buscarReserva(int id) {
        for (Reserva r : reservas) {
            if (r.getId() == id) {
                return r;
            }
        }
        throw new DatoInvalidoException("No existe una reserva con el ID:  " + id);
    }

    /**
     * Calcula el total facturado de reservas CERRADAS dentro de un rango de fechas.
     * @param fechaInicial inicio del periodo.
     * @param fechaFinal fin del periodo.
     * @return suma de ingresos en pesos.
     */
    public double calcularIngresos(LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        if (fechaInicial == null || fechaFinal == null || fechaFinal.isBefore(fechaInicial)) {
            throw new DatoInvalidoException("El rango de fechas no es válido.");
        }
        double total = 0;
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CERRADA
                    && r.getFechaCierre() != null
                    && !r.getFechaCierre().isBefore(fechaInicial)
                    && !r.getFechaCierre().isAfter(fechaFinal)) {
                total += r.getValorFinal();
            }
        }
        return total;
    }

    /** @return suma total de penalidades cobradas por cancelaciones tardías. */
    public double calcularTotalPenalidades() {
        double total = 0;
        for (Reserva r : reservas) {
            total += r.getPenalidad();
        }
        return total;
    }

    /** @return arreglo con la cantidad de reservas agrupadas según el orden de {@link EstadoReserva}. */
    public int[] contarReservasPorEstado() {
        int[] conteos = new int[EstadoReserva.values().length];
        for (Reserva r : reservas) {
            conteos[r.getEstado().ordinal()]++;
        }
        return conteos;
    }

    /** @return el bus con mayor cantidad de reservas no canceladas. */
    public Bus obtenerBusMasUsado() {
        Bus masUsado = null;
        int maxUso = -1;
        for (Bus b : buses) {
            int contador = 0;
            for (Reserva r : reservas) {
                if (r.getBus().equals(b) && r.getEstado() != EstadoReserva.CANCELADA) {
                    contador++;
                }
            }
            if (contador > maxUso) {
                maxUso = contador;
                masUsado = b;
            }
        }
        return masUsado;
    }

    /**
     * Busca un contratante por su número de documento o NIT.
     * @param documento identificador del cliente.
     * @return el contratante encontrado.
     * @throws ContratanteNoEncontradoException si no existe ningún contratante con ese documento.
     */
    public Contratante buscarContratante(String documento) throws ContratanteNoEncontradoException {

        if (documento == null || documento.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar el documento del contratante");
        }
        for (Contratante c : contratantes) {
            if (c.getDocumento().equals(documento)) {
                return c;
            }
        }
        throw new ContratanteNoEncontradoException("No existe un contratante con el documento:  " + documento);
    }

    /**
     * Busca un bus por su placa.
     * @param placa placa del vehículo.
     * @return el bus encontrado.
     */
    public Bus buscarBus(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar la placa del bus");
        }
        for (Bus b : buses) {
            if (b.getPlaca().equalsIgnoreCase(placa)) {
                return b;
            }
        }
        throw new DatoInvalidoException("No existe un bus con la placa " + placa);
    }

    /**
     * Busca un servicio por su nombre.
     * @param nombre nombre del servicio.
     * @return el servicio encontrado.
     */
    public Servicio buscarServicio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar el servicio");
        }
        for (Servicio s : servicios) {
            if (s.getNombre().equalsIgnoreCase(nombre.trim())) {
                return s;
            }
        }
        throw new DatoInvalidoException("No existe un servicio con el nombre " + nombre);
    }

    /**
     * Consulta las reservas programadas para un bus en una fecha específica.
     * @param placa placa del bus.
     * @param fecha día a consultar.
     * @return lista de reservas del bus en esa fecha.
     */
    public List<Reserva> consultarAgendaBus(String placa, LocalDate fecha) {
        if (fecha == null) {
            throw new DatoInvalidoException("La fecha de agenda es obligatoria.");
        }
        Bus bus = buscarBus(placa);
        List<Reserva> agenda = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getBus().getCodigo().equals(bus.getCodigo())
                    && reserva.getFechaInicio().toLocalDate().equals(fecha)) {
                agenda.add(reserva);
            }
        }
        return agenda;
    }

    /**
     * Obtiene todas las reservas asociadas a un contratante.
     * @param documento documento o NIT del cliente.
     * @return lista histórica de reservas del cliente.
     * @throws ContratanteNoEncontradoException si el cliente no existe.
     */
    public List<Reserva> historialCliente(String documento) throws ContratanteNoEncontradoException {
        Contratante c = buscarContratante(documento);
        List<Reserva> historial = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getContratante().getDocumento().equals(c.getDocumento())) {
                historial.add(r);
            }
        }
        return historial;
    }

    /**
     * Busca una encomienda por su código.
     * @param codigo código de la encomienda.
     * @return la encomienda encontrada.
     */
    public Encomienda buscarEncomienda(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe ingresar el código de la encomienda");
        }
        for (Encomienda e : encomiendas) {
            if (e.getCodigo().equalsIgnoreCase(codigo.trim())) return e;
        }
        throw new DatoInvalidoException("No existe encomienda con código " + codigo);
    }

    /** @return siguiente identificador consecutivo disponible para una nueva reserva. */
    public int generarIdReserva() {
        int max = 0;
        for (Reserva r : reservas) {
            if (r.getId() > max) max = r.getId();
        }
        return max + 1;
    }

    /**
     * Confirma una reserva por su ID.
     * @param id identificador de la reserva.
     * @throws TransicionInvalidaException si la transición no es válida.
     */
    public void confirmarReserva(int id) throws TransicionInvalidaException {
        buscarReserva(id).confirmar();
    }

    /**
     * Inicia una reserva por su ID.
     * @param id identificador de la reserva.
     * @param ahora fecha y hora actual.
     * @throws TransicionInvalidaException si la transición no es válida.
     */
    public void iniciarReserva(int id, LocalDateTime ahora) throws TransicionInvalidaException {
        buscarReserva(id).iniciar(ahora);
    }

    /**
     * Cierra una reserva por su ID.
     * @param id identificador de la reserva.
     * @param ahora fecha y hora de cierre.
     * @throws TransicionInvalidaException si la transición no es válida.
     */
    public void cerrarReserva(int id, LocalDateTime ahora) throws TransicionInvalidaException {
        buscarReserva(id).cerrar(ahora);
    }

    private Reserva nuevaReserva(int id, String documento, String placa, Servicio servicio, LocalDateTime inicio)
            throws ContratanteNoEncontradoException, BusOcupadoException {
        Reserva reserva = new Reserva(id, buscarContratante(documento), buscarBus(placa), servicio, inicio);
        agregarReserva(reserva);
        return reserva;
    }

    /** Carga los datos iniciales de fábrica cuando no existen archivos previos en disco. */
    public void precargarDatos() {
        if (!buses.isEmpty() || !contratantes.isEmpty()) {
            return;
        }

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
        encomiendas.add(new Encomienda("ENC-002", "Ana Torres", "Luis Gómez", 8.0, 90000));
        encomiendas.add(new Encomienda("ENC-003", "Carlos Ruiz", "Elena Díaz", 23.0, 320000));

        try {
            Reserva r1 = nuevaReserva(1, "123", "AAA001", new PaseoTuristico(6, 80), LocalDateTime.of(2026, 10, 5, 8, 0));
            r1.confirmar();
            r1.iniciar(LocalDateTime.of(2026, 10, 5, 8, 0));
            r1.cerrar(LocalDateTime.of(2026, 10, 5, 14, 0));

            Reserva r2 = nuevaReserva(2, "456", "AAA002", new ExcursionEscolar(6, 30), LocalDateTime.of(2026, 11, 10, 7, 0));
            r2.confirmar();

            Reserva r3 = nuevaReserva(3, "789", "AAA003", new TrasladoEmpresarial(4, 1), LocalDateTime.of(2026, 11, 11, 10, 0));
            r3.cancelar(LocalDateTime.of(2026, 11, 9, 10, 0));

            Reserva r4 = nuevaReserva(4, "208", "AAA004", new PaseoTuristico(5, 95), LocalDateTime.of(2026, 11, 12, 9, 0));
            r4.confirmar();
            r4.iniciar(LocalDateTime.of(2026, 11, 12, 9, 0));

            nuevaReserva(5, "123", "AAA005", new ExcursionEscolar(4, 15), LocalDateTime.of(2026, 11, 15, 8, 0));

            Reserva r6 = nuevaReserva(6, "456", "AAA006", new TrasladoEmpresarial(3, 2), LocalDateTime.of(2026, 11, 16, 6, 0));
            r6.confirmar();
            r6.iniciar(LocalDateTime.of(2026, 11, 16, 6, 0));
            r6.cerrar(LocalDateTime.of(2026, 11, 16, 9, 0));

            Reserva r7 = nuevaReserva(7, "789", "AAA001", new PaseoTuristico(4, 70), LocalDateTime.of(2026, 11, 20, 19, 0));
            r7.confirmar();

            Reserva r8 = nuevaReserva(8, "208", "AAA002", new ExcursionEscolar(5, 28), LocalDateTime.of(2026, 11, 21, 10, 0));
            r8.confirmar();
            r8.cancelar(LocalDateTime.of(2026, 11, 20, 12, 0));

            nuevaReserva(9, "123", "AAA003", new TrasladoEmpresarial(4, 0), LocalDateTime.of(2026, 12, 1, 8, 0));

            Reserva r10 = nuevaReserva(10, "456", "AAA004", new ExcursionEscolar(6, 25), LocalDateTime.of(2026, 10, 29, 7, 0));
            r10.confirmar();
            r10.iniciar(LocalDateTime.of(2026, 10, 29, 7, 0));
            r10.cerrar(LocalDateTime.of(2026, 10, 29, 13, 0));

        } catch (BusOcupadoException | ContratanteNoEncontradoException | TransicionInvalidaException excepcion) {
            throw new DatoInvalidoException("No fue posible preparar los datos iniciales: " + excepcion.getMessage());
        }
    }




}
