package com.gestioncitas.dao;

import com.gestioncitas.models.Usuario;
import com.gestioncitas.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UsuarioDAO implements GenericDAO<Usuario> {

    @Override
    public void crear(Usuario entidad) throws Exception {
        // No es necesario aún para el login; podrías implementarlo más adelante.
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void actualizar(Usuario entidad) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void eliminar(Usuario entidad) throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public Usuario buscarPorId(Object... claves) throws Exception {
        // En este DAO no lo usaremos todavía
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        throw new UnsupportedOperationException("No implementado");
    }

    /**
     * Método específico para login: recibe el nombre de usuario y el SHA-1 de la contraseña,
     * y devuelve el Usuario completo si coincide, o null en caso contrario.
     */
    public Usuario autenticar(String username, String sha1contrasena) throws Exception {
        String sql = "SELECT id_usuario, nombre, usuario, contrasena, rol, activo " +
                     "FROM usuarios WHERE usuario = ? AND contrasena = ? AND activo = 1";

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

