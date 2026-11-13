package datos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexiones a MySQL con los datos de un archivo de configuración que
 * vive fuera del código (parchis-bd.properties) y no se sube a GitHub.
 */
public class ConexionBD {

    public static final String ARCHIVO = "parchis-bd.properties";

    private final String url;
    private final String usuario;
    private final String contrasena;

    public ConexionBD(String url, String usuario, String contrasena) {
        this.url = url;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    /** Lee host, puerto, base, usuario y contraseña del archivo. */
    public static ConexionBD desdeArchivo(Path archivo) throws PersistenciaException {
        Properties p = new Properties();
        try (InputStream entrada = Files.newInputStream(archivo)) {
            p.load(entrada);
        } catch (IOException e) {
            throw new PersistenciaException("No se encontró " + archivo.toAbsolutePath()
                    + ". Copia parchis-bd.properties.ejemplo y pon tus datos de MySQL.", e);
        }
        String url = "jdbc:mysql://" + p.getProperty("host", "localhost") + ":" + p.getProperty("puerto", "3306")
                + "/" + p.getProperty("base", "parchis") + "?serverTimezone=UTC";
        return new ConexionBD(url, p.getProperty("usuario", ""), p.getProperty("contrasena", ""));
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    /** Para avisar al arrancar si la base de datos no responde. */
    public void probar() throws PersistenciaException {
        try (Connection conexion = abrir()) {
            conexion.isValid(2);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL (" + url + "): " + e.getMessage(), e);
        }
    }
}
