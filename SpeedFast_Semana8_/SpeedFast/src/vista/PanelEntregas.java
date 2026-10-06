package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;
import util.Dialogos;
import util.ModeloTabla;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña CRUD de entregas. Los combos de pedido y repartidor se cargan desde la BD y
 * conservan el objeto completo (por lo tanto su id), mostrando un texto legible.
 */
public class PanelEntregas extends JPanel {
    private static final String TODOS = "Todos";

    private final EntregaDAO dao = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final Runnable alCambiar;

    // Formulario
    private final JComboBox<Pedido> cbPedido = new JComboBox<>();
    private final JComboBox<Repartidor> cbRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(8);
    private final JTextField txtHora = new JTextField(5);

    // Filtros (primer ítem "Todos", luego Pedido / Repartidor)
    private final JComboBox<Object> cbFiltroPedido = new JComboBox<>();
    private final JComboBox<Object> cbFiltroRepartidor = new JComboBox<>();

    private final ModeloTabla modelo = new ModeloTabla("ID", "Pedido", "Repartidor", "Fecha", "Hora");
    private final JTable tabla = new JTable(modelo);
    private List<Entrega> entregasMostradas = new ArrayList<>(); // misma posición que las filas de la tabla
    private boolean cargando = false; // evita recargas en cascada mientras se llenan los combos

    public PanelEntregas(Runnable alCambiar) {
        super(new BorderLayout());
        this.alCambiar = alCambiar;
        construir();
        recargar();
    }

    private void construir() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Pedido:"));
        form.add(cbPedido);
        form.add(new JLabel("Repartidor:"));
        form.add(cbRepartidor);
        form.add(new JLabel("Fecha (AAAA-MM-DD):"));
        form.add(txtFecha);
        form.add(new JLabel("Hora (HH:MM):"));
        form.add(txtHora);
        JButton btnRegistrar = new JButton("Asignar entrega");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        form.add(btnRegistrar);
        form.add(btnActualizar);
        form.add(btnEliminar);
        form.add(btnLimpiar);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Pedido:"));
        filtros.add(cbFiltroPedido);
        filtros.add(new JLabel("Repartidor:"));
        filtros.add(cbFiltroRepartidor);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(filtros, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0 && fila < entregasMostradas.size()) {
                cargarEnFormulario(entregasMostradas.get(fila));
            }
        });

        cbFiltroPedido.addActionListener(e -> { if (!cargando) cargarTabla(); });
        cbFiltroRepartidor.addActionListener(e -> { if (!cargando) cargarTabla(); });
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        limpiarCamposFecha();
    }

    /** Lee y valida el formulario. Lanza IllegalArgumentException con el mensaje para el usuario. */
    private Entrega leerFormulario(int idExistente) throws SQLException {
        Pedido pedido = (Pedido) cbPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cbRepartidor.getSelectedItem();
        if (pedido == null) throw new IllegalArgumentException("Debe seleccionar un pedido (registre uno primero si la lista está vacía).");
        if (repartidor == null) throw new IllegalArgumentException("Debe seleccionar un repartidor (registre uno primero si la lista está vacía).");
        Date fecha = Validador.fecha(txtFecha.getText());
        Time hora = Validador.hora(txtHora.getText());
        if (dao.existeParaPedido(pedido.getId(), idExistente)) {
            throw new IllegalArgumentException("El pedido " + pedido.getId() + " ya tiene una entrega asignada.");
        }
        return new Entrega(idExistente, pedido.getId(), repartidor.getId(), fecha, hora);
    }

    private void registrar() {
        try {
            if (dao.create(leerFormulario(0))) {
                Dialogos.info(this, "Entrega registrada correctamente.");
                limpiar();
                alCambiar.run();
            }
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "registrar la entrega", ex);
            alCambiar.run(); // refresca combos por si el pedido/repartidor fue eliminado
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione una entrega de la tabla para actualizar.");
            return;
        }
        try {
            Entrega e = leerFormulario(entregasMostradas.get(fila).getId());
            if (dao.update(e)) {
                Dialogos.info(this, "Entrega actualizada.");
                limpiar();
            } else {
                Dialogos.aviso(this, "La entrega ya no existe.");
            }
            alCambiar.run();
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "actualizar la entrega", ex);
            alCambiar.run();
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione una entrega de la tabla.");
            return;
        }
        if (!Dialogos.confirmar(this, "¿Eliminar la entrega seleccionada?")) return;
        try {
            if (dao.delete(entregasMostradas.get(fila).getId())) {
                Dialogos.info(this, "Entrega eliminada.");
                limpiar();
                alCambiar.run();
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "eliminar la entrega", ex);
        }
    }

    private void cargarEnFormulario(Entrega e) {
        seleccionar(cbPedido, e.getIdPedido());
        seleccionar(cbRepartidor, e.getIdRepartidor());
        txtFecha.setText(e.getFecha().toString());
        txtHora.setText(e.getHora().toString().substring(0, 5));
    }

    /** Selecciona en el combo el elemento cuyo id coincide. */
    private void seleccionar(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object o = combo.getItemAt(i);
            int idItem = o instanceof Pedido ? ((Pedido) o).getId() : ((Repartidor) o).getId();
            if (idItem == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limpiar() {
        tabla.clearSelection();
        limpiarCamposFecha();
    }

    private void limpiarCamposFecha() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    /** Recarga combos (formulario y filtros) y la tabla desde la BD, conservando la selección de filtros. */
    public void recargar() {
        cargando = true;
        try {
            Integer filtroPedido = idSeleccionado(cbFiltroPedido);
            Integer filtroRepartidor = idSeleccionado(cbFiltroRepartidor);
            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();

            cbPedido.removeAllItems();
            cbFiltroPedido.removeAllItems();
            cbFiltroPedido.addItem(TODOS);
            for (Pedido p : pedidos) { cbPedido.addItem(p); cbFiltroPedido.addItem(p); }

            cbRepartidor.removeAllItems();
            cbFiltroRepartidor.removeAllItems();
            cbFiltroRepartidor.addItem(TODOS);
            for (Repartidor r : repartidores) { cbRepartidor.addItem(r); cbFiltroRepartidor.addItem(r); }

            if (filtroPedido != null) seleccionar(cbFiltroPedido, filtroPedido);
            if (filtroRepartidor != null) seleccionar(cbFiltroRepartidor, filtroRepartidor);
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "cargar pedidos y repartidores", ex);
        } finally {
            cargando = false;
        }
        cargarTabla();
    }

    private Integer idSeleccionado(JComboBox<Object> combo) {
        Object o = combo.getSelectedItem();
        if (o instanceof Pedido) return ((Pedido) o).getId();
        if (o instanceof Repartidor) return ((Repartidor) o).getId();
        return null; // "Todos" o vacío
    }

    private void cargarTabla() {
        try {
            entregasMostradas = dao.readFiltrado(idSeleccionado(cbFiltroPedido), idSeleccionado(cbFiltroRepartidor));
            modelo.setRowCount(0);
            for (Entrega e : entregasMostradas) {
                modelo.addRow(new Object[]{e.getId(), e.getInfoPedido(), e.getInfoRepartidor(), e.getFecha(), e.getHora()});
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "cargar las entregas", ex);
        }
    }
}
