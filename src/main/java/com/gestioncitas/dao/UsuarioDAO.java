package com.gestioncitas.dao;

import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements GenericDAO<Usuario> {

    @Override
    public void crear(Usuario entidad) throws Exception {
        String sql = "INSERT INTO usuarios (nombre, usuario, contrasena, rol, activo) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getUsuario());
            ps.setString(3, entidad.getContrasena()); // asume SHA-1 ya calculado
            ps.setString(4, entidad.getRol());
            ps.setBoolean(5, entidad.isActivo());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Crear usuario fallido, no se obtuvo ID.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entidad.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Usuario entidad) throws Exception {
        String sql = "UPDATE usuarios "
                   + "SET nombre = ?, usuario = ?, rol = ?, activo = ? "
                   + "WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getUsuario());
            ps.setString(3, entidad.getRol());
            ps.setBoolean(4, entidad.isActivo());
            ps.setInt(5, entidad.getIdUsuario());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Usuario entidad) throws Exception {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public Usuario buscarPorId(Object... claves) throws Exception {
        String sql = "SELECT id_usuario, nombre, usuario, contrasena, rol, activo "
                   + "FROM usuarios WHERE id_usuario = ?";

        Usuario usuario = null;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) claves[0]);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setUsuario(rs.getString("usuario"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setActivo(rs.getBoolean("activo"));
                }
            }
        }

        return usuario;
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, usuario, contrasena, rol, activo "
                   + "FROM usuarios ORDER BY nombre";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
        }

        return lista;
    }

    /**
     * Método para cambiar la contraseña de un usuario dado su ID.
     * @param idUsuario ID del usuario
     * @param sha1nueva nueva contraseña en SHA-1
     * @throws Exception si ocurre un error en la base de datos
     */
    public void cambiarContrasena(int idUsuario, String sha1nueva) throws Exception {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sha1nueva);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    /**
     * Método específico para login: recibe el nombre de usuario y el SHA-1 de la contraseña,
     * y devuelve el Usuario completo si coincide, o null en caso contrario.
     */
    public Usuario autenticar(String username, String sha1contrasena) throws Exception {
        String sql = "SELECT id_usuario, nombre, usuario, contrasena, rol, activo "
                   + "FROM usuarios WHERE usuario = ? AND contrasena = ? AND activo = 1";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, sha1contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                } else {
                    return null;
                }
            }
        }
    }
}


