package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión JDBC a la base de datos MySQL speedfast_db.
 * Los datos de acceso pueden sobreescribirse con las variables de entorno
 * SPEEDFAST_DB_URL, SPEEDFAST_DB_USER y SPEEDFAST_DB_PASSWORD.
 */
public class ConexionDB {
    private static final String URL = valor("SPEEDFAST_DB_URL",
            "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&serverTimezone=UTC");
    private static final String USER = valor("SPEEDFAST_DB_USER", "root");
    private static final String PASSWORD = valor("SPEEDFAST_DB_PASSWORD", "Shiruka2503.");

    private ConexionDB() {}

    private static String valor(String variable, String porDefecto) {
        String v = System.getenv(variable);
        return (v == null || v.isEmpty()) ? porDefecto : v;
    }

    /**
     * Abre una nueva conexión. El llamador debe cerrarla (try-with-resources).
     *
     * @throws SQLException si el driver no está disponible o la BD no responde
     */
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL (agregue mysql-connector-j al classpath).", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
