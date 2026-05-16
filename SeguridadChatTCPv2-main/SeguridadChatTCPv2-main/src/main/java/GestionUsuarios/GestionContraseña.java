package GestionUsuarios;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase utilitaria para la gestión de seguridad de credenciales.
 * Envuelve la librería BCrypt para aplicar funciones criptográficas de Hash y
 * validación (Salting incluido) a las contraseñas ingresadas.
 *
 * @author Alex García Trejo
 */
public class GestionContraseña {

    /**
     * Hashea una contraseña plana agregando automáticamente un "salt" dinámico
     * para mitigar ataques de diccionario o Rainbow Tables.
     *
     * @param password Contraseña original en texto claro.
     * @return El String resultante del proceso de encriptación unidireccional.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Compara una contraseña en texto plano contra el hash almacenado para
     * verificar su validez sin descifrar el hash.
     *
     * @param passwordUser Contraseña ingresada durante un intento de inicio de sesión.
     * @param hashPasswordUser Hash almacenado y asociado a ese usuario en base de datos.
     * @return true si coinciden, false de lo contrario.
     * @throws Exception Si el hash introducido no es válido o está malformado.
     */
    public static boolean checkPassword(String passwordUser, String hashPasswordUser) throws Exception {
        try {
            return BCrypt.checkpw(passwordUser, hashPasswordUser);
        } catch (Exception e) {
            throw new Exception("Contraseña incorrecta");
        }
    }
}