/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vistas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 *
 * @author Santo Tomas
 */
public class MainFrame extends JFrame {

    // El gestor de "tarjetas" que nos permite cambiar vistas
    private CardLayout cardLayout;
    
    // El panel que contendrá las "tarjetas" (tus JPanels)
    private JPanel panelContenedor;

    // Instancias de tus vistas
    private VistaLogin vistaLogin;
    private VistaRegistro vistaRegistro;
    private VistaPuntoVenta vistaPuntoVenta;

    public MainFrame() {
        // --- 1. Configuración básica de la ventana ---
        setTitle("Sistema de Tienda de Ropa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Termina la app al cerrar la ventana
        setSize(800, 600); // Dale un tamaño adecuado
        setLocationRelativeTo(null); // Centrar en pantalla

        // --- 2. Configurar el CardLayout ---
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // --- 3. Inicializar y añadir las vistas ---
        // Pasamos 'this' (esta instancia del MainFrame) a las vistas
        // para que puedan llamarnos y pedir un cambio de panel.
        vistaLogin = new VistaLogin(this);
        vistaRegistro = new VistaRegistro(this);
        vistaPuntoVenta = new VistaPuntoVenta(this); // Asumiendo que esta también navegará

        // Añadimos las vistas al contenedor con un nombre único
        panelContenedor.add(vistaLogin, "LOGIN");
        panelContenedor.add(vistaRegistro, "REGISTRO");
        panelContenedor.add(vistaPuntoVenta, "VENTA");

        // --- 4. Añadir el contenedor a la ventana ---
        this.add(panelContenedor);

        // --- 5. Mostrar el Login primero ---
        cardLayout.show(panelContenedor, "LOGIN");
        
        // Hacer visible la ventana
        setVisible(true);
    }

    // --- Métodos de Navegación ---
    // Estos métodos serán llamados DESDE los JPanels
    
    public void mostrarLogin() {
        cardLayout.show(panelContenedor, "LOGIN");
    }

    public void mostrarRegistro() {
        cardLayout.show(panelContenedor, "REGISTRO");
    }

    public void mostrarPuntoVenta() {
        cardLayout.show(panelContenedor, "VENTA");
    }

    /**
     * El NUEVO punto de entrada de tu aplicación.
     */
    public static void main(String[] args) {
        // Esto asegura que la interfaz gráfica se ejecute en el hilo correcto
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(); // Crear y mostrar nuestra ventana
            }
        });
    }
}