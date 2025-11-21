/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import DAO.UsuarioDAO;
import DAO.UsuarioDAOImpl;
import Modelo.Usuario;
import Conexion.Conn;
import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 *
 * @author aacev
 */
public class VistaRegistro extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaRegistro.class.getName());
    private UsuarioDAO usuarioDAO;

    /**
     * Creates new form VistaRegistro
     */
    public VistaRegistro() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        // Inicializar DAO
        Connection conexion = Conn.conectar();
        usuarioDAO = new UsuarioDAOImpl(conexion);
        
        // Configurar eventos de botones
        configurarEventos();
    }

    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        bttnRegistrar.addActionListener(e -> registrarUsuario());
        bttnAtras.addActionListener(e -> volverAtras());
        
        // Enter en campos de texto ejecuta registro
        txtContrasenaUsuario.addActionListener(e -> registrarUsuario());
    }

    /**
     * Método para registrar un nuevo usuario
     */
    private void registrarUsuario() {
        try {
            // Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }

            // Obtener datos del formulario
            String nombre = txtNombreUsuario.getText().trim();
            String apellido = txtApellidoUsuario.getText().trim();
            String rut = txtRutUsuario.getText().trim();
            String correo = txtCorreoUsuario.getText().trim();
            String contrasena = new String(txtContrasenaUsuario.getPassword());
            String rol = jComboBox1.getSelectedItem().toString();

            // Validar formato de RUT
            if (!validarRUT(rut)) {
                JOptionPane.showMessageDialog(this,
                    "El RUT ingresado no es válido",
                    "RUT inválido",
                    JOptionPane.WARNING_MESSAGE);
                txtRutUsuario.requestFocus();
                return;
            }

            // Validar formato de correo
            if (!validarCorreo(correo)) {
                JOptionPane.showMessageDialog(this,
                    "El correo electrónico no es válido",
                    "Correo inválido",
                    JOptionPane.WARNING_MESSAGE);
                txtCorreoUsuario.requestFocus();
                return;
            }

            // Validar que el RUT no exista
            if (usuarioDAO.existeRut(rut)) {
                JOptionPane.showMessageDialog(this,
                    "El RUT ya está registrado en el sistema",
                    "RUT duplicado",
                    JOptionPane.WARNING_MESSAGE);
                txtRutUsuario.requestFocus();
                return;
            }

            // Validar que el correo no exista
            if (usuarioDAO.existeCorreo(correo)) {
                JOptionPane.showMessageDialog(this,
                    "El correo electrónico ya está registrado en el sistema",
                    "Correo duplicado",
                    JOptionPane.WARNING_MESSAGE);
                txtCorreoUsuario.requestFocus();
                return;
            }

            // Crear objeto Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setRut(rut);
            nuevoUsuario.setCorreo(correo);
            nuevoUsuario.setContrasenaUsuario(contrasena); // En un sistema real, esto debería estar hasheado
            nuevoUsuario.setRoles(rol);

            // Guardar usuario en la base de datos
            boolean exito = usuarioDAO.crearUsuario(nuevoUsuario);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Usuario registrado exitosamente\n" +
                    "Nombre: " + nombre + " " + apellido + "\n" +
                    "RUT: " + rut + "\n" +
                    "Rol: " + rol,
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarFormulario();
            } else {
                throw new Exception("Error al guardar el usuario en la base de datos");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar usuario: " + e.getMessage(),
                "Error de Registro",
                JOptionPane.ERROR_MESSAGE);
            logger.severe("Error al registrar usuario: " + e.getMessage());
        }
    }

    /**
     * Valida que todos los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        String nombre = txtNombreUsuario.getText().trim();
        String apellido = txtApellidoUsuario.getText().trim();
        String rut = txtRutUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String contrasena = new String(txtContrasenaUsuario.getPassword());

        if (nombre.isEmpty()) {
            mostrarErrorCampo("nombre");
            txtNombreUsuario.requestFocus();
            return false;
        }

        if (apellido.isEmpty()) {
            mostrarErrorCampo("apellido");
            txtApellidoUsuario.requestFocus();
            return false;
        }

        if (rut.isEmpty()) {
            mostrarErrorCampo("RUT");
            txtRutUsuario.requestFocus();
            return false;
        }

        if (correo.isEmpty()) {
            mostrarErrorCampo("correo electrónico");
            txtCorreoUsuario.requestFocus();
            return false;
        }

        if (contrasena.isEmpty()) {
            mostrarErrorCampo("contraseña");
            txtContrasenaUsuario.requestFocus();
            return false;
        }

        if (contrasena.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos 4 caracteres",
                "Contraseña muy corta",
                JOptionPane.WARNING_MESSAGE);
            txtContrasenaUsuario.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Muestra mensaje de error para campo vacío
     */
    private void mostrarErrorCampo(String campo) {
        JOptionPane.showMessageDialog(this,
            "Por favor, complete el campo: " + campo,
            "Campo requerido",
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Valida el formato del RUT (formato simple: 12345678-9)
     */
    private boolean validarRUT(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        
        // Formato simple: permite números y un guión opcional
        String rutLimpio = rut.replaceAll("[^0-9kK]", "");
        return rutLimpio.length() >= 8 && rutLimpio.length() <= 10;
    }

    /**
     * Valida el formato del correo electrónico
     */
    private boolean validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        
        // Validación simple de formato de correo
        return correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Limpia el formulario después de un registro exitoso
     */
    private void limpiarFormulario() {
        txtNombreUsuario.setText("");
        txtApellidoUsuario.setText("");
        txtRutUsuario.setText("");
        txtCorreoUsuario.setText("");
        txtContrasenaUsuario.setText("");
        jComboBox1.setSelectedIndex(0);
        txtNombreUsuario.requestFocus();
    }

    /**
     * Vuelve a la ventana anterior (login)
     */
    private void volverAtras() {
        this.dispose();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtApellidoUsuario = new javax.swing.JTextField();
        txtRutUsuario = new javax.swing.JTextField();
        txtCorreoUsuario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtContrasenaUsuario = new javax.swing.JPasswordField();
        bttnRegistrar = new javax.swing.JButton();
        bttnAtras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("REGISTRO");

        jLabel2.setText("Nombre");

        jLabel3.setText("Apellido");

        txtCorreoUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoUsuarioActionPerformed(evt);
            }
        });

        jLabel4.setText("RUT ");

        jLabel5.setText("Correo");

        jLabel6.setText("Rol");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Vendedor" }));

        jLabel7.setText("Contraseña");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNombreUsuario)
                    .addComponent(txtApellidoUsuario)
                    .addComponent(txtRutUsuario)
                    .addComponent(txtCorreoUsuario)
                    .addComponent(jComboBox1, 0, 226, Short.MAX_VALUE)
                    .addComponent(txtContrasenaUsuario))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtApellidoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRutUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCorreoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtContrasenaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bttnRegistrar.setText("Registrar");

        bttnAtras.setText("Atras");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(124, 124, 124)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bttnRegistrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bttnAtras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bttnRegistrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnAtras)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCorreoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoUsuarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VistaRegistro().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnAtras;
    private javax.swing.JButton bttnRegistrar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtApellidoUsuario;
    private javax.swing.JPasswordField txtContrasenaUsuario;
    private javax.swing.JTextField txtCorreoUsuario;
    private javax.swing.JTextField txtNombreUsuario;
    private javax.swing.JTextField txtRutUsuario;
    // End of variables declaration//GEN-END:variables

}
