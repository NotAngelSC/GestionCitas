package com.gestioncitas.models;

public class Servicio {
    private int idServicio;
    private String nombre;
    private int duracionMin;  // en minutos
    private double precio;
    private String descripcion;

    public Servicio() {}

    public Servicio(int idServicio, String nombre, int duracionMin, double precio, String descripcion) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.duracionMin = duracionMin;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int duracionMin) { this.duracionMin = duracionMin; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Para que ComboBox muestre el nombre
    @Override
    public String toString() {
        return nombre;
    }
}
