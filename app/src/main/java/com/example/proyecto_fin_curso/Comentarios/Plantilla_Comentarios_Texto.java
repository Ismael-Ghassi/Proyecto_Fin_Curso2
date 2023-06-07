package com.example.proyecto_fin_curso.Comentarios;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_fin_curso.R;

import java.util.Date;

public class Plantilla_Comentarios_Texto  extends RecyclerView.ViewHolder {
    TextView nombre, comentario;
    TextView fecha;

    public Plantilla_Comentarios_Texto(@NonNull View itemView) {
        super(itemView);
        nombre = itemView.findViewById(R.id.nombre_lista_comentarios_texto);
        comentario = itemView.findViewById(R.id.comentario_lista_comentarios_texto);
        fecha = itemView.findViewById(R.id.fecha_lista_comentarios_texto);
    }

    public void bind(String nombre, String comentario, String fecha){
        this.nombre.setText(nombre);
        this.comentario.setText(comentario);
        this.fecha.setText(fecha);
    }
}
