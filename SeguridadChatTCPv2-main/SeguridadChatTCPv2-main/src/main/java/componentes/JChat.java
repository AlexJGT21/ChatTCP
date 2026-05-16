package componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Ventana principal para la interfaz gráfica de la sala de chat.
 * Implementa un diseño adaptativo mediante el uso de Layout Managers combinados,
 * permitiendo una visualización fluida de los mensajes y la lista de usuarios en línea.
 */
public class JChat extends JFrame {
    private String nombreChat;
    private String nombreAdmin;

    private JTextArea txtArea;
    private PButton btnEnviar;
    private PTextField txtMensaje;
    private JLabel lblAdmin;
    private JLabel lblNombreChat;
    private JLabel lblUsuarioActual;
    private JPanel pnlEste;

    /**
     * Constructor de la interfaz de la sala de chat.
     *
     * @param nombreChat  Nombre identificador de la sala.
     * @param nombreAdmin Nombre del administrador que aloja la sesión.
     */
    public JChat(String nombreChat, String nombreAdmin) {
        this.nombreChat = nombreChat;
        this.nombreAdmin = nombreAdmin;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setMinimumSize(new Dimension(600, 400));
        setTitle(nombreChat);
        setIconImage(new ImageIcon("src/main/resources/chat-icono.png").getImage());

        setLayout(new BorderLayout());

        agregarPnlNorte();
        agregarPnlCentro();
        agregarPnlEste();
        agregarPnlSur();
    }

    /**
     * Construye y añade el panel superior que contiene los metadatos de la sesión,
     * incluyendo el nombre de la sala, el administrador y la identidad del perfil local.
     */
    private void agregarPnlNorte() {
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlNorte.setBackground(new Color(33, 1, 46));
        pnlNorte.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblNombreChat = new JLabel("Sala: " + nombreChat);
        lblNombreChat.setForeground(Color.WHITE);
        lblNombreChat.setFont(new Font("Oswald", Font.BOLD, 22));

        lblAdmin = new JLabel(" | Admin: " + nombreAdmin);
        lblAdmin.setForeground(new Color(173, 216, 230));
        lblAdmin.setFont(new Font("Oswald", Font.PLAIN, 18));

        lblUsuarioActual = new JLabel("");
        lblUsuarioActual.setForeground(new Color(144, 238, 144));
        lblUsuarioActual.setFont(new Font("Oswald", Font.BOLD, 18));

        pnlNorte.add(lblNombreChat);
        pnlNorte.add(lblAdmin);
        pnlNorte.add(lblUsuarioActual);

        add(pnlNorte, BorderLayout.NORTH);
    }

