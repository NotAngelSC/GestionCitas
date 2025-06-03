package com.gestioncitas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Parámetros de conexión (ajústalos según tu entorno)
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=GestionCitas;encrypt=true;trustServerCertificate=true";
    private static final String USER = "gestion_user";
    private static final String PASS = "1234";

    // Singleton de la clase (no de la Connection)
    private static ConexionBD instancia;

    private ConexionBD() {
        // Constructor privado para implementar singleton de la clase.
        // NOTA: no abrimos ninguna Connection aquí.
    }

    /**
     * Retorna la instancia única de ConexionBD (para luego pedir conexiones nuevas).
     */
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Abre y retorna una NUEVA conexión a la base de datos.
     * Cada vez que se invoque este método, se creará una Connection distinta,
     * que el DAO podrá cerrar sin afectar a otras.
     *
     * @return una nueva Connection activa a SQL Server.
     * @throws SQLException si hay un error al crear la conexión.
     */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

