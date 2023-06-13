package com.example.finalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private ArrayList<ProjectClass> projectList;
    private Context context;
    LayoutInflater layoutInflater;

    public MyAdapter(ArrayList<ProjectClass> projectList, Context context) {
        this.projectList = projectList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null){
            view = layoutInflater.inflate(R.layout.grid_item, null);
        }

        ImageView gridImage = view.findViewById(R.id.gridPortada);
        TextView gridCaption = view.findViewById(R.id.gridTitulo);

        Glide.with(context).load(projectList.get(i).getPortada()).into(gridImage);
        gridCaption.setText(projectList.get(i).getTitulo());
        return view;
    }
}
