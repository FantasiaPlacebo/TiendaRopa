/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Modelo.DetalleVenta;
import java.util.List;

/**
 *
 * @author aacev
 */

public interface DetalleVentaDAO {
    
    boolean crearDetalleVenta(DetalleVenta detalle);
    List<DetalleVenta> obtenerPorVenta(int idVenta);
    List<DetalleVenta> obtenerPorProducto(int idProducto);
    boolean actualizarDetalleVenta(DetalleVenta detalle);
    boolean eliminarDetalleVenta(int idDetalle);
    boolean crearDetallesVenta(List<DetalleVenta> detalles);
    double obtenerTotalVentasPorProducto(int idProducto);
}
