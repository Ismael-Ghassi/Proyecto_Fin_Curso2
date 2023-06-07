package com.example.proyecto_fin_curso.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_fin_curso.Clases.Usuario;
import com.example.proyecto_fin_curso.Pantalla_home;
import com.example.proyecto_fin_curso.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Iniciar_Sesion extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    //Iniciar sesion correo y contraseña
    EditText correo;
    EditText password;
    Button iniciar_sesion;

    //Ir crear cuenta

    TextView irRegistrase;
    TextView recuperar_password;
    ImageView ocultar_password;

    // [START declare_auth]
    private FirebaseAuth mAuth;


    private FirebaseFirestore bdFirebase;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);


        correo=findViewById(R.id.iniciar_sesion_correo);
        password=findViewById(R.id.iniciar_sesion_passaword);
        ocultar_password= findViewById(R.id.ver_password_inicio_sesion);

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

        mAuth = FirebaseAuth.getInstance();
        recuperar_password= findViewById(R.id.recuperar_passwort);
        recuperar_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(correo.getText().toString().length()>7){
                    mAuth.sendPasswordResetEmail(correo.getText().toString());
                    Toast.makeText(Iniciar_Sesion.this, "Se ha enviado la nueva contraseña a tu correo.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Iniciar_Sesion.this, "Rellena el cambo del correo.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        try{
            correo.setText(getIntent().getExtras().getString("correo"));
            password.setText(getIntent().getExtras().getString("password"));
        }catch (Exception e){
            System.err.println(e);
        }


        findViewById(R.id.boton_iniciar_sesion_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        iniciar_sesion =findViewById(R.id.iniciar_sesion);
        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
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


        // [END initialize_auth]


        irRegistrase= findViewById(R.id.ir_pantalla_registrarse);
        irRegistrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Iniciar_Sesion.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bdFirebase= FirebaseFirestore.getInstance();
    }



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


    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn(){

        if(correo.getText().length()>1 && password.getText().length()>6 ){

            mAuth.signInWithEmailAndPassword(correo.getText().toString(), password.getText().toString()).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) { //Comprobar si el usuario existe
                            if(task.isSuccessful()){


                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        updateUI(mAuth.getCurrentUser());
                                    }else{

                                        Toast.makeText(Iniciar_Sesion.this, "Consulta tu correo y verifica tu cuenta.", Toast.LENGTH_SHORT).show();
                                    }

                            }else{
                                Toast.makeText(Iniciar_Sesion.this, "Ha ocurrido un error.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(Iniciar_Sesion.this, Pantalla_home.class);
            intent.putExtra("correo", user.getEmail());
            startActivity(intent);

        }
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

        Map<String, Object> map = new HashMap<>();
        map.put("correo_electronico",usuario.getCorreo());
        map.put("nombre",usuario.getNombre());

        bdFirebase.collection("Usuarios").document(mAuth.getUid()).set(map);

    }
}