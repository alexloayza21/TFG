package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class Search extends AppCompatActivity {

    BottomNavigationView btnNavView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<ProjectClass> projectLista;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private CollectionReference projects = db.collection("proyectos");
    String[] listaTitulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnNavView = findViewById(R.id.bottomNavigationView);
        btnNavView.setSelectedItemId(R.id.btn_search);
        listView = findViewById(R.id.listView);

        btnNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        return true;
                    case R.id.btn_search:
                        return true;
                    case R.id.btn_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        return true;
                }
                return false;
            }
        });

        projectLista = new ArrayList<>();
        projects.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                //RECORRE LOS DOCUMENTOS DE LA COLECCION
                for (DocumentSnapshot value : documents){
                    ProjectClass proyectos = value.toObject(ProjectClass.class);
                    if (proyectos != null){
                        projectLista.add(proyectos);
                    }

                    ArrayList<String> projectNames = new ArrayList<>();
                    for (int i = 0; i < projectLista.size(); i++) {
                        projectNames.add(projectLista.get(i).getTitulo());
                    }

                    listaTitulos = new String[projectNames.size()];
                    listaTitulos = projectNames.toArray(listaTitulos);

                    arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_customtext, listaTitulos);
                    listView.setAdapter(arrayAdapter);

                    //ENVIAR POR PARAMETROS EL PROYECTO SELECIONADO A LA CLASE LECTOR
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            ProjectClass projectEnviar;
                            Intent projectScreen = new Intent(getApplicationContext(), ProjectActivity.class);

                            for (int j = 0; j < projectLista.size(); j++) {
                                if (adapterView.getItemAtPosition(i).toString().equals(projectLista.get(j).getTitulo())){
                                    projectEnviar = projectLista.get(j);
                                    projectScreen.putExtra("projectSelected", projectEnviar);
                                }
                            }
                            startActivity(projectScreen);
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}