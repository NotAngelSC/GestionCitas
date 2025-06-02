package com.gestioncitas.dao;

import com.gestioncitas.models.Cliente;
import com.gestioncitas.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements GenericDAO<Cliente> {

    @Override
    public void crear(Cliente entidad) throws Exception {
        String sql = "INSERT INTO clientes (nombre, telefono, correo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getTelefono());
            ps.setString(3, entidad.getCorreo());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Crear cliente fallido, no se obtuvo ID.");
            }
            // Recuperar el ID generado y asignarlo al objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entidad.setIdCliente(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Cliente entidad) throws Exception {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getTelefono());
            ps.setString(3, entidad.getCorreo());
            ps.setInt(4, entidad.getIdCliente());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Cliente entidad) throws Exception {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdCliente());
            ps.executeUpdate();
        }
    }

    @Override
    public Cliente buscarPorId(Object... claves) throws Exception {
        String sql = "SELECT id_cliente, nombre, telefono, correo, fecha_registro " +
                     "FROM clientes WHERE id_cliente = ?";
        Cliente cliente = null;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) claves[0]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getDate("fecha_registro").toLocalDate()
                    );
                }
            }
        }
        return cliente;
    }

    @Override
    public List<Cliente> listarTodos() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, telefono, correo, fecha_registro " +
                     "FROM clientes ORDER BY nombre";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getDate("fecha_registro").toLocalDate()
                );
                lista.add(c);
            }
        }
        return lista;
    }
}
