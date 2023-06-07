package com.example.proyecto_fin_curso.Login;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Clases.Usuario;
import com.example.proyecto_fin_curso.Pantalla_home;
import com.example.proyecto_fin_curso.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    //Login correo y contraseña
    EditText correo;
    EditText password;
    Button registrarse;

    //VARIABLES VERIFICAR CORREO
    Button verificar;
    TextView ir_pantalla_inicioSesion_Verificar;
    EditText correo_verificación;


    //ir a pantalla de inicio sesion
    TextView irInicioSesion;
    ImageView ocultar_password;

    //Base de datos firebase
    private FirebaseFirestore bdFirebase;

    //PERMISOS
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };





    private GoogleSignInClient mGoogleSignInClient;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Proyecto_Fin_Curso);
        super.onCreate(savedInstanceState);
        String video = "android.resource://"+getPackageName()+"/"+ R.raw.video_intro;
        Uri uri= Uri.parse(video);
        setContentView(R.layout.activity_main);


        //PERMISOS
        checkAndRequestPermissions();




        findViewById(R.id.boton_login_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        registrarse=findViewById(R.id.registrarse);
        correo=findViewById(R.id.login_correo);
        password=findViewById(R.id.login_passaword);
        ocultar_password= findViewById(R.id.ver_password_regitrarse);
        ocultar_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Si la contraseña está oculta, mostrarla
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Si la contraseña está visible, ocultarla
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                // Mover el cursor al final del texto
                password.setSelection(password.getText().length());
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]


        irInicioSesion=findViewById(R.id.ir_pantalla_inicioSesion);

        irInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Iniciar_Sesion.class);

                try {
                    intent.putExtra("correo",correo.getText().toString());
                    intent.putExtra("password",password.getText().toString());
                }catch (Exception e){
                    System.err.println("Error controlado");
                }

                startActivity(intent);
            }
        });

        bdFirebase= FirebaseFirestore.getInstance();
    }

    private void checkAndRequestPermissions() {
        // Verificar los permisos que aún no han sido otorgados
        String[] notGrantedPermissions = getNotGrantedPermissions();
        if (notGrantedPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions, REQUEST_CODE_PERMISSIONS);
        } else {
        }
    }

    private String[] getNotGrantedPermissions() {
        // Obtener la lista de permisos que aún no han sido otorgados
        // Iterar sobre los permisos requeridos y verificar si están otorgados
        // Si un permiso no ha sido otorgado, se agrega a la lista de permisos no otorgados
        // La lista resultante contiene los permisos que aún se deben solicitar

        List<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }

        return notGrantedPermissions.toArray(new String[0]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // Permiso denegado
                        finish();
                    }
                }
            }
        }
    }



    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            insertarUsuarioBD();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]


    private void signIn(){

        if(correo.getText().length()>1 && password.getText().length()>6 ){
            mAuth.createUserWithEmailAndPassword(correo.getText().toString(), password.getText().toString()).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) { //Comprobar si el usuario existe
                            if(task.isSuccessful()){
                                boolean exite=false;

                                if(exite==true){
                                    Toast.makeText(MainActivity.this, "El usuario existe. Debes Iniciar Sesión.", Toast.LENGTH_SHORT).show();
                                }else{

                                    insertarUsuarioBD();
                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        updateUI(mAuth.getCurrentUser());
                                    }else{

                                        verificarCorreo();
                                    }

                                }
                            }else{
                                Toast.makeText(MainActivity.this, "El usuario existe. Debes Iniciar Sesión.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

        }else{
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUI(FirebaseUser user) {

        FirebaseUser user1=FirebaseAuth.getInstance().getCurrentUser();
        if (user1!=null && user1.isEmailVerified()){
                finish();
                Intent intent = new Intent(MainActivity.this, Pantalla_home.class);
                intent.putExtra("correo", user.getEmail());
                startActivity(intent);

            }
        }



    @SuppressLint({"MissingInflatedId", "ResourceAsColor", "ResourceType"})
    public void verificarCorreo(){
        BottomSheetDialog dialog= new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialogTheme);

        View vista = LayoutInflater.from(getApplicationContext()).inflate(R.layout.verificar_correo,null);
        verificar =vista.findViewById(R.id.boton_verificar);
        ir_pantalla_inicioSesion_Verificar =vista.findViewById(R.id.ir_pantalla_inicioSesion_Verificar);
        correo_verificación=vista.findViewById(R.id.login_correo_verificar);
        correo_verificación.setText(mAuth.getCurrentUser().getEmail());

        dialog.setContentView(vista);
        dialog.show();

        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, correo_verificación.getText().toString());
                            Toast.makeText(MainActivity.this, "Verificación enviada. Consulta tu correo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ir_pantalla_inicioSesion_Verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Iniciar_Sesion.class);
                intent.putExtra("correo",correo.getText().toString());
                intent.putExtra("password",password.getText().toString());

                startActivity(intent);
            }
        });

    }




    private void insertarUsuarioBD(){

        FirebaseUser user =mAuth.getCurrentUser();
        String nombreTT=mAuth.getCurrentUser().getDisplayName();
        String correoTT=mAuth.getCurrentUser().getEmail();

        Usuario usuario;
        if(nombreTT==null){

            String [] separacion = correoTT.split("@");

            usuario= new Usuario(separacion[0],user.getEmail());
        }else {
            usuario = new Usuario(nombreTT,user.getEmail());
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "No se pudo obtener el token de registro", task.getException());
                        Map<String, Object> map = new HashMap<>();
                        map.put("correo_electronico",usuario.getCorreo());
                        map.put("nombre",usuario.getNombre());

                        bdFirebase.collection("Usuarios").document(mAuth.getUid()).set(map);
                    }

                    // Token de registro obtenido exitosamente
                    String token = task.getResult();
                    Log.d(TAG, "Token de registro: " + token);

                    Map<String, Object> map = new HashMap<>();
                    map.put("correo_electronico",usuario.getCorreo());
                    map.put("nombre",usuario.getNombre());
                    map.put("token", token);

                    bdFirebase.collection("Usuarios").document(mAuth.getUid()).set(map);
                });



    }




}