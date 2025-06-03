package com.gestioncitas.util;

import com.gestioncitas.models.Usuario;

/**
 * Clase utilitaria para almacenar en memoria
 * los datos del usuario que ha iniciado sesión.
 */
public class Session {
    private static Usuario usuario;

    /** Retorna el usuario actualmente logueado, o null si no hay ninguno. */
    public static Usuario getUsuario() {
        return usuario;
    }

    /** Guarda el usuario que acaba de iniciar sesión. */
    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    /** Limpia la sesión (elimina cualquier usuario almacenado). */
    public static void clear() {
        usuario = null;
    }
}
