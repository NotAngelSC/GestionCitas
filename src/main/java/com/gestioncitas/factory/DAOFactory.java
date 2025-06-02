package com.gestioncitas.factory;

import com.gestioncitas.dao.ClienteDAO;
import com.gestioncitas.dao.CitaDAO;
import com.gestioncitas.dao.GenericDAO;
import com.gestioncitas.dao.ServicioDAO;
import com.gestioncitas.dao.SerieRecurrenteDAO;
import com.gestioncitas.dao.UsuarioDAO;
import com.gestioncitas.models.Cliente;
import com.gestioncitas.models.Cita;
import com.gestioncitas.models.Servicio;
import com.gestioncitas.models.SerieRecurrente;
import com.gestioncitas.models.Usuario;

/**
 * DAOFactory: obtiene instancias de DAO concretos.
 * Si en el futuro deseas cambiar la implementación (por ejemplo, usar otro Motor),
 * basta con modificar este factory, sin tocar el resto del código.
 */
public class DAOFactory {

    public static GenericDAO<Usuario> getUsuarioDAO() {
        return new UsuarioDAO();
    }

    public static GenericDAO<Cliente> getClienteDAO() {
        return new ClienteDAO();
    }

    public static GenericDAO<Servicio> getServicioDAO() {
        return new ServicioDAO();
    }

    public static GenericDAO<Cita> getCitaDAO() {
        return new CitaDAO();
    }

    public static GenericDAO<SerieRecurrente> getSerieRecurrenteDAO() {
        return new SerieRecurrenteDAO();
    }

    // Agrega más métodos si añades nuevos modelos/DAOs...
}
