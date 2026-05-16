package interfaz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import componentes.PButton;
import componentes.PMenuCliente;
import componentes.PMenuServidor;
import PantallasRegistros.Registro; // Importamos tu pantalla de registro

/**
 * Clase que representa la ventana principal de arranque de la aplicación.
 */
public class MenuPrincipal extends JFrame {
    private JPanel pnlContenido;
    private PButton btnCrear;
    private PButton btnUnirse;
    private PButton btnRegistrar; // Nuevo botón agregado
    private JPanel pnlCentro;

    public static void main(String[] args) {
        new MenuPrincipal().setVisible(true);
    }

    public MenuPrincipal() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 650); // Un poco más amplio para el nuevo diseño
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setResizable(false);
        setTitle("TCPvChat");
        setIconImage(new ImageIcon("src/main/resources/chat-icono.png").getImage());
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // panel norte (Título)
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        pnlNorte.setBackground(new Color(33, 1, 46));
        JLabel titulo = new JLabel("TCPvCHAT");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Oswald", Font.PLAIN, 45));
        pnlNorte.add(titulo);
        pnlNorte.setPreferredSize(new Dimension(getWidth(), 90));
        add(pnlNorte, BorderLayout.NORTH);

        // panel centro
        pnlCentro = new JPanel(new BorderLayout());

        // Usamos GridBagLayout para centrar los 3 botones elegantemente
        pnlContenido = new JPanel(new GridBagLayout());
        pnlContenido.setBackground(new Color(84, 0, 81));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Márgenes entre botones

        // Botón CREAR
        btnCrear = new PButton("src/main/resources/server-icono.png", "CREAR");
        btnCrear.setPreferredSize(new Dimension(250, 250));
        gbc.gridx = 0; gbc.gridy = 0;
        pnlContenido.add(btnCrear, gbc);

        // Botón UNIRSE
        btnUnirse = new PButton("src/main/resources/unirse-icono.png", "UNIRSE");
        btnUnirse.setPreferredSize(new Dimension(250, 250));
        gbc.gridx = 1; gbc.gridy = 0;
        pnlContenido.add(btnUnirse, gbc);

        // Botón REGISTRARSE (Abarca ambas columnas debajo de los anteriores)
        btnRegistrar = new PButton("REGISTRAR NUEVO USUARIO", new Color(167, 11, 175), new Color(137, 0, 127));
        btnRegistrar.setPreferredSize(new Dimension(540, 50));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2; // Ocupa las dos columnas
        pnlContenido.add(btnRegistrar, gbc);

        pnlCentro.add(pnlContenido, BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);

        // Activamos los eventos de clic
        runBtnCrear();
        runBtnUnirse();
        runBtnRegistrar();
    }

    private void runBtnCrear() {
        btnCrear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pnlContenido.setVisible(false);
                pnlCentro.add(new PMenuServidor(pnlContenido), BorderLayout.CENTER);
                pnlCentro.revalidate();
                pnlCentro.repaint();
            }
        });
    }

    private void runBtnUnirse() {
        btnUnirse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pnlContenido.setVisible(false);
                pnlCentro.add(new PMenuCliente(pnlContenido), BorderLayout.CENTER);
                pnlCentro.revalidate();
                pnlCentro.repaint();
            }
        });
    }

    // Nuevo evento para abrir la ventana de Registro
    private void runBtnRegistrar() {
        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Registro ventanaRegistro = new Registro();
                ventanaRegistro.setVisible(true);
            }
        });
    }
}