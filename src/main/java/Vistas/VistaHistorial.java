/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import DAO.VentaDAO;
import DAO.VentaDAOImpl;
import DAO.DetalleVentaDAO;
import DAO.DetalleVentaDAOImpl;
import DAO.ClienteDAO;
import DAO.ClienteDAOImpl;
import DAO.UsuarioDAO;
import DAO.UsuarioDAOImpl;
import Modelo.Venta;
import Modelo.DetalleVenta;
import Modelo.Cliente;
import Modelo.Usuario;
import Conexion.Conn;
import java.sql.Connection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author aacev
 */
public class VistaHistorial extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaHistorial.class.getName());
    private VentaDAO ventaDAO;
    private DetalleVentaDAO detalleVentaDAO;
    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates new form VistaHistorial
     */
    
    public VistaHistorial() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        // Inicializar DAOs
        Connection conexion = Conn.conectar();
        ventaDAO = new VentaDAOImpl(conexion);
        detalleVentaDAO = new DetalleVentaDAOImpl(conexion);
        clienteDAO = new ClienteDAOImpl(conexion);
        usuarioDAO = new UsuarioDAOImpl(conexion);
        
        // Configurar tabla
        inicializarTabla();
        
        // Cargar datos
        cargarVentas();
        
        // Configurar eventos
        configurarEventos();
    }

    /**
     * Inicializa la tabla con el modelo adecuado
     */
    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID Venta", "Fecha", "Cliente", "Vendedor", "Total", "Productos"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; // ID Venta
                    case 4: return Double.class;  // Total
                    default: return String.class;
                }
            }
        };
        
        jTable1.setModel(modeloTabla);
        
        // Configurar sorter para búsqueda
        sorter = new TableRowSorter<>(modeloTabla);
        jTable1.setRowSorter(sorter);
        
        // Ajustar anchos de columnas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150); // Cliente
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(120); // Vendedor
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);  // Total
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(200); // Productos
    }

    /**
     * Carga todas las ventas en la tabla
     */
    private void cargarVentas() {
        try {
            modeloTabla.setRowCount(0); // Limpiar tabla
            
            List<Venta> ventas = ventaDAO.obtenerTodos();
            
            if (ventas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay ventas registradas en el sistema",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Venta venta : ventas) {
                // Obtener información del cliente
                String nombreCliente = "Sin cliente";
                if (venta.getIdCliente() > 0) {
                    Cliente cliente = clienteDAO.obtenerPorId(venta.getIdCliente());
                    if (cliente != null) {
                        nombreCliente = cliente.getNombre() + " " + cliente.getApelido();
                    }
                }
                
                // Obtener información del vendedor
                String nombreVendedor = "Desconocido";
                if (venta.getIdUsuario() > 0) {
                    Usuario vendedor = usuarioDAO.obtenerPorId(venta.getIdUsuario());
                    if (vendedor != null) {
                        nombreVendedor = vendedor.getNombre() + " " + vendedor.getApellido();
                    }
                }
                
                // Obtener detalles de la venta
                List<DetalleVenta> detalles = detalleVentaDAO.obtenerPorVenta(venta.getIdVenta());
                String productos = formatearProductos(detalles);
                
                // Agregar fila a la tabla
                Object[] fila = {
                    venta.getIdVenta(),
                    formatearFecha(venta.getFecha()),
                    nombreCliente,
                    nombreVendedor,
                    venta.getTotalVenta(),
                    productos
                };
                
                modeloTabla.addRow(fila);
            }
            
            // Actualizar estadísticas
            actualizarEstadisticas();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar el historial de ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.severe("Error al cargar ventas: " + e.getMessage());
        }
    }

    /**
     * Formatea la lista de productos para mostrar en la tabla
     */
    private String formatearProductos(List<DetalleVenta> detalles) {
        if (detalles.isEmpty()) {
            return "Sin productos";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < detalles.size() && i < 3; i++) { // Máximo 3 productos
            DetalleVenta detalle = detalles.get(i);
            if (i > 0) sb.append(", ");
            sb.append(detalle.getCantidadVenta()).append("x Prod.").append(detalle.getIdProducto());
        }
        
        if (detalles.size() > 3) {
            sb.append("... (+").append(detalles.size() - 3).append(" más)");
        }
        
        return sb.toString();
    }

    /**
     * Formatea la fecha para mostrar
     */
    private String formatearFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return "Fecha no disponible";
        }
        
        try {
            // Formato simple: si la fecha tiene formato largo, acortarla
            if (fecha.length() > 16) {
                return fecha.substring(0, 16); // YYYY-MM-DD HH:MM
            }
            return fecha;
        } catch (Exception e) {
            return fecha;
        }
    }

    /**
     * Actualiza las estadísticas de ventas
     */
    private void actualizarEstadisticas() {
        int totalVentas = modeloTabla.getRowCount();
        double totalIngresos = 0;
        
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            totalIngresos += (Double) modeloTabla.getValueAt(i, 4);
        }
        
        jLabel1.setText("HISTORIAL DE VENTAS - Total: " + totalVentas + " ventas - $" + String.format("%,.0f", totalIngresos));
    }

    /**
     * Filtra las ventas según el texto de búsqueda
     */
    private void filtrarVentas(String texto) {
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                // Buscar en todas las columnas
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
            } catch (Exception e) {
                // Si el texto no es válido para regex, ignorar
            }
        }
    }

    /**
     * Muestra los detalles completos de una venta seleccionada
     */
    private void mostrarDetallesVenta() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta para ver los detalles",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Obtener el ID de la venta seleccionada
            int filaModelo = jTable1.convertRowIndexToModel(filaSeleccionada);
            int idVenta = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            
            // Obtener la venta y sus detalles
            Venta venta = ventaDAO.obtenerPorId(idVenta);
            List<DetalleVenta> detalles = detalleVentaDAO.obtenerPorVenta(idVenta);
            
            if (venta == null) {
                throw new Exception("No se pudo encontrar la venta seleccionada");
            }
            
            // Construir mensaje de detalles
            StringBuilder detallesMsg = new StringBuilder();
            detallesMsg.append("=== DETALLES DE VENTA ===\n");
            detallesMsg.append("N° Venta: ").append(venta.getIdVenta()).append("\n");
            detallesMsg.append("Fecha: ").append(venta.getFecha()).append("\n");
            detallesMsg.append("Total: $").append(venta.getTotalVenta()).append("\n\n");
            
            // Información del cliente
            if (venta.getIdCliente() > 0) {
                Cliente cliente = clienteDAO.obtenerPorId(venta.getIdCliente());
                if (cliente != null) {
                    detallesMsg.append("Cliente: ").append(cliente.getNombre()).append(" ").append(cliente.getApelido()).append("\n");
                    detallesMsg.append("RUT: ").append(cliente.getRut()).append("\n");
                }
            } else {
                detallesMsg.append("Cliente: Venta sin cliente registrado\n");
            }
            
            // Información del vendedor
            if (venta.getIdUsuario() > 0) {
                Usuario vendedor = usuarioDAO.obtenerPorId(venta.getIdUsuario());
                if (vendedor != null) {
                    detallesMsg.append("Vendedor: ").append(vendedor.getNombre()).append(" ").append(vendedor.getApellido()).append("\n");
                }
            }
            
            detallesMsg.append("\n--- PRODUCTOS ---\n");
            
            if (detalles.isEmpty()) {
                detallesMsg.append("No hay productos registrados para esta venta\n");
            } else {
                double totalCalculado = 0;
                for (DetalleVenta detalle : detalles) {
                    detallesMsg.append(String.format("Producto ID: %d - Cantidad: %d - Subtotal: $%,d\n", 
                        detalle.getIdProducto(), detalle.getCantidadVenta(), detalle.getPrecioVenta()));
                    totalCalculado += detalle.getPrecioVenta();
                }
                detallesMsg.append(String.format("\nTotal calculado: $%,.0f", totalCalculado));
            }
            
            JOptionPane.showMessageDialog(this,
                detallesMsg.toString(),
                "Detalles de Venta #" + idVenta,
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar detalles de la venta: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.severe("Error al cargar detalles de venta: " + e.getMessage());
        }
    }

    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        bttnAtras.addActionListener(e -> volverAtras());
        bttnDetalles.addActionListener(e -> mostrarDetallesVenta());
        bttnRefrescar.addActionListener(e -> cargarVentas());
        
        // Doble click en tabla muestra detalles
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetallesVenta();
                }
            }
        });
    }

    /**
     * Vuelve a la ventana anterior
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        bttnAtras = new javax.swing.JButton();
        bttnDetalles = new javax.swing.JButton();
        bttnRefrescar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HISTORIAL DE VENTAS");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bttnAtras.setText("Atras");

        bttnDetalles.setText("Detalle");

        bttnRefrescar.setText("Refrescar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bttnRefrescar)
                        .addGap(18, 18, 18)
                        .addComponent(bttnDetalles)
                        .addGap(18, 18, 18)
                        .addComponent(bttnAtras)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bttnAtras)
                    .addComponent(bttnDetalles)
                    .addComponent(bttnRefrescar))
                .addContainerGap())
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnAtras;
    private javax.swing.JButton bttnDetalles;
    private javax.swing.JButton bttnRefrescar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
