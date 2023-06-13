package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Profile extends AppCompatActivity {

    BottomNavigationView btnNavView;
    TextView nombreUsuario;
    FloatingActionButton fab;
    ImageView banner;
    final private FirebaseAuth auth = FirebaseAuth.getInstance();
    GridView gridView;
    Button btnEditar;
    ArrayList<ProjectClass> projectLista;
    MyAdapter adapter;
    FirebaseUser currenteUser = FirebaseAuth.getInstance().getCurrentUser();
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private CollectionReference users = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnNavView = findViewById(R.id.bottomNavigationView);
        btnNavView.setSelectedItemId(R.id.btn_profile);
        fab = findViewById(R.id.addFab);
        gridView = findViewById(R.id.gridProfile);
        nombreUsuario = findViewById(R.id.userProfileName);
        btnEditar = findViewById(R.id.btnEditar);
        banner = findViewById(R.id.userBanner);


        projectLista = new ArrayList<>();
        adapter = new MyAdapter(projectLista, this);
        gridView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);


        final String[] userName = new String[1];
        final String userEmail = currenteUser.getEmail(); // mi solucion a un nullPointerException porque el currentUser.getEmail() podria ser nulo
        //CONSEGUIR LOS DEL USUARIO LOGEADO
        if (userEmail != null) {
            users.document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshots) {
                    User user = documentSnapshots.toObject(User.class);
                    if (user != null) {
                        if(user.getUserProjects()!=null){
                            projectLista.addAll(user.getUserProjects());
                        }
                        userName[0] = user.getUserName();
                        toolbar.setTitle(userName[0]);
                        nombreUsuario.setText(userName[0]);
                        if(user.getBanner()!=null){
                            Glide.with(getApplicationContext()).load(user.getBanner()).into(banner);
                        }
                    }

                    //ENVIAR POR PARAMETROS EL PROYECTO SELECIONADO A LA CLASE LECTOR
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent home = new Intent(getApplicationContext(), Lector.class);
                            home.putExtra("pages", projectLista.get(i).getPages());
                            startActivity(home);
                        }
                    });
                    adapter.notifyDataSetChanged();

                }
            });
        }

        setSupportActionBar(toolbar);

        btnNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        return true;
                    case R.id.btn_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        return true;
                    case R.id.btn_profile:
                        return true;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UploadProject.class));
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditarPerfil.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbarMenu){
            showBottomDialog();
        }
        return true;
    }

    private void showBottomDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout settingsLayout = dialog.findViewById(R.id.linearSettings);
        LinearLayout favoritesLayout = dialog.findViewById(R.id.linearFavorites);
        LinearLayout logoutLayout = dialog.findViewById(R.id.linearLogOut);
        LinearLayout deleteProject = dialog.findViewById(R.id.linearDeleteProject);

        ImageView cancel = dialog.findViewById(R.id.imageCancel);
        
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(Profile.this, "Settings", Toast.LENGTH_SHORT).show();
            }
        });

        favoritesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(Profile.this, "Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        //DELETE PROJECT
        deleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(Profile.this, DeleteProject.class));
            }
        });

        //LOG OUT
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showLogoutDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showLogoutDialog(){
        ConstraintLayout logoutConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(Profile.this).inflate(R.layout.logout_dialog, logoutConstraintLayout);

        Button logoutDone = view.findViewById(R.id.logoutDone);
        Button logoutCancel = view.findViewById(R.id.logoutCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //BOTÓN CERRAR SESION
        logoutDone.findViewById(R.id.logoutDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(Profile.this, "Adios!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                auth.signOut();
            }
        });

        logoutCancel.findViewById(R.id.logoutCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}