package modelo;

import java.sql.Date;
import java.sql.Time;

/** Entidad que asocia un pedido con un repartidor en una fecha y hora (tabla entregas). */
public class Entrega {
    private int id;
    private int idPedido;
    private int idRepartidor;
    private Date fecha;
    private Time hora;

    // Atributos de solo lectura (vienen del JOIN) para mostrar texto legible en la tabla
    private String infoPedido;
    private String infoRepartidor;

    public Entrega() {}

    public Entrega(int id, int idPedido, int idRepartidor, Date fecha, Time hora) {
        this.id = id;
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Entrega(int idPedido, int idRepartidor, Date fecha, Time hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Time getHora() { return hora; }
    public void setHora(Time hora) { this.hora = hora; }

    public String getInfoPedido() { return infoPedido; }
    public void setInfoPedido(String infoPedido) { this.infoPedido = infoPedido; }

    public String getInfoRepartidor() { return infoRepartidor; }
    public void setInfoRepartidor(String infoRepartidor) { this.infoRepartidor = infoRepartidor; }
}
