package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;
import util.Dialogos;
import util.ModeloTabla;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Pestaña CRUD de repartidores. */
public class PanelRepartidores extends JPanel {
    private final RepartidorDAO dao = new RepartidorDAO();
    private final Runnable alCambiar; // avisa a la ventana para refrescar otros paneles

    private final JTextField txtNombre = new JTextField(20);
    private final ModeloTabla modelo = new ModeloTabla("ID", "Nombre");
    private final JTable tabla = new JTable(modelo);

    public PanelRepartidores(Runnable alCambiar) {
        super(new BorderLayout());
        this.alCambiar = alCambiar;
        construir();
        recargar();
    }

    private void construir() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        form.add(btnRegistrar);
        form.add(btnActualizar);
        form.add(btnEliminar);
        form.add(btnLimpiar);
        add(form, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Al seleccionar una fila se carga en el formulario para poder editarla
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtNombre.setText((String) modelo.getValueAt(fila, 1));
            }
        });

        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void registrar() {
        try {
            String nombre = Validador.nombre(txtNombre.getText());
            if (dao.create(new Repartidor(nombre))) {
                Dialogos.info(this, "Repartidor registrado con éxito.");
                limpiar();
                alCambiar.run();
            }
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "registrar el repartidor", ex);
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione un repartidor de la tabla para actualizar.");
            return;
        }
        try {
            String nombre = Validador.nombre(txtNombre.getText());
            int id = (int) modelo.getValueAt(fila, 0);
            if (dao.update(new Repartidor(id, nombre))) {
                Dialogos.info(this, "Repartidor actualizado.");
                limpiar();
                alCambiar.run();
            } else {
                Dialogos.aviso(this, "El repartidor ya no existe.");
                alCambiar.run();
            }
        } catch (IllegalArgumentException ex) {
            Dialogos.validacion(this, ex.getMessage());
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "actualizar el repartidor", ex);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Seleccione un repartidor de la tabla.");
            return;
        }
        if (!Dialogos.confirmar(this, "¿Eliminar al repartidor seleccionado?")) return;
        try {
            if (dao.delete((int) modelo.getValueAt(fila, 0))) {
                Dialogos.info(this, "Repartidor eliminado.");
                limpiar();
                alCambiar.run();
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "eliminar el repartidor", ex);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        tabla.clearSelection();
    }

    /** Vuelve a leer los repartidores desde la BD y actualiza la tabla. */
    public void recargar() {
        try {
            modelo.setRowCount(0);
            for (Repartidor r : dao.readAll()) {
                modelo.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException ex) {
            Dialogos.errorSQL(this, "cargar los repartidores", ex);
        }
    }
}
