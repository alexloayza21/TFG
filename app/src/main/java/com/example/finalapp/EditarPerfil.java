package com.example.finalapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditarPerfil extends AppCompatActivity {

    Button btnGuardar, btnCancelar;
    EditText newUserName;
    ImageView banner;
    private Uri imageUriBanner;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    final private CollectionReference userCollention = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        newUserName = findViewById(R.id.editUserName);
        banner = findViewById(R.id.editUserBanner);
        btnGuardar = findViewById(R.id.btnGuardarEdit);
        btnCancelar = findViewById(R.id.btnCancelarEdit);


        //SELECCIONAR PORTADA
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUriBanner = data.getData();
                            banner.setImageURI(imageUriBanner);
                        }else{
                            Toast.makeText(EditarPerfil.this, "No has seleccionado una imagen", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });



        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFireBase(imageUriBanner);
                Toast.makeText(EditarPerfil.this, "Espera UN SEGUNDO", Toast.LENGTH_SHORT).show();
            }
        });

        //CANELAR Y VOLVER A PERFIL
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

    }

    private void uploadToFireBase(Uri uriBanner){

        StorageReference imageReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uriBanner));

        String nuevoNombre = newUserName.getText().toString().trim();

        imageReference.putFile(uriBanner).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //GUARDAR EL BANNER
                        if (nuevoNombre.isEmpty()){
                            userCollention.document(usuario.getEmail()).update("banner", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditarPerfil.this, "Banner actualizado con exito", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            userCollention.document(usuario.getEmail()).update("banner", uri.toString(), "userName", nuevoNombre).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditarPerfil.this, "Banner y nombre de usuario actualizados con exito", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        Toast.makeText(EditarPerfil.this, "PERFIL ACTUALIZADO!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditarPerfil.this, Profile.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarPerfil.this, "FALLO", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}