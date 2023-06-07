package com.example.proyecto_fin_curso.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Asignar.Adapter_Asignar_Usuario;
import com.example.proyecto_fin_curso.Clases.Comentarios_Tarea;
import com.example.proyecto_fin_curso.Clases.Tarea;
import com.example.proyecto_fin_curso.Clases.Usuario;
import com.example.proyecto_fin_curso.Comentarios.Adapter_Comentarios;
import com.example.proyecto_fin_curso.Crear_proyecto;
import com.example.proyecto_fin_curso.Dialogos.Dialog_Imagenes;
import com.example.proyecto_fin_curso.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.messaging.MessagingAnalytics;
import com.google.firebase.messaging.NotificationParams;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Ver_Tarea extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    ImageView imagenTarea;
    EditText nombre_tarea,tarea_descripcion;
    RadioGroup estado;
    RadioButton radioButtonCopletado;
    RadioButton radioButtonNoCompletado;
    ProgressBar carga_pantalla;
    ScrollView contenido;

    TextView correo_asigando;
    ImageView asignar_usuario;

    CardView carta_correo;

    BottomSheetDialog dialog;

    RecyclerView recyclerListaUsuarios;

    Bundle extras;

    //COMENTARIOS

    ImageView flecha;

    ImageView galeria_comentario;
    EditText texto_comentario;

    RecyclerView lista_comentarios;
    List<Comentarios_Tarea> listComentarios;
    Adapter_Comentarios adapter_comentarios;
    ImageView eliminar_tarea;

    Uri uri;
    String url_imagen_comentario;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_tarea);

        mAuth = FirebaseAuth.getInstance();

        imagenTarea=findViewById(R.id.imagen_VerTarea);
        nombre_tarea=findViewById(R.id.titulo_VerTarea);
        tarea_descripcion=findViewById(R.id.texto_descripcion_VerTarea);
        estado=findViewById(R.id.radioGroupEstadoTarea);
        radioButtonCopletado = findViewById(R.id.radioButtonCompletado);
        radioButtonNoCompletado = findViewById(R.id.radioButtonNoCompletado);
        carga_pantalla=findViewById(R.id.progressBar_carga_ver_tarea);
        contenido=findViewById(R.id.scroll_verTarea);
        correo_asigando= findViewById(R.id.texto_correo_asignado);
        asignar_usuario = findViewById(R.id.boton_asignar_usuario);
        carta_correo= findViewById(R.id.carta_correo_ver_tarea);
        lista_comentarios = findViewById(R.id.recyclerView_comentarios_tarea);
        eliminar_tarea = findViewById(R.id.eliminar_tarea);

        //COMENTARIOS
        flecha= findViewById(R.id.insertar_comentario);
        galeria_comentario= findViewById(R.id.insertar_imagen_comentario);
        texto_comentario= findViewById(R.id.texto_Escribir_comentario);

        texto_comentario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    flecha.setVisibility(View.VISIBLE);
                    galeria_comentario.setVisibility(View.GONE);
                } else {
                    flecha.setVisibility(View.GONE);
                    galeria_comentario.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        extras = getIntent().getExtras();
        nombre_tarea.setText(extras.getString("nombre"));
        this.setTitle(extras.getString("nombre"));
        tarea_descripcion.setText(extras.getString("descripcion"));

        Uri uri = Uri.parse(extras.getString("imagen"));
        Glide.with(imagenTarea).load(uri).into(imagenTarea);

        if(extras.getBoolean("estado")==true){
            radioButtonCopletado.setChecked(true);
            radioButtonNoCompletado.setChecked(false);
        }else{
            radioButtonCopletado.setChecked(false);
            radioButtonNoCompletado.setChecked(true);
        }

        if(!mAuth.getCurrentUser().getEmail().contains(extras.getString("correo_admin"))){
            asignar_usuario.setVisibility(View.INVISIBLE);
            eliminar_tarea.setVisibility(View.INVISIBLE);
        }

        try {
            if(extras.getString("correo_encargado").length()>2){
                correo_asigando.setText(extras.getString("correo_encargado"));
                carta_correo.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            System.err.println(e.toString());
        }


        comprobarAsignado(extras);
        descargarComentarios(extras);


        ocultarElementos(false);

        asignar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desplegarSelectorUsuario(extras);
            }
        });

        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirComentario(extras,"texto");


            }
        });

        imagenTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog_Imagenes dialogFragment = Dialog_Imagenes.newInstance(extras.getString("imagen"));
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });


        galeria_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(Ver_Tarea.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });


        //ESTADO ACTUALIZAR

        radioButtonCopletado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        actualizarEstado(extras,true);
                    }
                }).start();

            }
        });

        radioButtonNoCompletado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        actualizarEstado(extras,false);
                    }
                }).start();
            }
        });

        //ELIMINAR TAREA

        eliminar_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ver_Tarea.this);
                builder.setMessage("¿Quieres eliminar la tarea?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarTarea(extras.getString("id_proyecto"),extras.getString("id"));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();


            }
        });



    }



    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        uri=data.getData();
        subirImagenComentario();
    }


    public void ocultarElementos(Boolean ocultar){

        if(ocultar==true){
            contenido.setVisibility(View.INVISIBLE);
            carga_pantalla.setVisibility(View.VISIBLE);

        } else if (ocultar==false) {
            carga_pantalla.setVisibility(View.INVISIBLE);
            contenido.setVisibility(View.VISIBLE);
        }

    }


    private void comprobarAsignado(Bundle extras){
        if(extras.getString("correo_encargado").contains(mAuth.getCurrentUser().getEmail()) || extras.getString("correo_admin").contains(mAuth.getCurrentUser().getEmail())){

        }else{
            if(extras.getString("correo_encargado").length()>2){
                estado.setEnabled(false);
                radioButtonNoCompletado.setClickable(false);
                radioButtonNoCompletado.setEnabled(false);
                radioButtonCopletado.setClickable(false);
                radioButtonCopletado.setEnabled(false);
            }else{

            }

        }
    }


    private void desplegarSelectorUsuario(Bundle extras){
        dialog= new BottomSheetDialog(Ver_Tarea.this,R.style.BottomSheetDialogTheme);
        View vista = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pantalla_seleccionar_usuario,null);
        recyclerListaUsuarios = vista.findViewById(R.id.recyclerView_usuarios_asignar);
        ScrollView scrollView = vista.findViewById(R.id.idScrollView_asignarUsuario);
        ProgressBar progressBar = vista.findViewById(R.id.progressBar_carga_selec_usuario);


        recyclerListaUsuarios.setLayoutManager(new LinearLayoutManager(this));
        List<Usuario> listUsuarios = new ArrayList<>();
        Adapter_Asignar_Usuario adapter_asignar_usuario = new Adapter_Asignar_Usuario(listUsuarios, this, extras.getString("id_proyecto"), extras.getString("id"));
        recyclerListaUsuarios.setAdapter(adapter_asignar_usuario);

        firestore.collection("Proyectos").document(extras.getString("id_proyecto")).collection("Informacion_Usuarios").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());

                        }

                        for(int i=0; i<snab.size();i++){
                            Usuario usuario = new Usuario();
                            usuario.setNombre(snab.get(i).getString("nombre"));
                            usuario.setCorreo(snab.get(i).getString("correo"));
                            listUsuarios.add(usuario);
                        }


                        System.out.println("Tareas totales aqui: "+ snab.size());
                        adapter_asignar_usuario.notifyDataSetChanged();


                        scrollView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Ver_Tarea.this, "Error, intentelo mas tarde.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

        dialog.setCancelable(true);
        dialog.setContentView(vista);
        dialog.show();






    }


    private void descargarComentarios(Bundle extras){
        lista_comentarios.setLayoutManager(new LinearLayoutManager(this));
        listComentarios = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter_comentarios = new Adapter_Comentarios(listComentarios,fragmentManager);
        lista_comentarios.setAdapter(adapter_comentarios);

        firestore.collection("Proyectos")
                .document(extras.getString("id_proyecto"))
                .collection("Tareas")
                .document(extras.getString("id"))
                .collection("Comentarios")
                .orderBy("fecha", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString());

                        }

                        for(int i=0; i<snab.size();i++){
                            Comentarios_Tarea comentario = new Comentarios_Tarea();
                            comentario.setTexto_nombre(snab.get(i).getString("nombre"));
                            comentario.setTipo(snab.get(i).getString("tipo"));
                            comentario.setImagen_o_texto(snab.get(i).getString("imagen_o_texto"));
                            comentario.setFecha(snab.get(i).getTimestamp("fecha"));
                            listComentarios.add(comentario);
                        }

                        adapter_comentarios.notifyDataSetChanged();
                    }
                });

    }



    private void subirComentario(Bundle extras, String tipo){
        Comentarios_Tarea com= new Comentarios_Tarea();
        com.setTipo(tipo);
        com.setTexto_nombre(mAuth.getCurrentUser().getDisplayName());
        com.setFecha(Timestamp.now());

        Map<String, Object> map = new HashMap<>();
        map.put("nombre",com.getTexto_nombre());
        map.put("tipo",com.getTipo());
        map.put("fecha",com.getFecha());

        if(tipo.equals("imagen")){
            com.setImagen_o_texto(url_imagen_comentario);
            map.put("imagen_o_texto",com.getImagen_o_texto());

            firestore.collection("Proyectos")
                    .document(extras.getString("id_proyecto"))
                    .collection("Tareas")
                    .document(extras.getString("id"))
                    .collection("Comentarios").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            listComentarios.add(com);
                            adapter_comentarios.notifyDataSetChanged();
                            texto_comentario.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else{

            com.setImagen_o_texto(texto_comentario.getText().toString());
            map.put("imagen_o_texto",com.getImagen_o_texto());

            firestore.collection("Proyectos")
                    .document(extras.getString("id_proyecto"))
                    .collection("Tareas")
                    .document(extras.getString("id"))
                    .collection("Comentarios").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            listComentarios.add(com);
                            adapter_comentarios.notifyDataSetChanged();
                            texto_comentario.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }


    private void subirImagenComentario(){
            StorageReference storageRef = storage.getReference().child("imagenes_proyectos");
            StorageReference imageRef = storageRef.child(uri.getLastPathSegment());

            imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri2) {
                            url_imagen_comentario=uri2.toString();

                            subirComentario(extras, "imagen");

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


    private void actualizarEstado(Bundle extras, boolean estado){

        Map<String, Object> map = new HashMap<>();
        map.put("estado",estado);

        firestore.collection("Proyectos")
                .document(extras.getString("id_proyecto"))
                .collection("Tareas")
                .document(extras.getString("id")).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View view = findViewById(R.id.id_tareas); 
                                String message = "Estado actualizado";
                                int duration = Snackbar.LENGTH_SHORT;

                                Snackbar snackbar = Snackbar.make(view, message, duration);
                                snackbar.show();
                            }
                        });

                    }
                });

    }

    public void eliminarTarea(String id_proyecto, String id_tarea){
        firestore.collection("Proyectos").
                document(id_proyecto).
                collection("Tareas").
                document(id_tarea).
                collection("Comentarios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snab = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snab){
                            Log.d(TAG,"onSuccess: "+snapshot.getData().toString() );

                            firestore.collection("Proyectos").
                                    document(id_proyecto).
                                    collection("Tareas").
                                    document(id_tarea).
                                    collection("Comentarios").document(snapshot.getId()).delete();
                        }

                        firestore.collection("Proyectos").
                                document(id_proyecto).
                                collection("Tareas").
                                document(id_tarea).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(Ver_Tarea.this, Ver_proyecto.class);
                                        intent.putExtra("id",id_proyecto);
                                        startActivity(intent);
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        firestore.collection("Proyectos").
                                document(id_proyecto).
                                collection("Tareas").
                                document(id_tarea).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(Ver_Tarea.this, Ver_proyecto.class);
                                        intent.putExtra("id",id_proyecto);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
    }











}