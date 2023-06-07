package com.example.proyecto_fin_curso.Comentarios;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.R;

public class Plantilla_Comentarios_Imagen extends RecyclerView.ViewHolder {
    TextView nombre,fecha;
    ImageView imagen;

    public Plantilla_Comentarios_Imagen(@NonNull View itemView) {
        super(itemView);
        nombre = itemView.findViewById(R.id.nombre_lista_comentarios_imagen);
        imagen = itemView.findViewById(R.id.imagen_lista_comentarios_imagen);
        fecha = itemView.findViewById(R.id.fecha_lista_comentarios_imagen);
    }

    public void bind(String nombre, String imagen_url, String fecha){
        this.nombre.setText(nombre);
        this.fecha.setText(fecha);
        Uri uri = Uri.parse(imagen_url);
        Glide.with(this.imagen).load(uri).into(this.imagen);

    }
}
