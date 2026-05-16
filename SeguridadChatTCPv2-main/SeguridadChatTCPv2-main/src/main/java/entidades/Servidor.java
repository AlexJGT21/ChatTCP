package entidades;

import GestionUsuarios.GestionContraseña;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import componentes.JChat;
import componentes.PButton;
import componentes.PTextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import utilidades.GestorLogs;

/**
 * Clase principal que representa el nodo Servidor del chat.
 * Administra conexiones de clientes simultáneas, coordina el enrutamiento de
 * mensajes (públicos y privados), gestiona los historiales del chat y maneja
 * el control de acceso y registro de cuentas.
 *
 * @author erubiel
 */
public class Servidor {

    private ServerSocket socket;
    private static HashMap<ObjectOutputStream, Encriptador> encriptadores = new HashMap<>();
    private static HashMap<String, ObjectOutputStream> clientes = new HashMap<>();
    private static LinkedList<String> historial = new LinkedList<>();

    private String nombreServidor;
    private int capacidad;
    private InetAddress ip;
    private int puerto;
    private String nombreAdmin;

    private JChat pnlChat;
    private JTextArea chat;
    private PTextField texto;
    private PButton btnEnviar;

    private int clientesOn = 0;
    private static final String USUARIOS = "usuarios_registrados";
    private HashMap<String, String> dbUsuarios = cargarUsuarios();

    /**
     * Constructor del Servidor.
     *
     * @param nombreServidor Nombre visible de la sala de chat.
     * @param nombreAdmin Nickname del administrador del servidor.
     * @param ip IP en la que el servidor escuchará peticiones.
     * @param puerto Puerto TCP a abrir.
     * @param capacidad Límite máximo de usuarios simultáneos.
     * @throws IOException Si la IP no puede ser resuelta.
     */
    public Servidor(String nombreServidor, String nombreAdmin, String ip, int puerto, int capacidad) throws IOException {
        this.nombreServidor = nombreServidor;
        this.nombreAdmin = nombreAdmin;
        this.ip = InetAddress.getByName(ip);
        this.puerto = puerto;
        this.capacidad = capacidad;
    }

    /**
     * Vincula la interfaz gráfica del administrador del servidor para poder
     * reflejar mensajes locales y eventos.
     *
     * @param pnlChat Panel JChat principal del administrador.
     */
    public void asignarComponentes(JChat pnlChat) {
        this.pnlChat = pnlChat;
        this.btnEnviar = pnlChat.getBtnEnviar();
        this.chat = pnlChat.getTxtArea();
        this.texto = pnlChat.getTxtMensaje();
    }

    /**
     * Enciende el ServerSocket y delega la aceptación de clientes entrantes
     * a un hilo separado para mantener la GUI fluida.
     *
     * @throws IOException Si el puerto está ocupado o hay un fallo de I/O.
     * @throws ClassNotFoundException Si hay errores al interpretar los flujos.
     */
    public void iniciarServidor() throws IOException, ClassNotFoundException {
        socket = new ServerSocket(puerto, capacidad, ip);
        chat.append("Servidor " + nombreServidor + " iniciado\n");

        GestorLogs.registrarInfo("Servidor '" + nombreServidor + "' iniciado exitosamente en " + ip.getHostAddress() + ":" + puerto);

        new Thread(() -> {
            try {
                escucharConexiones(socket);
            } catch (Exception ex) {
                GestorLogs.registrarError("Excepción crítica: El servidor se ha detenido por completo", ex);
            }
        }).start();

        runBtnEnviar();
    }

