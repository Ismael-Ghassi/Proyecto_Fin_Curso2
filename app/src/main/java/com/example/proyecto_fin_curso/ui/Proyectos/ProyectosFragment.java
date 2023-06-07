package com.example.proyecto_fin_curso.ui.Proyectos;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_fin_curso.Clases.Proyectos_Usuario;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.databinding.FragmentGalleryBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProyectosFragment extends Fragment {

    //VARIABLES
    RecyclerView recyclerProyectos;
    ProyectosUsuarioAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ProgressBar carga_proyectos;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        mAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        System.out.println("Este es el id "+mAuth.getUid());
        firestore.collection("Usuarios").document(mAuth.getUid()).collection("Proyectos").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());
                        }
                        Proyectos_Usuario p = new Proyectos_Usuario();
                        //System.out.println("Resuelto "+snab.get(0).getData().get("id").toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });




        recyclerProyectos= root.findViewById(R.id.recyler_Proyectos);
        carga_proyectos=root.findViewById(R.id.progressBar_carga_proyectos);
        /*
        Sirve para saber cuando a terminado de cargar
         */
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    carga_proyectos.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        };

        recyclerProyectos.addOnScrollListener(scrollListener);


//----------------------------------------------------------------------------------------
        recyclerProyectos.setLayoutManager(new LinearLayoutManager(root.getContext()));

        Query query = firestore.collection("Usuarios").document(mAuth.getUid()).collection("Proyectos");

        FirestoreRecyclerOptions<Proyectos_Usuario> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Proyectos_Usuario>().setQuery(query, Proyectos_Usuario.class).build();

        adapter = new ProyectosUsuarioAdapter(firestoreRecyclerOptions,getActivity());
        adapter.notifyDataSetChanged();
        recyclerProyectos.setAdapter(adapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //Genera errores
    /*
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
     */

}