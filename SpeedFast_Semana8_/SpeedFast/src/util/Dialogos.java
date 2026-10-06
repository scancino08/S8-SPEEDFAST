package util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

/** Mensajes al usuario (JOptionPane) y traducción de errores SQL a texto comprensible. */
public final class Dialogos {
    private static final int ERR_FK_PADRE_REFERENCIADO = 1451; // DELETE/UPDATE con filas hijas
    private static final int ERR_FK_HIJO_INVALIDO = 1452;      // INSERT/UPDATE con padre inexistente

    private Dialogos() {}

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "SpeedFast", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void validacion(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    /** Muestra un error de base de datos con una explicación clara de la causa. */
    public static void errorSQL(Component padre, String accion, SQLException ex) {
        System.err.println("Error SQL al " + accion + ": " + ex.getMessage()); // detalle técnico para depuración
        JOptionPane.showMessageDialog(padre, "No se pudo " + accion + ".\n" + explicar(ex),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    /** Pregunta Sí/No antes de una acción destructiva. */
    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static String explicar(SQLException ex) {
        switch (ex.getErrorCode()) {
            case ERR_FK_PADRE_REFERENCIADO:
                return "El registro está siendo usado por una o más entregas. Elimine primero esas entregas.";
            case ERR_FK_HIJO_INVALIDO:
                return "El pedido o repartidor seleccionado ya no existe. Se refrescarán las listas.";
            default:
                String sqlState = ex.getSQLState();
                if (sqlState != null && sqlState.startsWith("08")) {
                    return "No hay conexión con la base de datos. Verifique que MySQL esté activo y las credenciales sean correctas.";
                }
                return "Detalle: " + ex.getMessage();
        }
    }
}
