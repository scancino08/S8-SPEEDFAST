package dao;

import config.ConexionDB;
import modelo.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla entregas.
 * Todos los métodos propagan {@link SQLException} para que la vista informe el motivo al usuario.
 */
public class EntregaDAO {

    /** Inserta una entrega. @return true si se insertó la fila. */
    public boolean create(Entrega e) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista todas las entregas con los datos del pedido y repartidor (JOIN). */
    public List<Entrega> readAll() throws SQLException {
        return readFiltrado(null, null);
    }

    /**
     * Lista entregas aplicando filtros opcionales.
     *
     * @param idPedido      id del pedido, o null para no filtrar
     * @param idRepartidor  id del repartidor, o null para no filtrar
     */
    public List<Entrega> readFiltrado(Integer idPedido, Integer idRepartidor) throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, " +
                     "p.direccion, p.tipo, r.nombre AS repartidor_nombre " +
                     "FROM entregas e " +
                     "JOIN pedidos p ON e.id_pedido = p.id " +
                     "JOIN repartidores r ON e.id_repartidor = r.id " +
                     "WHERE (? IS NULL OR e.id_pedido = ?) AND (? IS NULL OR e.id_repartidor = ?) " +
                     "ORDER BY e.id";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, idPedido, java.sql.Types.INTEGER);
            ps.setObject(2, idPedido, java.sql.Types.INTEGER);
            ps.setObject(3, idRepartidor, java.sql.Types.INTEGER);
            ps.setObject(4, idRepartidor, java.sql.Types.INTEGER);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega e = new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha"),
                            rs.getTime("hora"));
                    e.setInfoPedido(rs.getInt("id_pedido") + " - " + rs.getString("direccion")
                            + " (" + rs.getString("tipo") + ")");
                    e.setInfoRepartidor(rs.getInt("id_repartidor") + " - " + rs.getString("repartidor_nombre"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    /**
     * Indica si un pedido ya tiene una entrega registrada.
     *
     * @param idPedido   pedido a consultar
     * @param excluirId  id de entrega a ignorar (para editar), o 0 para no excluir ninguna
     */
    public boolean existeParaPedido(int idPedido, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM entregas WHERE id_pedido = ? AND id <> ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** Actualiza pedido, repartidor, fecha y hora de la entrega con el id indicado. */
    public boolean update(Entrega e) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina una entrega por id. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
