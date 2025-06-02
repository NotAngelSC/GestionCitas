package com.gestioncitas.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private int idCliente;
    private int idServicio;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String notas;
    private Integer idSerie;      // null si no pertenece a una serie
    private int version;          // para control de concurrencia

    // Campos adicionales para mostrar en la tabla
    private String nombreCliente;
    private String nombreServicio;

    public Cita() {}

    /** Constructor para crear/editar (sin idSerie o con idSerie si es parte de serie) */
    public Cita(int idCliente,
                int idServicio,
                LocalDate fecha,
                LocalTime horaInicio,
                LocalTime horaFin,
                String notas,
                Integer idSerie) {
        this.idCliente   = idCliente;
        this.idServicio  = idServicio;
        this.fecha       = fecha;
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
        this.notas       = notas;
        this.idSerie     = idSerie;
        this.version     = 1;
    }

    /** Constructor completo (para listar desde BD, con nombres de cliente/servicio y versión) */
    public Cita(int idCliente,
                String nombreCliente,
                int idServicio,
                String nombreServicio,
                LocalDate fecha,
                LocalTime horaInicio,
                LocalTime horaFin,
                String notas,
                Integer idSerie,
                int version) {
        this.idCliente       = idCliente;
        this.nombreCliente   = nombreCliente;
        this.idServicio      = idServicio;
        this.nombreServicio  = nombreServicio;
        this.fecha           = fecha;
        this.horaInicio      = horaInicio;
        this.horaFin         = horaFin;
        this.notas           = notas;
        this.idSerie         = idSerie;
        this.version         = version;
    }

    // Getters y setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Integer getIdSerie() { return idSerie; }
    public void setIdSerie(Integer idSerie) { this.idSerie = idSerie; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }
}
