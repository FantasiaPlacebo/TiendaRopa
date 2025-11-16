/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import DAO.ProductoDAO;
import DAO.ProductoDAOImpl;
import Modelo.Producto;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Santo Tomas
 */
public class VistaVenta extends javax.swing.JFrame {

    private ProductoDAO productoDAO;
    
    public VistaVenta() {
        initComponents();
        this.setLocationRelativeTo(null);
        productoDAO = new ProductoDAOImpl();
        cargarProductosEnComboBox();
        configurarEventoComboBox();
    }

    private void cargarProductosEnComboBox() {
            try {
                cmbNombreProductos.removeAllItems();
                cmbNombreProductos.addItem("-- Seleccione un producto --");

                List<Producto> productos = productoDAO.obtenerTodos();

                for (Producto producto : productos) {
                    String item = producto.getNombre() + " - " + producto.getMarca();
                    cmbNombreProductos.addItem(item);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
            }
        }
    
    private void cargarPrecioProducto() {
        try {
            if (cmbNombreProductos.getSelectedIndex() == 0) {
                lblPrecioProducto.setText("-");
                return;
            }

            String productoSeleccionado = cmbNombreProductos.getSelectedItem().toString();
            String nombreProducto = productoSeleccionado.split(" - ")[0].trim();
            List<Producto> productos = productoDAO.buscarPorNombre(nombreProducto);

            if (!productos.isEmpty()) {
                Producto producto = productos.get(0);
                lblPrecioProducto.setText("$" + producto.getPrecio());
                recalcularTotal();
            } else {
                lblPrecioProducto.setText("-");
                lblTotalProducto.setText("-");
            }

        } catch (Exception e) {
            lblPrecioProducto.setText("-");
            lblTotalProducto.setText("-");
        }
    }
    
    private void recalcularTotal() {
        try {
            if (cmbNombreProductos.getSelectedIndex() == 0) {
                lblTotalProducto.setText("$0");
                return;
            }

            String precioTexto = lblPrecioProducto.getText().replace("$", "");
            if (precioTexto.equals("-")) {
                lblTotalProducto.setText("$0");
                return;
            }

            int precio = Integer.parseInt(precioTexto);
            String cantidadTexto = txtCantidadProducto.getText().trim();

            if (cantidadTexto.isEmpty()) {
                lblTotalProducto.setText("$0");
                return;
            }

            int cantidad = Integer.parseInt(cantidadTexto);
            int total = precio * cantidad;
            lblTotalProducto.setText("$" + total);

        } catch (NumberFormatException e) {
            lblTotalProducto.setText("$0");
    }
}
    
    private void configurarEventoComboBox() {
        cmbNombreProductos.addActionListener(e -> {
            cargarPrecioProducto();
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        layoutPrincipal = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblDatosCliente = new javax.swing.JLabel();
        layoutDatosCliente = new javax.swing.JPanel();
        layoutDatos = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        lblRut = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        lblDireccion = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        txtApellidoCliente = new javax.swing.JTextField();
        txtRutCliente = new javax.swing.JTextField();
        txtCorreoCliente = new javax.swing.JTextField();
        txtDireccionCliente = new javax.swing.JTextField();
        layoutBotones = new javax.swing.JPanel();
        bttnInventario = new javax.swing.JButton();
        bttnRegistro = new javax.swing.JButton();
        lblProductos = new javax.swing.JLabel();
        layoutProductos = new javax.swing.JPanel();
        layoutDatosProductos = new javax.swing.JPanel();
        lblNombreP = new javax.swing.JLabel();
        lblCantidadP = new javax.swing.JLabel();
        lblPrecioP = new javax.swing.JLabel();
        lblTotalP = new javax.swing.JLabel();
        txtCantidadProducto = new javax.swing.JTextField();
        lblTotalProducto = new javax.swing.JLabel();
        lblPrecioProducto = new javax.swing.JLabel();
        layoutBotonesP = new javax.swing.JPanel();
        bttnEliminarProducto = new javax.swing.JButton();
        bttnAgregarProducto = new javax.swing.JButton();
        cmbNombreProductos = new javax.swing.JComboBox<>();
        layoutTabla = new javax.swing.JPanel();
        scrollTabla = new javax.swing.JScrollPane();
        tableProductos = new javax.swing.JTable();
        layoutDetalle = new javax.swing.JPanel();
        lblDetalle = new javax.swing.JLabel();
        layoutCalculoPrecio = new javax.swing.JPanel();
        lblPrecioD = new javax.swing.JLabel();
        lblIVAP = new javax.swing.JLabel();
        lblTotalD = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblIVA = new javax.swing.JLabel();
        lblPrecioSinIVA = new javax.swing.JLabel();
        layoutDetalleVendedor = new javax.swing.JPanel();
        lblVendedorD = new javax.swing.JLabel();
        lblMetodoPago = new javax.swing.JLabel();
        lblVendedor = new javax.swing.JLabel();
        cbMetodoDePago = new javax.swing.JComboBox<>();
        bttnAceptar = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("PUNTO DE VENTA");

        lblDatosCliente.setText("Datos del cliente");

        layoutDatos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombre.setText("NOMBRE");

        lblApellido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApellido.setText("APELLIDO");

        lblRut.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRut.setText("RUT");

        lblCorreo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCorreo.setText("CORREO");

        lblDireccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDireccion.setText("DIRECCIÓN");

        txtNombreCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreClienteActionPerformed(evt);
            }
        });
        txtNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyTyped(evt);
            }
        });

        txtApellidoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApellidoClienteActionPerformed(evt);
            }
        });
        txtApellidoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidoClienteKeyTyped(evt);
            }
        });

        txtRutCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtRutClienteKeyTyped(evt);
            }
        });

        txtCorreoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCorreoClienteKeyTyped(evt);
            }
        });

        txtDireccionCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDireccionClienteActionPerformed(evt);
            }
        });
        txtDireccionCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDireccionClienteKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layoutDatosLayout = new javax.swing.GroupLayout(layoutDatos);
        layoutDatos.setLayout(layoutDatosLayout);
        layoutDatosLayout.setHorizontalGroup(
            layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblApellido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtApellidoCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRutCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCorreo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCorreoCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(txtDireccionCliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layoutDatosLayout.setVerticalGroup(
            layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layoutDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(lblApellido)
                    .addComponent(lblRut)
                    .addComponent(lblCorreo)
                    .addComponent(lblDireccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRutCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bttnInventario.setText("Inventario");

        bttnRegistro.setText("Registro");

        javax.swing.GroupLayout layoutBotonesLayout = new javax.swing.GroupLayout(layoutBotones);
        layoutBotones.setLayout(layoutBotonesLayout);
        layoutBotonesLayout.setHorizontalGroup(
            layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutBotonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bttnInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bttnRegistro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layoutBotonesLayout.setVerticalGroup(
            layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bttnInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bttnRegistro)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layoutDatosClienteLayout = new javax.swing.GroupLayout(layoutDatosCliente);
        layoutDatosCliente.setLayout(layoutDatosClienteLayout);
        layoutDatosClienteLayout.setHorizontalGroup(
            layoutDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDatosClienteLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(layoutDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(layoutBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layoutDatosClienteLayout.setVerticalGroup(
            layoutDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDatosClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(layoutBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(layoutDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblProductos.setText("Productos");

        layoutDatosProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNombreP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreP.setText("NOMBRE");

        lblCantidadP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCantidadP.setText("CANTIDAD");

        lblPrecioP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPrecioP.setText("PRECIO");

        lblTotalP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalP.setText("TOTAL");

        txtCantidadProducto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCantidadProducto.setText("1");
        txtCantidadProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadProductoActionPerformed(evt);
            }
        });
        txtCantidadProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadProductoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadProductoKeyTyped(evt);
            }
        });

        lblTotalProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalProducto.setText("-");

        lblPrecioProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPrecioProducto.setText("-");

        bttnEliminarProducto.setBackground(new java.awt.Color(255, 0, 51));
        bttnEliminarProducto.setText("  -  ");
        bttnEliminarProducto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        bttnAgregarProducto.setBackground(new java.awt.Color(102, 204, 0));
        bttnAgregarProducto.setText("  +  ");
        bttnAgregarProducto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout layoutBotonesPLayout = new javax.swing.GroupLayout(layoutBotonesP);
        layoutBotonesP.setLayout(layoutBotonesPLayout);
        layoutBotonesPLayout.setHorizontalGroup(
            layoutBotonesPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layoutBotonesPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bttnAgregarProducto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bttnEliminarProducto)
                .addContainerGap())
        );
        layoutBotonesPLayout.setVerticalGroup(
            layoutBotonesPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layoutBotonesPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layoutBotonesPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bttnEliminarProducto)
                    .addComponent(bttnAgregarProducto))
                .addContainerGap())
        );

        cmbNombreProductos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layoutDatosProductosLayout = new javax.swing.GroupLayout(layoutDatosProductos);
        layoutDatosProductos.setLayout(layoutDatosProductosLayout);
        layoutDatosProductosLayout.setHorizontalGroup(
            layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDatosProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbNombreProductos, 0, 236, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCantidadP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCantidadProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPrecioP, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layoutDatosProductosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblPrecioProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalP, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layoutDatosProductosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblTotalProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(layoutBotonesP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        layoutDatosProductosLayout.setVerticalGroup(
            layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDatosProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreP)
                    .addComponent(lblCantidadP)
                    .addComponent(lblPrecioP)
                    .addComponent(lblTotalP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDatosProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidadProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalProducto)
                    .addComponent(lblPrecioProducto)
                    .addComponent(cmbNombreProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layoutDatosProductosLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(layoutBotonesP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layoutProductosLayout = new javax.swing.GroupLayout(layoutProductos);
        layoutProductos.setLayout(layoutProductosLayout);
        layoutProductosLayout.setHorizontalGroup(
            layoutProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layoutDatosProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layoutProductosLayout.setVerticalGroup(
            layoutProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layoutDatosProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scrollTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tableProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTabla.setViewportView(tableProductos);

        javax.swing.GroupLayout layoutTablaLayout = new javax.swing.GroupLayout(layoutTabla);
        layoutTabla.setLayout(layoutTablaLayout);
        layoutTablaLayout.setHorizontalGroup(
            layoutTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addContainerGap())
        );
        layoutTablaLayout.setVerticalGroup(
            layoutTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        layoutDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDetalle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalle.setText("DETALLE");

        layoutCalculoPrecio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPrecioD.setText("PRECIO");

        lblIVAP.setText("IVA");

        lblTotalD.setText("TOTAL");

        lblTotal.setText(":");

        lblIVA.setText(":");

        lblPrecioSinIVA.setText(":");

        javax.swing.GroupLayout layoutCalculoPrecioLayout = new javax.swing.GroupLayout(layoutCalculoPrecio);
        layoutCalculoPrecio.setLayout(layoutCalculoPrecioLayout);
        layoutCalculoPrecioLayout.setHorizontalGroup(
            layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutCalculoPrecioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblPrecioD, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(lblIVAP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPrecioSinIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layoutCalculoPrecioLayout.setVerticalGroup(
            layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutCalculoPrecioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecioD)
                    .addComponent(lblPrecioSinIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIVAP)
                    .addComponent(lblIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layoutCalculoPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalD)
                    .addComponent(lblTotal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layoutDetalleVendedor.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblVendedorD.setText("Vendedor");

        lblMetodoPago.setText("Metodo de pago");

        lblVendedor.setText(":");

        cbMetodoDePago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Debito", "Credito", "Efectivo", "Transferencia" }));

        javax.swing.GroupLayout layoutDetalleVendedorLayout = new javax.swing.GroupLayout(layoutDetalleVendedor);
        layoutDetalleVendedor.setLayout(layoutDetalleVendedorLayout);
        layoutDetalleVendedorLayout.setHorizontalGroup(
            layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDetalleVendedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMetodoPago, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(lblVendedorD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(cbMetodoDePago, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layoutDetalleVendedorLayout.setVerticalGroup(
            layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDetalleVendedorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVendedorD)
                    .addComponent(lblVendedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layoutDetalleVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMetodoPago)
                    .addComponent(cbMetodoDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bttnAceptar.setBackground(new java.awt.Color(51, 204, 0));
        bttnAceptar.setForeground(new java.awt.Color(255, 255, 255));
        bttnAceptar.setText("ACEPTAR");
        bttnAceptar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout layoutDetalleLayout = new javax.swing.GroupLayout(layoutDetalle);
        layoutDetalle.setLayout(layoutDetalleLayout);
        layoutDetalleLayout.setHorizontalGroup(
            layoutDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(layoutCalculoPrecio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(layoutDetalleVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bttnAceptar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layoutDetalleLayout.setVerticalGroup(
            layoutDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layoutCalculoPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layoutDetalleVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnAceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layoutPrincipalLayout = new javax.swing.GroupLayout(layoutPrincipal);
        layoutPrincipal.setLayout(layoutPrincipalLayout);
        layoutPrincipalLayout.setHorizontalGroup(
            layoutPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(layoutProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layoutPrincipalLayout.createSequentialGroup()
                .addGroup(layoutPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layoutPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layoutPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDatosCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layoutPrincipalLayout.createSequentialGroup()
                        .addComponent(layoutTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(layoutDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(layoutDatosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 737, Short.MAX_VALUE))
                .addContainerGap())
        );
        layoutPrincipalLayout.setVerticalGroup(
            layoutPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDatosCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layoutDatosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblProductos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layoutProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layoutPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(layoutTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layoutPrincipalLayout.createSequentialGroup()
                        .addComponent(layoutDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(layoutPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layoutPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreClienteActionPerformed

    private void txtApellidoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidoClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidoClienteActionPerformed

    private void txtDireccionClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionClienteActionPerformed

    private void txtNombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyTyped
        String nombreCliente = txtNombreCliente.getText();
        char c = evt.getKeyChar();
        
        if (nombreCliente.length() >= 15){
            evt.consume();
        }
        if (!Character.isLetter(c)){
            evt.consume();
        }
    }//GEN-LAST:event_txtNombreClienteKeyTyped

    private void txtApellidoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoClienteKeyTyped
        String apellidoCliente = txtApellidoCliente.getText();
        char c = evt.getKeyChar();
        
        if (apellidoCliente.length() >= 15){
            evt.consume();
        }
        if (!Character.isLetter(c)){
            evt.consume();
        }
    }//GEN-LAST:event_txtApellidoClienteKeyTyped

    private void txtRutClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRutClienteKeyTyped
        String rutCliente = txtRutCliente.getText();
        char c = evt.getKeyChar();
        char cUpper = Character.toUpperCase(c);
        
        if (rutCliente.length() >= 10){
            evt.consume();
        }
        if (Character.isLetter(c)&& cUpper != 'K'){
            evt.consume();
        }
        if (Character.isWhitespace(c)) {
        evt.consume();
        }
    }//GEN-LAST:event_txtRutClienteKeyTyped

    private void txtCorreoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorreoClienteKeyTyped
        String correoCliente = txtCorreoCliente.getText();
        char c = evt.getKeyChar();
        
        if(correoCliente.length() >= 50){
            evt.consume();
        }
        if (Character.isWhitespace(c)) {
        evt.consume();
        }
    }//GEN-LAST:event_txtCorreoClienteKeyTyped

    private void txtDireccionClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDireccionClienteKeyTyped
        String correoCliente = txtCorreoCliente.getText();
        char c = evt.getKeyChar();
        
        if(correoCliente.length() >= 50){
            evt.consume();
        }
    }//GEN-LAST:event_txtDireccionClienteKeyTyped

    private void txtCantidadProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadProductoKeyTyped
        char c = evt.getKeyChar();
        
        if(!Character.isDigit(c) && c != '\b' && c != '\u007F'){
            evt.consume();
        }
    }//GEN-LAST:event_txtCantidadProductoKeyTyped

    private void txtCantidadProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadProductoActionPerformed
        
    }//GEN-LAST:event_txtCantidadProductoActionPerformed

    private void txtCantidadProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadProductoKeyReleased
        recalcularTotal();
    }//GEN-LAST:event_txtCantidadProductoKeyReleased

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaVenta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnAceptar;
    private javax.swing.JButton bttnAgregarProducto;
    private javax.swing.JButton bttnEliminarProducto;
    private javax.swing.JButton bttnInventario;
    private javax.swing.JButton bttnRegistro;
    private javax.swing.JComboBox<String> cbMetodoDePago;
    private javax.swing.JComboBox<String> cmbNombreProductos;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel layoutBotones;
    private javax.swing.JPanel layoutBotonesP;
    private javax.swing.JPanel layoutCalculoPrecio;
    private javax.swing.JPanel layoutDatos;
    private javax.swing.JPanel layoutDatosCliente;
    private javax.swing.JPanel layoutDatosProductos;
    private javax.swing.JPanel layoutDetalle;
    private javax.swing.JPanel layoutDetalleVendedor;
    private javax.swing.JPanel layoutPrincipal;
    private javax.swing.JPanel layoutProductos;
    private javax.swing.JPanel layoutTabla;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblCantidadP;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDatosCliente;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblIVA;
    private javax.swing.JLabel lblIVAP;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreP;
    private javax.swing.JLabel lblPrecioD;
    private javax.swing.JLabel lblPrecioP;
    private javax.swing.JLabel lblPrecioProducto;
    private javax.swing.JLabel lblPrecioSinIVA;
    private javax.swing.JLabel lblProductos;
    private javax.swing.JLabel lblRut;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalD;
    private javax.swing.JLabel lblTotalP;
    private javax.swing.JLabel lblTotalProducto;
    private javax.swing.JLabel lblVendedor;
    private javax.swing.JLabel lblVendedorD;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tableProductos;
    private javax.swing.JTextField txtApellidoCliente;
    private javax.swing.JTextField txtCantidadProducto;
    private javax.swing.JTextField txtCorreoCliente;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtRutCliente;
    // End of variables declaration//GEN-END:variables
}
