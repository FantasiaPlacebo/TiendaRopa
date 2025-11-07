/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vistas;


import Modelo.Cliente;
import Modelo.DetalleVenta;
import Modelo.Venta;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Santo Tomas
 */
public class VistaPuntoVenta extends javax.swing.JPanel implements ActionListener {

    // Lista para simular el carrito de compras
    private ArrayList<DetalleVenta> carrito;
    // Variable para simular el ID de la venta (en una BD sería autoincremental)
    private int idVentaSimulada = 1;

    /**
     * Creates new form VistaPuntoVenta
     */
    public VistaPuntoVenta() {
        initComponents();
        
        // Inicializar el carrito
        this.carrito = new ArrayList<>();
        
        // Añadir ActionListeners a los botones
        bttnMas.addActionListener(this);
        bttnMenos.addActionListener(this); // Aunque no le daremos lógica aún
        bttnPagar.addActionListener(this);
        bttnMetodo.addActionListener(this);
        
        // Botones de navegación
        bttnVenta.addActionListener(this);
        bttnInventario.addActionListener(this);
        bttnRegistro.addActionListener(this);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        try {
            // --- AÑADIR PRODUCTO AL CARRITO ---
            if (source == bttnMas) {
                // 1. Validar y obtener datos del producto
                if (txtCantidad.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtCodigo.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar Código, Cantidad y Precio del producto.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int idProducto = Integer.parseInt(txtCodigo.getText());
                int cantidad = Integer.parseInt(txtCantidad.getText());
                int precio = Integer.parseInt(txtPrecio.getText());
                
                if(cantidad <= 0 || precio <= 0){
                    JOptionPane.showMessageDialog(this, "Cantidad y Precio deben ser mayores a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Crear el item de detalle
                // (idVenta, idCliente, idProducto, cantidad, precio)
                // Usamos 0 para idVenta e idCliente por ahora, se asignarán al pagar
                DetalleVenta item = new DetalleVenta(0, 0, idProducto, cantidad, precio);
                
                // 3. Añadir al carrito
                carrito.add(item);
                
                System.out.println("Producto añadido: ID " + idProducto + ", Cant: " + cantidad + ", Precio: " + precio);

                // 4. Actualizar etiquetas de totales
                actualizarTotales();

                // 5. Limpiar campos de producto
                limpiarCamposProducto();
            } 
            
            // --- PAGAR LA VENTA ---
            else if (source == bttnPagar) {
                if (carrito.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El carrito está vacío. Añada productos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 1. Validar datos del cliente
                if (txtRut.getText().isEmpty() || txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar al menos RUT, Nombre y Apellido del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // --- SIMULACIÓN DE GUARDADO EN BD ---
                
                // 2. Simular creación de Cliente
                // (idCliente, nombre, apellido, correo, telefono, direccion)
                // TODO: Aquí deberías buscar si el cliente existe por RUT. Si no, crearlo.
                Cliente cliente = new Cliente(
                        1, // ID Simulada
                        txtNombre.getText(),
                        txtApellido.getText(),
                        txtCorreo.getText(),
                        (txtRut.getText().hashCode() % 100000), // Teléfono simulado
                        txtDireccion.getText()
                );
                System.out.println("Cliente (simulado): " + cliente.getNombre() + " " + cliente.getApelido());

                // 3. Simular creación de Venta
                // (idVenta, idCliente, fecha)
                Venta venta = new Venta(idVentaSimulada, cliente.getIdCliente(), new java.util.Date().toString());
                System.out.println("Venta Creada (simulada): ID " + venta.getIdVenta());

                // 4. Asignar IDs a los detalles y "guardarlos"
                for(DetalleVenta item : carrito) {
                    item.setIdVenta(venta.getIdVenta());
                    item.setIdCliente(cliente.getIdCliente());
                    // TODO: Aquí llamarías a DetalleVentaDAO.guardar(item)
                    System.out.println("Guardando Detalle: VentaID " + item.getIdVenta() + ", ProdID " + item.getIdProducto() + ", Cant: " + item.getCantidadVenta());
                }
                
                // 5. Mostrar confirmación y limpiar todo
                JOptionPane.showMessageDialog(this, "Venta " + venta.getIdVenta() + " registrada exitosamente.\nTotal: " + lblResultadoTotal.getText(), "Venta Completada", JOptionPane.INFORMATION_MESSAGE);
                
                idVentaSimulada++; // Incrementar para la próxima venta
                limpiarTodo();
            } 
            
            // --- OTROS BOTONES ---
            else if (source == bttnMetodo) {
                String metodo = JOptionPane.showInputDialog(this, "Ingrese método de pago:", "Método de Pago", JOptionPane.PLAIN_MESSAGE);
                if (metodo != null && !metodo.isEmpty()) {
                    lblMetodoPago.setText(metodo);
                }
            } 
            else if (source == bttnVenta || source == bttnInventario || source == bttnRegistro) {
                // Lógica de navegación simulada
                // TODO: Esta lógica debe manejarla el JFrame principal (Cambiando JPanels)
                String vista = ((javax.swing.JButton) source).getText();
                JOptionPane.showMessageDialog(this, "Navegando a la vista: " + vista);
            }
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese solo números válidos para Código, Cantidad y Precio.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Recalcula los totales (subtotal, iva, total) y actualiza las etiquetas
     */
    private void actualizarTotales() {
        double subtotal = 0;
        int cantidadItems = 0;
        
        for (DetalleVenta item : carrito) {
            subtotal += item.getPrecioVenta() * item.getCantidadVenta();
            cantidadItems += item.getCantidadVenta();
        }
        
        double iva = subtotal * 0.19; // Asumiendo IVA del 19%
        double total = subtotal + iva;
        
        // Actualizar etiquetas (Labels)
        lblResultadoCantidad.setText(String.valueOf(cantidadItems));
        lblResultadoPrecio.setText(String.format("$%.0f", subtotal)); // Precio = Subtotal
        lblResultadoIva.setText(String.format("$%.0f", iva));
        lblResultadoTotal.setText(String.format("$%.0f", total));
    }

    /**
     * Limpia solo los campos de ingreso de producto
     */
    private void limpiarCamposProducto() {
        txtCodigo.setText("");
        txtPrenda.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtTotal.setText(""); // Este campo no parece tener uso, pero lo limpiamos
    }

    /**
     * Limpia todos los campos (cliente, producto y carrito)
     */
    private void limpiarTodo() {
        // Limpiar carrito y totales
        carrito.clear();
        actualizarTotales();
        
        // Limpiar campos de cliente
        txtNombre.setText("");
        txtApellido.setText("");
        txtRut.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        
        // Limpiar campos de producto
        limpiarCamposProducto();
        
        // Resetear etiquetas
        lblMetodoPago.setText("Metodo Pago");
        lblResultadoVendedor.setText("--"); // No hemos usado este campo
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane2 = new javax.swing.JLayeredPane();
        bttnVenta = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        txtCodigo = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        lblPrenda = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        txtPrenda = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        lblCantidad = new javax.swing.JLabel();
        lblRut = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        txtRut = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        lblDireccion = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        bttnMas = new javax.swing.JButton();
        lblNombre = new javax.swing.JLabel();
        lblCodigo = new javax.swing.JLabel();
        bttnMenos = new javax.swing.JButton();
        bttnInventario = new javax.swing.JButton();
        bttnRegistro = new javax.swing.JButton();
        lblNumeroDeVenta = new javax.swing.JLabel();
        lblCantidad2 = new javax.swing.JLabel();
        lblPrecio2 = new javax.swing.JLabel();
        lblIva = new javax.swing.JLabel();
        lblTotal2 = new javax.swing.JLabel();
        lblVendedor = new javax.swing.JLabel();
        lblResultadoCantidad = new javax.swing.JLabel();
        lblResultadoPrecio = new javax.swing.JLabel();
        lblResultadoIva = new javax.swing.JLabel();
        lblResultadoTotal = new javax.swing.JLabel();
        lblResultadoVendedor = new javax.swing.JLabel();
        bttnMetodo = new javax.swing.JButton();
        bttnPagar = new javax.swing.JButton();
        lblMetodoPago = new javax.swing.JLabel();

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        bttnVenta.setText("Venta");

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        lblPrenda.setText("Prenda");

        lblApellido.setText("Apellido");

        lblCantidad.setText("Cantidad");

        lblRut.setText("Rut");

        txtRut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRutActionPerformed(evt);
            }
        });

        lblPrecio.setText("Precio");

        lblCorreo.setText("Correo");

        lblTotal.setText("Total");

        lblDireccion.setText("Direccion");

        bttnMas.setText("+");

        lblNombre.setText("Nombre");

        lblCodigo.setText("Codigo");

        bttnMenos.setText("-");

        jLayeredPane1.setLayer(txtCodigo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtNombre, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblPrenda, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblApellido, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtPrenda, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtApellido, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblCantidad, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblRut, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtCantidad, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtRut, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblPrecio, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblCorreo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtPrecio, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtCorreo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblTotal, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblDireccion, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtTotal, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(txtDireccion, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(bttnMas, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblNombre, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblCodigo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(bttnMenos, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCodigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPrenda, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblApellido, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtApellido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPrenda))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblRut, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtRut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCantidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblCorreo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPrecio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTotal)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(bttnMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnMas, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(lblApellido)
                    .addComponent(lblRut)
                    .addComponent(lblCorreo)
                    .addComponent(lblDireccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(lblPrenda)
                    .addComponent(lblCantidad)
                    .addComponent(lblPrecio)
                    .addComponent(lblTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bttnMenos)
                    .addComponent(bttnMas))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        bttnInventario.setText("Inventario");

        bttnRegistro.setText("Registro");

        lblNumeroDeVenta.setText("Numero De Venta: ");

        lblCantidad2.setText("Cantidad");

        lblPrecio2.setText("Precio");

        lblIva.setText("IVA");

        lblTotal2.setText("Total");

        lblVendedor.setText("Vendedor");

        lblResultadoCantidad.setText("--");

        lblResultadoPrecio.setText("--");

        lblResultadoIva.setText("--");

        lblResultadoTotal.setText("--");

        lblResultadoVendedor.setText("--");

        bttnMetodo.setText("Metodo");
        bttnMetodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnMetodoActionPerformed(evt);
            }
        });

        bttnPagar.setText("Pagar");

        lblMetodoPago.setText("Metodo Pago");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bttnVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bttnInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bttnRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(114, 114, 114))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(261, 261, 261)
                        .addComponent(lblNumeroDeVenta))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCantidad2)
                            .addComponent(lblPrecio2)
                            .addComponent(lblIva)
                            .addComponent(lblTotal2)
                            .addComponent(lblVendedor))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblResultadoVendedor)
                            .addComponent(lblResultadoTotal)
                            .addComponent(lblResultadoCantidad)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblResultadoPrecio)
                                    .addComponent(lblResultadoIva))
                                .addGap(69, 69, 69)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(bttnMetodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bttnPagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(64, 64, 64)
                .addComponent(lblMetodoPago)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(bttnVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnRegistro)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumeroDeVenta)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidad2)
                    .addComponent(lblResultadoCantidad))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPrecio2)
                            .addComponent(lblResultadoPrecio)
                            .addComponent(bttnMetodo)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(lblMetodoPago)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIva)
                    .addComponent(lblResultadoIva)
                    .addComponent(bttnPagar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal2)
                    .addComponent(lblResultadoTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVendedor)
                    .addComponent(lblResultadoVendedor))
                .addGap(0, 29, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtRutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRutActionPerformed

    private void bttnMetodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnMetodoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bttnMetodoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnInventario;
    private javax.swing.JButton bttnMas;
    private javax.swing.JButton bttnMenos;
    private javax.swing.JButton bttnMetodo;
    private javax.swing.JButton bttnPagar;
    private javax.swing.JButton bttnRegistro;
    private javax.swing.JButton bttnVenta;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCantidad2;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblIva;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNumeroDeVenta;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblPrecio2;
    private javax.swing.JLabel lblPrenda;
    private javax.swing.JLabel lblResultadoCantidad;
    private javax.swing.JLabel lblResultadoIva;
    private javax.swing.JLabel lblResultadoPrecio;
    private javax.swing.JLabel lblResultadoTotal;
    private javax.swing.JLabel lblResultadoVendedor;
    private javax.swing.JLabel lblRut;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JLabel lblVendedor;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPrenda;
    private javax.swing.JTextField txtRut;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
