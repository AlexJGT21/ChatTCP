package entidades;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import componentes.JChat;
import componentes.PButton;
import componentes.PTextField;
import utilidades.GestorLogs;

/**
 * Representa a un cliente dentro de la arquitectura del chat.
 * Se encarga de establecer la conexión con el servidor, realizar el
 * intercambio seguro de claves (handshake), y gestionar el envío y
 * recepción de mensajes en hilos separados para no bloquear la interfaz gráfica.
 *
 * @author angel
 */
public class Cliente {
    private String nombre;
    private String password;
    private String tipoAccion;
    private InetAddress ipServer;
    private int puertoServer;
    private Encriptador encriptador;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private JTextArea chat;
    private PTextField texto;
    private PButton btnEnviar;
    private JLabel lblAdmin;
    private JLabel lblNombreChat;
    private JChat pnlChatVentana;

    /**
     * Constructor utilizado para operaciones que requieren autenticación (Login o Registro).
     *
     * @param nombre Nombre del usuario.
     * @param password Contraseña en texto plano.
     * @param tipoAccion Acción a realizar ("LOGIN" o "REGISTRO").
     * @param ipServer Dirección IP del servidor.
     * @param puertoServer Puerto de escucha del servidor.
     * @throws UnknownHostException Si la IP proporcionada no es válida.
     */
    public Cliente(String nombre, String password, String tipoAccion, String ipServer, int puertoServer) throws UnknownHostException {
        this.nombre = nombre;
        this.password = password;
        this.tipoAccion = tipoAccion;
        this.ipServer = InetAddress.getByName(ipServer);
        this.puertoServer = puertoServer;
    }

    /**
     * Constructor simplificado para operaciones básicas sin autenticación inicial.
     *
     * @param nombre Nombre del usuario.
     * @param ipServer Dirección IP del servidor.
     * @param puertoServer Puerto del servidor.
     * @throws IOException Si ocurre un error de red.
     * @throws ClassNotFoundException Si ocurre un error de conversión de clases.
     */
    public Cliente(String nombre, String ipServer, int puertoServer) throws IOException, ClassNotFoundException {
        this.nombre = nombre;
        this.ipServer = InetAddress.getByName(ipServer);
        this.puertoServer = puertoServer;
    }

    /**
     * Vincula los componentes de la interfaz gráfica de la sala de chat (JChat)
     * con las variables de esta clase para poder actualizarlos.
     *
     * @param pnlChat Panel principal del chat.
     */
    public void asignarComponentes(JChat pnlChat) {
        this.pnlChatVentana = pnlChat;
        this.btnEnviar = pnlChat.getBtnEnviar();
        this.chat = pnlChat.getTxtArea();
        this.texto = pnlChat.getTxtMensaje();
        this.lblAdmin = pnlChat.getLblAdmin();
        this.lblNombreChat = pnlChat.getLblServer();
    }

    /**
     * Inicia la conexión mediante Sockets con el servidor.
     * Configura los flujos de entrada/salida, ejecuta el handshake de seguridad,
     * e inicia un hilo para escuchar mensajes entrantes.
     *
     * @throws IOException Si ocurre un problema de conexión.
     * @throws ClassNotFoundException Si el objeto recibido no es reconocido.
     * @throws Exception Para errores generales o rechazos por parte del servidor.
     */
    public void unirseServidor() throws IOException, ClassNotFoundException, Exception {
        GestorLogs.registrarInfo("Intentando establecer conexión TCP con el servidor en " + ipServer.getHostAddress() + ":" + puertoServer);
        Socket socket = new Socket(ipServer, puertoServer);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        handshake();
        GestorLogs.registrarInfo("Handshake criptográfico y autenticación completados con éxito para: " + nombre);

        new Thread(() -> {
            try {
                recibirMensajes(socket);
            } catch (IOException e) {
                GestorLogs.registrarError("Fallo crítico en el hilo de recepción de mensajes", e);
            }
        }).start();

        runBtnEnviar();
    }

