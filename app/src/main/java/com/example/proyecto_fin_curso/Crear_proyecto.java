package com.example.proyecto_fin_curso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Crear_proyecto extends AppCompatActivity {

    ImageView imagen;
    FloatingActionButton seleccionar;

    EditText nombre_proyecto,texto_descripcion;
    TextView fecha_texto;
    Button seleccionar_fecha,crear_proyecto;

    private FirebaseFirestore bdFirebase;
    private FirebaseAuth mAuth;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String id_proyecto;

    Uri uri=Uri.parse("");
    String url_imagen_bd;

    ProgressBar carga_subida;
    ScrollView scrollView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_proyecto);
        this.setTitle("Crear Proyecto");


        imagen=findViewById(R.id.imagen_crearProyecto);
        seleccionar=findViewById(R.id.seleccionar_Imagen);
        nombre_proyecto=findViewById(R.id.texto_nombre);
        texto_descripcion=findViewById(R.id.texto_descripcion);
        fecha_texto=findViewById(R.id.texto_fecha_limite);
        seleccionar_fecha=findViewById(R.id.establecer_fecha);
        crear_proyecto=findViewById(R.id.crear_proyecto2);
        carga_subida=findViewById(R.id.progressBar_carga_crear_proyecto);
        scrollView = findViewById(R.id.idScrollView_crearProyecto);


        bdFirebase= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(Crear_proyecto.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });


        seleccionar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();
                int dia = calendario.get(Calendar.DAY_OF_MONTH);
                int mes = calendario.get(Calendar.MONTH);
                int anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Crear_proyecto.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                fecha_texto.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, anio, mes, dia);

                datePickerDialog.show();

            }
        });

        crear_proyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre_proyecto.getText() !=null && uri.toString().length()>5 && fecha_texto.getText()!=null && texto_descripcion.getText()!=null){
                    System.out.println("uri: "+uri.toString().length() );
                    ocultarElementos(true);
                    carga_subida.setVisibility(View.VISIBLE);
                    subirTodo();
                }else{
                    Toast.makeText(Crear_proyecto.this, "Debes rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                    System.out.println("Nombre: "+nombre_proyecto.getText()+" "+uri.toString()+" "+fecha_texto.getText()+" "+texto_descripcion.getText());
                }


            }
        });


    }





    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        uri=data.getData();
        imagen.setImageURI(uri);
    }




    //Subir proyecto BD
    public void subirProyecto(){

        Map<String, Object> map = new HashMap<>();
        map.put("nombre",nombre_proyecto.getText().toString());
        map.put("correo_admin",mAuth.getCurrentUser().getEmail().toString());
        map.put("codigo_acceso","");
        map.put("imagen",url_imagen_bd);
        map.put("comentario",texto_descripcion.getText().toString());
        map.put("estado",false);
        map.put("fecha_fin",fecha_texto.getText().toString());


        bdFirebase.collection("Proyectos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                id_proyecto = documentReference.getId();
                insertarProyectoBDUsuario();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    //Se guarda el id del proyecto en la coleccion de usuarios para la busqueda.
    public void insertarProyectoBDUsuario(){
        Map<String, Object> map = new HashMap<>();
        map.put("id",id_proyecto);

        bdFirebase.collection("Usuarios").document(mAuth.getCurrentUser().getUid()).collection("Proyectos").document(id_proyecto).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String, Object> establecer_codigo_acceso = new HashMap<>();

                        //El codigo para que otras personas puedan acceder al proyecto es el id del proyecto que es único.
                        establecer_codigo_acceso.put("codigo_acceso",id_proyecto);
                        bdFirebase.collection("Proyectos").document(id_proyecto).update(establecer_codigo_acceso);
                        ocultarElementos(false);
                        carga_subida.setVisibility(View.INVISIBLE);


                        Toast.makeText(Crear_proyecto.this, "Proyecto agregado correctamente.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Crear_proyecto.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void subirTodo(){


        if(uri == null){
            subirProyecto();
        }else{
            StorageReference storageRef = storage.getReference().child("imagenes_proyectos");
            StorageReference imageRef = storageRef.child(uri.getLastPathSegment());

            imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri2) {
                            url_imagen_bd=uri2.toString();
                            System.out.println("La url de la imagen es: "+url_imagen_bd);

                            //Primero se sube la imagen y luego el proyecto.
                            subirProyecto();

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
            scrollView.setVisibility(View.INVISIBLE);

        } else if (ocultar==false) {
            scrollView.setVisibility(View.VISIBLE);
        }

    }
}