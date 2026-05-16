package utilidades;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Centraliza el registro de eventos del sistema.
 * Guarda la información tanto en consola como en un archivo físico.
 */
public class GestorLogs {
    private static final Logger logger = Logger.getLogger("TCPvChat");

    static {
        try {
            FileHandler fh = new FileHandler("historial_servidor.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("CRÍTICO: No se pudo inicializar el archivo de logs.");
        }
    }

    public static void registrarInfo(String mensaje) {
        logger.info(mensaje);
    }

    public static void registrarAdvertencia(String mensaje) {
        logger.warning(mensaje);
    }

    public static void registrarError(String mensaje, Throwable excepcion) {
        logger.log(Level.SEVERE, mensaje, excepcion);
    }
}