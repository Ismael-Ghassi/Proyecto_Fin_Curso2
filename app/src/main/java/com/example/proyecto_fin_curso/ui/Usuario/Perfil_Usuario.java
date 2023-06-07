package com.example.proyecto_fin_curso.ui.Usuario;

import static android.content.ContentValues.TAG;
import static android.content.Intent.getIntent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.Pantalla_home;
import com.example.proyecto_fin_curso.R;
import com.example.proyecto_fin_curso.databinding.FragmentSlideshowBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil_Usuario extends Fragment {


    //Informacion usuario
    ImageView imagen;
    FloatingActionButton seleccionar_imagen;
    EditText nombre_usuario;
    Button guardar;

    private FirebaseFirestore bdFirebase;

    private FirebaseAuth mAuth;

    Uri uri;

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();





        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }





}