
package componentes;

import PantallasRegistros.Registro;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import entidades.Cliente;
import javax.swing.JOptionPane;

public class PMenuCliente extends JPanel {
    private JPanel pnlPadre;
    private PButton btnUnirse;
    private PButton btnRegresar;

    private ArrayList<PTextField> entradas = new ArrayList<>();

    public PMenuCliente(JPanel pnlPadre) {
        super(null);
        this.pnlPadre = pnlPadre;
        setBackground(new Color(84, 0, 81));

        Font font = new Font("Oswald", Font.PLAIN, 20);
        //label de nombre del usario
        JLabel lblNomUsuario = new JLabel("Nombre De Usuario");
        lblNomUsuario.setForeground(Color.WHITE);
        lblNomUsuario.setFont(font);
        lblNomUsuario.setBounds(150, 50, 200, 30);
        //JTextField de nombre del usuario
        PTextField txtNomUsuario = new PTextField();
        txtNomUsuario.setBounds(350, 50, 230, 30);

        //label de contraña de usuario
        JLabel lblPassword = new JLabel("Contraseña usuario");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(font);
        lblPassword.setBounds(150, 100, 200, 30);
        //JTextField de contraseña del usario
        PTextField txtPassword = new PTextField();
        txtPassword.setBounds(350, 100, 230, 30);
                
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

        //boton de regresar
        btnRegresar = new PButton("REGRESAR",null,null);
        btnRegresar.setBounds(150, 280, 200, 30);
        btnUnirse = new PButton("UNIRSE", null, null);
        btnUnirse.setBounds(380, 280, 200, 30);

        //añadir los componentes al panel
        add(lblNomUsuario);
        add(txtNomUsuario);
        
        add(lblPassword);
        add(txtPassword);
        
        add(lblIp);
        add(txtIp);
        
        add(lblPuerto);
        add(txtPuerto);
        
        add(btnRegresar);
        add(btnUnirse);

        //añadir los componentes a la lista
        entradas.add(txtNomUsuario);
        entradas.add(txtPassword);
        entradas.add(txtIp);
        entradas.add(txtPuerto);
                
        runBtnUnirse();
        runBtnRegresar();
    }
    
    private void runBtnUnirse() {
        btnUnirse.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String[] opciones = {"Continuar(Login)", "Registrarse"};
                int eleccion = JOptionPane.showOptionDialog(
                        null,
                        "¿Que desea realizar?",
                        "Seleccione una opción",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );
                if (eleccion == 0) {
                    procesarLogin();
                } else if (eleccion == 1) {
                    registrarse();
                }
            }
        });
    }
    private void procesarLogin() {
        String nombreUsuario = entradas.get(0).getText();
        String passwordUsuaruio = entradas.get(1).getText();
        String ip = entradas.get(2).getText();
        int puerto = entradas.get(3).getInt();
        
        try {
            Cliente cliente = new Cliente(nombreUsuario, passwordUsuaruio, "LOGIN", ip, puerto);
            
            JChat chat = new JChat("none","none");
            cliente.asignarComponentes(chat);
            cliente.unirseServidor();
            chat.setNewTitle(cliente.getNombreServidor());
            chat.setVisible(true);
            limpiarEntradas();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: No fue posible conectarse:" + e.getMessage(), "ERROR DE CONEXIÓN", JOptionPane.ERROR_MESSAGE);                    e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }                
    }
    
    private void registrarse() {
        Registro registro = new Registro();
        registro.setVisible(true);
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
    }
}