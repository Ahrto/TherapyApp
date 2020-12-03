package com.alberto.medaap2.models;

public class Usuario {

    private String uid;
    private String nombre;

    public Usuario(String uid, String nombre) {
        this.uid = uid;
        this.nombre = nombre;
    }

    public Usuario() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
