/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Cliente;
import java.util.List;

/**
 *
 * @author aacev
 */

public interface ClienteDAO {
    
    boolean crearCliente(Cliente cliente);
    Cliente obtenerPorId(int idCliente);
    List<Cliente> obtenerTodos();
    boolean actualizarCliente(Cliente cliente);
    boolean eliminarCliente(int idCliente);
    Cliente obtenerPorRut(String rut);
    List<Cliente> buscarPorNombre(String nombre);
    List<Cliente> buscarPorApellido(String apellido);
    Cliente obtenerPorCorreo(String correo);
    boolean existeRut(String rut);
    boolean existeCorreo(String correo);
}
