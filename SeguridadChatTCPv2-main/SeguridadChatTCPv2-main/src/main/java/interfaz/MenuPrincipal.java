
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import componentes.PButton;
import componentes.PMenuCliente;
import componentes.PMenuServidor;

import java.io.IOException;


/**
 * clase que representa una ventana principal de un chat
 * 
 * @author erubiel
 */
public class MenuPrincipal extends JFrame {
    //private String operacion;
    private JPanel pnlContenido;
    private PButton btnCrear;
    private PButton btnUnirse;
    private JPanel pnlCentro;


    /**
     * @param args the command line arguments
     * @throws ClassNotFoundException 
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        new MenuPrincipal().setVisible(true);
    }

    /**
     * Metodo constructor de la ventana
     */
    public MenuPrincipal() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setTitle("TCPvChat");
        setIconImage(new ImageIcon("src/main/resources/chat-icono.png").getImage());
        setLayout(new BorderLayout());
        initComponents();
    }

    /**
     * Metodo que inicializa los componentes visuales de la ventana
     */
    private void initComponents() {
        // panel norte
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        pnlNorte.setBackground(new Color(33, 1, 46));
        JLabel titulo = new JLabel("TCPvCHAT");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Oswald", Font.PLAIN, 45));
        pnlNorte.add(titulo);
        pnlNorte.setPreferredSize(new Dimension(getWidth(), 90));
        add(pnlNorte, BorderLayout.NORTH);

        // panel centro
        pnlCentro = new JPanel();
        pnlCentro.setLayout(new BorderLayout());

        pnlContenido = new JPanel();
        pnlContenido.setBackground(new Color(84, 0, 81));
        pnlContenido.setLayout(null);

        // botones
        btnCrear = new PButton("src/main/resources/server-icono.png", "CREAR");
        btnUnirse = new PButton("src/main/resources/unirse-icono.png", "UNIRSE");
        btnCrear.setBounds(120, 120, 250, 250); // x, y, width, height
        btnUnirse.setBounds(420, 120, 250, 250); // Adjust size and position
        pnlContenido.add(btnCrear);
        pnlContenido.add(btnUnirse);
        pnlCentro.add(pnlContenido, BorderLayout.CENTER);

        add(pnlCentro, BorderLayout.CENTER);
        runBtnCrear();
        runBtnUnirse();
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
}
