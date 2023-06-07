package com.example.proyecto_fin_curso.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Tarea;
import com.example.proyecto_fin_curso.R;

import java.util.List;

public class Adapter_Tareas extends RecyclerView.Adapter<Adapter_Tareas.ViewHolder> {
    private List<Tarea> tareas;
    private String id_proyecto;

    public Adapter_Tareas(List<Tarea> tareas, String id_proyecto){
        this.tareas=tareas; this.id_proyecto=id_proyecto;
    }

    @NonNull
    @Override
    public Adapter_Tareas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tareas, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Tareas.ViewHolder holder, int position) {

        Tarea tarea = tareas.get(position);
        holder.nombre.setText(tarea.getNombre_tarea());

        if(tarea.getCorreo_encargado().length()>2){
            holder.persona_asignada.setText(tarea.getCorreo_encargado());
        }else{
            holder.persona_asignada.setText(" ");
        }

        if(tarea.isEstado()==false){
            holder.estado.setText("No completado");
            holder.estado.setTextColor(Color.BLACK);
        }else{
            holder.estado.setText("Completado");
            holder.estado.setTextColor(Color.GREEN);
        }

        Uri uri = Uri.parse(tarea.getImagen());
        Glide.with(holder.imagen).load(uri).into(holder.imagen);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context= view.getContext();
                Intent intent = new Intent(context,Ver_Tarea.class);

                intent.putExtra("id",tarea.getId());
                intent.putExtra("id_proyecto",id_proyecto);
                intent.putExtra("nombre",tarea.getNombre_tarea());
                intent.putExtra("descripcion",tarea.getDescripcion());
                intent.putExtra("estado",tarea.isEstado());
                intent.putExtra("imagen",tarea.getImagen());
                intent.putExtra("correo_admin",tarea.getCorreo_admin());
                intent.putExtra("correo_encargado",tarea.getCorreo_encargado());
                intent.putExtra("correo_ultima_mod",tarea.getCorreo_encargado());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre;
        TextView persona_asignada;
        TextView estado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imagen_etiqueta_tarea);
            nombre =itemView.findViewById(R.id.nombreTarea);
            persona_asignada = itemView.findViewById(R.id.persona_asignada_tarea);
            estado = itemView.findViewById(R.id.estado_tarea);


        }
    }
}
