package vista;

import javax.swing.*;

/**
 * Ventana principal de SpeedFast: una pestaña por entidad.
 * Cuando cualquier panel modifica datos, se refrescan todos los demás (tablas y combos).
 */
public class VentanaPrincipal extends JFrame {

    private final PanelRepartidores panelRepartidores;
    private final PanelPedidos panelPedidos;
    private final PanelEntregas panelEntregas;

    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de Pedidos y Entregas");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelRepartidores = new PanelRepartidores(this::refrescarTodo);
        panelPedidos = new PanelPedidos(this::refrescarTodo);
        panelEntregas = new PanelEntregas(this::refrescarTodo);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", panelRepartidores);
        pestanas.addTab("Pedidos", panelPedidos);
        pestanas.addTab("Entregas", panelEntregas);
        add(pestanas);
    }

    /** Recarga todas las tablas y combos desde la base de datos. */
    private void refrescarTodo() {
        panelRepartidores.recargar();
        panelPedidos.recargar();
        panelEntregas.recargar();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
