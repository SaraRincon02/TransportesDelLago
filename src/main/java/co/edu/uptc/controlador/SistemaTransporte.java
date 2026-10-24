package co.edu.uptc.controlador;

import co.edu.uptc.modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaTransporte {
    private List<Reserva> reservas;
    private List<Contratante> contratantes;
    private List<Bus> buses;

    public SistemaTransporte(){
        this.reservas = new ArrayList<>();
        this.contratantes = new ArrayList<>();
        this.buses = new ArrayList<>();
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
