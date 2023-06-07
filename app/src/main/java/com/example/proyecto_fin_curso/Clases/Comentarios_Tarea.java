package com.example.proyecto_fin_curso.Clases;

import com.google.firebase.Timestamp;

public class Comentarios_Tarea {
    String  tipo, texto_nombre, imagen_o_texto;
    Timestamp fecha;

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTexto_nombre() {
        return texto_nombre;
    }

    public void setTexto_nombre(String texto_nombre) {
        this.texto_nombre = texto_nombre;
    }

    public String getImagen_o_texto() {
        return imagen_o_texto;
    }

    public void setImagen_o_texto(String imagen_o_texto) {
        this.imagen_o_texto = imagen_o_texto;
    }
}