    /**
     * Bucle infinito que escucha continuamente los mensajes que llegan desde el servidor.
     * Descifra cada mensaje y determina si es una instrucción de sincronización de interfaz
     * o un mensaje de texto normal para el chat.
     *
     * @param socket Socket de conexión actual.
     * @throws IOException Si se pierde la conexión.
     */
    private void recibirMensajes(Socket socket) throws IOException {
        try {
            while (true) {
                String mensaje = (String) in.readObject();
                encriptador.setMensaje(mensaje);
                encriptador.decifrar();
                String msjDecifrado = encriptador.getMensaje();

                if (msjDecifrado.startsWith("USER_JOIN:")) {
                    String usuarioConectado = msjDecifrado.substring(10);
                    SwingUtilities.invokeLater(() -> pnlChatVentana.dibujaUsuario(usuarioConectado));
                } else if (msjDecifrado.startsWith("USER_LEAVE:")) {
                    String usuarioDesconectado = msjDecifrado.substring(11);
                    SwingUtilities.invokeLater(() -> pnlChatVentana.eliminarUsuario(usuarioDesconectado));
                } else {
                    SwingUtilities.invokeLater(() -> {
                        chat.append(msjDecifrado + "\n");
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            socket.close();
            GestorLogs.registrarError("Conexión cerrada abruptamente o error leyendo del servidor", e);
        }
    }

    /**
     * Ejecuta el protocolo de establecimiento de conexión segura y autenticación.
     * Realiza el intercambio Diffie-Hellman y luego envía las credenciales cifradas.
     *
     * @throws IOException Si falla el envío o recepción de datos.
     * @throws ClassNotFoundException Si los objetos leídos no coinciden con lo esperado.
     * @throws Exception Si el servidor rechaza el inicio de sesión o registro.
     */
    private void handshake() throws IOException, ClassNotFoundException, Exception {
        encriptador = new Encriptador();
        BigInteger[] parametros = encriptador.getParam();
        out.writeObject(parametros);
        out.flush();
        out.reset();

        BigInteger[] bobParam = (BigInteger[]) in.readObject();
        encriptador.finalizar(bobParam);

        String trama = tipoAccion + ":" + nombre + ":" + password;
        encriptador.setMensaje(trama);
        encriptador.cifrar();
        out.writeObject(encriptador.getMensaje());
        out.flush();
        out.reset();

        String respuestaCifrada = (String) in.readObject();
        encriptador.setMensaje(respuestaCifrada);
        encriptador.decifrar();
        String respuesta = encriptador.getMensaje();

        if (!respuesta.equals("OK")) {
            throw new Exception(respuesta);
        }

        encriptador.setMensaje(nombre);
        encriptador.cifrar();
        out.writeObject(encriptador.getMensaje());
        out.flush();
        out.reset();

        String mensaje = (String) in.readObject();
        encriptador.setMensaje(mensaje);
        encriptador.decifrar();
        lblNombreChat.setText("Sala: " + encriptador.getMensaje());

        mensaje = (String) in.readObject();
        encriptador.setMensaje(mensaje);
        encriptador.decifrar();
        lblAdmin.setText(" | Admin: " + encriptador.getMensaje());
    }

    /**
     * Obtiene el nombre del servidor actual recuperándolo de la interfaz.
     *
     * @return String con el nombre del servidor.
     */
    public String getNombreServidor(){
        return lblNombreChat.getText();
    }

    /**
     * Configura el evento de clic del botón "Enviar" para que cifre el
     * contenido del campo de texto y lo mande al servidor de forma segura.
     */
    private void runBtnEnviar() {
        btnEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    if(texto.getText() != null && !texto.getText().isBlank()) {
                        encriptador.setMensaje(texto.getText());
                        encriptador.cifrar();
                        out.writeObject(encriptador.getMensaje());
                        out.flush();
                        out.reset();
                    }
                } catch (IOException ex) {
                    GestorLogs.registrarError("Error al intentar enviar el mensaje al servidor", ex);
                } finally {
                    texto.setText("");
                }
            }
        });
    }
}