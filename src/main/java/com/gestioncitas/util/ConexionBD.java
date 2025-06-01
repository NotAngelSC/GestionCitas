package com.gestioncitas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    // Cambia estos valores según tu servidor y credenciales
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=GestionCitas;encrypt=true;trustServerCertificate=true";
    private static final String USER = "gestion_user";
    private static final String PASS = "1234";

    private ConexionBD() throws SQLException {
        // Carga del driver (en la mayoría de casos ya no es necesario explícitamente)
        // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
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
