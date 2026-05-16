package componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entidades.Cliente;
import utilidades.ValidaciónYSanitizacion;

/**
 * Panel de interfaz gráfica que representa el menú de acceso para el Cliente.
 * Gestiona el formulario de inicio de sesión, la validación y sanitización
 * de los campos de red, y la instanciación de la ventana de chat principal.
 */
public class PMenuCliente extends JPanel {
    private JPanel pnlPadre;
    private PButton btnUnirse;
    private PButton btnRegresar;
    private ArrayList<PTextField> entradas = new ArrayList<>();

    /**
     * Constructor del menú de configuración del cliente.
     *
     * @param pnlPadre Panel contenedor previo para gestionar la navegación hacia atrás.
     */
    public PMenuCliente(JPanel pnlPadre) {
        super(new GridBagLayout());
        this.pnlPadre = pnlPadre;
        setBackground(new Color(84, 0, 81));

        Font font = new Font("Oswald", Font.PLAIN, 20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblNomUsuario = new JLabel("Nombre De Usuario:");
        lblNomUsuario.setForeground(Color.WHITE);
        lblNomUsuario.setFont(font);

        PTextField txtNomUsuario = new PTextField();
        txtNomUsuario.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 0;
        add(lblNomUsuario, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(txtNomUsuario, gbc);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(font);

        PTextField txtPassword = new PTextField();
        txtPassword.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(txtPassword, gbc);

        JLabel lblIp = new JLabel("IP Del Servidor:");
        lblIp.setForeground(Color.WHITE);
        lblIp.setFont(font);

        PTextField txtIp = new PTextField();
        txtIp.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 2;
        add(lblIp, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        add(txtIp, gbc);

        JLabel lblPuerto = new JLabel("Puerto Del Servidor:");
        lblPuerto.setForeground(Color.WHITE);
        lblPuerto.setFont(font);

        PTextField txtPuerto = new PTextField();
        txtPuerto.setCampoNumerico();
        txtPuerto.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 3;
        add(lblPuerto, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        add(txtPuerto, gbc);

        btnRegresar = new PButton("REGRESAR", null, null);
        btnRegresar.setPreferredSize(new Dimension(150, 40));

        btnUnirse = new PButton("UNIRSE", null, null);
        btnUnirse.setPreferredSize(new Dimension(150, 40));

        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnRegresar);
        pnlBotones.add(btnUnirse);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 10, 15);
        add(pnlBotones, gbc);

        entradas.add(txtNomUsuario);
        entradas.add(txtPassword);
        entradas.add(txtIp);
        entradas.add(txtPuerto);

        runBtnUnirse();
        runBtnRegresar();
    }

    /**
     * Vincula el evento de escucha para disparar el flujo de inicio de sesión.
     */
    private void runBtnUnirse() {
        btnUnirse.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                procesarLogin();
            }
        });
    }

    /**
     * Extrae, sanitiza y valida los criterios de seguridad del formulario.
     * Si las condiciones son correctas, genera una sesión de red, inyecta la identidad
     * local en la interfaz y despliega la sala de chat de forma asíncrona.
     */
    private void procesarLogin() {
        String nombreUsuario = ValidaciónYSanitizacion.sanitizarTexto(entradas.get(0).getText());
        String passwordUsuaruio = ValidaciónYSanitizacion.sanitizarTexto(entradas.get(1).getText());
        String ip = entradas.get(2).getText();
        Integer puerto = entradas.get(3).getInt();

        if (!ValidaciónYSanitizacion.esLongitudValida(nombreUsuario, 3, 15)) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario debe tener entre 3 y 15 caracteres.", "USUARIO INVÁLIDO", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidaciónYSanitizacion.esLongitudValida(passwordUsuaruio, 6, 20)) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener entre 6 y 20 caracteres.", "CONTRASEÑA INVÁLIDA", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidaciónYSanitizacion.esIpValida(ip)) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una IP válida (ej. localhost o 192.168.1.1).", "IP INVÁLIDA", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidaciónYSanitizacion.esPuertoValido(puerto)) {
            JOptionPane.showMessageDialog(this, "El puerto debe ser un número válido entre 1024 y 65535.", "PUERTO INVÁLIDO", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Cliente cliente = new Cliente(nombreUsuario, passwordUsuaruio, "LOGIN", ip, puerto);

            JChat chat = new JChat("none", "none");
            cliente.asignarComponentes(chat);
            chat.setUsuarioActual(nombreUsuario);

            cliente.unirseServidor();
            chat.setNewTitle(cliente.getNombreServidor());
            chat.setVisible(true);
            limpiarEntradas();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: No fue posible conectarse al servidor.\n" + e.getMessage(), "ERROR DE CONEXIÓN", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Acceso denegado por el servidor:\n" + ex.getMessage(), "LOGIN RECHAZADO", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Vincula el evento de escucha para destruir la visibilidad del panel actual
     * y restaurar el lienzo del menú principal original.
     */
    private void runBtnRegresar(){
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setVisible(false);
                pnlPadre.setVisible(true);
            }
        });
    }

    /**
     * Limpia el contenido de las cajas de texto tras una transacción exitosa.
     */
    private void limpiarEntradas() {
        entradas.get(0).setText("");
        entradas.get(1).setText("");
        entradas.get(2).setText("");
        entradas.get(3).setText("");
    }
}