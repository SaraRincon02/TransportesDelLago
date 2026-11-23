package co.edu.uptc.negocio;

import co.edu.uptc.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
/** Pruebas unitarias de las reglas de negocio y cálculos de Transportes del Lago. */
public class SistemaTransporteTest {

    @TempDir
    Path carpeta;

    private SistemaTransporte sistema;
    private Bus bus;
    private Contratante contratante;

    @BeforeEach
    void preparar() throws ContratanteNoEncontradoException {
        sistema = new SistemaTransporte(carpeta);
        bus = sistema.buscarBus("AAA001");            // BUS-01
        contratante = sistema.buscarContratante("456"); // Empresa JRS, 2 años
    }

    @Test
    void prueba1_rechazaDosViajesCruzadosEnElMismoBus() throws Exception {
        Reserva primera = new Reserva(101, contratante, bus,
                new PaseoTuristico(6, 100),
                LocalDateTime.of(2026, 11, 16, 6, 0));
        sistema.agregarReserva(primera);
        Reserva segunda = new Reserva(102, contratante, bus,
                new PaseoTuristico(4, 80),
                LocalDateTime.of(2026, 11, 16, 10, 0));

        assertThrows(BusOcupadoException.class,
                () -> sistema.agregarReserva(segunda));
    }

    @Test
    void prueba2_permiteViajesContiguosEnElMismoBus() throws Exception {
        Reserva primera = new Reserva(103, contratante, bus,
                new PaseoTuristico(6, 100),
                LocalDateTime.of(2026, 11, 16, 6, 0));
        sistema.agregarReserva(primera);
        Reserva segunda = new Reserva(104, contratante, bus,
                new PaseoTuristico(4, 80),
                LocalDateTime.of(2026, 11, 16, 12, 0));

        assertDoesNotThrow(() -> sistema.agregarReserva(segunda));
    }

    @Test
    void prueba3_permiteCrearLaFranjaCuandoLaReservaCruzadaFueCancelada() throws Exception {
        Reserva primera = new Reserva(105, contratante, bus,
                new PaseoTuristico(6, 100),
                LocalDateTime.of(2026, 11, 16, 6, 0));
        sistema.agregarReserva(primera);


        primera.cancelar(LocalDateTime.of(2026, 11, 15, 8, 0));
        Reserva segunda = new Reserva(106, contratante, bus,
                new PaseoTuristico(4, 80),
                LocalDateTime.of(2026, 11, 16, 10, 0));

        assertDoesNotThrow(() -> sistema.agregarReserva(segunda));
    }

    @Test
    void prueba4_rechazaConfirmarUnaReservaCancelada() throws Exception {
        Reserva reserva = new Reserva(107, contratante, bus,
                new PaseoTuristico(6, 100),
                LocalDateTime.of(2026, 11, 16, 6, 0));

        reserva.cancelar(LocalDateTime.of(2026, 11, 15, 8, 0));

        assertThrows(TransicionInvalidaException.class, reserva::confirmar);
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    @Test
    void prueba5_aplicaElTopeDelQuincePorCientoAlTrasladoEmpresarial() throws Exception {
        // Contratante con 20 años de antigüedad
        Contratante antiguo = new Contratante("TEST-20", "Contratante de prueba",
                "5550000", LocalDate.of(2006, 1, 1));
        Reserva reserva = new Reserva(108, antiguo, bus,
                new TrasladoEmpresarial(4, 0),
                LocalDateTime.of(2026, 11, 17, 8, 0));
        assertEquals(212500, reserva.getValorFinal());
    }
    @Test
    void prueba6_aplicaUnSoloRecargoEnPaseoNocturnoDeSabado() throws Exception {
        // Contratante nuevo (0 años de antigüedad)
        Contratante nuevo = new Contratante("TEST-NUEVO", "Contratante nuevo",
                "5550001", LocalDate.of(2026, 11, 20));
        Reserva reserva = new Reserva(109, nuevo, bus,
                new PaseoTuristico(4, 80),
                LocalDateTime.of(2026, 11, 21, 19, 0));
        assertEquals(414000, reserva.getValorFinal());
    }
}