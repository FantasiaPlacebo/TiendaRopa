/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.*;

/**
 *
 * @author Santo Tomas
 */

public class Conn {
    private static final String URL = "jdbc:mysql://localhost:3306/tiendaRopa";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    public static Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }
}