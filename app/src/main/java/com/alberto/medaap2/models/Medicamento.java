package com.alberto.medaap2.models;


public class Medicamento {

    private String nombre;
    private String cn;
    private String pushKey;
    private String nregistro;
    private String url;
    private String dias;
    private String horas;
    private String inicio;
    private int idAlarma;
    private String usuario;
    private String fechaInicio;

    public Medicamento() {
    }


    public Medicamento(String nombre, String usuario, int idAlarma, String dias, String horas, String inicio, String pushKey, String nregistro, String cn, String fechaInicio) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.idAlarma = idAlarma;
        this.dias = dias;
        this.horas = horas;
        this.inicio = inicio;
        this.pushKey = pushKey;
        this.nregistro = nregistro;
        this.cn = cn;
        this.fechaInicio = fechaInicio;
    }


    public Medicamento(String nombre, String nregistro, String dias, String horas, String inicio, int idAlarma, String usuario) {
        this.nombre = nombre;
        this.nregistro = nregistro;
        this.dias = dias;
        this.horas = horas;
        this.inicio = inicio;
        this.idAlarma = idAlarma;
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getNregistro() {
        return nregistro;
    }

    public void setNregistro(String nregistro) {
        this.nregistro = nregistro;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public int getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(int idAlarma) {
        this.idAlarma = idAlarma;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "nombre='" + nombre + '\'' +
                ", cn='" + cn + '\'' +
                ", pushKey='" + pushKey + '\'' +
                ", nregistro='" + nregistro + '\'' +
                ", url='" + url + '\'' +
                ", dias='" + dias + '\'' +
                ", horas='" + horas + '\'' +
                ", inicio='" + inicio + '\'' +
                ", idAlarma=" + idAlarma +
                ", usuario='" + usuario + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                '}';
    }
}
