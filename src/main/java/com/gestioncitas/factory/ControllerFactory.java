package com.gestioncitas.factory;

import com.gestioncitas.controllers.*;

/**
 * ControllerFactory: si necesitas instanciar controladores manualmente
 * (por ejemplo, para pasar parámetros o hacer pruebas), puedes centralizarlo aquí.
 *
 * Nota: en la mayoría de casos JavaFX se encarga de instanciar los controladores
 * cuando cargas con FXMLLoader, pero puede servir para pruebas unitarias.
 */
public class ControllerFactory {

    public static LoginController getLoginController() {
        return new LoginController();
    }

    public static DashboardController getDashboardController() {
        return new DashboardController();
    }

    public static ClienteController getClienteController() {
        return new ClienteController();
    }

    public static CitaController getCitaController() {
        return new CitaController();
    }

    public static ConfiguracionController getConfiguracionController() {
        return new ConfiguracionController();
    }

    // Agrega métodos para otros controladores según los vayas creando...
}
