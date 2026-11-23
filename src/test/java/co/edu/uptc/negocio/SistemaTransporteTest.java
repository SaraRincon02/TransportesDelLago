package co.edu.uptc.negocio;
import co.edu.uptc.controlador.BusOcupadoException;
import co.edu.uptc.controlador.ContratanteNoEncontradoException;
import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SistemaTransporteTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);
    private static final LocalDate MARTES = LocalDate.of(2026, 11, 17);
    private static final LocalDate SABADO = LocalDate.of(2026, 11, 21);

    private SistemaTransporte sistema;
    private Bus bus;
    private Contratante contratante;

    @TempDir
    Path carpeta;
    @BeforeEach
    void preparar() throws ContratanteNoEncontradoException {
        sistema = new SistemaTransporte(carpeta.resolve("reservas.txt"));
        bus = sistema.buscarBus("AAA001");
        contratante = sistema.buscarContratante("456");
    }

    @Test
    void rechazaDosViajesCruzadosEnElMismoBus() throws Exception {
        sistema.agregarReserva(reserva(101, LUNES.atTime(6, 0), new TrasladoEmpresarial(6, 0)));

        assertThrows(BusOcupadoException.class,
                () -> sistema.agregarReserva(reserva(102, LUNES.atTime(10, 0), new TrasladoEmpresarial(4, 0))));
    }

    @Test
    void permiteViajesContiguosEnElMismoBus() throws Exception {
        sistema.agregarReserva(reserva(103, LUNES.atTime(6, 0), new TrasladoEmpresarial(6, 0)));

        assertDoesNotThrow(
                () -> sistema.agregarReserva(reserva(104, LUNES.atTime(12, 0), new TrasladoEmpresarial(4, 0))));
    }

    @Test
    void permiteCrearLaFranjaCuandoLaReservaCruzadaFueCancelada() throws Exception {
        Reserva primera = reserva(105, LUNES.atTime(6, 0), new TrasladoEmpresarial(6, 0));
        sistema.agregarReserva(primera);
        primera.cancelar(LUNES.minusDays(1).atTime(6, 0));

        assertDoesNotThrow(
                () -> sistema.agregarReserva(reserva(106, LUNES.atTime(10, 0), new TrasladoEmpresarial(4, 0))));
    }

    @Test
    void rechazaConfirmarUnaReservaCancelada() throws Exception {
        Reserva reserva = reserva(107, LUNES.atTime(6, 0), new TrasladoEmpresarial(4, 0));
        reserva.cancelar(LUNES.minusDays(1).atTime(6, 0));

        assertThrows(TransicionInvalidaException.class, reserva::confirmar);
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    @Test
    void aplicaElTopeDelQuincePorCientoAlTrasladoEmpresarial() throws Exception {
        Contratante antiguo = new Contratante("TEST-20", "Contratante de prueba", "5550000", LocalDate.of(2006, 1, 1));
        Reserva reserva = new Reserva(108, antiguo, bus, new TrasladoEmpresarial(4, 0),
                LocalDate.of(2026, 11, 17).atTime(8, 0));

        assertEquals(212500, reserva.getValorFinal());
    }

    @Test
    void aplicaUnSoloRecargoEnPaseoNocturnoDeSabado() throws Exception {
        Contratante nuevo = new Contratante("TEST-NUEVO", "Contratante nuevo", "5550001", LocalDate.of(2026, 11, 20));
        Reserva reserva = new Reserva(109, nuevo, bus, new PaseoTuristico(4, 80),
                LocalDate.of(2026, 11, 21).atTime(19, 0));

        assertEquals(414000, reserva.getValorFinal());
    }

    private Reserva reserva(int id, LocalDateTime inicio, TrasladoEmpresarial servicio) {
        return new Reserva(id, contratante, bus, servicio, inicio);
    }
}

