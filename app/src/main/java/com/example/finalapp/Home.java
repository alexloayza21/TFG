package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    BottomNavigationView btnNavView;
    GridView gridView;
    ArrayList<ProjectClass> projectLista;
    MyAdapter adapter;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private CollectionReference projects = db.collection("proyectos");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnNavView = findViewById(R.id.bottomNavigationView);
        gridView = findViewById(R.id.gridView);

        projectLista = new ArrayList<>();
        adapter = new MyAdapter(projectLista, this);
        gridView.setAdapter(adapter);

        projects.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                //RECORRE LOS DOCUMENTOS DE LA COLECCION
                for (DocumentSnapshot value : documents){
                    ProjectClass proyectos = value.toObject(ProjectClass.class);
                    projectLista.add(proyectos);

                    //ENVIAR POR PARAMETROS EL PROYECTO SELECIONADO A LA CLASE LECTOR
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent home = new Intent(getApplicationContext(), Lector.class);
                            home.putExtra("pages", projectLista.get(i).getPages());
                            startActivity(home);
                        }
                    });

                }
                adapter.notifyDataSetChanged();
            }
        });



        btnNavView.setSelectedItemId(R.id.btn_home);

        btnNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_home:
                        return true;
                    case R.id.btn_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        //btnNavView.setSelectedItemId(R.id.btn_search);
                        return true;
                    case R.id.btn_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        //btnNavView.setSelectedItemId(R.id.btn_profile);
                        return true;
                }
                return false;
            }
        });
    }
}