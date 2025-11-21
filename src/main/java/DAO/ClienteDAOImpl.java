/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacev
 */

public class ClienteDAOImpl implements ClienteDAO {
    
    private Connection conexion;
    
    public ClienteDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    @Override
    public boolean crearCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombreCliente, apellidoCliente, rut, correo, telefono, direccion) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApelido()); // Nota: en BD es apellidoCliente, en clase es apelido
            stmt.setString(3, ""); // RUT - agregar campo RUT en la clase Cliente si es necesario
            stmt.setString(4, cliente.getCorreo());
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getDireccion());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE idCliente = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    @Override
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombreCliente = ?, apellidoCliente = ?, rut = ?, " +
                    "correo = ?, telefono = ?, direccion = ? WHERE idCliente = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApelido());
            stmt.setString(3, ""); // RUT
            stmt.setString(4, cliente.getCorreo());
            stmt.setInt(5, cliente.getTelefono());
            stmt.setString(6, cliente.getDireccion());
            stmt.setInt(7, cliente.getIdCliente());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean eliminarCliente(int idCliente) {
        String sql = "DELETE FROM clientes WHERE idCliente = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Cliente obtenerPorRut(String rut) {
        String sql = "SELECT * FROM clientes WHERE rut = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rut);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por RUT: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nombreCliente LIKE ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    @Override
    public List<Cliente> buscarPorApellido(String apellido) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE apellidoCliente LIKE ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "%" + apellido + "%");
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por apellido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    @Override
    public Cliente obtenerPorCorreo(String correo) {
        String sql = "SELECT * FROM clientes WHERE correo = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por correo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean existeRut(String rut) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE rut = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rut);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar RUT: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE correo = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método auxiliar para mapear ResultSet a Cliente
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNombre(rs.getString("nombreCliente"));
        cliente.setApelido(rs.getString("apellidoCliente")); // Mapeo de apellidoCliente (BD) a apelido (clase)
        cliente.setCorreo(rs.getString("correo"));
        
        // Manejar el campo telefono (en BD es VARCHAR, en clase es int)
        try {
            cliente.setTelefono(Integer.parseInt(rs.getString("telefono")));
        } catch (NumberFormatException e) {
            cliente.setTelefono(0); // Valor por defecto si hay error
        }
        
        cliente.setDireccion(rs.getString("direccion"));
        return cliente;
    }
    
    // Método adicional para buscar por nombre y apellido
    public List<Cliente> buscarPorNombreYApellido(String nombre, String apellido) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nombreCliente LIKE ? AND apellidoCliente LIKE ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            stmt.setString(2, "%" + apellido + "%");
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre y apellido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
}
