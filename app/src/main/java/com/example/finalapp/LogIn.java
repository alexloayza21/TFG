package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

     private Button btn_registrar, btn_entrar;
     private EditText loginMail, loginPassword;
     private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        btn_registrar = findViewById(R.id.btn_registar_del_login);
        btn_entrar = findViewById(R.id.login_button);
        loginMail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        auth = FirebaseAuth.getInstance();

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = loginMail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();

                if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    if (!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(LogIn.this, "Bienvenido!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogIn.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        loginPassword.setError("el campo contraseña no puede estar vacio");
                    }
                } else if (mail.isEmpty()) {
                    loginMail.setError("El campo correo no puede estar vacio");
                }else {
                    loginMail.setError("Escribe un correo electronico válido");
                }
            }
        });


        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
    }
}