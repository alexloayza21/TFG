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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UploadProject extends AppCompatActivity {

    private Button btnUpload, btnPages, btnCancelar;
    private ImageView uploadImage;
    EditText uploadTitulo, uploadDescripcion;
    private Uri imageUriPortada, imageUripages;

    private ArrayList<Uri> pages;
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    final private CollectionReference userCollention = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btnUpload = findViewById(R.id.btn_upload);
        btnPages = findViewById(R.id.btn_pages);
        uploadImage = findViewById(R.id.uploadImage);
        uploadTitulo = findViewById(R.id.uploadTitulo);
        uploadDescripcion = findViewById(R.id.uploadDescripcion);
        btnCancelar = findViewById(R.id.btn_cancel);


        //SELECCIONAR PORTADA
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUriPortada = data.getData();
                            uploadImage.setImageURI(imageUriPortada);
                        }else{
                            Toast.makeText(UploadProject.this, "No has seleccionado una imagen", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        //SELECCIONAR PAGINAS

        pages = new ArrayList<>();

        ActivityResultLauncher<Intent> intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData().getClipData() != null){
                            //seleccionar varias imagenes
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                imageUripages = result.getData().getClipData().getItemAt(i).getUri();
                                pages.add(imageUripages);
                            }
                        }else{
                            //seleccionar una sola imagen
                            imageUripages = result.getData().getData();
                            pages.add(imageUripages);
                        }
                    }
                });

        btnPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(intent.ACTION_GET_CONTENT);

                intentLauncher.launch(Intent.createChooser(intent, "Select Image(s)"));
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUriPortada != null){
                    uploadToFireBase(imageUriPortada, pages);
                    Toast.makeText(UploadProject.this, "Espera UN SEGUNDO", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UploadProject.this, "Selecciona una imagen, por favor", Toast.LENGTH_SHORT).show();
                }
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

    //SUBIR PROYECTO A FIRESTORE
    private void uploadToFireBase(Uri uriPortada, ArrayList<Uri>paginas){

        ArrayList<String> pagesArray = new ArrayList<>();
        for(Uri page : paginas){

            StorageReference imgRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(page));

            imgRef.putFile(page).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//OBTENIENDO LINK PARA DESCARGAR
                        @Override
                        public void onSuccess(Uri uri) {
                            pagesArray.add(uri.toString());
                        }
                    });
                }
            });
        }

        String titulo = uploadTitulo.getText().toString().trim();
        String descripcion = uploadDescripcion.getText().toString().trim();

        StorageReference imageReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uriPortada));

        imageReference.putFile(uriPortada).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //GUARDANDO PROYECTO EN LA COLECCION PROYECTOS
                        if (!titulo.isEmpty()){

                            ProjectClass newProject = new ProjectClass(titulo, descripcion, uri.toString(), pagesArray);
                            db.collection("proyectos").document(titulo).set(newProject);

                            //GUARDAR EL PROYECTO EN EL USURAIO QUE ESTA LOGEADO
                            userCollention.document(usuario.getEmail()).update("userProjects", FieldValue.arrayUnion(newProject));

                            Toast.makeText(UploadProject.this, "PROYECTO SUBIDO CON EXITO", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UploadProject.this, Profile.class));
                        }else{
                            Toast.makeText(UploadProject.this, "El titulo es obligatorio", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadProject.this, "FALLO", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}