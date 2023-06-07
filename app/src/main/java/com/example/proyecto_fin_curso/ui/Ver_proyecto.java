package com.example.proyecto_fin_curso.ui;

import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Tarea;
import com.example.proyecto_fin_curso.Crear_proyecto;
import com.example.proyecto_fin_curso.Dialogos.Dialog_Imagenes;
import com.example.proyecto_fin_curso.Login.MainActivity;
import com.example.proyecto_fin_curso.Pantalla_home;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.SettingActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ver_proyecto extends AppCompatActivity {

    ImageView imagenProyecto;
    EditText nombre_proyecto,texto_descripcion;
    TextView tareas_completadas;
    ProgressBar barra_tareas;
    TextView estado_proyecto;
    Button insertar_tarea;
    RecyclerView tareas_proyecto;
    private FirebaseAuth mAuth;

    private FirebaseStorage storage = FirebaseStorage.getInstance();


    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ScrollView scrollView;
    ProgressBar carga;

    String id_proyecto;

    //Datos tarea
    ImageView imagen_tarea;
    FloatingActionButton seleccionar_imagen_tarea;

    EditText nombre_tarea,texto_descripcion_tarea;

    Button crear_tarea;

    Uri uri=Uri.parse("");
    String url_imagen_tarea_bd;

    BottomSheetDialog dialog;

    ProgressBar carga_subida_tarea;
    ScrollView scrollView_crear_tarea;
    List<Tarea> tareasList;
    Adapter_Tareas adapter_tareas;

    TextView codigo_animado;
    ImageView copiar_codigo;

    CardView marco_codigo;

    Dialog_Imagenes dialogFragment;

    ImageView eliminar_proyecto;
    String correo_admin;

    private SwipeRefreshLayout swipeRefreshLayout;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_proyecto);

        imagenProyecto = findViewById(R.id.imagen_proyecto);
        nombre_proyecto = findViewById(R.id.titulo_proyecto);
        texto_descripcion = findViewById(R.id.texto_descripcion_proyecto);
        tareas_completadas = findViewById(R.id.tareas_completadas_proyecto);
        barra_tareas = findViewById(R.id.progreso_tarea_Ver_Proyecto);
        estado_proyecto = findViewById(R.id.estado_proyecto);
        insertar_tarea = findViewById(R.id.insertar_tarea);
        tareas_proyecto = findViewById(R.id.recyclerView_tareas_proyecto);
        scrollView=findViewById(R.id.scroll_verProyecto);
        carga=findViewById(R.id.progressBar_carga_ver_proyecto);
        codigo_animado=findViewById(R.id.codigo_animado);
        copiar_codigo = findViewById(R.id.copiar_codigo);
        marco_codigo =findViewById(R.id.carta_marco);
        eliminar_proyecto= findViewById(R.id.eliminar_proyecto);

        nombre_proyecto.setFocusable(false);
        texto_descripcion.setFocusable(false);

        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            scrollView.setVisibility(View.INVISIBLE);
                            obtenerDatosProyecto();


                        scrollView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000); // 2 segundos de retraso
            }
        });




        mAuth = FirebaseAuth.getInstance();
        obtenerDatosProyecto();




        insertar_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                formulario_CrearTarea();
            }
        });

        copiar_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Crear un ClipData con el texto a copiar
                ClipData clipData = ClipData.newPlainText("Texto copiado", codigo_animado.getText());

                // Copiar el texto al portapapeles
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(Ver_proyecto.this, "Código copiado.", Toast.LENGTH_SHORT).show();
            }
        });

        imagenProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        //Eliminar o salir del proyecto
        eliminar_proyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser().getEmail().contains(correo_admin)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ver_proyecto.this);
                    builder.setMessage("¿Quieres eliminar el proyecto?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            scrollView.setVisibility(View.INVISIBLE);
                            carga.setVisibility(View.VISIBLE);
                            eliminarProyecto(id_proyecto);

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ver_proyecto.this);
                    builder.setMessage("¿Quieres salir del proyecto?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            scrollView.setVisibility(View.INVISIBLE);
                            carga.setVisibility(View.VISIBLE);
                            salirProyecto(id_proyecto);

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }


            }
        });






    }




    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        uri=data.getData();
        imagen_tarea.setImageURI(uri);
    }


    public void obtenerDatosProyecto(){



        Bundle extras = getIntent().getExtras();
        id_proyecto = extras.getString("id_proyecto");
        codigo_accesoAnimado(id_proyecto);
        firestore.collection("Proyectos").document(id_proyecto).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                nombre_proyecto.setText(documentSnapshot.getData().get("nombre").toString());
                Ver_proyecto.super.setTitle(documentSnapshot.getData().get("nombre").toString());
                correo_admin=documentSnapshot.getData().get("correo_admin").toString();
                //fecha_fin.setText(documentSnapshot.getData().get("fecha_fin").toString());
                Uri uri = Uri.parse(documentSnapshot.getData().get("imagen").toString());
                dialogFragment = Dialog_Imagenes.newInstance(documentSnapshot.getData().get("imagen").toString());

                Glide.with(imagenProyecto).load(uri).into(imagenProyecto);
                texto_descripcion.setText(documentSnapshot.getData().get("comentario").toString());

                boolean estado_proyectoI=documentSnapshot.getBoolean("estado");
                if(estado_proyectoI==false){
                    estado_proyecto.setTextColor(Color.BLACK);
                    estado_proyecto.setText("No completado");
                }else{
                    estado_proyecto.setTextColor(Color.GREEN);
                    estado_proyecto.setText("Completado");
                }



                System.out.println("correos: "+mAuth.getCurrentUser().getEmail()+"  "+documentSnapshot.getData().get("correo_admin").toString());
                if(!(mAuth.getCurrentUser().getEmail().contains(documentSnapshot.getData().get("correo_admin").toString()))){
                    insertar_tarea.setVisibility(View.INVISIBLE);
                    copiar_codigo.setVisibility(View.INVISIBLE);
                    codigo_animado.setVisibility(View.INVISIBLE);
                    marco_codigo.setVisibility(View.INVISIBLE);
                }

                obtenerTareas();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




        //Cuenta las tareas totales
        firestore.collection("Proyectos").document(id_proyecto).collection("Tareas").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());
                        }
                        System.out.println("Tareas totales aqui: "+ snab.size());
                        barra_tareas.setMax(snab.size());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                firestore.collection("Proyectos").document(id_proyecto).collection("Tareas").whereEqualTo("estado",true).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<DocumentSnapshot> snab2 = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot snapshot: snab2){
                                                    Log.d(TAG,"onSuccess: "+snapshot.getData().toString());
                                                }
                                                System.out.println("Tareas totales ver: "+ snab2.size());
                                                String tareasCompletas=snab2.size()+"";

                                                try {
                                                    Thread.sleep(200);
                                                } catch (InterruptedException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                Ver_proyecto.this.runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                tareas_completadas.setText(tareasCompletas);
                                                                barra_tareas.setProgress(snab2.size(),true);

                                                                if(snab.size()==snab2.size() && estado_proyecto.getText()=="No completado"){
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    map.put("estado",true);
                                                                    firestore.collection("Proyectos").document(id_proyecto).update(map);
                                                                    estado_proyecto.setTextColor(Color.GREEN);
                                                                    estado_proyecto.setText("Completado");

                                                                } else if (snab.size()!=snab2.size() && estado_proyecto.getText()=="Completado") {
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    map.put("estado",false);
                                                                    firestore.collection("Proyectos").document(id_proyecto).update(map);
                                                                    estado_proyecto.setTextColor(Color.BLACK);
                                                                    estado_proyecto.setText("No completado");
                                                                }

                                                            }
                                                        }
                                                );
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }).start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                carga.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);


    }

    @SuppressLint("MissingInflatedId")
    public void formulario_CrearTarea(){
        dialog= new BottomSheetDialog(Ver_proyecto.this,R.style.BottomSheetDialogTheme);

        View vista = LayoutInflater.from(getApplicationContext()).inflate(R.layout.crear_tarea,null);

        imagen_tarea= vista.findViewById(R.id.imagen_crearTarea);
        seleccionar_imagen_tarea= vista.findViewById(R.id.seleccionar_Imagen_Tarea);
        nombre_tarea= vista.findViewById(R.id.texto_nombre_tarea);
        texto_descripcion_tarea= vista.findViewById(R.id.texto_descripcion_tarea);
        crear_tarea= vista.findViewById(R.id.crear_tarea2);
        carga_subida_tarea=vista.findViewById(R.id.progressBar_carga_crear_tarea);
        scrollView_crear_tarea=vista.findViewById(R.id.idScrollView_crearTarea);


        dialog.setCancelable(true);
        dialog.setContentView(vista);
        dialog.show();

        seleccionar_imagen_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(Ver_proyecto.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        crear_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subirTodo();

            }
        });


    }

    public void subirDatosTarea(){
        Map<String, Object> map = new HashMap<>();
        map.put("id","");
        map.put("nombre",nombre_tarea.getText().toString());
        map.put("correo_admin",mAuth.getCurrentUser().getEmail().toString());
        map.put("correo_encargado","");
        map.put("imagen",url_imagen_tarea_bd);
        map.put("descripcion",texto_descripcion_tarea.getText().toString());
        map.put("estado",false);
        map.put("imagen_completado","");
        map.put("correo_ultima_mod","");


        firestore.collection("Proyectos").document(id_proyecto).collection("Tareas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Map<String, Object> map2 = new HashMap<>();
                map2.put("id",documentReference.getId());
                firestore.collection("Proyectos").document(id_proyecto).collection("Tareas").document(documentReference.getId()).update(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ocultarElementos(false);
                        dialog.dismiss();
                        Toast.makeText(Ver_proyecto.this, "Tarea creada correctamente.", Toast.LENGTH_SHORT).show();
                        Tarea tarea = new Tarea();
                        tarea.setNombre_tarea(nombre_tarea.getText().toString());
                        tarea.setCorreo_admin(mAuth.getCurrentUser().getEmail().toString());
                        tarea.setCorreo_encargado("");
                        tarea.setDescripcion(texto_descripcion_tarea.getText().toString());
                        tarea.setEstado(false);
                        tarea.setImagen(url_imagen_tarea_bd);
                        tarea.setImagen_completado("");
                        tarea.setId(documentReference.getId());
                        tareasList.add(tarea);

                        adapter_tareas.notifyDataSetChanged();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void subirTodo(){

        ocultarElementos(true);

        if(uri == null){
            subirDatosTarea();
        }else{
            StorageReference storageRef = storage.getReference().child("imagenes_tareas");
            StorageReference imageRef = storageRef.child(uri.getLastPathSegment());

            imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri2) {
                            url_imagen_tarea_bd=uri2.toString();
                            System.out.println("La url de la imagen es: "+url_imagen_tarea_bd);

                            //Primero se sube la imagen y luego el proyecto.
                            subirDatosTarea();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.err.println("No recupera la ruta de la imagen.");
                        }
                    });
                }
            });
        }



    }

    public void ocultarElementos(Boolean ocultar){

        if(ocultar==true){
            scrollView_crear_tarea.setVisibility(View.INVISIBLE);
            carga_subida_tarea.setVisibility(View.VISIBLE);

        } else if (ocultar==false) {
            carga_subida_tarea.setVisibility(View.INVISIBLE);
            scrollView_crear_tarea.setVisibility(View.VISIBLE);
        }

    }

    public void obtenerTareas(){

        tareas_proyecto.setLayoutManager(new LinearLayoutManager(this));
        tareasList = new ArrayList<>();
        adapter_tareas= new Adapter_Tareas(tareasList, id_proyecto);
        tareas_proyecto.setAdapter(adapter_tareas);


        firestore.collection("Proyectos").document(id_proyecto).collection("Tareas").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());

                        }

                        for(int i=0; i<snab.size();i++){
                            Tarea tarea = new Tarea();
                            tarea.setNombre_tarea(snab.get(i).getString("nombre"));
                            tarea.setCorreo_admin(snab.get(i).getString("correo_admin"));
                            tarea.setCorreo_encargado(snab.get(i).getString("correo_encargado"));
                            tarea.setDescripcion(snab.get(i).getString("descripcion"));
                            tarea.setEstado(snab.get(i).getBoolean("estado"));
                            tarea.setImagen(snab.get(i).getString("imagen"));
                            tarea.setImagen_completado(snab.get(i).getString("imagen_completado"));
                            tarea.setCorreo_ultima_mod(snab.get(i).getString("correo_ultima_mod"));
                            tarea.setId(snab.get(i).getString("id"));
                            tareasList.add(tarea);
                        }


                        System.out.println("Tareas totales aqui: "+ snab.size());
                        adapter_tareas.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void codigo_accesoAnimado(String codigo){
        // Crear animación de desplazamiento
        codigo_animado.setText(codigo);
        /*
        Animation animation = new TranslateAnimation(0, 1000, 0, 0);
        animation.setDuration(5000); // Duración de la animación en milisegundos
        animation.setRepeatCount(Animation.INFINITE); // Repetir la animación infinitamente
        animation.setRepeatMode(Animation.REVERSE);
        codigo_animado.startAnimation(animation);
         */
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
                                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                                            carga.setVisibility(View.INVISIBLE);

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
                                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                                            carga.setVisibility(View.INVISIBLE);

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
                                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                                            carga.setVisibility(View.INVISIBLE);
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
                                        firestore.collection("Proyectos")
                                                .document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        scrollView.setVisibility(View.VISIBLE);
                                                        carga.setVisibility(View.INVISIBLE);

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
                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                            carga.setVisibility(View.INVISIBLE);

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
                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                            carga.setVisibility(View.INVISIBLE);
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
                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                            carga.setVisibility(View.INVISIBLE);
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
                                                                                            scrollView.setVisibility(View.VISIBLE);
                                                                                            carga.setVisibility(View.INVISIBLE);
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
                                                        scrollView.setVisibility(View.VISIBLE);
                                                        carga.setVisibility(View.INVISIBLE);

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

    public void  salirProyecto(String id){

                        firestore.collection("Usuarios").
                                document(mAuth.getCurrentUser().getUid()).
                                collection("Proyectos_Ajenos").
                                document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        firestore.collection("Proyectos").document(id).
                                                collection("Informacion_Usuarios").
                                                document(mAuth.getCurrentUser().getUid()).
                                                delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        scrollView.setVisibility(View.VISIBLE);
                                                        carga.setVisibility(View.INVISIBLE);
                                                        Intent intent = new Intent(Ver_proyecto.this, Pantalla_home.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                });

    }

    public void onBackPressed() {
        Intent intent = new Intent(Ver_proyecto.this, Pantalla_home.class);
        startActivity(intent);
    }
}