/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author Santo Tomas
 */

public class Venta {
    private int idVenta;
    private int idCliente;
    private int idUsuario;
    private String fecha;
    private double totalVenta;

    public Venta() {
    }

    public Venta(int idVenta, int idCliente, int idUsuario, String fecha, double totalVenta) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.totalVenta = totalVenta;
    }

    public Venta(int idCliente, int idUsuario, double totalVenta) {
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.totalVenta = totalVenta;
        this.fecha = "";
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}