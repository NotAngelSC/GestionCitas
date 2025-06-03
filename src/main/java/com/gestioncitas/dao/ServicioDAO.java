package com.gestioncitas.dao;

import com.gestioncitas.models.Servicio;
import com.gestioncitas.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO implements GenericDAO<Servicio> {

    @Override
    public void crear(Servicio entidad) throws Exception {
        String sql = "INSERT INTO servicios (nombre, duracion_min, precio, descripcion) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getDuracionMin());
            ps.setDouble(3, entidad.getPrecio());
            ps.setString(4, entidad.getDescripcion());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Crear servicio fallido, no se obtuvo ID.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entidad.setIdServicio(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Servicio entidad) throws Exception {
        String sql = "UPDATE servicios "
                   + "SET nombre = ?, duracion_min = ?, precio = ?, descripcion = ? "
                   + "WHERE id_servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getDuracionMin());
            ps.setDouble(3, entidad.getPrecio());
            ps.setString(4, entidad.getDescripcion());
            ps.setInt(5, entidad.getIdServicio());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Servicio entidad) throws Exception {
        String sql = "DELETE FROM servicios WHERE id_servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdServicio());
            ps.executeUpdate();
        }
    }

    @Override
    public Servicio buscarPorId(Object... claves) throws Exception {
        String sql = "SELECT id_servicio, nombre, duracion_min, precio, descripcion "
                   + "FROM servicios WHERE id_servicio = ?";

        Servicio servicio = null;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) claves[0]);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    servicio = new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getString("nombre"),
                        rs.getInt("duracion_min"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion")
                    );
                }
            }
        }

        return servicio;
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

