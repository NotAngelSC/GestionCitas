package com.gestioncitas.dao;

import com.gestioncitas.models.Servicio;
import com.gestioncitas.util.ConexionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO implements GenericDAO<Servicio> {
    @Override
    public void crear(Servicio entidad) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void actualizar(Servicio entidad) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void eliminar(Servicio entidad) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public Servicio buscarPorId(Object... claves) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public List<Servicio> listarTodos() throws Exception {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, duracion_min, precio, descripcion "
                   + "FROM servicios ORDER BY nombre";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    rs.getInt("duracion_min"),
                    rs.getDouble("precio"),
                    rs.getString("descripcion")
                );
                lista.add(s);
            }
        }

        return lista;
    }
}

