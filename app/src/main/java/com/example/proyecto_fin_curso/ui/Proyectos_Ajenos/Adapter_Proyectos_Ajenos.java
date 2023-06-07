package com.example.proyecto_fin_curso.ui.Proyectos_Ajenos;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Proyecto;
import com.example.proyecto_fin_curso.Clases.Tarea;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.ui.Adapter_Tareas;
import com.example.proyecto_fin_curso.ui.Ver_proyecto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Adapter_Proyectos_Ajenos extends RecyclerView.Adapter<Adapter_Proyectos_Ajenos .ViewHolder>{

    private List<Proyecto> proyectos;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Activity activity;

    public Adapter_Proyectos_Ajenos(List<Proyecto> proyectos,Activity activity){

        this.proyectos=proyectos;
        this.activity=activity;
    }

    @NonNull
    @Override
    public Adapter_Proyectos_Ajenos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_proyectos, parent,false);


        return new Adapter_Proyectos_Ajenos.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull Adapter_Proyectos_Ajenos.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Proyecto proyecto = proyectos.get(position);
        holder.nombre.setText(proyecto.getNombre());
        holder.fecha_fin.setText(proyecto.getFecha_fin());
        Uri uri = Uri.parse(proyecto.getImagen());
        Glide.with(holder.imagen).load(uri).into(holder.imagen);



        //Para leer la cantidad de tareas completadas

        //Lectura 2
        firestore.collection("Proyectos").document(proyecto.getCodigo_acceso()).collection("Tareas").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());
                        }
                        System.out.println("Tareas totales: "+ snab.size());
                        holder.tareas.setText(snab.size()+"");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        //Lectura 3
                        firestore.collection("Proyectos").document(proyecto.getCodigo_acceso()).collection("Tareas").whereEqualTo("estado",true).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot: snab){
                                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());
                                        }
                                        System.out.println("Tareas totales: "+ snab.size());
                                        String tareasCompletas=snab.size()+"";

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.tareas_completadas.setText(tareasCompletas);
                                                holder.barra_tareas_completas.setMax(Integer.parseInt(holder.tareas.getText().toString()));
                                                holder.barra_tareas_completas.setProgress(Integer.parseInt(tareasCompletas),true);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }
        ).start();

        String id_proyecto=proyectos.get(position).getCodigo_acceso();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context= view.getContext();
                Intent intent = new Intent(context, Ver_proyecto.class);
                intent.putExtra("id_proyecto",id_proyecto);
                context.startActivity(intent);
            }
        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }



    @Override
    public int getItemCount() {
        return proyectos.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,
                tareas,
                tareas_completadas,
                fecha_fin;

        ShapeableImageView imagen;
        ProgressBar barra_tareas_completas;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreProyectoView);
            tareas = itemView.findViewById(R.id.total_tareas_View);
            tareas_completadas = itemView.findViewById(R.id.tareas_completadas_View);
            fecha_fin = itemView.findViewById(R.id.fechaFinView);
            imagen = itemView.findViewById(R.id.imagen_etiqueta_proyecto);
            barra_tareas_completas = itemView.findViewById(R.id.progreso_tarea_View);
        }
    }
}
