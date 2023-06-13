package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProjectActivity extends AppCompatActivity {

    ImageView imgPortada;
    TextView titulo, descripcion;
    Button btnleer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        imgPortada = findViewById(R.id.imgPortada);
        titulo = findViewById(R.id.proyectTitulo);
        descripcion = findViewById(R.id.descripcion);
        btnleer = findViewById(R.id.btnLeer);

        ProjectClass project = (ProjectClass) getIntent().getSerializableExtra("projectSelected");

        Glide.with(this).load(project.getPortada()).into(imgPortada);
        titulo.setText(project.getTitulo());
        descripcion.setText(project.getDescripcion());

        btnleer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lector = new Intent(getApplicationContext(), Lector.class);
                lector.putExtra("pages", project.getPages());
                startActivity(lector);
            }
        });
    }
}