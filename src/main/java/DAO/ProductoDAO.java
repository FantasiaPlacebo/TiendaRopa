/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Producto;
import java.util.List;

/**
 *
 * @author aacev
 */
public interface ProductoDAO {
    boolean insertar(Producto producto);
    List<Producto> obtenerTodos();
    Producto obtenerPorId(int id);
    boolean actualizar(Producto producto);
    boolean eliminar(int id);
    List<Producto> buscarPorNombre(String nombre);
    public Producto buscarPorId(int idProducto);
    boolean actualizarStock(int idProducto, int nuevoStock);
    boolean existeProducto(String nombre, String marca);
}