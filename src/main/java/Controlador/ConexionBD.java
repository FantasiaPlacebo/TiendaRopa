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
    
    // Variables de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/tiendaropa"; // Cambia 'tiendaropa' por el nombre de tu BD
    private static final String USUARIO = "root"; // Tu usuario de MySQL
    private static final String PASSWORD = "tu_contraseña"; // Tu contraseña de MySQL
    
    private static Connection conexion = null;

    /**
     * Método para obtener la conexión a la base de datos.
     * Implementa un patrón Singleton simple.
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Cargar el driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("Conexión exitosa a la BD.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar con la Base de Datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }

    /**
     * Método para cerrar la conexión.
     */
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