package modelo;

/** Entidad que representa un pedido (tabla pedidos). */
public class Pedido {
    private int id;
    private String direccion;
    private TipoPedido tipo;
    private EstadoPedido estado;

    public Pedido() {}

    public Pedido(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.id = id;
        this.direccion = direccion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public Pedido(String direccion, TipoPedido tipo, EstadoPedido estado) {
        this(0, direccion, tipo, estado);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public TipoPedido getTipo() { return tipo; }
    public void setTipo(TipoPedido tipo) { this.tipo = tipo; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    /** Texto legible usado por los JComboBox: "id - dirección (tipo)". */
    @Override
    public String toString() {
        return id + " - " + direccion + " (" + tipo + ")";
    }
}
