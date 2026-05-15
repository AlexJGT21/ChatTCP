
package componentes;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import entidades.Servidor;

public class PMenuServidor extends JPanel {

    private PButton btnMas;
    private PButton btnMenos;
    private PButton btnCrear;
    private PButton btnRegresar;

    private ArrayList<PTextField> entradas = new ArrayList<>();
    private PTextField txtCapacidad;
    private int capacidad = 0;
    private JPanel pnlPadre;
    
    //Datos del cliente
    private String usuarioPendiente;
    private String passwordPendiente;
    private String tipoOperacion;

    public PMenuServidor() {
    }

    public PMenuServidor(JPanel pnlPadre, String usuarioPendiente, String passwordPendiente, String tipoOperacion) {
        this(pnlPadre);
        this.usuarioPendiente = usuarioPendiente;
        this.passwordPendiente = passwordPendiente;
        this.tipoOperacion = tipoOperacion;
        
        entradas.get(4).setText(this.usuarioPendiente);
        entradas.get(4).setEditable(false);
    }        
    
    public PMenuServidor(JPanel pnlPadre) {
        super(null);
        this.pnlPadre = pnlPadre;
        setBackground(new Color(84, 0, 81));

        Font font = new Font("Oswald", Font.PLAIN, 20);
        //label de nombre del servidor
        JLabel lblNomServidor = new JLabel("Nombre Del Servidor");
        lblNomServidor.setForeground(Color.WHITE);
        lblNomServidor.setFont(font);
        lblNomServidor.setBounds(150, 50, 200, 30);
        //JTextField de nombre del servidor
        PTextField txtNomServer = new PTextField();
        txtNomServer.setBounds(350, 50, 230, 30);

        //label de capacidad
        JLabel lblCapacidad = new JLabel("Capacidad");
        lblCapacidad.setForeground(Color.WHITE);
        lblCapacidad.setFont(font);
        lblCapacidad.setBounds(150, 100, 200, 30);
        //JTextField de capacidad
        txtCapacidad = new PTextField();

        txtCapacidad.setText(String.valueOf(capacidad));
        txtCapacidad.setEditable(false);
        txtCapacidad.setFocusable(false);
        txtCapacidad.setBounds(350, 100, 130, 30);
        //boton de menos
        btnMenos = new PButton("-",null,null);
        btnMenos.setBounds(480, 100, 50, 30);
        //boton de mas
        btnMas = new PButton("+", null, null);
        btnMas.setBounds(530, 100, 50, 30);
  

        //label de ip
        JLabel lblIp = new JLabel("IP Del Servidor");
        lblIp.setForeground(Color.WHITE);
        lblIp.setFont(font);
        lblIp.setBounds(150, 150, 200, 30); //x, y, width, height
        //JTextField de ip
        PTextField txtIp = new PTextField();
        txtIp.setBounds(350, 150, 230, 30);

        //label de puerto
        JLabel lblPuerto = new JLabel("Puerto Del Servidor");
        lblPuerto.setForeground(Color.WHITE);
        lblPuerto.setFont(font);
        lblPuerto.setBounds(150, 200, 200, 30);
        //JTextField de puerto
        PTextField txtPuerto = new PTextField();
        txtPuerto.setCampoNumerico();
        txtPuerto.setBounds(350, 200, 230, 30);

        JLabel lblNombre = new JLabel("Nombre De Administrador");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(font);
        lblNombre.setBounds(150, 250, 200, 30);
        //JTextField de puerto
        PTextField txtNombre = new PTextField();
        txtNombre.setBounds(350, 250, 230, 30);        

        btnRegresar = new PButton("REGRESAR",null,null);
        btnRegresar.setBounds(150, 300, 200, 30);
        btnCrear = new PButton("CREAR",null,null);
        btnCrear.setBounds(380, 300, 200, 30);

        //añadir los componentes al panel
        add(lblNomServidor);
        add(txtNomServer);
        add(lblCapacidad);
        add(txtCapacidad);
        add(btnMas);
        add(btnMenos);
        add(lblIp);
        add(txtIp);
        add(lblPuerto);
        add(txtPuerto);
        add(lblNombre);
        add(txtNombre);
        add(btnRegresar);
        add(btnCrear);

        //añadir los componentes a la lista
        entradas.add(txtNomServer);
        entradas.add(txtCapacidad);
        entradas.add(txtIp);
        entradas.add(txtPuerto);
        entradas.add(txtNombre);
        
        //añadir eventos a los botones
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
                String nombreServer = entradas.get(0).getText();
                String ip = entradas.get(2).getText();
                int puerto = entradas.get(3).getInt();
                String nombreAdmin = entradas.get(4).getText();
                
                JChat chat = new JChat(nombreServer, nombreAdmin);
                chat.setVisible(true);
                limpiarEntradas();                
                try {
                    Servidor servidor = new Servidor(nombreServer, nombreAdmin, ip, puerto, capacidad);
                    servidor.asignarComponentes(chat);
                    servidor.iniciarServidor();
                } catch (IOException | ClassNotFoundException e) {
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
        entradas.get(1).setText("");
        entradas.get(2).setText("");
        entradas.get(3).setText("");
        entradas.get(4).setText("");
    }
}