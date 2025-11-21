/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author Santo Tomas
 */
public class Cliente {
    private int idCliente;
    private String nombre;
    private String apelido;
    private String correo;
    private String rut;
    private int telefono;
    private String direccion;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String apelido, String correo, String rut, int telefono, String direccion) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apelido = apelido;
        this.correo = correo;
        this.rut = rut;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }
    
    
    public String getRut(){
        return rut;
    }
    
}
