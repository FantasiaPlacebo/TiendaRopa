/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Santo Tomas
 */
public class ConexionBD {
    
    private static final String URL = "jdbc:mysql://localhost:3306/tiendaropa";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root";
    
    private static Connection conexion = null;

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("Conexión exitosa a la BD.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar con la Base de Datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }

    public static void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}