    /**
     * Bucle continuo que espera nuevas conexiones TCP (socket.accept).
     * Por cada cliente, realiza el handshake, lo autentica y le asigna
     * un hilo dedicado (manejaUsuario) para su tráfico asíncrono.
     *
     * @param socket El ServerSocket escuchando.
     * @throws IOException Fallos de I/O de red.
     * @throws ClassNotFoundException Fallo de casteo de objetos entrantes.
     * @throws Exception Excepciones generales de validación o criptografía.
     */
    private void escucharConexiones(ServerSocket socket) throws Exception {
        while (true) {
            try {
                Socket cliente = socket.accept();
                ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());

                Encriptador en = handshake(in, out);
                String nombreCliente = validarDatos(en, in, out);

                clientes.put(nombreCliente, out);
                pnlChat.dibujaUsuario(nombreCliente);
                new Thread(() -> manejaUsuario(en, cliente, in, out, nombreCliente)).start();

            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("Registro exitoso")) {
                    GestorLogs.registrarInfo("Conexión de registro liberada limpiamente por el servidor.");
                } else {
                    GestorLogs.registrarError("Error al procesar la solicitud de un cliente entrante", e);
                }
            } catch (ClassNotFoundException e) {
                GestorLogs.registrarError("Se recibió un objeto corrupto o no reconocido durante el acceso", e);
            }
        }
    }

    /**
     * Hilo dedicado para atender a un usuario específico conectado.
     * Recibe los mensajes encriptados, los analiza en busca de comandos
     * como "/salir" o comandos privados "/priv", y rutea el texto en consecuencia.
     *
     * @param encriptador Motor de cifrado vinculado a este cliente.
     * @param socket Socket de la conexión con el cliente.
     * @param in Flujo de entrada de datos.
     * @param out Flujo de salida de datos.
     * @param nombreCliente El nombre verificado del usuario.
     */
    private void manejaUsuario(Encriptador encriptador, Socket socket, ObjectInputStream in, ObjectOutputStream out, String nombreCliente) {
        try {
            Pattern patronPriv = Pattern.compile("^/priv\\s+(\\w+)\\s+(.+)$");
            Matcher matcherPriv;
            while (true) {
                String mensaje = (String) in.readObject();
                if (mensaje instanceof String) {
                    encriptador.setMensaje(mensaje);
                    encriptador.decifrar();
                    String mensajeDecifrado = encriptador.getMensaje();
                    matcherPriv = patronPriv.matcher(mensajeDecifrado);

                    if (mensajeDecifrado.equalsIgnoreCase("/salir")) {
                        break;
                    } else if (matcherPriv.matches()) {
                        String destinatario = matcherPriv.group(1);
                        String msjPriv = matcherPriv.group(2);
                        GestorLogs.registrarInfo("El cliente '" + nombreCliente + "' envió un mensaje privado a '" + destinatario + "'.");
                        enviarMensajePrivado(nombreCliente, destinatario, msjPriv);
                    } else {
                        enviarMensaje(nombreCliente, mensajeDecifrado);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                GestorLogs.registrarAdvertencia("El cliente '" + nombreCliente + "' perdió la conexión de manera abrupta.");
                socket.close();
                pnlChat.eliminarUsuario(nombreCliente);
                encriptadores.remove(out);
                clientes.remove(nombreCliente);

                notificarSalidaUsuario(nombreCliente);

                enviarMensaje("SERVIDOR", "Cliente Ha Perdido La Conexion: " + nombreCliente);
            } catch (IOException e1) {
                GestorLogs.registrarError("Error al cerrar conexión perdida de: " + nombreCliente, e1);
            }
        } finally {
            try {
                socket.close();
                clientesOn--;
                pnlChat.eliminarUsuario(nombreCliente);
                encriptadores.remove(out);
                clientes.remove(nombreCliente);

                notificarSalidaUsuario(nombreCliente);

                GestorLogs.registrarInfo("El cliente '" + nombreCliente + "' se ha desconectado limpiamente.");
                enviarMensaje("SERVIDOR", "Cliente Se Ha Desconectado: " + nombreCliente);
            } catch (IOException e) {
                GestorLogs.registrarError("Error al cerrar socket en desconexión limpia de: " + nombreCliente, e);
            }
        }
    }

    /**
     * Ejecuta el protocolo Diffie-Hellman del lado del Servidor.
     *
     * @param in Flujo de entrada para recibir parámetros.
     * @param out Flujo de salida para devolver parámetros.
     * @return Un objeto Encriptador listo y sincronizado.
     * @throws IOException Fallos de I/O.
     * @throws ClassNotFoundException Objeto inesperado en el flujo.
     */
    private Encriptador handshake(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        BigInteger[] clienteParam = (BigInteger[]) in.readObject();
        Encriptador encriptador = new Encriptador(clienteParam);
        out.writeObject(encriptador.getParam());
        out.flush();
        out.reset();
        return encriptador;
    }

    /**
     * Interpreta la trama inicial del cliente ("ACCION:USUARIO:PASSWORD").
     * Determina si es un REGISTRO o LOGIN y aplica validaciones lógicas
     * de servidor lleno, usuarios duplicados o contraseñas inválidas.
     *
     * @param encriptador Motor criptográfico para leer la trama de forma segura.
     * @param in Flujo de entrada.
     * @param out Flujo de salida.
     * @return El nombre del cliente validado exitosamente.
     * @throws IOException Si la validación falla (lanza excepción para desconectar).
     * @throws ClassNotFoundException Si la trama no es interpretable.
     * @throws Exception Para otras condiciones anómalas.
     */
    private String validarDatos(Encriptador encriptador, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, Exception {
        String mensaje = (String) in.readObject();
        encriptador.setMensaje(mensaje);
        encriptador.decifrar();
        String contenidoCompleto = encriptador.getMensaje();
        String[] partes = contenidoCompleto.split(":", 3);

        if (partes.length < 3) {
            throw new IOException("Protocolo de datos invalido.");
        }

        String accion = partes[0];
        String nombreCliente = partes[1];
        String passwordTxt = partes[2];

        if (accion.equalsIgnoreCase("REGISTRO")) {
            if (dbUsuarios.containsKey(nombreCliente)) {
                enviarRespuestaSegura(encriptador, out, "Error: Usuario ya existe");
                GestorLogs.registrarAdvertencia("Intento de registro fallido: Usuario '" + nombreCliente + "' ya existe.");
                throw new IOException("Nombre ocupado.");
            }
            guardarUsuario(nombreCliente, GestionUsuarios.GestionContraseña.hashPassword(passwordTxt));
            GestorLogs.registrarInfo("Nuevo usuario registrado con éxito: " + nombreCliente);
            enviarRespuestaSegura(encriptador, out, "LOGIN COMPLETO: Registro exitoso");
            throw new IOException("Registro exitoso. Desconectando...");
        }

        if (accion.equalsIgnoreCase("LOGIN")) {
            if (clientesOn >= capacidad) {
                enviarRespuestaSegura(encriptador, out, "Error: Servidor lleno");
                GestorLogs.registrarAdvertencia("Conexión rechazada para '" + nombreCliente + "': Servidor lleno.");
                throw new IOException("Servidor lleno");
            }
            String hash = dbUsuarios.get(nombreCliente);
            if (hash == null || !GestionContraseña.checkPassword(passwordTxt, hash)) {
                enviarRespuestaSegura(encriptador, out, "Error: Constraseña incorrecta");
                GestorLogs.registrarAdvertencia("Fallo de autenticación para el usuario: " + nombreCliente);
                throw new IOException("Error de inicio de sesión");
            }
            if (clientes.containsKey(nombreCliente)) {
                enviarRespuestaSegura(encriptador, out, "Error: El usuario ya esta conectado");
                GestorLogs.registrarAdvertencia("El usuario '" + nombreCliente + "' intentó iniciar sesión doble.");
                throw new IOException("Usuario en linea");
            }
            clientesOn++;
            enviarRespuestaSegura(encriptador, out, "OK");
        }

        if (nombreCliente.equals(nombreAdmin) || nombreCliente.equals("SERVIDOR") || clientes.containsKey(nombreCliente)) {
            enviarRespuestaSegura(encriptador, out, "ERROR: Nombre reservado.");
            GestorLogs.registrarAdvertencia("El usuario intentó usar un nombre reservado: " + nombreCliente);
            throw new IOException("Nombre de usuario no valido");
        } else {
            encriptadores.put(out, encriptador);

            encriptador.setMensaje(nombreServidor);
            encriptador.cifrar();
            out.writeObject(encriptador.getMensaje());
            out.flush();
            out.reset();

            encriptador.setMensaje(nombreAdmin);
            encriptador.cifrar();
            out.writeObject(encriptador.getMensaje());
            out.flush();
            out.reset();

            for (String m : historial) {
                encriptador.setMensaje(m);
                encriptador.cifrar();
                out.writeObject(encriptador.getMensaje());
                out.flush();
                out.reset();
            }

            clientes.forEach((nombreActivo, outActivo) -> {
                try {
                    encriptador.setMensaje("USER_JOIN:" + nombreActivo);
                    encriptador.cifrar();
                    out.writeObject(encriptador.getMensaje());
                    out.flush();
                    out.reset();
                } catch (IOException e) {
                    GestorLogs.registrarError("Error al enviar lista de usuarios previos", e);
                }
            });

            encriptadores.forEach((outTarget, encTarget) -> {
                try {
                    encTarget.setMensaje("USER_JOIN:" + nombreCliente);
                    encTarget.cifrar();
                    outTarget.writeObject(encTarget.getMensaje());
                    outTarget.flush();
                    outTarget.reset();
                } catch (IOException e) {
                    GestorLogs.registrarError("Error al notificar nuevo usuario en línea", e);
                }
            });

            GestorLogs.registrarInfo("El cliente '" + nombreCliente + "' se ha unido a la sala.");
            enviarMensaje("SERVIDOR", "Nuevo Cliente Conectado: " + nombreCliente);
        }
        return nombreCliente;
    }

    /**
     * Herramienta auxiliar para contestar al cliente de forma cifrada durante las validaciones.
     *
     * @param encriptador Motor del cliente objetivo
     * @param out Flujo de salida del cliente.
     * @param msj Mensaje en texto plano que será cifrado antes de salir.
     * @throws IOException Si falla el envío de red.
     */
    private void enviarRespuestaSegura(Encriptador encriptador, ObjectOutputStream out, String msj) throws IOException {
        encriptador.setMensaje(msj);
        encriptador.cifrar();
        out.writeObject(encriptador.getMensaje());
        out.flush();
        out.reset();
    }

    /**
     * Transmite un mensaje global (broadcast) a todos los clientes conectados.
     *
     * @param emisor Quién generó el mensaje.
     * @param mensaje El cuerpo del mensaje.
     * @throws IOException Si ocurre error al intentar escribir en algún socket.
     */
    private void enviarMensaje(String emisor, String mensaje) throws IOException {
        String tiempo = String.format("%02d:%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        final String msjFormateado = "[" + tiempo + "][" + emisor + "]: " + mensaje;

        encriptadores.forEach((out, encriptador) -> {
            try {
                encriptador.setMensaje(msjFormateado);
                encriptador.cifrar();
                out.writeObject(encriptador.getMensaje());
                out.flush();
                out.reset();
            } catch (IOException e) {
                GestorLogs.registrarError("Error al enviar mensaje global a un cliente.", e);
            }
        });
        chat.append(msjFormateado + "\n");
        historial.add(msjFormateado);
    }

    /**
     * Localiza a un cliente específico en el HashMap y le manda un mensaje
     * directo cifrado, invisible para el resto de clientes.
     *
     * @param emisor Nombre del remitente.
     * @param destinatario Nombre del receptor deseado.
     * @param mensaje Cuerpo del mensaje privado.
     * @throws IOException Excepciones de red I/O.
     */
    private void enviarMensajePrivado(String emisor, String destinatario, String mensaje) throws IOException {
        String tiempo = String.format("%02d:%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        final String msjFormateado = "[" + tiempo + "][" + emisor + "]: " + mensaje;
        clientes.forEach((nombre, out) -> {
            if (nombre.equals(destinatario)) {
                try {
                    Encriptador enc = encriptadores.get(out);
                    if (enc != null) {
                        enc.setMensaje(msjFormateado);
                        enc.cifrar();
                        out.writeObject(enc.getMensaje());
                    }
                    out.flush();
                    out.reset();
                } catch (IOException e) {
                    GestorLogs.registrarError("Error al enviar mensaje privado a: " + nombre, e);
                }
            }
        });
    }

    /**
     * Envía un comando de remoción a todos los clientes restantes cuando alguien se sale del chat.
     */
    private void notificarSalidaUsuario(String nombreCliente) {
        encriptadores.forEach((outTarget, encTarget) -> {
            try {
                encTarget.setMensaje("USER_LEAVE:" + nombreCliente);
                encTarget.cifrar();
                outTarget.writeObject(encTarget.getMensaje());
                outTarget.flush();
                outTarget.reset();
            } catch (IOException e) {
            }
        });
    }

    /**
     * Vincula el botón físico de la GUI del Servidor para que el Administrador
     * envíe comandos o mensajes como "Admin".
     */
    private void runBtnEnviar() {
        btnEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    enviarMensaje(nombreAdmin, texto.getText());
                } catch (IOException ex) {
                    GestorLogs.registrarError("Error al enviar mensaje local como Administrador", ex);
                } finally {
                    texto.setText("");
                }
            }
        });
    }

    /**
     * Escribe un nuevo usuario y su hash en un archivo local .txt como base de datos simple.
     *
     * @param user Nombre del usuario registrado.
     * @param hash Contraseña previamente transformada por BCrypt.
     */
    private void guardarUsuario(String user, String hash) {
        try (PrintWriter out = new PrintWriter(new FileWriter(USUARIOS, true))){
            out.println(user + ":" + hash);
            dbUsuarios.put(user, hash);
        } catch (IOException e) {
            GestorLogs.registrarError("Error al guardar nuevo usuario en archivo local", e);
        }
    }

    /**
     * Lee la persistencia local de usuarios durante la inicialización del servidor.
     *
     * @return Estructura HashMap en memoria con pares Usuario -> Hash de Password.
     */
    private HashMap<String, String> cargarUsuarios() {
        HashMap<String, String> usuarios = new HashMap<>();
        File file = new File(USUARIOS);
        if (!file.exists()) {
            return usuarios;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea  = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length == 2) {
                    usuarios.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            GestorLogs.registrarError("Error al leer la base de datos de usuarios locales", e);
        }
        return usuarios;
    }
}