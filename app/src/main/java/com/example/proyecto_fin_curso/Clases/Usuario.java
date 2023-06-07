package com.example.proyecto_fin_curso.Clases;

public class Usuario {
    private String correo, nombre;

    public Usuario(String correo, String nombre, String imagen) {
        this.correo = correo;
        this.nombre = nombre;
    }


    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    public Usuario() {
        this.correo = "";
        this.nombre = "";
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