    /**
     * Construye y añade el panel central destinado a la visualización del historial
     * de la conversación mediante un JTextArea auto-ajustable con barras de desplazamiento.
     */
    private void agregarPnlCentro() {
        JPanel pnlCentro = new JPanel(new BorderLayout());
        pnlCentro.setBackground(new Color(84, 0, 81));
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtArea = new JTextArea();
        txtArea.setBackground(new Color(46,2,48));
        txtArea.setEditable(false);
        txtArea.setFocusable(false);
        txtArea.setFont(new Font("Oswald", Font.PLAIN, 16));
        txtArea.setForeground(Color.WHITE);
        txtArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(137,0,127), 2));

        pnlCentro.add(scrollPane, BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);
    }

    /**
     * Construye y añade el panel lateral derecho encargado de listar los usuarios
     * en línea que interactúan concurrentemente en el servidor.
     */
    private void agregarPnlEste() {
        JPanel pnlContenedorEste = new JPanel(new BorderLayout());
        pnlContenedorEste.setBackground(new Color(33, 1, 46));
        pnlContenedorEste.setPreferredSize(new Dimension(220, 0));
        pnlContenedorEste.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 15));

        JLabel lblUsuarios = new JLabel("USUARIOS EN LÍNEA", JLabel.CENTER);
        lblUsuarios.setForeground(Color.WHITE);
        lblUsuarios.setFont(new Font("Oswald", Font.BOLD, 16));
        lblUsuarios.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pnlContenedorEste.add(lblUsuarios, BorderLayout.NORTH);

        pnlEste = new JPanel();
        pnlEste.setLayout(new BoxLayout(pnlEste, BoxLayout.Y_AXIS));
        pnlEste.setBackground(new Color(84, 0, 81));

        JScrollPane scrollUsuarios = new JScrollPane(pnlEste);
        scrollUsuarios.setBorder(BorderFactory.createLineBorder(new Color(137,0,127), 2));
        scrollUsuarios.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pnlContenedorEste.add(scrollUsuarios, BorderLayout.CENTER);
        add(pnlContenedorEste, BorderLayout.EAST);
    }

    /**
     * Construye y añade el panel inferior que agrupa la caja de entrada de texto
     * y el botón de disparo para transmitir la información hacia la red.
     */
    private void agregarPnlSur() {
        JPanel pnlSur = new JPanel(new BorderLayout(15, 0));
        pnlSur.setBackground(new Color(33, 1, 46));
        pnlSur.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMensaje = new PTextField();
        txtMensaje.setPreferredSize(new Dimension(0, 40));

        btnEnviar = new PButton("ENVIAR", Color.WHITE, new Color(6,79,146));
        btnEnviar.setPreferredSize(new Dimension(120, 40));

        pnlSur.add(txtMensaje, BorderLayout.CENTER);
        pnlSur.add(btnEnviar, BorderLayout.EAST);

        add(pnlSur, BorderLayout.SOUTH);
    }

    public PTextField getTxtMensaje() { return txtMensaje; }
    public PButton getBtnEnviar() { return btnEnviar; }
    public JTextArea getTxtArea() { return txtArea; }
    public JLabel getLblServer() { return lblNombreChat; }
    public JLabel getLblAdmin() { return lblAdmin; }
    public void setNewTitle(String title) { setTitle(title); }

    /**
     * Define el nombre del usuario dueño de la sesión actual de la ventana.
     *
     * @param nombre Nombre del usuario local.
     */
    public void setUsuarioActual(String nombre) {
        lblUsuarioActual.setText(" | Mi Perfil: " + nombre);
    }

    /**
     * Genera dinámicamente una tarjeta visual para representar un usuario activo en la red,
     * dibujando un indicador circular de estado en color verde y su respectivo nickname.
     *
     * @param nombre Identificador del cliente conectado.
     */
    public void dibujaUsuario(String nombre){
        JPanel pnlUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlUsuario.setBackground(new Color(84, 0, 81));
        pnlUsuario.setMaximumSize(new Dimension(220, 40));

        JPanel circulo = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0,200,0));
                g.fillOval(0, 5, 12, 12);
            }
        };
        circulo.setOpaque(false);
        circulo.setPreferredSize(new Dimension(15, 25));

        JLabel lblUsuario = new JLabel(nombre);
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Oswald", Font.BOLD, 16));

        pnlUsuario.add(circulo);
        pnlUsuario.add(lblUsuario);

        pnlEste.add(pnlUsuario);
        pnlEste.revalidate();
        pnlEste.repaint();
    }

    /**
     * Escanea el contenedor lateral en busca de la tarjeta vinculada al usuario indicado
     * y la remueve limpiamente de la interfaz de red actualizando el lienzo gráfico.
     *
     * @param nombre Identificador del cliente desconectado.
     */
    public void eliminarUsuario(String nombre){
        for (int i = 0; i < pnlEste.getComponentCount(); i++) {
            JPanel panel = (JPanel) pnlEste.getComponent(i);
            JLabel label = (JLabel) panel.getComponent(1);
            if (label.getText().equals(nombre)) {
                pnlEste.remove(panel);
                break;
            }
        }
        pnlEste.revalidate();
        pnlEste.repaint();
    }
}