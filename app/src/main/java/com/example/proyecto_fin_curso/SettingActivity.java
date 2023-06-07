package com.example.proyecto_fin_curso;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Usuario;
import com.example.proyecto_fin_curso.Login.Iniciar_Sesion;
import com.example.proyecto_fin_curso.Login.MainActivity;
import com.example.proyecto_fin_curso.ui.Usuario.Perfil_Usuario;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    Button cerrar_sesion;
    Button borrar_cuenta;


    //Informacion usuario
    ImageView imagen;
    FloatingActionButton seleccionar_imagen;
    EditText nombre_usuario;
    Button guardar;



    Uri uri;
    FirebaseFirestore firestore;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Ajustes");
        setContentView(R.layout.activity_setting);


        cerrar_sesion=findViewById(R.id.cerrar_sesion);
        borrar_cuenta=findViewById(R.id.borrar_cuenta);
        mAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();
        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("¿Quieres cerrar tu sesión?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mAuth.signOut();
                                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });



        borrar_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("¿Seguro que quieres borrar tu cuenta?\n"+"Tus datos serán borrados de la base de datos.")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        eliminarCuenta();
                                    }
                                }).start();


                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });



        imagen= findViewById(R.id.imagen_Foto_Usuario);
        seleccionar_imagen = findViewById(R.id.seleccionar_Imagen_Usuario);
        nombre_usuario=findViewById(R.id.nombre_usuario);
        guardar=findViewById(R.id.guardar_modificaciones_usuario);

        mAuth=FirebaseAuth.getInstance();
        //storageReference= FirebaseStorage.getInstance().getReference();
        //bdFirebase= FirebaseFirestore.getInstance();
        comprobarDatos();

        uri=mAuth.getCurrentUser().getPhotoUrl();

        seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {

                                                      ImagePicker.with(SettingActivity.this)
                                                              .crop()
                                                              .compress(1024)
                                                              .maxResultSize(1080, 1080)
                                                              .start();
                                                  }
                                              }




        );

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre_usuario.getText().length()>0){
                    modificarDatos();
                }else{
                    Toast.makeText(SettingActivity.this, "El nombre de usuario es obligatorio.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        uri=data.getData();
        imagen.setImageURI(uri);
    }

    private void comprobarDatos(){
        if(mAuth.getCurrentUser().getDisplayName().isEmpty() || mAuth.getCurrentUser().getDisplayName().length()==0){
            String [] separacion = mAuth.getCurrentUser().getEmail().split("@");
            nombre_usuario.setText(separacion[0]);

        }else{
            nombre_usuario.setText(mAuth.getCurrentUser().getDisplayName());
        }


        String rutaFoto=mAuth.getCurrentUser().getPhotoUrl()+"";
        if(rutaFoto.length()<=4){
            Glide.with(imagen.getContext()).load(R.drawable.ic_menu_camera).into(imagen);
        }else{
            Glide.with(imagen.getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).into(imagen);
        }
    }

    private void modificarDatos(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando datos");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre_usuario.getText().toString())
                .setPhotoUri(Uri.parse(String.valueOf(uri)))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(SettingActivity.this, "Datos actualizados con exito.", Toast.LENGTH_SHORT).show();
                            modificarUsuarioBD();
                        }
                    }
                });

        progressDialog.dismiss();


    }

    private void modificarUsuarioBD(){
        FirebaseFirestore bdFirebase = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("correo_electronico",mAuth.getCurrentUser().getEmail());
        map.put("nombre",nombre_usuario.getText().toString());

        bdFirebase.collection("Usuarios").document(mAuth.getUid()).set(map);

    }

    public void eliminarCuenta(){

        firestore.collection("Proyectos")
                .whereEqualTo("correo_admin", mAuth.getCurrentUser()
                        .getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab_principal = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab_principal){
                            eliminarProyecto(snapshot.getId());
                        }

                        firestore.collection("Usuarios")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("Proyectos_Ajenos")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> snab_ajena = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot snapshot: snab_ajena){
                                    firestore.collection("Usuarios")
                                            .document(mAuth.getCurrentUser().getUid())
                                            .collection("Proyectos_Ajenos")
                                            .document(snapshot.getId())
                                            .delete();
                                }

                                firestore.collection("Usuarios")
                                        .document(mAuth.getCurrentUser().getUid())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                mAuth.getCurrentUser().delete();
                                                Intent intent = new Intent(SettingActivity.this,Iniciar_Sesion.class);
                                                startActivity(intent);
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                firestore.collection("Usuarios")
                                        .document(mAuth.getCurrentUser().getUid())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                mAuth.getCurrentUser().delete();
                                                Intent intent = new Intent(SettingActivity.this,Iniciar_Sesion.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Codigo eliminar cuenta
                    }
                });
    }

    public void eliminarProyecto(String id){

        firestore.collection("Proyectos")
                .document(id).
                collection("Informacion_Usuarios").
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                            //Se elimina la informacion de los usuarios en el proyecto
                            firestore.collection("Proyectos")
                                    .document(id).
                                    collection("Informacion_Usuarios").
                                    document(snapshot.getId())
                                    .delete().addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            //Se elimina el proyecto de las entradas de todos los usuarios que estaban inscritos.
                                            firestore.collection("Usuarios")
                                                    .document(snapshot.getId())
                                                    .collection("Proyectos_Ajenos")
                                                    .document(id)
                                                    .delete();
                                        }
                                    });


                        }

                        System.out.println("He salido");

                        firestore.collection("Proyectos")
                                .document(id).
                                collection("Tareas").
                                get().addOnCompleteListener( task -> {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        firestore.collection("Proyectos")
                                                .document(id).
                                                collection("Tareas").
                                                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        System.out.println("Aqui estoy fallando");

                                                        List<DocumentSnapshot> snab2 = queryDocumentSnapshots.getDocuments();
                                                        for (DocumentSnapshot snapshot: snab2){
                                                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                                                            firestore.collection("Proyectos")
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

                                                                                firestore.collection("Proyectos")
                                                                                        .document(id).
                                                                                        collection("Tareas").
                                                                                        document(snapshot.getId())
                                                                                        .collection("Comentarios").
                                                                                        document(snapshot2.getId()).
                                                                                        delete();
                                                                            }

                                                                            //Se elimina la tarea
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).
                                                                                    collection("Tareas").
                                                                                    document(snapshot.getId()).
                                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            firestore.collection("Proyectos")
                                                                                                    .document(id)
                                                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                                            //carga.setVisibility(View.INVISIBLE);

                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                            //Se elimina la tarea
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).
                                                                                    collection("Tareas").
                                                                                    document(snapshot.getId()).
                                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            firestore.collection("Proyectos")
                                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                                            //carga.setVisibility(View.INVISIBLE);

                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            firestore.collection("Proyectos")
                                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                                            //carga.setVisibility(View.INVISIBLE);
                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                                            firestore.collection("Usuarios")
                                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }else{
                                        //Si no existen tareas.
                                        System.out.println("No existen tareas");
                                        firestore.collection("Proyectos")
                                                .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //scrollView.setVisibility(View.VISIBLE);
                                                        //carga.setVisibility(View.INVISIBLE);

                                                        firestore.collection("Usuarios")
                                                                .document(mAuth.getCurrentUser().getUid())
                                                                .collection("Proyectos").document(id).delete();

                                                        firestore.collection("Usuarios")
                                                                .document(mAuth.getCurrentUser().getUid())
                                                                .collection("Proyectos_Ajenos").document(id).delete();
                                                    }
                                                });
                                    }
                                });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //Todo seguir por aqui
                        firestore.collection("Proyectos")
                                .document(id).
                                collection("Tareas").
                                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> snab2 = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot: snab2){
                                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                                            firestore.collection("Proyectos")
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

                                                                firestore.collection("Proyectos")
                                                                        .document(id).
                                                                        collection("Tareas").
                                                                        document(snapshot.getId())
                                                                        .collection("Comentarios").
                                                                        document(snapshot2.getId()).
                                                                        delete();
                                                            }

                                                            //Se elimina la tarea
                                                            firestore.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                            //carga.setVisibility(View.INVISIBLE);

                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                            //carga.setVisibility(View.INVISIBLE);
                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            //Se elimina la tarea
                                                            firestore.collection("Proyectos")
                                                                    .document(id).
                                                                    collection("Tareas").
                                                                    document(snapshot.getId()).
                                                                    delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                            //carga.setVisibility(View.INVISIBLE);
                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            firestore.collection("Proyectos")
                                                                                    .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            //scrollView.setVisibility(View.VISIBLE);
                                                                                            //carga.setVisibility(View.INVISIBLE);
                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos").document(id).delete();

                                                                                            firestore.collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                                                    .collection("Proyectos_Ajenos").document(id).delete();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        firestore.collection("Proyectos")
                                                .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //scrollView.setVisibility(View.VISIBLE);
                                                        //carga.setVisibility(View.INVISIBLE);

                                                        firestore.collection("Usuarios")
                                                                .document(mAuth.getCurrentUser().getUid())
                                                                .collection("Proyectos").document(id).delete();

                                                        firestore.collection("Usuarios")
                                                                .document(mAuth.getCurrentUser().getUid())
                                                                .collection("Proyectos_Ajenos").document(id).delete();


                                                    }
                                                });
                                    }
                                });

                    }
                });

    }
}