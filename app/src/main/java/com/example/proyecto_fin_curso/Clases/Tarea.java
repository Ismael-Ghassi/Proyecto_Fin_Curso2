package com.example.proyecto_fin_curso.Clases;

public class Tarea {
    private String id,correo_admin,correo_encargado,descripcion,imagen,imagen_completado,nombre,correo_ultima_mod;
    private boolean estado;


    public Tarea(){}

    public Tarea(String id, String nombre,String correo_admin, String correo_encargado, String imagen,String descripcion, boolean estado, String imagen_completado, String correo_ultima_mod) {
        this.id = id;
        this.correo_admin = correo_admin;
        this.correo_encargado = correo_encargado;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.imagen_completado = imagen_completado;
        this.nombre= nombre;
        this.estado = estado;
        this.correo_ultima_mod=correo_ultima_mod;
    }

    public String getCorreo_ultima_mod() {
        return correo_ultima_mod;
    }

    public void setCorreo_ultima_mod(String correo_ultima_mod) {
        this.correo_ultima_mod = correo_ultima_mod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo_admin() {
        return correo_admin;
    }

    public void setCorreo_admin(String correo_admin) {
        this.correo_admin = correo_admin;
    }

    public String getCorreo_encargado() {
        return correo_encargado;
    }

    public void setCorreo_encargado(String correo_encargado) {
        this.correo_encargado = correo_encargado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getImagen_completado() {
        return imagen_completado;
    }

    public void setImagen_completado(String imagen_completado) {
        this.imagen_completado = imagen_completado;
    }

    public String getNombre_tarea() {
        return nombre;
    }

    public void setNombre_tarea(String nombre_tarea) {
        this.nombre = nombre_tarea;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
