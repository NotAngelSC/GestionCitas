package com.gestioncitas.dao;

import com.gestioncitas.models.SerieRecurrente;
import com.gestioncitas.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieRecurrenteDAO implements GenericDAO<SerieRecurrente> {

    @Override
    public void crear(SerieRecurrente entidad) throws Exception {
        String sql = "INSERT INTO series_recurrentes "
                   + "(tipo_recurrencia, fecha_inicio, fecha_fin, repeticiones) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getTipoRecurrencia());
            ps.setDate(2, Date.valueOf(entidad.getFechaInicio()));

            if (entidad.getFechaFin() != null) {
                ps.setDate(3, Date.valueOf(entidad.getFechaFin()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (entidad.getRepeticiones() != null) {
                ps.setInt(4, entidad.getRepeticiones());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Crear serie recurrente fallido, no se obtuvo ID.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entidad.setIdSerie(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(SerieRecurrente entidad) throws Exception {
        String sql = "UPDATE series_recurrentes "
                   + "SET tipo_recurrencia = ?, fecha_inicio = ?, fecha_fin = ?, repeticiones = ? "
                   + "WHERE id_serie = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entidad.getTipoRecurrencia());
            ps.setDate(2, Date.valueOf(entidad.getFechaInicio()));

            if (entidad.getFechaFin() != null) {
                ps.setDate(3, Date.valueOf(entidad.getFechaFin()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (entidad.getRepeticiones() != null) {
                ps.setInt(4, entidad.getRepeticiones());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, entidad.getIdSerie());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(SerieRecurrente entidad) throws Exception {
        String sql = "DELETE FROM series_recurrentes WHERE id_serie = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdSerie());
            ps.executeUpdate();
        }
    }

    @Override
    public SerieRecurrente buscarPorId(Object... claves) throws Exception {
        String sql = "SELECT id_serie, tipo_recurrencia, fecha_inicio, fecha_fin, repeticiones "
                   + "FROM series_recurrentes WHERE id_serie = ?";

        SerieRecurrente serie = null;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) claves[0]);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    serie = new SerieRecurrente(
                        rs.getInt("id_serie"),
                        rs.getString("tipo_recurrencia"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        (rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null),
                        (rs.getObject("repeticiones") != null ? rs.getInt("repeticiones") : null)
                    );
                }
            }
        }

        return serie;
    }

    @Override
    public List<SerieRecurrente> listarTodos() throws Exception {
        List<SerieRecurrente> lista = new ArrayList<>();
        String sql = "SELECT id_serie, tipo_recurrencia, fecha_inicio, fecha_fin, repeticiones "
                   + "FROM series_recurrentes ORDER BY id_serie";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                SerieRecurrente serie = new SerieRecurrente(
                    rs.getInt("id_serie"),
                    rs.getString("tipo_recurrencia"),
                    rs.getDate("fecha_inicio").toLocalDate(),
                    (rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null),
                    (rs.getObject("repeticiones") != null ? rs.getInt("repeticiones") : null)
                );
                lista.add(serie);
            }
        }

        return lista;
    }
}
