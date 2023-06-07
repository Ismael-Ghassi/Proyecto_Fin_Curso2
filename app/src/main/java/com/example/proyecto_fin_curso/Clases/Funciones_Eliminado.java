package com.example.proyecto_fin_curso.Clases;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class Funciones_Eliminado {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth=  FirebaseAuth.getInstance();
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

    public void eliminarCuenta(){

        firestoreDb.collection("Proyectos")
                .whereEqualTo("correo_admin", mAuth.getCurrentUser()
                .getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snab_principal = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot: snab_principal){
                    eliminarProyecto(snapshot.getId());
                }
                //Codigo eliminar cuenta

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Codigo eliminar cuenta
                    }
                });
    }



    public void eliminarProyecto(String id){
        firestoreDb.collection("Proyectos")
                .document(id).
                collection("Informacion_Usuarios").
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                            //Se elimina la informacion de los usuarios en el proyecto
                            firestoreDb.collection("Proyectos")
                                    .document(id).
                                    collection("Informacion_Usuarios").
                                    document(snapshot.getId())
                                    .delete();
                        }

                        firestoreDb.collection("Proyectos")
                                .document(id).
                                collection("Tareas").
                                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> snab2 = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot: snab2){
                                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                                            firestoreDb.collection("Proyectos")
                                                    .document(id).
                                                    collection("Tareas").
                                                    document(snapshot.getId())
                                                    .collection("Comentarios")
                                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> snab3 = queryDocumentSnapshots.getDocuments();
                                                            //Se eliminan los comentarios
                                                            for (DocumentSnapshot snapshot2: snab3){
                                                                Log.d(TAG,"onSuccess: "+snapshot2.getData().toString() );

                                                                firestoreDb.collection("Proyectos")
                                                                        .document(id).
                                                                        collection("Tareas").
                                                                        document(snapshot.getId())
                                                                        .collection("Comentarios").
                                                                        document(snapshot2.getId()).
                                                                        delete();
                                                            }

                                                            //Se elimina la tarea
                                                            firestoreDb.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id)
                                                                                    .delete();
                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            //Se elimina la tarea
                                                            firestoreDb.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        firestoreDb.collection("Proyectos")
                                .document(id).
                                collection("Tareas").
                                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> snab2 = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot: snab2){
                                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                                            firestoreDb.collection("Proyectos")
                                                    .document(id).
                                                    collection("Tareas").
                                                    document(snapshot.getId())
                                                    .collection("Comentarios")
                                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> snab3 = queryDocumentSnapshots.getDocuments();
                                                            //Se eliminan los comentarios
                                                            for (DocumentSnapshot snapshot2: snab3){
                                                                Log.d(TAG,"onSuccess: "+snapshot2.getData().toString() );

                                                                firestoreDb.collection("Proyectos")
                                                                        .document(id).
                                                                        collection("Tareas").
                                                                        document(snapshot.getId())
                                                                        .collection("Comentarios").
                                                                        document(snapshot2.getId()).
                                                                        delete();
                                                            }

                                                            //Se elimina la tarea
                                                            firestoreDb.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            //Se elimina la tarea
                                                            firestoreDb.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            firestoreDb.collection("Proyectos")
                                                                                    .document(id).delete();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        firestoreDb.collection("Proyectos")
                                                .document(id).delete();
                                    }
                                });

                    }
                });

    }

    public void  salirProyecto(String id){
        firestoreDb.collection("Proyecos").document(id).
                collection("Informacion_Usuarios").
                document(mAuth.getCurrentUser().getUid()).
                delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firestoreDb.collection("Usuarios").
                                document(mAuth.getCurrentUser().getUid()).
                                collection("Proyectos_Ajenos").
                                document(id).delete();
                    }
                });
    }


    public void eliminarTarea(String id_proyecto, String id_tarea){
        firestoreDb.collection("Proyectos").
                document(id_proyecto).
                collection("Tareas").
                document(id_tarea).
                collection("Comentarios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                            firestoreDb.collection("Proyectos").
                                    document(id_proyecto).
                                    collection("Tareas").
                                    document(id_tarea).
                                    collection("Comentarios").document(snapshot.getId()).delete();
                        }

                        firestoreDb.collection("Proyectos").
                                document(id_proyecto).
                                collection("Tareas").
                                document(id_tarea).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Intent
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        firestoreDb.collection("Proyectos").
                                document(id_proyecto).
                                collection("Tareas").
                                document(id_tarea).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Intent
                                    }
                                });
                    }
                });
    }
}
