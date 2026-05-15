
package GestionUsuarios;

import org.mindrot.jbcrypt.BCrypt;


/**
 *
 * @author Alex García Trejo
 */
public class GestionContraseña {

    public static String hashPassword(String password) {        
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String passwordUser, String hashPasswordUser) throws Exception {
        try {
            return BCrypt.checkpw(passwordUser, hashPasswordUser);
        } catch (Exception e) {
            throw new Exception("Contraseña incorrecta");
        }
    }        
}