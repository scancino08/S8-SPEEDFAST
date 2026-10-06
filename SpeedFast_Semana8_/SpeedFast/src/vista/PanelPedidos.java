package vista;

import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;
import util.Dialogos;
import util.ModeloTabla;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Pestaña CRUD de pedidos, con filtros opcionales por estado y tipo. */
public class PanelPedidos extends JPanel {
    private static final String TODOS = "TODOS";

    private final PedidoDAO dao = new PedidoDAO();
    private final Runnable alCambiar;

    private final JTextField txtDireccion = new JTextField(18);
    private final JComboBox<TipoPedido> cbTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> cbEstado = new JComboBox<>(EstadoPedido.values());
    private final JComboBox<Object> cbFiltroEstado = new JComboBox<>();
    private final JComboBox<Object> cbFiltroTipo = new JComboBox<>();
    private final ModeloTabla modelo = new ModeloTabla("ID", "Dirección", "Tipo", "Estado");
    private final JTable tabla = new JTable(modelo);

    public PanelPedidos(Runnable alCambiar) {
        super(new BorderLayout());
        this.alCambiar = alCambiar;
        construir();
        recargar();
    }

    private void construir() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Dirección:"));
        form.add(txtDireccion);
        form.add(new JLabel("Tipo:"));
        form.add(cbTipo);
        form.add(new JLabel("Estado:"));
        form.add(cbEstado);
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        form.add(btnRegistrar);
        form.add(btnActualizar);
        form.add(btnEliminar);
        form.add(btnLimpiar);

        // Filtros opcionales: la primera opción "TODOS" desactiva el filtro
        cbFiltroEstado.addItem(TODOS);
        for (EstadoPedido e : EstadoPedido.values()) cbFiltroEstado.addItem(e);
        cbFiltroTipo.addItem(TODOS);
        for (TipoPedido t : TipoPedido.values()) cbFiltroTipo.addItem(t);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Estado:"));
        filtros.add(cbFiltroEstado);
        filtros.add(new JLabel("Tipo:"));
        filtros.add(cbFiltroTipo);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(filtros, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtDireccion.setText((String) modelo.getValueAt(fila, 1));
                cbTipo.setSelectedItem(modelo.getValueAt(fila, 2));
                cbEstado.setSelectedItem(modelo.getValueAt(fila, 3));
            }
        });

        cbFiltroEstado.addActionListener(e -> recargar());
        cbFiltroTipo.addActionListener(e -> recargar());
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void registrar() {
        try {
            String direccion = Validador.direccion(txtDireccion.getText());
            Pedido p = new Pedido(direccion, (TipoPedido) cbTipo.getSelectedItem(), (EstadoPedido) cbEstado.getSelectedItem());
            if (dao.create(p)) {
                Dialogos.info(this, "Pedido registrado con éxito.");
                limpiar();
                alCambiar.run();
            }
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "registrar el pedido", ex);
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione un pedido de la tabla para actualizar.");
            return;
        }
        try {
            String direccion = Validador.direccion(txtDireccion.getText());
            int id = (int) modelo.getValueAt(fila, 0);
            Pedido p = new Pedido(id, direccion, (TipoPedido) cbTipo.getSelectedItem(), (EstadoPedido) cbEstado.getSelectedItem());
            if (dao.update(p)) {
                Dialogos.info(this, "Pedido actualizado.");
                limpiar();
            } else {
                Dialogos.aviso(this, "El pedido ya no existe.");
            }
            alCambiar.run();
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "actualizar el pedido", ex);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione un pedido de la tabla.");
            return;
        }
        if (!Dialogos.confirmar(this, "¿Eliminar el pedido seleccionado?")) return;
        try {
            if (dao.delete((int) modelo.getValueAt(fila, 0))) {
                Dialogos.info(this, "Pedido eliminado.");
                limpiar();
                alCambiar.run();
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "eliminar el pedido", ex);
        }
    }

    private void limpiar() {
        txtDireccion.setText("");
        cbTipo.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }

    /** Vuelve a leer los pedidos desde la BD respetando los filtros seleccionados. */
    public void recargar() {
        Object fe = cbFiltroEstado.getSelectedItem();
        Object ft = cbFiltroTipo.getSelectedItem();
        EstadoPedido estado = fe instanceof EstadoPedido ? (EstadoPedido) fe : null;
        TipoPedido tipo = ft instanceof TipoPedido ? (TipoPedido) ft : null;
        try {
            modelo.setRowCount(0);
            for (Pedido p : dao.readFiltrado(estado, tipo)) {
                modelo.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "cargar los pedidos", ex);
        }
    }
}
