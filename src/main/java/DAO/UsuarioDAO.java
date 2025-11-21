/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Usuario;
import java.util.List;

/**
 *
 * @author aacev
 */

public interface UsuarioDAO {
    Usuario autenticar(String correo, String contrasena);
    Usuario obtenerPorCorreo(String correo);
    Usuario obtenerPorRut(String rut);
    boolean crearUsuario(Usuario usuario);
    Usuario obtenerPorId(int idUsuario);
    List<Usuario> obtenerTodos();
    boolean actualizarUsuario(Usuario usuario);
    boolean eliminarUsuario(int idUsuario);
    List<Usuario> obtenerPorRol(String rol);
    boolean existeCorreo(String correo);
    boolean existeRut(String rut);
}
