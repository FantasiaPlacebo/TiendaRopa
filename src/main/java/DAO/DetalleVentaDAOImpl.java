/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacev
 */

public class DetalleVentaDAOImpl implements DetalleVentaDAO {
    
    private Connection conexion;
    
    public DetalleVentaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    @Override
    public boolean crearDetalleVenta(DetalleVenta detalle) {
        String sql = "INSERT INTO detalleVenta (idVenta, idProducto, cantidad, subTotal) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getIdVenta());
            stmt.setInt(2, detalle.getIdProducto());
            stmt.setInt(3, detalle.getCantidadVenta());
            stmt.setDouble(4, detalle.getPrecioVenta()); // En tu clase es precioVenta, en BD es subTotal
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear detalle de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<DetalleVenta> obtenerPorVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.*, p.nombre as nombreProducto " +
                    "FROM detalleVenta dv " +
                    "INNER JOIN productos p ON dv.idProducto = p.idProducto " +
                    "WHERE dv.idVenta = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DetalleVenta detalle = mapearDetalleVenta(rs);
                // Podrías agregar el nombre del producto si lo necesitas
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de venta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    @Override
    public List<DetalleVenta> obtenerPorProducto(int idProducto) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalleVenta WHERE idProducto = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                detalles.add(mapearDetalleVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles por producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    @Override
    public boolean actualizarDetalleVenta(DetalleVenta detalle) {
        String sql = "UPDATE detalleVenta SET cantidad = ?, subTotal = ? WHERE idDetalle = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getCantidadVenta());
            stmt.setDouble(2, detalle.getPrecioVenta());
            stmt.setInt(3, detalle.getIdVenta()); // CORREGIDO: debería ser idDetalle
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar detalle de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean eliminarDetalleVenta(int idDetalle) {
        String sql = "DELETE FROM detalleVenta WHERE idDetalle = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idDetalle);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean crearDetallesVenta(List<DetalleVenta> detalles) {
        String sql = "INSERT INTO detalleVenta (idVenta, idProducto, cantidad, subTotal) VALUES (?, ?, ?, ?)";
        
        try {
            conexion.setAutoCommit(false);
            
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                for (DetalleVenta detalle : detalles) {
                    stmt.setInt(1, detalle.getIdVenta());
                    stmt.setInt(2, detalle.getIdProducto());
                    stmt.setInt(3, detalle.getCantidadVenta());
                    stmt.setDouble(4, detalle.getPrecioVenta());
                    stmt.addBatch();
                }
                
                int[] resultados = stmt.executeBatch();
                conexion.commit();
                
                // Verificar que todos los inserts fueron exitosos
                for (int resultado : resultados) {
                    if (resultado <= 0) {
                        return false;
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("Error al crear detalles de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar auto-commit: " + e.getMessage());
            }
        }
    }
    
    @Override
    public double obtenerTotalVentasPorProducto(int idProducto) {
        String sql = "SELECT SUM(subTotal) as total FROM detalleVenta WHERE idProducto = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas por producto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Método auxiliar para mapear ResultSet a DetalleVenta
    private DetalleVenta mapearDetalleVenta(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdVenta(rs.getInt("idVenta"));
        detalle.setIdProducto(rs.getInt("idProducto"));
        detalle.setCantidadVenta(rs.getInt("cantidad"));
        detalle.setPrecioVenta(rs.getInt("subTotal")); // Mapeo correcto: subTotal (BD) -> precioVenta (clase)
        return detalle;
    }
    
    // Método adicional para obtener el detalle completo con información del producto
    public List<DetalleVenta> obtenerDetallesCompletosPorVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.*, p.nombre as nombreProducto, p.precio as precioUnitario " +
                    "FROM detalleVenta dv " +
                    "INNER JOIN productos p ON dv.idProducto = p.idProducto " +
                    "WHERE dv.idVenta = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DetalleVenta detalle = mapearDetalleVenta(rs);
                // Aquí podrías setear información adicional si la necesitas
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles completos de venta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    // Método para eliminar todos los detalles de una venta
    public boolean eliminarDetallesPorVenta(int idVenta) {
        String sql = "DELETE FROM detalleVenta WHERE idVenta = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalles de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
