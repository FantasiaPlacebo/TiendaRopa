/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import DAO.ProductoDAO;
import DAO.ProductoDAOImpl;
import Modelo.Producto;
import Modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import DAO.VentaDAO;
import DAO.VentaDAOImpl;
import DAO.DetalleVentaDAO;
import DAO.DetalleVentaDAOImpl;
import Modelo.Venta;
import Modelo.DetalleVenta;
import Conexion.Conn;
import DAO.ClienteDAO;
import DAO.ClienteDAOImpl;
import Modelo.Cliente;
import java.awt.event.ActionEvent;
import java.sql.Connection;
/**
 *
 * @author Santo Tomas
 */
public class VistaVenta extends javax.swing.JFrame {

    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    private VentaDAO ventaDAO;
    private DetalleVentaDAO detalleVentaDAO;
    private DefaultTableModel modeloTabla;
    private List<Producto> productosEnTabla;
    private Usuario usuarioActual;
    
    public VistaVenta(Usuario usuario) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.usuarioActual = usuario;
        Connection conexion = Conn.conectar();
        productoDAO = new ProductoDAOImpl(conexion);
        clienteDAO = new ClienteDAOImpl(conexion);
        ventaDAO = new VentaDAOImpl(conexion);
        detalleVentaDAO = new DetalleVentaDAOImpl(conexion);
        
