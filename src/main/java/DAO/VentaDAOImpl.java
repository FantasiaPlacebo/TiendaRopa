/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacev
 */

public class VentaDAOImpl implements VentaDAO {
    
    private Connection conexion;
    
    public VentaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    @Override
    public int crearVenta(Venta venta) {
        String sql = "INSERT INTO ventas (idCliente, idUsuario, fecha, totalVenta) VALUES (?, ?, NOW(), ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Si idCliente es 0, lo tratamos como NULL
            if (venta.getIdCliente() == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, venta.getIdCliente());
            }
            
            // idUsuario es requerido (el vendedor)
            stmt.setInt(2, venta.getIdUsuario());
            stmt.setDouble(3, venta.getTotalVenta());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Error al crear venta: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    @Override
    public Venta obtenerPorId(int idVenta) {
        String sql = "SELECT * FROM ventas WHERE idVenta = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearVenta(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener venta por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Venta> obtenerTodos() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha DESC";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    @Override
    public List<Venta> obtenerPorCliente(int idCliente) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE idCliente = ? ORDER BY fecha DESC";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    @Override
    public List<Venta> obtenerPorFecha(String fecha) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE DATE(fecha) = ? ORDER BY fecha DESC";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    @Override
    public boolean actualizarVenta(Venta venta) {
        String sql = "UPDATE ventas SET idCliente = ?, idUsuario = ?, totalVenta = ? WHERE idVenta = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (venta.getIdCliente() == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, venta.getIdCliente());
            }
            stmt.setInt(2, venta.getIdUsuario());
            stmt.setDouble(3, venta.getTotalVenta());
            stmt.setInt(4, venta.getIdVenta());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean eliminarVenta(int idVenta) {
        // Primero eliminar los detalles de la venta
        String sqlDetalles = "DELETE FROM detalleVenta WHERE idVenta = ?";
        String sqlVenta = "DELETE FROM ventas WHERE idVenta = ?";
        
        try {
            conexion.setAutoCommit(false);
            
            // Eliminar detalles
            try (PreparedStatement stmtDetalles = conexion.prepareStatement(sqlDetalles)) {
                stmtDetalles.setInt(1, idVenta);
                stmtDetalles.executeUpdate();
            }
            
            // Eliminar venta
            try (PreparedStatement stmtVenta = conexion.prepareStatement(sqlVenta)) {
                stmtVenta.setInt(1, idVenta);
                int filasAfectadas = stmtVenta.executeUpdate();
                
                conexion.commit();
                return filasAfectadas > 0;
            }
            
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("Error al eliminar venta: " + e.getMessage());
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
    public double obtenerTotalVentasDelDia() {
        String sql = "SELECT SUM(totalVenta) as total FROM ventas WHERE DATE(fecha) = CURDATE()";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas del día: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int obtenerUltimoIdVenta() {
        String sql = "SELECT MAX(idVenta) as ultimoId FROM ventas";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("ultimoId");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener último ID de venta: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Método auxiliar para mapear ResultSet a Venta
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("idVenta"));
        venta.setIdCliente(rs.getInt("idCliente"));
        venta.setIdUsuario(rs.getInt("idUsuario"));
        venta.setFecha(rs.getString("fecha"));
        venta.setTotalVenta(rs.getDouble("totalVenta"));
        return venta;
    }
    
    // Método adicional para obtener ventas por usuario
    public List<Venta> obtenerPorUsuario(int idUsuario) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE idUsuario = ? ORDER BY fecha DESC";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
    
    // Método adicional para obtener ventas en un rango de fechas
    public List<Venta> obtenerPorRangoFechas(String fechaInicio, String fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE DATE(fecha) BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por rango de fechas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ventas;
    }
}
