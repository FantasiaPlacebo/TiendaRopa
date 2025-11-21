/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacev
 */

public class UsuarioDAOImpl implements UsuarioDAO {
    
    private Connection conexion;
    
    public UsuarioDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    @Override
    public Usuario autenticar(String correo, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasenaUsuario = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public Usuario obtenerPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por correo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public Usuario obtenerPorRut(String rut) {
        String sql = "SELECT * FROM usuarios WHERE rut = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rut);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por RUT: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean crearUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombreUsuario, apellidoUsuario, rut, correo, contrasenaUsuario, rol) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getRut());
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, usuario.getContrasenaUsuario()); // Esta línea
            stmt.setString(6, usuario.getRoles());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Usuario obtenerPorId(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE idUsuario = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombreUsuario = ?, apellidoUsuario = ?, rut = ?, " +
                    "correo = ?, rol = ? WHERE idUsuario = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getRut());
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, usuario.getRoles());
            stmt.setInt(6, usuario.getIdUsuario());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE idUsuario = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Usuario> obtenerPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rol);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios por rol: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    @Override
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
        
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
    
    @Override
    public boolean existeRut(String rut) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rut = ?";
        
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
    
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setNombre(rs.getString("nombreUsuario"));
        usuario.setApellido(rs.getString("apellidoUsuario"));
        usuario.setRut(rs.getString("rut"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setContrasenaUsuario(rs.getString("contrasenaUsuario"));
        usuario.setRoles(rs.getString("rol"));
        return usuario;
    }
    
    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasenaUsuario = ? WHERE idUsuario = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevaContrasena);
            stmt.setInt(2, idUsuario);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
