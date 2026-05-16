package componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import entidades.Servidor;
import utilidades.ValidaciónYSanitizacion;

public class PMenuServidor extends JPanel {

    private PButton btnMas;
    private PButton btnMenos;
    private PButton btnCrear;
    private PButton btnRegresar;

    private ArrayList<PTextField> entradas = new ArrayList<>();
    private PTextField txtCapacidad;
    private int capacidad = 0;
    private JPanel pnlPadre;


    private String usuarioPendiente;
    private String passwordPendiente;
    private String tipoOperacion;

    public PMenuServidor() {}

    public PMenuServidor(JPanel pnlPadre, String usuarioPendiente, String passwordPendiente, String tipoOperacion) {
        this(pnlPadre);
        this.usuarioPendiente = usuarioPendiente;
        this.passwordPendiente = passwordPendiente;
        this.tipoOperacion = tipoOperacion;

        entradas.get(4).setText(this.usuarioPendiente);
        entradas.get(4).setEditable(false);
    }

    public PMenuServidor(JPanel pnlPadre) {

        super(new GridBagLayout());
        this.pnlPadre = pnlPadre;
        setBackground(new Color(84, 0, 81));

        Font font = new Font("Oswald", Font.PLAIN, 20);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;


        JLabel lblNomServidor = new JLabel("Nombre Del Servidor:");
        lblNomServidor.setForeground(Color.WHITE);
        lblNomServidor.setFont(font);

        PTextField txtNomServer = new PTextField();
        txtNomServer.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 0;
        add(lblNomServidor, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(txtNomServer, gbc);


        JLabel lblCapacidad = new JLabel("Capacidad:");
        lblCapacidad.setForeground(Color.WHITE);
        lblCapacidad.setFont(font);


        JPanel pnlCapacidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlCapacidad.setOpaque(false);

        txtCapacidad = new PTextField();
        txtCapacidad.setText(String.valueOf(capacidad));
        txtCapacidad.setEditable(false);
        txtCapacidad.setFocusable(false);
        txtCapacidad.setPreferredSize(new Dimension(100, 35));

        btnMenos = new PButton("-", null, null);
        btnMenos.setPreferredSize(new Dimension(40, 35));

        btnMas = new PButton("+", null, null);
        btnMas.setPreferredSize(new Dimension(40, 35));

        pnlCapacidad.add(txtCapacidad);
        pnlCapacidad.add(btnMenos);
        pnlCapacidad.add(btnMas);

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblCapacidad, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(pnlCapacidad, gbc);


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


        JLabel lblNombre = new JLabel("Nombre De Administrador:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(font);

        PTextField txtNombre = new PTextField();
        txtNombre.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0; gbc.gridy = 4;
        add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        add(txtNombre, gbc);


        btnRegresar = new PButton("REGRESAR", null, null);
        btnRegresar.setPreferredSize(new Dimension(150, 40));

        btnCrear = new PButton("CREAR", null, null);
        btnCrear.setPreferredSize(new Dimension(150, 40));


        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnRegresar);
        pnlBotones.add(btnCrear);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        add(pnlBotones, gbc);


        entradas.add(txtNomServer);
        entradas.add(txtCapacidad);
        entradas.add(txtIp);
        entradas.add(txtPuerto);
        entradas.add(txtNombre);

        runBtnMas();
        runBtnMenos();
        runBtnCrear();
        runBtnRegresar();
    }

    private void runBtnMas() {
        btnMas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                capacidad++;
                txtCapacidad.setText(String.valueOf(capacidad));
            }
        });
    }

    private void runBtnMenos() {
        btnMenos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (capacidad > 0) {
                    capacidad--;
                    txtCapacidad.setText(String.valueOf(capacidad));
                }
            }
        });
    }

    private void runBtnCrear() {
        btnCrear.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String nombreServer = ValidaciónYSanitizacion.sanitizarTexto(entradas.get(0).getText());
                String ip = entradas.get(2).getText();
                Integer puerto = entradas.get(3).getInt();
                String nombreAdmin = ValidaciónYSanitizacion.sanitizarTexto(entradas.get(4).getText());

                if (!ValidaciónYSanitizacion.esLongitudValida(nombreServer, 3, 20)) {
                    JOptionPane.showMessageDialog(null, "El nombre del servidor debe tener entre 3 y 20 caracteres.", "NOMBRE DE SERVIDOR INVÁLIDO", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (capacidad <= 0) {
                    JOptionPane.showMessageDialog(null, "La capacidad del servidor debe ser mayor a 0 para aceptar usuarios.", "CAPACIDAD INVÁLIDA", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!ValidaciónYSanitizacion.esIpValida(ip)) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese una IP válida (ej. localhost o 192.168.1.1).", "IP INVÁLIDA", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!ValidaciónYSanitizacion.esPuertoValido(puerto)) {
                    JOptionPane.showMessageDialog(null, "El puerto debe ser un número válido entre 1024 y 65535.", "PUERTO INVÁLIDO", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!ValidaciónYSanitizacion.esLongitudValida(nombreAdmin, 3, 15)) {
                    JOptionPane.showMessageDialog(null, "El nombre de administrador debe tener entre 3 y 15 caracteres.", "ADMINISTRADOR INVÁLIDO", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JChat chat = new JChat(nombreServer, nombreAdmin);
                chat.setVisible(true);
                limpiarEntradas();
                try {
                    Servidor servidor = new Servidor(nombreServer, nombreAdmin, ip, puerto, capacidad);
                    servidor.asignarComponentes(chat);
                    servidor.iniciarServidor();
                } catch (IOException | ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Error crítico al iniciar el servidor.\nAsegúrese de que el puerto " + puerto + " no esté en uso.\nDetalle: " + e.getMessage(), "FALLO DE SERVIDOR", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }

    private void runBtnRegresar(){
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setVisible(false);
                pnlPadre.setVisible(true);
            }
        });
    }

    private void limpiarEntradas() {
        entradas.get(0).setText("");
        entradas.get(2).setText("");
        entradas.get(3).setText("");
        entradas.get(4).setText("");
    }
}