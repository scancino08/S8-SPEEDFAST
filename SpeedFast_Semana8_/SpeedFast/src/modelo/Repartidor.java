package modelo;

/** Entidad que representa a un repartidor de SpeedFast (tabla repartidores). */
public class Repartidor {
    private int id;
    private String nombre;

    public Repartidor() {}

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** Texto legible usado por los JComboBox: "id - nombre". */
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
