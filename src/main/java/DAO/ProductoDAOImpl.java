/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Producto;
import Conexion.Conn;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacev
 */

public class ProductoDAOImpl implements ProductoDAO {
    
    @Override
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, categoria, marca, precio, stock) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getCategoria());
            ps.setString(3, producto.getMarca());
            ps.setInt(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY nombre";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(crearProductoDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        
        return productos;
    }
    
    @Override
    public Producto obtenerPorId(int id) {
        String sql = "SELECT * FROM productos WHERE idProducto = ?";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return crearProductoDesdeResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, categoria = ?, marca = ?, precio = ?, stock = ? WHERE idProducto = ?";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getCategoria());
            ps.setString(3, producto.getMarca());
            ps.setInt(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setInt(6, producto.getIdProducto());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE idProducto = ?";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                productos.add(crearProductoDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
        }
        
        return productos;
    }
    
    @Override
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE productos SET stock = ? WHERE idProducto = ?";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean existeProducto(String nombre, String marca) {
        String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ? AND marca = ?";
        
        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setString(2, marca);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar producto: " + e.getMessage());
        }
        
        return false;
    }
    
    private Producto crearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setMarca(rs.getString("marca"));
        producto.setPrecio(rs.getInt("precio"));
        producto.setStock(rs.getInt("stock"));
        return producto;
    }

    @Override
    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT * FROM productos WHERE idProducto = ?";

        try (Connection con = Conn.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return crearProductoDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }

        return null;
    }    
}
