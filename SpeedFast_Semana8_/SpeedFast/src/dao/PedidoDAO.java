package dao;

import config.ConexionDB;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla pedidos.
 * Todos los métodos propagan {@link SQLException} para que la vista informe el motivo al usuario.
 */
public class PedidoDAO {

    /** Inserta un pedido. @return true si se insertó la fila. */
    public boolean create(Pedido p) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getTipo().name());
            ps.setString(3, p.getEstado().name());
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista todos los pedidos. */
    public List<Pedido> readAll() throws SQLException {
        return readFiltrado(null, null);
    }

    /**
     * Lista pedidos aplicando filtros opcionales.
     *
     * @param estado estado a filtrar, o null para no filtrar
     * @param tipo   tipo a filtrar, o null para no filtrar
     */
    public List<Pedido> readFiltrado(EstadoPedido estado, TipoPedido tipo) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        // Cada filtro es opcional: "(? IS NULL OR columna = ?)"
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos " +
                     "WHERE (? IS NULL OR estado = ?) AND (? IS NULL OR tipo = ?) ORDER BY id";
        String e = estado == null ? null : estado.name();
        String t = tipo == null ? null : tipo.name();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e);
            ps.setString(2, e);
            ps.setString(3, t);
            ps.setString(4, t);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pedido(
                            rs.getInt("id"),
                            rs.getString("direccion"),
                            TipoPedido.valueOf(rs.getString("tipo")),
                            EstadoPedido.valueOf(rs.getString("estado"))));
                }
            }
        }
        return lista;
    }

    /** Actualiza dirección, tipo y estado del pedido con el id indicado. */
    public boolean update(Pedido p) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getTipo().name());
            ps.setString(3, p.getEstado().name());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina un pedido. Falla (código 1451) si tiene entregas asociadas. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
