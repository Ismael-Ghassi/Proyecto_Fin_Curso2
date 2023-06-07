package com.example.proyecto_fin_curso.ui.Proyectos_Ajenos;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.proyecto_fin_curso.Clases.Proyecto;
import com.example.proyecto_fin_curso.Clases.Tarea;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.ui.Proyectos.ProyectosUsuarioAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProjectosAjenosFragment extends Fragment {

    RecyclerView recyclerProyectos;
    Adapter_Proyectos_Ajenos adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ProgressBar carga_proyectos;


    List<Proyecto> proyectosList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        recyclerProyectos = root.findViewById(R.id.recyler_Proyectos_Ajenos);
        carga_proyectos = root.findViewById(R.id.progressBar_carga_proyectos_ajenos);
        swipeRefreshLayout=root.findViewById(R.id.swipeRefreshLayout_proyectos_ajenos);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerProyectos.setVisibility(View.INVISIBLE);
                        obtenerProyectos();


                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000); // 2 segundos de retraso
            }
        });


        obtenerProyectos();
        System.out.println("Este codigo se esta ejecutando");

        return root;
    }





    public void obtenerProyectos() {
        recyclerProyectos.setLayoutManager(new LinearLayoutManager(getContext()));
        proyectosList = new ArrayList<>();
        adapter = new Adapter_Proyectos_Ajenos(proyectosList, getActivity());
        firestore.collection("Usuarios").document(mAuth.getUid()).collection("Proyectos_Ajenos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {


            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snab) {
                    Log.d(TAG, "onSuccess: " + snapshot.getData().toString());

                }


                for (int i = 0; i < snab.size(); i++) {

                    System.out.println("Codigo ver " + snab.get(i).getString("id"));
                    firestore.collection("Proyectos").document(snab.get(i).getString("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Proyecto proyecto = new Proyecto();
                            proyecto.setNombre(documentSnapshot.getData().get("nombre").toString());
                            proyecto.setCodigo_acceso(documentSnapshot.getData().get("codigo_acceso").toString());
                            proyecto.setImagen(documentSnapshot.getData().get("imagen").toString());
                            proyecto.setFecha_fin(documentSnapshot.getData().get("fecha_fin").toString());

                            System.out.println("Tamaño " + proyectosList.size());
                            proyectosList.add(proyecto);
                            recyclerProyectos.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerProyectos.setVisibility(View.VISIBLE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            System.err.println("Ojo que esta fallando");
                        }
                    });
                    System.out.println("Todo cargado " + proyectosList.size());
                }



            }
        });

    }
}