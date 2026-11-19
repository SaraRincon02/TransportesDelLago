package co.edu.uptc.negocio;
import co.edu.uptc.controlador.SistemaTransporte;
import co.edu.uptc.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SistemaTransporteTest {

    private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);
    private static final LocalDate MARTES = LocalDate.of(2026, 11, 17);
    private static final LocalDate SABADO = LocalDate.of(2026, 11, 21);

    private SistemaTransporte sistema;

    @BeforeEach
    void preparar(){
        sistema = new SistemaTransporte();

    }
}
