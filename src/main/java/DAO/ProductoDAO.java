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
    
    boolean crearProducto(Producto producto);
    Producto obtenerPorId(int idProducto);
    List<Producto> obtenerTodos();
    boolean actualizarProducto(Producto producto);
    boolean eliminarProducto(int idProducto);
    List<Producto> buscarPorNombre(String nombre);
    List<Producto> buscarPorCategoria(String categoria);
    List<Producto> buscarPorMarca(String marca);
    List<Producto> buscarPorPrecio(double precioMin, double precioMax);
    boolean actualizarStock(int idProducto, int nuevoStock);
    boolean reducirStock(int idProducto, int cantidad);
    boolean aumentarStock(int idProducto, int cantidad);
    boolean existeProducto(String nombre, String marca);
    int obtenerStock(int idProducto);
}