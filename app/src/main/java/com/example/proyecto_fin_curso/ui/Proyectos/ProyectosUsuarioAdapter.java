package com.example.proyecto_fin_curso.ui.Proyectos;

import static android.content.ContentValues.TAG;

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
import com.example.proyecto_fin_curso.Clases.Proyectos_Usuario;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.ui.Ver_proyecto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.DatabaseMetaData;
import java.util.List;

public class ProyectosUsuarioAdapter extends FirestoreRecyclerAdapter<Proyectos_Usuario, ProyectosUsuarioAdapter.ViewHolder> {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    //FirebaseAuth mAuth= FirebaseAuth.getInstance();
    Activity activity;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProyectosUsuarioAdapter(@NonNull FirestoreRecyclerOptions<Proyectos_Usuario> options, Activity activity) {
        super(options);
        this.activity=activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProyectosUsuarioAdapter.ViewHolder holder, int position, @NonNull Proyectos_Usuario model) {

        //Lee de la coleccion proyectos a partir de los id de los proyectos del cliente
        System.err.println(model.getId()+ " Error encontardo error ");
        firestore.collection("Proyectos").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                System.out.println("Nombre: "+documentSnapshot.getData().get("nombre").toString());
                holder.nombre.setText(documentSnapshot.getData().get("nombre").toString());
                holder.fecha_fin.setText(documentSnapshot.getData().get("fecha_fin").toString());
                Uri uri = Uri.parse(documentSnapshot.getString("imagen"));
                Glide.with(holder.imagen).load(uri).into(holder.imagen);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Lectura 2
        firestore.collection("Proyectos").document(model.getId()).collection("Tareas").get()
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
                                        firestore.collection("Proyectos").document(model.getId()).collection("Tareas").whereEqualTo("estado",true).get()
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



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context= view.getContext();
                Intent intent = new Intent(context, Ver_proyecto.class);
                intent.putExtra("id_proyecto",model.getId());
                context.startActivity(intent);
            }
        });






    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_proyectos, parent, false);
        return new ViewHolder(v);
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
