package com.example.proyecto_fin_curso.Asignar;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_fin_curso.Clases.Proyecto;
import com.example.proyecto_fin_curso.Clases.Usuario;
import com.example.proyecto_fin_curso.Login.MainActivity;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.SettingActivity;
import com.example.proyecto_fin_curso.ui.Proyectos_Ajenos.Adapter_Proyectos_Ajenos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_Asignar_Usuario extends RecyclerView.Adapter<Adapter_Asignar_Usuario.ViewHolder>{
    private List<Usuario> usuarios;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String id_proyecto, id_tarea="";
    Activity activity;

    public Adapter_Asignar_Usuario(List<Usuario> usuarios,Activity activity,String id_proyecto, String id_tarea){

        this.usuarios=usuarios;
        this.activity=activity;
        this.id_tarea=id_tarea;
        this.id_proyecto=id_proyecto;
    }


    @NonNull
    @Override
    public Adapter_Asignar_Usuario.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_usuarios, parent,false);


        return new Adapter_Asignar_Usuario.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Adapter_Asignar_Usuario.ViewHolder holder, int position) {

        Usuario usuario = usuarios.get(position);
        holder.nombre.setText(usuario.getNombre());
        holder.correo.setText(usuario.getCorreo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("¿Quieres asignar a "+ usuario.getNombre()+" como encargado de esta tarea?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("correo_encargado",usuario.getCorreo());

                                System.out.println("Aqui esta el error "+id_tarea);
                                                firestore.collection("Proyectos")
                                                        .document(id_proyecto)
                                                        .collection("Tareas")
                                                        .document(id_tarea)
                                                        .update(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(activity, "Usuario asignado correctamente.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(activity, "No se ha podido asiganar al usuario. Intentelo mas tarde.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });



                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });

    }


    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,
                correo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.lista_usuarios_nombre);
            correo = itemView.findViewById(R.id.lista_usuarios_correo);
        }
    }
}
