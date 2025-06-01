package com.gestioncitas.dao;

import java.util.List;

public interface GenericDAO<T> {
    void crear(T entidad) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(T entidad) throws Exception;
    T buscarPorId(Object... clave) throws Exception; // clave compuesta o simple
    List<T> listarTodos() throws Exception;
}
