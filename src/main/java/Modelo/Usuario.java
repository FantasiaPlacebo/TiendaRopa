/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author Santo Tomas
 */
public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String rut;
    private String correo;
    private String contrasenaUsuario; // Nuevo campo
    private String roles;

    public Usuario() {
    }

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String apellido, String rut, String correo, String contrasenaUsuario, String roles) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.correo = correo;
        this.contrasenaUsuario = contrasenaUsuario;
        this.roles = roles;
    }

    // Constructor sin ID (para nuevos usuarios)
    public Usuario(String nombre, String apellido, String rut, String correo, String contrasenaUsuario, String roles) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.correo = correo;
        this.contrasenaUsuario = contrasenaUsuario;
        this.roles = roles;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaUsuario() {
        return contrasenaUsuario;
    }

    public void setContrasenaUsuario(String contrasenaUsuario) {
        this.contrasenaUsuario = contrasenaUsuario;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", rut='" + rut + '\'' +
                ", correo='" + correo + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}