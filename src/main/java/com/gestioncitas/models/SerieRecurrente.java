package com.gestioncitas.models;

import java.time.LocalDate;

public class SerieRecurrente {
    private int idSerie;
    private String tipoRecurrencia;   // "diaria", "semanal" o "mensual"
    private LocalDate fechaInicio;
    private LocalDate fechaFin;       // Puede ser null si no hay fecha límite
    private Integer repeticiones;     // Puede ser null si se usa fechaFin
    // createdAt y updatedAt no se exponen para simplificar el modelo de negocio

    public SerieRecurrente() {}

    /** Constructor para creación (sin idSerie, createdAt ni updatedAt) */
    public SerieRecurrente(String tipoRecurrencia, LocalDate fechaInicio,
                           LocalDate fechaFin, Integer repeticiones) {
        this.tipoRecurrencia = tipoRecurrencia;
        this.fechaInicio     = fechaInicio;
        this.fechaFin        = fechaFin;
        this.repeticiones    = repeticiones;
    }

    /** Constructor completo (incluye idSerie) */
    public SerieRecurrente(int idSerie, String tipoRecurrencia, 
                           LocalDate fechaInicio, LocalDate fechaFin, 
                           Integer repeticiones) {
        this.idSerie         = idSerie;
        this.tipoRecurrencia = tipoRecurrencia;
        this.fechaInicio     = fechaInicio;
        this.fechaFin        = fechaFin;
        this.repeticiones    = repeticiones;
    }

    // Getters y setters
    public int getIdSerie() { return idSerie; }
    public void setIdSerie(int idSerie) { this.idSerie = idSerie; }

    public String getTipoRecurrencia() { return tipoRecurrencia; }
    public void setTipoRecurrencia(String tipoRecurrencia) { this.tipoRecurrencia = tipoRecurrencia; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    @Override
    public String toString() {
        return tipoRecurrencia + " desde " + fechaInicio +
               (fechaFin != null ? " hasta " + fechaFin : "") +
               (repeticiones != null ? " (" + repeticiones + " repeticiones)" : "");
    }
}

