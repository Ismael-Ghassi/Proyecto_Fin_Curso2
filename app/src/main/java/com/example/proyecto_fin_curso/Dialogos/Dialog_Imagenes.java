package com.example.proyecto_fin_curso.Dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.proyecto_fin_curso.R;
import com.github.chrisbanes.photoview.PhotoView;

public class Dialog_Imagenes extends DialogFragment {

    private String imageUrl;

    public static Dialog_Imagenes newInstance(String imageUrl) {
        Dialog_Imagenes fragment = new Dialog_Imagenes();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_imagenes, null);
        builder.setView(view);

        PhotoView imageView = view.findViewById(R.id.imageView_dialog);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("imageUrl");
        }

        if (imageUrl != null) {
            // Descargar la imagen desde la URL y establecerla en el ImageView
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageView);
        }

        return builder.create();
    }
}
