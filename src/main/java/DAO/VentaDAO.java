/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.Venta;
import java.util.List;

/**
 *
 * @author aacev
 */


public interface VentaDAO {
    
    int crearVenta(Venta venta);
    Venta obtenerPorId(int idVenta);
    List<Venta> obtenerTodos();
    List<Venta> obtenerPorCliente(int idCliente);
    List<Venta> obtenerPorFecha(String fecha);
    boolean actualizarVenta(Venta venta);
    boolean eliminarVenta(int idVenta);
    double obtenerTotalVentasDelDia();
    int obtenerUltimoIdVenta();
}
