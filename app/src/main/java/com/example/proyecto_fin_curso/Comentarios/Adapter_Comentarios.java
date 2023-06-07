package com.example.proyecto_fin_curso.Comentarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_fin_curso.Clases.Comentarios_Tarea;
import com.example.proyecto_fin_curso.Dialogos.Dialog_Imagenes;
import com.example.proyecto_fin_curso.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;

public class Adapter_Comentarios extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM1 = 1;
    private static final int TYPE_ITEM2 = 2;

    List<Comentarios_Tarea> comentarios;
    FragmentManager fragmentManager;
    public Adapter_Comentarios(List<Comentarios_Tarea> comentarios, FragmentManager fragmentManager){
        this.comentarios=comentarios;
        this.fragmentManager=fragmentManager;
    }

    @Override
    public int getItemViewType(int position) {
        if (comentarios.get(position).getTipo().equals("texto")) {
            return TYPE_ITEM1;
        } else {
            return TYPE_ITEM2;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_ITEM1:
                view = inflater.inflate(R.layout.lista_comentarios_texto, parent, false);
                return new Plantilla_Comentarios_Texto(view);
            case TYPE_ITEM2:
                view = inflater.inflate(R.layout.lista_comentarios_imagen, parent, false);
                return new Plantilla_Comentarios_Imagen(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String nombre=comentarios.get(position).getTexto_nombre();
        Timestamp fecha=comentarios.get(position).getFecha();
        String tipo=comentarios.get(position).getTipo();
        String imagen_o_comentario=comentarios.get(position).getImagen_o_texto();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String formatoDef=formatoFecha.format(fecha.toDate());

        switch (holder.getItemViewType()) {
            case TYPE_ITEM1:


                Plantilla_Comentarios_Texto plantilla1 = (Plantilla_Comentarios_Texto) holder;
                plantilla1.bind(nombre,imagen_o_comentario, formatoDef);
                break;
            case TYPE_ITEM2:
                Plantilla_Comentarios_Imagen platilla2 = (Plantilla_Comentarios_Imagen) holder;
                platilla2.bind(nombre,imagen_o_comentario,formatoDef);

                platilla2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("Aquii si entra");

                        Dialog_Imagenes dialogFragment = Dialog_Imagenes.newInstance(imagen_o_comentario);
                        dialogFragment.show(fragmentManager, "dialog");
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Invalid view type: " + holder.getItemViewType());
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }
}
