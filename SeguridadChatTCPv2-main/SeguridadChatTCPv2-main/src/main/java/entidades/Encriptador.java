package entidades;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase que permite encriptar texto segun "Vigenere mod 88". usando una clave definida por dos puntos
 * de manera segura usando DiffieHellman
 * @author erwbyel
 */
public class Encriptador {

    //variables de Viggenere
    private String mensaje;
    private String clave;
    private final String letras = " AaBbCcDdEeFfGgHhIiJjKkLlMmNnÑñOoPpQqRrSsTtUuVvWwXxYyZz0123456789!@#$%&()[]{}<>?¿¡|/*-+.,_;:'^`~ÇüéâäàåçêëèïîìÄÅÉæÆôöûùÿÖÜø£Øƒáíóúªº®¬½¼«»░▒▓│┤ÁÂÀ©╣║╗╝¢¥┐└┴┬├─┼ãÃ╚╔╩╦╠═╬¤ðÐÊËÈıÍÎÏ┘┌█▄¦Ì▀ÓßÔÒõÕµþÞÚÛÙýÝ¯´≡±‗¾¶§¸°¨¹³²■";
    private final int tamDic = letras.length(); //aqui podemos agregar todo el ASCII
    private ArrayList<String> lstDiccionario = enlistador(letras);;
    private ArrayList<String> lstMensaje;

    //variables de DiffieHellman
    private BigInteger P;
    private BigInteger G;
    private BigInteger numPriv;
    private BigInteger numPub;
    private BigInteger K;

    //metodos de determinacion de clave
    
    /**
     * constructor que inicializa el encriptador como Alice
     */
    public Encriptador() {
        this.P = new BigInteger("3273390607896141870013189696827599152216642046043064789483291368096133796404674554883270092325904157150886684127560071009217256545885393053328527589431"); 
        this.G = new BigInteger("2");
        this.numPriv = new BigInteger(P.bitLength(), new Random()).mod(P);
        this.numPub = G.modPow(numPriv, P);
    }

    /**
     * constructor que inicializa el encriptador como Bob
     * @param datosAlice 
     */
    public Encriptador(BigInteger[] datosAlice) { //como somos los receptores podemos determinar la clave
        this.G = datosAlice[0];
        this.P = datosAlice[1];
        this.numPriv = new BigInteger(P.bitLength(), new Random()).mod(P);
        this.numPub = G.modPow(numPriv, P);
        //clave determinada
        BigInteger A = datosAlice[2];
        this.K = A.modPow(numPriv, P);
        this.formatearClave();
    }

    /**
     * metodo para Alice posteriori a recibir parametros de bob
     * @param datosBob
     */
    public void finalizar(BigInteger datosBob[]) {
        //solo calcularemos si no se ha determinado una clave
        if(this.clave != null) return;
        //clave determinada
        this.K = datosBob[2].modPow(this.numPriv, datosBob[1]);
        this.formatearClave();
    }

    // Metodos de encriptacion
    
    /**
     * Metodo que permite cifrar un texto usando viggenere
     * requiere establecer una conecion y determinar una clave
     */
    public void cifrar() {
        if(clave == null) return;
        //iniciar la llave
        ArrayList<String> lstClave = enlistador(clave);
        //algoritmo de viggenere
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
     * Metodo que permite decifrar el texto
     * requiere establecer una conecion y determinar una clave
     */
    public void decifrar() {
        if (clave == null) return;
        //iniciar la llave
        ArrayList<String> lstClave = enlistador(clave);
        //algoritmo de viggenere inverso
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

    //Metodos de Utilidad getter y setter y privados de la clase
    
    /**
     * metodo que permite pasar de String a Lista para poder iterar sobre cada
     * caracter
     *
     * @param texto
     * @return
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
     * metodo que descompone el valor BigInteger a valores asociados de un indice
     */
    private void formatearClave() {//92 base de mi diccionario
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
     * regresa los datos que pueden ser conocidos por otros P,G,"A|B"
     * @return BigInteger[]
     */
    public BigInteger[] getParam() {
        return new BigInteger[]{this.G, this.P, this.numPub};
    }

    /**
     * metodo que regresa el mensaje
     * @return
     */
    public String getMensaje() {
        return this.mensaje;
    }

    /**
     * metodo que settea el mensaje a trabajar
     *
     * @param mensaje
     */
    public void setMensaje(String mensaje) {
        // inicializar el mensaje formateado
        lstMensaje = enlistador(mensaje);
        this.mensaje = mensaje;
    }
    
    public String s(){ //metodo demostrativo, debe ser borrado!
        return this.clave;
    }
}
