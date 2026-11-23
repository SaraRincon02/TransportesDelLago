# Transportes del Lago

Sistema de agendamiento de reservas para viajes especiales (colegios, empresas y familias).

**Asignatura:** Programación I · Grupo 62 · Ing. de Sistemas y Computación  
**Docente:** Ing. Juan Andrés González Estepa  
**Entrega:** Lunes 23 de noviembre, 11:59 p.m.

---

## 👥 Integrantes y repartición equitativa del trabajo

| Integrante       | Código    | Responsabilidad principal | Aporte concreto                                                                                                                                                                                                                                                                                                      |
|------------------|-----------|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Sara Rincon      | 202113509 | **Modelo del negocio** | Clases `Reserva`, `Bus`, `Contratante`, `Servicio` (y sus 3 hijas), `Encomienda`, `EstadoReserva`, `Asegurable` y las 4 excepciones. Implementación de las reglas de valor (recargo, descuento, penalidad) y de las transiciones de estado.                                                                          |
| Angelica Beltran | 202520199 | **Persistencia y control** | Clases `SistemaTransporte` y `ArchivoTransporte`. Guardado y carga en 4 archivos de texto, verificación de cruce de buses, cálculo de reportes (ingresos, estados, bus más usado, penalidades) y precarga de datos de fábrica, Clase `MenuConsola` (menú por consola con `Scanner` y los 3 `JOptionPane` requeridos. |
| Karoll Perez     | 202521888 | **Presentación, pruebas y documentación** | `Main`, las 6 pruebas JUnit de `SistemaTransporteTest`, el `README.md`, el `INTEGRANTES.txt` y el diagrama UML.                                                                                                                                                                                                      |

> **Nota:** Las tres personas participaron en la revisión conjunta del código, en la corrección de errores y en la sustentación oral. La repartición anterior es la **responsabilidad principal** de cada una; el trabajo fue colaborativo en todo momento.

---

## 🚀 Cómo ejecutar

1. Abrir el proyecto en IntelliJ IDEA.
2. Ejecutar `Main.java`.
3. El menú aparece en consola.
4. Los datos se guardan automáticamente en la carpeta `datos/` (4 archivos `.txt`).

---

## 📁 Estructura del proyecto

- **`modelo`**: `Reserva`, `Bus`, `Contratante`, `Servicio` (abstracta), `PaseoTuristico`, `ExcursionEscolar`, `TrasladoEmpresarial`, `Encomienda`, `EstadoReserva`, `Asegurable`, `DatoInvalidoException`, `TransicionInvalidaException`.
- **`negocio`**: `SistemaTransporte`, `ArchivoTransporte`, `BusOcupadoException`, `ContratanteNoEncontradoException`.
- **`presentacion`**: `MenuConsola`.

---

## ✅ Funcionalidades implementadas

- Registrar buses, contratantes y encomiendas sin identificadores repetidos.
- Crear reservas con cálculo de valor mostrado **antes** de guardar (con `JOptionPane`).
- Confirmar, iniciar, cerrar, cancelar y reprogramar reservas.
- Consultar agenda de un bus en una fecha e historial de un cliente.
- Reportes:
    1. Ingresos de reservas cerradas entre dos fechas.
    2. Cantidad de reservas en cada estado.
    3. Bus más usado.
    4. Total cobrado en penalidades.
- Seguro y póliza de excursiones escolares y encomiendas (interfaz `Asegurable`).
- Persistencia en 4 archivos de texto (`reservas.txt`, `buses.txt`, `contratantes.txt`, `encomiendas.txt`).
- Precarga de datos de fábrica: 6 buses, 4 contratantes (uno con más de 8 años), 3 encomiendas y 10 reservas repartidas entre los 3 servicios y los 5 estados.

---

## 📐 Reglas de negocio

- **Recargo:** 15 % si la franja empieza a las 6:00 p.m. o después, o si es sábado o domingo. Se aplica **una sola vez**, aunque se cumplan las dos condiciones.
- **Descuento:** 2 % por año de antigüedad del contratante, con tope del 15 %. Se aplica **sobre el valor ya recargado**.
- **Penalidad:** 30 % del valor si se cancela una reserva confirmada con menos de 24 horas de anticipación.
- **Cruce de buses:** dos reservas del mismo bus se cruzan si `inicioA.isBefore(finB) && inicioB.isBefore(finA)`. Las canceladas no cuentan. Las contiguas (una termina a la hora en que empieza la otra) **no** se cruzan.
- **Transiciones de estado:** solo las permitidas por la tabla del enunciado. Cualquier otra lanza `TransicionInvalidaException`.

---

## 🧪 Pruebas

Ejecutar `SistemaTransporteTest` en IntelliJ. Son **6 pruebas JUnit** que cubren:

1. Cruce de franjas en el mismo bus → `BusOcupadoException`.
2. Franjas contiguas → **no** lanza.
3. Cancelación libera la franja → **no** lanza.
4. Confirmar una reserva cancelada → `TransicionInvalidaException`.
5. Tope del 15 % de descuento en traslado empresarial → `$212.500`.
6. Un solo recargo en paseo nocturno de sábado → `$414.000`.

Las pruebas usan `@TempDir`, así que **no tocan** los datos reales del usuario.

---

## 🛠️ Requisitos

- **Java 17** o superior.
- **Maven** (opcional, para ejecutar las pruebas).

---

