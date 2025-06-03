package com.gestioncitas.dao;

import com.gestioncitas.models.Cita;
import com.gestioncitas.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO implements GenericDAO<Cita> {

    @Override
    public void crear(Cita cita) throws Exception {
        String sql = "INSERT INTO citas "
                   + "(id_cliente, id_servicio, fecha, hora_inicio, hora_fin, notas, id_serie) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdServicio());
            ps.setDate(3, Date.valueOf(cita.getFecha()));
            ps.setTime(4, Time.valueOf(cita.getHoraInicio()));
            ps.setTime(5, Time.valueOf(cita.getHoraFin()));
            ps.setString(6, cita.getNotas());

            if (cita.getIdSerie() != null) {
                ps.setInt(7, cita.getIdSerie());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cita cita) throws Exception {
        String sql = "UPDATE citas "
                   + "SET hora_fin = ?, notas = ?, id_serie = ?, version = version + 1 "
                   + "WHERE id_cliente = ? AND id_servicio = ? AND fecha = ? AND hora_inicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTime(1, Time.valueOf(cita.getHoraFin()));
            ps.setString(2, cita.getNotas());

            if (cita.getIdSerie() != null) {
                ps.setInt(3, cita.getIdSerie());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, cita.getIdCliente());
            ps.setInt(5, cita.getIdServicio());
            ps.setDate(6, Date.valueOf(cita.getFecha()));
            ps.setTime(7, Time.valueOf(cita.getHoraInicio()));

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Cita cita) throws Exception {
        String sql = "DELETE FROM citas "
                   + "WHERE id_cliente = ? AND id_servicio = ? AND fecha = ? AND hora_inicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdServicio());
            ps.setDate(3, Date.valueOf(cita.getFecha()));
            ps.setTime(4, Time.valueOf(cita.getHoraInicio()));

            ps.executeUpdate();
        }
    }

    @Override
    public Cita buscarPorId(Object... claves) throws Exception {
        String sql = "SELECT c.id_cliente, cli.nombre AS nombre_cliente, "
                   + "       c.id_servicio, ser.nombre AS nombre_servicio, "
                   + "       c.fecha, c.hora_inicio, c.hora_fin, c.notas, c.id_serie, c.version "
                   + "FROM citas c "
                   + "INNER JOIN clientes cli ON c.id_cliente = cli.id_cliente "
                   + "INNER JOIN servicios ser ON c.id_servicio = ser.id_servicio "
                   + "WHERE c.id_cliente = ? "
                   + "  AND c.id_servicio = ? "
                   + "  AND c.fecha = ? "
                   + "  AND c.hora_inicio = ?";

        Cita cita = null;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) claves[0]);
            ps.setInt(2, (int) claves[1]);
            ps.setDate(3, Date.valueOf((LocalDate) claves[2]));
            ps.setTime(4, Time.valueOf((LocalTime) claves[3]));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cita = new Cita(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio").toLocalTime(),
                        rs.getTime("hora_fin").toLocalTime(),
                        rs.getString("notas"),
                        (rs.getObject("id_serie") != null ? rs.getInt("id_serie") : null),
                        rs.getInt("version")
                    );
                }
            }
        }

        return cita;
    }

    @Override
    public List<Cita> listarTodos() throws Exception {
        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT c.id_cliente, cli.nombre AS nombre_cliente, "
                   + "       c.id_servicio, ser.nombre AS nombre_servicio, "
                   + "       c.fecha, c.hora_inicio, c.hora_fin, c.notas, c.id_serie, c.version "
                   + "FROM citas c "
                   + "INNER JOIN clientes cli ON c.id_cliente = cli.id_cliente "
                   + "INNER JOIN servicios ser ON c.id_servicio = ser.id_servicio "
                   + "ORDER BY c.fecha, c.hora_inicio";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Cita cita = new Cita(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre_cliente"),
                    rs.getInt("id_servicio"),
                    rs.getString("nombre_servicio"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora_inicio").toLocalTime(),
                    rs.getTime("hora_fin").toLocalTime(),
                    rs.getString("notas"),
                    (rs.getObject("id_serie") != null ? rs.getInt("id_serie") : null),
                    rs.getInt("version")
                );
                lista.add(cita);
            }
        }

        return lista;
    }

    /**
     * Método adicional para eliminar todas las citas de una serie.
     * Útil si deseas borrar todas las filas cuyo id_serie coincida.
     */
    public void eliminarPorSerie(int idSerie) throws Exception {
        String sql = "DELETE FROM citas WHERE id_serie = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSerie);
            ps.executeUpdate();
        }
    }
}
