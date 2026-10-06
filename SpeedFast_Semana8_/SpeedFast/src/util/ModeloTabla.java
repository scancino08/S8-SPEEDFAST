package util;

import javax.swing.table.DefaultTableModel;

/** DefaultTableModel de solo lectura: evita que el usuario edite celdas directamente en la tabla. */
public class ModeloTabla extends DefaultTableModel {
    public ModeloTabla(String... columnas) {
        super(columnas, 0);
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
