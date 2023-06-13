package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class Lector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);

        //HIDE STATUS BAR
        /*-------------------------------------------------*/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        /*-------------------------------------------------*/

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        ArrayList<String> pages = getIntent().getStringArrayListExtra("pages");

        for (String paginas : pages){
            slideModels.add(new SlideModel(paginas, ScaleTypes.CENTER_INSIDE));
        }

        imageSlider.setImageList(slideModels);
    }
}