        productosEnTabla = new ArrayList<>();
        inicializarTabla();
        cargarProductosEnComboBox();
        configurarEventoComboBox();
        lblVendedor.setText(usuario.getNombre());
        esAdmin(usuario);
        bttnAceptar.addActionListener(e -> guardarVenta());
    }
    private void esAdmin(Usuario usuario) {
        if (usuario.getRoles().equals("Administrador")){
            bttnRegistro.setVisible(true);
        } else{
            bttnRegistro.setVisible(false);
        }
    }

    private void inicializarTabla() {

        modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Producto", "Cantidad", "Precio Unitario", "Subtotal"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer editable solo la columna de cantidad
                return column == 2;
            }
        };
        tableProductos.setModel(modeloTabla);
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
    
        private void guardarVenta() {
        try {
            // Validar que haya productos en la tabla
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos en la venta", 
                    "Venta vacía", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar método de pago
            if (cbMetodoDePago.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un método de pago", 
                    "Método de pago requerido", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Deshabilitar botón durante el proceso
            bttnAceptar.setEnabled(false);

            // 1. Procesar cliente (crear o buscar)
            int idCliente = procesarCliente();

            // 2. Calcular total de la venta
            double totalVenta = calcularTotalVenta();

            // 3. Crear objeto Venta
            Venta venta = new Venta(idCliente, usuarioActual.getIdUsuario(), totalVenta);

            // 4. Guardar venta en la base de datos
            int idVenta = ventaDAO.crearVenta(venta);

            if (idVenta == -1) {
                throw new Exception("Error al crear la venta en la base de datos");
            }

            // 5. Guardar detalles de la venta
            boolean detallesGuardados = guardarDetallesVenta(idVenta);

            if (!detallesGuardados) {
                throw new Exception("Error al guardar los detalles de la venta");
            }

            // 6. Actualizar stock de productos
            boolean stockActualizado = actualizarStockProductos();

            if (!stockActualizado) {
                throw new Exception("Error al actualizar el stock de productos");
            }

            // 7. Mostrar comprobante y limpiar
            mostrarComprobante(idVenta, totalVenta);
            limpiarVenta();

            JOptionPane.showMessageDialog(this, 
                "Venta guardada exitosamente\nN° de Venta: " + idVenta, 
                "Venta Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar la venta: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            bttnAceptar.setEnabled(true);
        }
    }

    /**
     * Procesa el cliente (busca por RUT o crea uno nuevo)
     */
    private int procesarCliente() {
        String rut = txtRutCliente.getText().trim();
        String nombre = txtNombreCliente.getText().trim();
        String apellido = txtApellidoCliente.getText().trim();
        String correo = txtCorreoCliente.getText().trim();
        String direccion = txtDireccionCliente.getText().trim();

        // Si no hay datos de cliente, venta sin cliente
        if (rut.isEmpty() && nombre.isEmpty() && apellido.isEmpty()) {
            return 0; // idCliente = 0 significa NULL en la BD
        }

        // Buscar cliente por RUT si existe
        if (!rut.isEmpty()) {
            Cliente clienteExistente = clienteDAO.obtenerPorRut(rut);
            if (clienteExistente != null) {
                return clienteExistente.getIdCliente();
            }
        }

        // Crear nuevo cliente
        try {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(nombre);
            nuevoCliente.setApelido(apellido);
            nuevoCliente.setRut(rut);
            nuevoCliente.setCorreo(correo.isEmpty() ? null : correo);
            nuevoCliente.setDireccion(direccion.isEmpty() ? null : direccion);
            
            // Para el teléfono, podrías agregar un campo en la interfaz
            nuevoCliente.setTelefono(0); // Valor por defecto

            if (clienteDAO.crearCliente(nuevoCliente)) {
                // Obtener el ID del cliente recién creado
                Cliente clienteCreado = clienteDAO.obtenerPorRut(rut);
                return clienteCreado != null ? clienteCreado.getIdCliente() : 0;
            }
        } catch (Exception e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
        }

        return 0; // Si falla, venta sin cliente
    }


    private double calcularTotalVenta() {
        double total = 0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object subtotalObj = modeloTabla.getValueAt(i, 4); // Columna subtotal

            if (subtotalObj instanceof Integer) {
                total += ((Integer) subtotalObj).doubleValue();
            } else if (subtotalObj instanceof Double) {
                total += (Double) subtotalObj;
            } else {
                total += Double.parseDouble(subtotalObj.toString());
            }
        }
        return total;
    }

    /**
     * Guarda los detalles de la venta en la base de datos
     */

    private boolean guardarDetallesVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int idProducto = (int) modeloTabla.getValueAt(i, 0); // Columna ID
            int cantidad = (int) modeloTabla.getValueAt(i, 2);   // Columna Cantidad

            // CORRECCIÓN: No hacer cast directo, convertir apropiadamente
            Object subtotalObj = modeloTabla.getValueAt(i, 4); // Columna Subtotal
            double subtotal;

            if (subtotalObj instanceof Integer) {
                subtotal = ((Integer) subtotalObj).doubleValue();
            } else if (subtotalObj instanceof Double) {
                subtotal = (Double) subtotalObj;
            } else {
                // Si es otro tipo, convertirlo a String y luego a double
                subtotal = Double.parseDouble(subtotalObj.toString());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdVenta(idVenta);
            detalle.setIdProducto(idProducto);
            detalle.setCantidadVenta(cantidad);
            detalle.setPrecioVenta((int) subtotal); // Cast a int si es necesario

            detalles.add(detalle);
        }

        return detalleVentaDAO.crearDetallesVenta(detalles);
    }

    /**
     * Actualiza el stock de los productos vendidos
     */
    private boolean actualizarStockProductos() {
        try {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idProducto = (int) modeloTabla.getValueAt(i, 0);
                int cantidadVendida = (int) modeloTabla.getValueAt(i, 2);

                // Verificar stock disponible
                Producto producto = productoDAO.obtenerPorId(idProducto);
                if (producto != null) {
                    if (producto.getStock() < cantidadVendida) {
                        throw new Exception("Stock insuficiente para: " + producto.getNombre());
                    }
                }

                // Usar reducirStock directamente (más eficiente)
                boolean stockActualizado = productoDAO.reducirStock(idProducto, cantidadVendida);
                if (!stockActualizado) {
                    throw new Exception("Error al actualizar stock para producto ID: " + idProducto);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        }
    }


    private void mostrarComprobante(int idVenta, double totalVenta) {
        StringBuilder comprobante = new StringBuilder();
        comprobante.append("=== COMPROBANTE DE VENTA ===\n");
        comprobante.append("N° Venta: ").append(idVenta).append("\n");
        comprobante.append("Fecha: ").append(new java.util.Date()).append("\n");
        comprobante.append("Vendedor: ").append(usuarioActual.getNombre()).append("\n");
        comprobante.append("Método de Pago: ").append(cbMetodoDePago.getSelectedItem()).append("\n");
        comprobante.append("\n--- Productos ---\n");

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String producto = (String) modeloTabla.getValueAt(i, 1);
            int cantidad = (int) modeloTabla.getValueAt(i, 2);

            // CORRECCIÓN: Convertir apropiadamente
            Object precioObj = modeloTabla.getValueAt(i, 3);
            Object subtotalObj = modeloTabla.getValueAt(i, 4);

            double precio, subtotal;

            if (precioObj instanceof Integer) {
                precio = ((Integer) precioObj).doubleValue();
            } else {
                precio = (Double) precioObj;
            }

            if (subtotalObj instanceof Integer) {
                subtotal = ((Integer) subtotalObj).doubleValue();
            } else {
                subtotal = (Double) subtotalObj;
            }

            comprobante.append(String.format("%s x%d - $%.0f\n", producto, cantidad, subtotal));
        }

        comprobante.append("\nTOTAL: $").append(totalVenta).append("\n");
        comprobante.append("=============================");

        // Mostrar en un diálogo o puedes imprimirlo
        JOptionPane.showMessageDialog(this, 
            comprobante.toString(), 
            "Comprobante de Venta", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private double convertirADouble(Object numero) {
        if (numero instanceof Integer) {
            return ((Integer) numero).doubleValue();
        } else if (numero instanceof Double) {
            return (Double) numero;
        } else if (numero instanceof Long) {
            return ((Long) numero).doubleValue();
        } else if (numero instanceof Float) {
            return ((Float) numero).doubleValue();
        } else {
            return Double.parseDouble(numero.toString());
        }
    }
    
    private void limpiarVenta() {
        // Limpiar campos del cliente
        txtNombreCliente.setText("");
        txtApellidoCliente.setText("");
        txtRutCliente.setText("");
        txtCorreoCliente.setText("");
        txtDireccionCliente.setText("");

        // Limpiar tabla de productos
        modeloTabla.setRowCount(0);
        productosEnTabla.clear();

        // Limpiar campos de productos
        cmbNombreProductos.setSelectedIndex(0);
        txtCantidadProducto.setText("1");
        lblPrecioProducto.setText("-");
        lblTotalProducto.setText("$0");

        // Limpiar totales
        lblPrecioSinIVA.setText(":");
        lblIVA.setText(":");
        lblTotal.setText(":");

        // Resetear método de pago
        cbMetodoDePago.setSelectedIndex(0);

        // Poner foco en el primer campo
        txtNombreCliente.requestFocus();
    }
 
    private void agregarProductoATabla() {
        try {
            // Validar que se haya seleccionado un producto
            if (cmbNombreProductos.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un producto", 
                    "Producto no seleccionado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar cantidad
            String cantidadTexto = txtCantidadProducto.getText().trim();
            if (cantidadTexto.isEmpty() || Integer.parseInt(cantidadTexto) <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, ingrese una cantidad válida", 
                    "Cantidad inválida", 
                    JOptionPane.WARNING_MESSAGE);
                txtCantidadProducto.requestFocus();
                return;
            }

            // Obtener el producto seleccionado
            String productoSeleccionado = cmbNombreProductos.getSelectedItem().toString();
            String nombreProducto = productoSeleccionado.split(" - ")[0].trim();
            List<Producto> productos = productoDAO.buscarPorNombre(nombreProducto);

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Producto no encontrado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = productos.get(0);
            int cantidad = Integer.parseInt(cantidadTexto);

            // Validar stock disponible
            if (producto.getStock() < cantidad) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Stock disponible: " + producto.getStock(), 
                    "Stock insuficiente", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int precio = producto.getPrecio();
            int subtotal = precio * cantidad;

            // Verificar si el producto ya está en la tabla
            int filaExistente = buscarProductoEnTabla(producto.getIdProducto());

            if (filaExistente != -1) {
                // Actualizar cantidad y subtotal si el producto ya existe
                int cantidadActual = (int) modeloTabla.getValueAt(filaExistente, 2);
                int nuevaCantidad = cantidadActual + cantidad;

                // Validar stock para la cantidad actualizada
                if (producto.getStock() < nuevaCantidad) {
                    JOptionPane.showMessageDialog(this, 
                        "Stock insuficiente al sumar cantidades. Stock disponible: " + producto.getStock(), 
                        "Stock insuficiente", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int nuevoSubtotal = precio * nuevaCantidad;

                modeloTabla.setValueAt(nuevaCantidad, filaExistente, 2);
                modeloTabla.setValueAt(nuevoSubtotal, filaExistente, 4);
            } else {
                // Agregar nuevo producto a la tabla
                Object[] fila = {
                    producto.getIdProducto(),
                    producto.getNombre() + " - " + producto.getMarca(),
                    cantidad,
                    precio,
                    subtotal
                };
                modeloTabla.addRow(fila);
                productosEnTabla.add(producto);
            }

            // Actualizar totales
            actualizarTotalesVenta();

            // Limpiar campos para nuevo producto
            cmbNombreProductos.setSelectedIndex(0);
            txtCantidadProducto.setText("1");
            lblPrecioProducto.setText("-");
            lblTotalProducto.setText("$0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al agregar producto: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Método para buscar si un producto ya está en la tabla
     */
    private int buscarProductoEnTabla(int idProducto) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int idEnTabla = (int) modeloTabla.getValueAt(i, 0);
            if (idEnTabla == idProducto) {
                return i;
            }
        }
        return -1;
    }
    

    private void actualizarTotalesVenta() {
        double subtotalVenta = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object subtotalObj = modeloTabla.getValueAt(i, 4);

            if (subtotalObj instanceof Integer) {
                subtotalVenta += ((Integer) subtotalObj).doubleValue();
            } else if (subtotalObj instanceof Double) {
                subtotalVenta += (Double) subtotalObj;
            } else {
                subtotalVenta += Double.parseDouble(subtotalObj.toString());
            }
        }

        double iva = subtotalVenta * 0.19; // 19% de IVA
        double totalVenta = subtotalVenta + iva;

        // Actualizar etiquetas
        lblPrecioSinIVA.setText("$" + (int) subtotalVenta);
        lblIVA.setText("$" + (int) iva);
        lblTotal.setText("$" + (int) totalVenta);
    }
    
    /**
     * Método para eliminar producto seleccionado de la tabla
     */
    private void eliminarProductoDeTabla() {
        int filaSeleccionada = tableProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un producto de la tabla para eliminar", 
                "No hay producto seleccionado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar este producto de la venta?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaSeleccionada);
            productosEnTabla.remove(filaSeleccionada);
            actualizarTotalesVenta();
            
            JOptionPane.showMessageDialog(this, 
                "Producto eliminado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        }
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
        bttnHistorial = new javax.swing.JButton();
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
                .addContainerGap(10, Short.MAX_VALUE))
        );

        bttnInventario.setText("Inventario");
        bttnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnInventarioActionPerformed(evt);
            }
        });

        bttnRegistro.setText("Registro");
        bttnRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnRegistroActionPerformed(evt);
            }
        });

        bttnHistorial.setText("Historial");
        bttnHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnHistorialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layoutBotonesLayout = new javax.swing.GroupLayout(layoutBotones);
        layoutBotones.setLayout(layoutBotonesLayout);
        layoutBotonesLayout.setHorizontalGroup(
            layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bttnHistorial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(bttnRegistro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bttnInventario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layoutBotonesLayout.setVerticalGroup(
            layoutBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bttnInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnHistorial)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGroup(layoutDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(layoutBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layoutDatosClienteLayout.createSequentialGroup()
                        .addComponent(layoutDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
        bttnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnEliminarProductoActionPerformed(evt);
            }
        });

        bttnAgregarProducto.setBackground(new java.awt.Color(102, 204, 0));
        bttnAgregarProducto.setText("  +  ");
        bttnAgregarProducto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bttnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnAgregarProductoActionPerformed(evt);
            }
        });

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
                .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
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
                    .addComponent(layoutDatosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 739, Short.MAX_VALUE))
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
                        .addGap(0, 12, Short.MAX_VALUE)))
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

    private void bttnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnAgregarProductoActionPerformed
        agregarProductoATabla();
    }//GEN-LAST:event_bttnAgregarProductoActionPerformed

    private void bttnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnEliminarProductoActionPerformed
        eliminarProductoDeTabla();
    }//GEN-LAST:event_bttnEliminarProductoActionPerformed

    private void bttnRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnRegistroActionPerformed
        VistaRegistro registro = new VistaRegistro();
        registro.setVisible(true);
    }//GEN-LAST:event_bttnRegistroActionPerformed

    private void bttnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnInventarioActionPerformed
        VistaInventario inventario = new VistaInventario();
        inventario.setVisible(true);
    }//GEN-LAST:event_bttnInventarioActionPerformed

    private void bttnHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnHistorialActionPerformed
        VistaHistorial historial = new VistaHistorial();
        historial.setVisible(true);
    }//GEN-LAST:event_bttnHistorialActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnAceptar;
    private javax.swing.JButton bttnAgregarProducto;
    private javax.swing.JButton bttnEliminarProducto;
    private javax.swing.JButton bttnHistorial;
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
