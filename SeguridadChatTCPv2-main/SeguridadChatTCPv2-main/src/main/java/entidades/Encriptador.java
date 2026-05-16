package entidades;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase responsable de la seguridad criptográfica de la aplicación.
 * Implementa el protocolo Diffie-Hellman para establecer una clave secreta
 * compartida de manera segura, y un algoritmo inspirado en Vigenère para
 * cifrar y descifrar el tráfico utilizando un alfabeto personalizado.
 *
 */
public class Encriptador {

    private String mensaje;
    private String clave;
    private final String letras = " AaBbCcDdEeFfGgHhIiJjKkLlMmNnÑñOoPpQqRrSsTtUuVvWwXxYyZz0123456789!@#$%&()[]{}<>?¿¡|/*-+.,_;:'^`~ÇüéâäàåçêëèïîìÄÅÉæÆôöûùÿÖÜø£Øƒáíóúªº®¬½¼«»░▒▓│┤ÁÂÀ©╣║╗╝¢¥┐└┴┬├─┼ãÃ╚╔╩╦╠═╬¤ðÐÊËÈıÍÎÏ┘┌█▄¦Ì▀ÓßÔÒõÕµþÞÚÛÙýÝ¯´≡±‗¾¶§¸°¨¹³²■";
    private final int tamDic = letras.length(); //aqui podemos agregar todo el ASCII
    private ArrayList<String> lstDiccionario = enlistador(letras);;
    private ArrayList<String> lstMensaje;

    private BigInteger P;
    private BigInteger G;
    private BigInteger numPriv;
    private BigInteger numPub;
    private BigInteger K;


    /**
     * Constructor por defecto. Inicializa el encriptador asumiendo el rol de Cliente (Iniciador).
     * Genera un número primo grande (P), una base (G), y calcula su clave pública.
     */
    public Encriptador() {
        this.P = new BigInteger("3273390607896141870013189696827599152216642046043064789483291368096133796404674554883270092325904157150886684127560071009217256545885393053328527589431");
        this.G = new BigInteger("2");
        this.numPriv = new BigInteger(P.bitLength(), new Random()).mod(P);
        this.numPub = G.modPow(numPriv, P);
    }

    /**
     * Constructor parametrizado. Inicializa el encriptador asumiendo el rol de Servidor (Receptor).
     * Toma los datos públicos del Cliente y genera su propia clave pública y la clave secreta final.
     *
     * @param datosCliente Arreglo con la base (G), el número primo (P) y la clave pública del Cliente.
     */
    public Encriptador(BigInteger[] datosCliente) {
        this.G = datosCliente[0];
        this.P = datosCliente[1];
        this.numPriv = new BigInteger(P.bitLength(), new Random()).mod(P);
        this.numPub = G.modPow(numPriv, P);
        BigInteger clavePublicaCliente = datosCliente[2];
        this.K = clavePublicaCliente.modPow(numPriv, P);
        this.formatearClave();
    }

    /**
     * Método invocado por el Cliente después de recibir los parámetros públicos del Servidor.
     * Calcula la clave secreta (K) y formatea la clave para el algoritmo de Vigenère.
     *
     * @param datosServidor Arreglo con la base, número primo y clave pública del Servidor.
     */
    public void finalizar(BigInteger datosServidor[]) {
        if(this.clave != null) return;
        this.K = datosServidor[2].modPow(this.numPriv, datosServidor[1]);
        this.formatearClave();
    }

    /**
     * Cifra el mensaje actual almacenado en la clase utilizando el algoritmo
     * de Vigenère con la clave calculada por Diffie-Hellman.
     * Sobrescribe el mensaje claro con su versión cifrada.
     */
    public void cifrar() {
        if(clave == null) return;
        ArrayList<String> lstClave = enlistador(clave);
        String mensajeEncriptado = "";
        for (int i = 0; i < lstMensaje.size(); i++) {
            int a = lstDiccionario.indexOf(lstMensaje.get(i));
            int b = lstDiccionario.indexOf(lstClave.get(i % lstClave.size()));
            String letraNueva = lstDiccionario.get((a + b) % tamDic);
            mensajeEncriptado += letraNueva;
        }
        this.mensaje = mensajeEncriptado;
        this.lstMensaje = enlistador(mensajeEncriptado);
    }

    /**
     * Descifra el mensaje cifrado almacenado en la clase utilizando la operación
     * inversa del algoritmo de Vigenère y la clave secreta.
     * Sobrescribe el mensaje cifrado con su versión en texto claro.
     */
    public void decifrar() {
        if (clave == null) return;
        ArrayList<String> lstClave = enlistador(clave);
        String mensajeClaro = "";
        for (int i = 0; i < lstMensaje.size(); i++) {
            int a = lstDiccionario.indexOf(lstMensaje.get(i));
            int b = lstDiccionario.indexOf(lstClave.get(i % lstClave.size()));
            String letraNueva = lstDiccionario.get((a - b + tamDic) % tamDic);
            mensajeClaro += letraNueva;
        }
        this.mensaje = mensajeClaro;
        this.lstMensaje = enlistador(mensajeClaro);
    }

    /**
     * Transforma una cadena de texto (String) en una lista de caracteres (ArrayList)
     * para facilitar la iteración letra por letra durante el cifrado.
     *
     * @param texto Cadena a transformar.
     * @return Lista de Strings donde cada elemento es un carácter.
     */
    private ArrayList<String> enlistador(String texto) {
        ArrayList<String> lista = new ArrayList<String>();
        for (int i = 0; i < texto.length(); i++) {
            String x = String.valueOf(texto.charAt(i));
            lista.add(x);
        }
        return lista;
    }

    /**
     * Convierte la clave numérica gigante (BigInteger) resultante de Diffie-Hellman
     * en una cadena de caracteres usando el diccionario personalizado como base numérica.
     */
    private void formatearClave() {
        if(this.K == null) return;
        StringBuilder result = new StringBuilder();
        BigInteger base = BigInteger.valueOf(tamDic);
        while (K.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divAndRem = K.divideAndRemainder(base);
            int remainder = divAndRem[1].intValue();
            result.insert(0, letras.charAt(remainder));
            K = divAndRem[0];
        }
        this.clave = result.toString();
    }

    /**
     * Devuelve los parámetros públicos necesarios para el intercambio Diffie-Hellman.
     *
     * @return Arreglo con la base G, el primo P y la clave pública generada.
     */
    public BigInteger[] getParam() {
        return new BigInteger[]{this.G, this.P, this.numPub};
    }

    /**
     * Retorna el mensaje actualmente almacenado (cifrado o descifrado según la última operación).
     *
     * @return Cadena con el mensaje.
     */
    public String getMensaje() {
        return this.mensaje;
    }

    /**
     * Establece el mensaje con el cual trabajará el encriptador antes de llamar
     * a cifrar() o decifrar().
     *
     * @param mensaje Cadena de texto objetivo.
     */
    public void setMensaje(String mensaje) {
        lstMensaje = enlistador(mensaje);
        this.mensaje = mensaje;
    }

    /**
     * Método demostrativo para retornar la clave calculada.
     * Por motivos de seguridad, debe eliminarse en producción.
     *
     * @return La clave secreta formateada como String.
     */
    public String s(){ //metodo demostrativo, debe ser borrado!
        return this.clave;
    }
}