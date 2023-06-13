package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;

    private Button btnEntra, btnSignup;
    private EditText userName, mail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnEntra = findViewById(R.id.btn_entrar_del_singup);
        btnSignup = findViewById(R.id.signup_button);
        userName = findViewById(R.id.userName);
        mail = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        auth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = userName.getText().toString().trim();
                String correo = mail.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (correo.isEmpty() || pass.isEmpty() || name.isEmpty()){
                    mail.setError("El correo es requerido");
                    password.setError("La contraseña es requerida");
                    userName.setError("El nombre de usuario es requirido");
                }else {

                    auth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                //AÑADIR UN USARIO AL FIRESTORE COMO COLECCION
                                User user = new User(name, correo, pass);
                                db.collection("users").document(correo).set(user);

                                Toast.makeText(SignUp.this, "El registro ha sido un exito", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LogIn.class));
                            }else {
                                Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

            }
        });



        btnEntra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });
    }
}