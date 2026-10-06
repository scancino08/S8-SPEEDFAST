# SpeedFast - Semana 8 (Desarrollo Orientado a Objetos II)

Aplicación Java Swing con persistencia en MySQL mediante JDBC (patrón DAO, `PreparedStatement` y `ResultSet`)
para gestionar **repartidores**, **pedidos** y **entregas** (CRUD completo).

## Estructura
| Carpeta / archivo | Contenido |
|---|---|
| `src/config/ConexionDB.java` | Conexión JDBC (credenciales ajustables por variables de entorno) |
| `src/modelo/` | Entidades `Repartidor`, `Pedido`, `Entrega` y enums `TipoPedido`, `EstadoPedido` |
| `src/dao/` | `RepartidorDAO`, `PedidoDAO`, `EntregaDAO` con `create()`, `readAll()`, `update()`, `delete()` y lecturas filtradas |
| `src/vista/` | `VentanaPrincipal` + un panel por entidad (`PanelRepartidores`, `PanelPedidos`, `PanelEntregas`) |
| `src/util/` | `Validador` (validaciones), `Dialogos` (mensajes y errores SQL), `ModeloTabla` (tabla de solo lectura) |
| `script.sql` | Creación de la base `speedfast_db` y sus tablas |

## Cómo ejecutar
1. Ejecutar `script.sql` en MySQL Workbench o consola.
2. Descargar MySQL Connector/J, copiarlo en `lib/` y agregarlo como librería en IntelliJ (ver `lib/LEEME.txt`).
3. Ajustar usuario y contraseña en `ConexionDB.java` o definir `SPEEDFAST_DB_USER` / `SPEEDFAST_DB_PASSWORD`.
4. Ejecutar `vista.VentanaPrincipal` (JDK 11 o superior).

## Funcionalidades
- **Repartidores / Pedidos / Entregas:** registrar, actualizar (al seleccionar una fila se carga en el formulario), eliminar (con confirmación) y listar en `JTable`.
- **Filtros:** pedidos por estado y/o tipo; entregas por pedido y/o repartidor.
- **Combos relacionados:** pedido y repartidor en Entregas se cargan desde la BD (`id - texto`) y se refrescan tras cualquier cambio.
- **Validaciones:** campos obligatorios, largo máximo (100), formato de nombre, fecha `AAAA-MM-DD` real, hora `HH:MM`, un pedido no puede tener dos entregas.
- **Errores SQL:** se muestran con mensajes claros (por ejemplo, no se puede eliminar un pedido o repartidor con entregas asociadas, o la BD no está disponible).
