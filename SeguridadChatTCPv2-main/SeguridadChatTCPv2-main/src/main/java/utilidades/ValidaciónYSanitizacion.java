package utilidades;

import java.util.regex.Pattern;

/**
 * Clase utilitaria encargada de limpiar las entradas del usuario 
 * y validar que los datos cumplan con las reglas de negocio y de red.
 *
 * @author
 */
public class ValidaciónYSanitizacion {

    // Expresión regular para validar el formato estándar de una IPv4 (ej. 192.168.1.1)
    private static final Pattern PATRON_IP = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
    );

    /**
     * Sanitiza el texto ingresado por el usuario.
     * Elimina espacios en blanco a los extremos y, lo más importante, 
     * borra los ":" para evitar que se rompa el protocolo de mensajes del servidor.
     * * @param entrada Texto crudo obtenido de la interfaz.
     * @return Texto limpio y seguro. Regresa una cadena vacía si la entrada es nula.
     */
    public static String sanitizarTexto(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            return "";
        }
        // .trim() quita espacios al inicio y final
        // .replace() elimina nuestro delimitador del protocolo
        return entrada.trim().replace(":", "");
    }

    /**
     * Valida que un texto no esté vacío y cumpla con una longitud específica.
     * Útil para nombres de usuario, contraseñas o nombres de servidor.
     * * @param texto La cadena a evaluar.
     * @param min Longitud mínima permitida.
     * @param max Longitud máxima permitida.
     * @return true si es válido, false en caso contrario.
     */
    public static boolean esLongitudValida(String texto, int min, int max) {
        if (texto == null || texto.isBlank()) {
            return false;
        }
        int len = texto.length();
        return len >= min && len <= max;
    }

    /**
     * Valida que el puerto ingresado esté en un rango seguro.
     * Evita los puertos del 0 al 1023 ya que suelen estar reservados por el sistema operativo.
     * * @param puerto Número de puerto a evaluar.
     * @return true si es un puerto utilizable, false si es nulo o está fuera de rango.
     */
    public static boolean esPuertoValido(Integer puerto) {
        if (puerto == null) {
            return false;
        }
        // Rango de puertos efímeros o registrados seguros
        return puerto > 1023 && puerto <= 65535;
    }

    /**
     * Valida si la cadena ingresada es una dirección IP utilizable por el Socket.
     * Acepta explícitamente "localhost" o cualquier IPv4 válida.
     * * @param ip Cadena de texto con la IP.
     * @return true si el formato es correcto.
     */
    public static boolean esIpValida(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }

        // Limpiamos espacios por si el usuario tecleó un espacio extra al final
        String ipLimpia = ip.trim();

        if (ipLimpia.equalsIgnoreCase("localhost")) {
            return true;
        }

        return PATRON_IP.matcher(ipLimpia).matches();
    }
}