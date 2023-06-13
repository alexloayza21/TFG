package com.example.finalapp;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjectClass implements Serializable {

    private String titulo, descripcion, portada;
    private ArrayList<String> pages;


    public ProjectClass(){

    }

    public ProjectClass(String titulo, String descripcion, String portada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.portada = portada;
    }
    public ProjectClass(String titulo, String descripcion, String portada, ArrayList<String> pages) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.portada = portada;
        this.pages = pages;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public ArrayList<String> getPages() {
        return pages;
    }

    public void setPages(ArrayList<String> pages) {
        this.pages = pages;
    }
}
