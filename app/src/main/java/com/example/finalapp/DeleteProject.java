package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DeleteProject extends AppCompatActivity {
    GridView gridView;
    ArrayList<ProjectClass> projectLista;
    MyAdapter adapter;
    FirebaseUser currenteUser = FirebaseAuth.getInstance().getCurrentUser();
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private CollectionReference users = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_project);

        gridView = findViewById(R.id.gridViewDelete);

        projectLista = new ArrayList<>();
        adapter = new MyAdapter(projectLista, this);
        gridView.setAdapter(adapter);

        final String userEmail = currenteUser.getEmail();
        if (userEmail != null) {
            users.document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshots) {
                    User user = documentSnapshots.toObject(User.class);
                    if (user != null) {
                        if(user.getUserProjects()!=null){
                            projectLista.addAll(user.getUserProjects());
                        }
                    }

                    //BORRAR PROYECTO SELECCIONADO DE LA BASE DE LA BASE DE DATOS DEL USUARIO Y DEL LOS PROYECTOS
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent home = new Intent(DeleteProject.this, Lector.class);
                            home.putExtra("pages", projectLista.get(i).getPages());
                            startActivity(home);
                        }
                    });
                    adapter.notifyDataSetChanged();

                }
            });
        }
    }
}