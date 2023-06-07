package com.example.proyecto_fin_curso;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_fin_curso.databinding.ActivityPantallaHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Pantalla_home extends AppCompatActivity {

    TextView nombre_usuario,correo_usuario;
    ImageView imagen_usuario;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;


    //VARIABLES AÑADIR PROYECTO
    Button unirse;
    EditText codigo_proyecto;




    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPantallaHomeBinding binding;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityPantallaHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();





        obtenerReferencias();
        imagen_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pantalla_home.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(binding.appBarPantallaHome.toolbar);


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pantalla_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        crearProyecto();
        unirseProyecto();


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pantalla_home, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();

        if(idItem==R.id.action_settings){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawerLayout= findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pantalla_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void obtenerReferencias(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nombre_usuario=navigationView.getHeaderView(0).findViewById(R.id.nombre_usuario);
        correo_usuario=navigationView.getHeaderView(0).findViewById(R.id.correo_usuario);
        imagen_usuario=navigationView.getHeaderView(0).findViewById(R.id.imagen_usuario);


        try {
            correo_usuario.setText(mAuth.getCurrentUser().getEmail());


            if(mAuth.getCurrentUser().getDisplayName().isEmpty()){
                String [] separacion = mAuth.getCurrentUser().getEmail().split("@");
                nombre_usuario.setText(separacion[0]);

            }else{
                nombre_usuario.setText(mAuth.getCurrentUser().getDisplayName());
            }


            String rutaFoto=mAuth.getCurrentUser().getPhotoUrl()+"";
            if(rutaFoto.length()<=4){
                Glide.with(imagen_usuario.getContext()).load(R.drawable.ic_menu_camera).into(imagen_usuario);
            }else{
                Glide.with(imagen_usuario.getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).into(imagen_usuario);
            }


        }catch (Exception ex){
            Toast.makeText(this, "Debes añadir un nombre y una imagen.", Toast.LENGTH_SHORT).show();
            System.err.println(ex);
        }




    }


    public void crearProyecto(){
        com.getbase.floatingactionbutton.FloatingActionButton crearProyecto;
        crearProyecto=findViewById(R.id.crear_proyecto);

        crearProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pantalla_home.this, Crear_proyecto.class);
                startActivity(intent);
            }
        });
    }

    public void unirseProyecto(){
        com.getbase.floatingactionbutton.FloatingActionButton unirseProyecto;
        unirseProyecto=findViewById(R.id.unirse_proyecto);

        unirseProyecto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog= new BottomSheetDialog(Pantalla_home.this,R.style.BottomSheetDialogTheme);

                View vista = LayoutInflater.from(getApplicationContext()).inflate(R.layout.unirse_proyecto,null);
                unirse =vista.findViewById(R.id.unirse_proyecto);
                codigo_proyecto=vista.findViewById(R.id.texto_codigo_acceso);

                dialog.setCancelable(true);
                dialog.setContentView(vista);
                dialog.show();

                    unirse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Comprueba si el proyecto te pertenece
                            firestore.collection("Usuarios").document(mAuth.getCurrentUser().getUid())
                                    .collection("Proyectos")
                                    .document(codigo_proyecto.getText().toString())
                                    .get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Toast.makeText(Pantalla_home.this, "Este proyecto te pertenece.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //Se busca el proyecto
                                                firestore.collection("Proyectos").document(codigo_proyecto.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("id",mAuth.getCurrentUser().getUid());
                                                        map.put("correo",mAuth.getCurrentUser().getEmail());
                                                        map.put("nombre",mAuth.getCurrentUser().getDisplayName());

                                                        //Se registra al usuario en el proyecto
                                                        firestore.collection("Proyectos").document(codigo_proyecto.getText().toString()).collection("Informacion_Usuarios")
                                                                .document(mAuth.getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Map<String, Object> map2 = new HashMap<>();
                                                                        map2.put("id",codigo_proyecto.getText().toString());

                                                                        //Se guarda el id del proyecto en la coleccion del usuario
                                                                        firestore.collection("Usuarios").document(mAuth.getUid()).collection("Proyectos_Ajenos")
                                                                                .document(codigo_proyecto.getText().toString()).set(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        Toast.makeText(Pantalla_home.this, "Has ingresado al proyecto.", Toast.LENGTH_SHORT).show();
                                                                                        dialog.dismiss();
                                                                                        codigo_proyecto.setText("");
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(Pantalla_home.this, "No se a podido acceder al proyecto", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(Pantalla_home.this, "El proyecto no existe", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } else {
                                            Toast.makeText(Pantalla_home.this, "Error. Intentelo mas tarde.", Toast.LENGTH_SHORT).show();
                                        }
                                    });



                        }
                    });

            }
        });
    }









}