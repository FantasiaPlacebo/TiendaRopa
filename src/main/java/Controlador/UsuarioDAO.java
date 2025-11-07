/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author Santo Tomas
 */
public class UsuarioDAO {

    /**
     * Valida si un usuario existe en la BD con correo y contraseña.
     * Devuelve un objeto Usuario si lo encuentra, o null si no.
     */
    public Usuario validarUsuario(String correo, String contraseña) {
        Connection conn = ConexionBD.getConexion();
        // ¡¡ADVERTENCIA DE SEGURIDAD!! Ver nota al final
        String sql = "SELECT * FROM usuario WHERE correo = ? AND contraseña = ?";
        Usuario usuario = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, correo);
            pstmt.setString(2, contraseña);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Usuario encontrado, creamos el objeto
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setRut(rs.getString("rut"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setRoles(rs.getString("roles"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        // No cerramos la conexión aquí, la gestionará la clase ConexionBD
        return usuario;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Devuelve true si fue exitoso, false si no.
     */
    public boolean registrarUsuario(Usuario usuario, String contraseña) {
        Connection conn = ConexionBD.getConexion();
        String sql = "INSERT INTO usuario (nombre, apellido, rut, correo, contraseña, roles) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getRut());
            pstmt.setString(4, usuario.getCorreo());
            pstmt.setString(5, contraseña); // ¡¡ADVERTENCIA DE SEGURIDAD!!
            pstmt.setString(6, usuario.getRoles());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}