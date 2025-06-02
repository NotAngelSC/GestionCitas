package com.gestioncitas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    // Cambia estos parámetros según tu servidor SQL Server
    private static final String URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=GestionCitas;encrypt=true;trustServerCertificate=true";
    private static final String USER = "gestion_user";
    private static final String PASS = "1234";

    private ConexionBD() throws SQLException {
        // En JDBC 4+ no hace falta cargar el driver con Class.forName(...);
        this.conexion = DriverManager.getConnection(URL, USER, PASS);
    }

    public static synchronized ConexionBD getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}
