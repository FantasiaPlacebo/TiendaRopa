/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;
import DAO.ProductoDAO;
import DAO.ProductoDAOImpl;
import Modelo.Producto;
import Conexion.Conn;
import java.sql.Connection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
/**
 *
 * @author aacev
 */
public class VistaInventario extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaInventario.class.getName());
    private ProductoDAO productoDAO;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public VistaInventario() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        // Inicializar DAO
        Connection conexion = Conn.conectar();
        productoDAO = new ProductoDAOImpl(conexion);
        
        // Configurar tabla
        inicializarTabla();
        
        // Cargar datos
        cargarProductos();
        
        // Configurar eventos
        configurarEventos();
    }
    
    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Nombre", "Categoría", "Marca", "Precio", "Stock"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; // ID
                    case 4: return Double.class;  // Precio
                    case 5: return Integer.class; // Stock
                    default: return String.class;
                }
            }
        };

        jTable1.setModel(modeloTabla);

        // Configurar sorter para búsqueda
        sorter = new TableRowSorter<>(modeloTabla);
        jTable1.setRowSorter(sorter);

        // Ajustar anchos de columnas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);  // Categoría
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);  // Marca
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);   // Precio
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(60);   // Stock
    }
    
    private void cargarProductos() {
    try {
        modeloTabla.setRowCount(0); // Limpiar tabla

        List<Producto> productos = productoDAO.obtenerTodos();

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay productos en el inventario",
                "Inventario vacío",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Producto producto : productos) {
            Object[] fila = {
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getMarca(),
                producto.getPrecio(), // Asegurar que esto sea Double, no Integer
                producto.getStock()
            };
            modeloTabla.addRow(fila);
        }

        // Actualizar estadísticas
        actualizarEstadisticas();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar inventario: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        logger.severe("Error al cargar productos: " + e.getMessage());
    }
}
    
    private void actualizarEstadisticas() {
        int totalProductos = modeloTabla.getRowCount();
        int stockTotal = 0;
        double valorTotal = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int stock = (Integer) modeloTabla.getValueAt(i, 5);
            double precio = (Double) modeloTabla.getValueAt(i, 4);
            stockTotal += stock;
            valorTotal += (stock * precio);
        }

        jLabel1.setText("INVENTARIO - Productos: " + totalProductos + " - Stock total: " + stockTotal + " - Valor: $" + String.format("%,.0f", valorTotal));
    }
    
    private void consultarProducto() {
        try {
            String nombre = txtNombreProducto.getText().trim();
            String categoria = txtCategoriaProducto.getText().trim();
            String marca = txtMarcaProducto.getText().trim();

            List<Producto> productos;

            if (!nombre.isEmpty()) {
                productos = productoDAO.buscarPorNombre(nombre);
            } else if (!categoria.isEmpty()) {
                productos = productoDAO.buscarPorCategoria(categoria);
            } else if (!marca.isEmpty()) {
                productos = productoDAO.buscarPorMarca(marca);
            } else {
                productos = productoDAO.obtenerTodos();
            }

            modeloTabla.setRowCount(0);
            for (Producto producto : productos) {
                Object[] fila = {
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getCategoria(),
                    producto.getMarca(),
                    producto.getPrecio(),
                    producto.getStock()
                };
                modeloTabla.addRow(fila);
            }

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron productos con los criterios especificados",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar productos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarProducto() {
    try {
        if (!validarCamposProducto()) {
            return;
        }

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(txtNombreProducto.getText().trim());
        nuevoProducto.setCategoria(txtCategoriaProducto.getText().trim());
        nuevoProducto.setMarca(txtMarcaProducto.getText().trim());
        
        // SOLUCIÓN: Reemplazar coma por punto
        String precioTexto = txtPrecioProducto.getText().trim().replace(",", ".");
        nuevoProducto.setPrecio((int) Double.parseDouble(precioTexto));
        
        nuevoProducto.setStock(Integer.parseInt(txtStockProducto.getText().trim()));

        boolean exito = productoDAO.crearProducto(nuevoProducto);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                "Producto agregado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarProductos();
        } else {
            throw new Exception("No se pudo agregar el producto");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Error en formato numérico: " + e.getMessage() + 
            "\nUse punto o coma decimal (ej: 15.50 o 15,50)",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al agregar producto: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void modificarProducto() {
    try {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un producto para modificar",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener ID del producto seleccionado
        int filaModelo = jTable1.convertRowIndexToModel(filaSeleccionada);
        int idProducto = (Integer) modeloTabla.getValueAt(filaModelo, 0);

        // Validar campos
        if (!validarCamposProducto()) {
            return;
        }

        // Obtener producto actual
        Producto producto = productoDAO.obtenerPorId(idProducto);
        if (producto == null) {
            throw new Exception("No se encontró el producto seleccionado");
        }

        // Actualizar datos - SOLUCIÓN PARA EL ERROR DEL DOUBLE
        producto.setNombre(txtNombreProducto.getText().trim());
        producto.setCategoria(txtCategoriaProducto.getText().trim());
        producto.setMarca(txtMarcaProducto.getText().trim());
        
        // SOLUCIÓN: Manejar correctamente el formato del precio
        String precioTexto = txtPrecioProducto.getText().trim().replace(",", ".");
        producto.setPrecio((int) Double.parseDouble(precioTexto));
        
        producto.setStock(Integer.parseInt(txtStockProducto.getText().trim()));

        // Guardar cambios
        boolean exito = productoDAO.actualizarProducto(producto);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                "Producto modificado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarProductos();
        } else {
            throw new Exception("No se pudo modificar el producto");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Error en formato numérico: " + e.getMessage() + 
            "\n• Precio: Use punto o coma decimal (ej: 15.50 o 15,50)" +
            "\n• Stock: Debe ser un número entero",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al modificar producto: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void eliminarProducto() {
        try {
            int filaSeleccionada = jTable1.getSelectedRow();

            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para eliminar",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener ID del producto seleccionado
            int filaModelo = jTable1.convertRowIndexToModel(filaSeleccionada);
            int idProducto = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            String nombreProducto = (String) modeloTabla.getValueAt(filaModelo, 1);

            // Confirmar eliminación
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el producto:\n" + nombreProducto + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean exito = productoDAO.eliminarProducto(idProducto);

                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Producto eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarProductos();
                } else {
                    throw new Exception("No se pudo eliminar el producto");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCamposProducto() {
    String nombre = txtNombreProducto.getText().trim();
    String categoria = txtCategoriaProducto.getText().trim();
    String marca = txtMarcaProducto.getText().trim();
    String precio = txtPrecioProducto.getText().trim();
    String stock = txtStockProducto.getText().trim();
    
    if (nombre.isEmpty() || categoria.isEmpty() || marca.isEmpty() || 
        precio.isEmpty() || stock.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Todos los campos son obligatorios",
            "Campos incompletos",
            JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    try {
        double precioValor = Double.parseDouble(precio);
        int stockValor = Integer.parseInt(stock);
        
        if (precioValor <= 0 || stockValor < 0) {
            JOptionPane.showMessageDialog(this,
                "Precio debe ser mayor a 0 y stock no puede ser negativo",
                "Valores inválidos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Precio y stock deben ser valores numéricos válidos",
            "Valores inválidos",
            JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    return true;
}
    
    private void limpiarCampos() {
    txtNombreProducto.setText("");
    txtCategoriaProducto.setText("");
    txtMarcaProducto.setText("");
    txtPrecioProducto.setText("");
    txtStockProducto.setText("");
    txtNombreProducto.requestFocus();
}
    
    private void cargarProductoSeleccionado() {
    int filaSeleccionada = jTable1.getSelectedRow();
    
    if (filaSeleccionada != -1) {
        int filaModelo = jTable1.convertRowIndexToModel(filaSeleccionada);
        
        txtNombreProducto.setText(modeloTabla.getValueAt(filaModelo, 1).toString());
        txtCategoriaProducto.setText(modeloTabla.getValueAt(filaModelo, 2).toString());
        txtMarcaProducto.setText(modeloTabla.getValueAt(filaModelo, 3).toString());
        
        // SOLUCIÓN: Manejar correctamente el tipo de dato del precio
        Object precioObj = modeloTabla.getValueAt(filaModelo, 4);
        if (precioObj instanceof Double) {
            txtPrecioProducto.setText(String.valueOf((Double) precioObj));
        } else if (precioObj instanceof Integer) {
            txtPrecioProducto.setText(String.valueOf(((Integer) precioObj).doubleValue()));
        } else {
            txtPrecioProducto.setText(precioObj.toString());
        }
        
        txtStockProducto.setText(modeloTabla.getValueAt(filaModelo, 5).toString());
    }
}
    
    private void configurarEventos() {
    bttnAtras.addActionListener(e -> volverAtras());
    bttnConsultar.addActionListener(e -> consultarProducto());
    bttnAgregar.addActionListener(e -> agregarProducto());
    bttnModificar.addActionListener(e -> modificarProducto());
    bttnEliminar.addActionListener(e -> eliminarProducto());
    
    // Doble click en tabla carga producto en campos
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 1) {
                cargarProductoSeleccionado();
            }
        }
    });
    
    // Enter en campos ejecuta consulta
    txtNombreProducto.addActionListener(e -> consultarProducto());
    txtCategoriaProducto.addActionListener(e -> consultarProducto());
    txtMarcaProducto.addActionListener(e -> consultarProducto());
}

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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        txtCategoriaProducto = new javax.swing.JTextField();
        txtMarcaProducto = new javax.swing.JTextField();
        txtPrecioProducto = new javax.swing.JTextField();
        txtStockProducto = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        bttnConsultar = new javax.swing.JButton();
        bttnAgregar = new javax.swing.JButton();
        bttnModificar = new javax.swing.JButton();
        bttnEliminar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        bttnAtras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INVENTARIO");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("NOMBRE");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("CATEGORIA");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("MARCA");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PRECIO");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("STOCK");

        txtMarcaProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarcaProductoActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        bttnConsultar.setText("Consultar");

        bttnAgregar.setText("Agregar");

        bttnModificar.setText("Modificar");

        bttnEliminar.setText("Eliminar");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bttnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(bttnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(bttnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(bttnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bttnConsultar)
                    .addComponent(bttnAgregar)
                    .addComponent(bttnModificar)
                    .addComponent(bttnEliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCategoriaProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMarcaProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPrecioProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStockProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCategoriaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMarcaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtStockProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPrecioProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("Gestor de productos");

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bttnAtras.setText("Atras");
        bttnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnAtrasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bttnAtras)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttnAtras)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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

    private void txtMarcaProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarcaProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMarcaProductoActionPerformed

    private void bttnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnAtrasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bttnAtrasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttnAgregar;
    private javax.swing.JButton bttnAtras;
    private javax.swing.JButton bttnConsultar;
    private javax.swing.JButton bttnEliminar;
    private javax.swing.JButton bttnModificar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtCategoriaProducto;
    private javax.swing.JTextField txtMarcaProducto;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtPrecioProducto;
    private javax.swing.JTextField txtStockProducto;
    // End of variables declaration//GEN-END:variables
}
