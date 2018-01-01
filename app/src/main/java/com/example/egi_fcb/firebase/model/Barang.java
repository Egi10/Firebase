package com.example.egi_fcb.firebase.model;

import java.io.Serializable;

/**
 * Created by egi_fcb on 12/31/17.
 */

public class Barang implements Serializable {

    private String nama, merk, key, urll;
    private int harga;

    public Barang(){

    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getNama(){
        return nama;
    }

    public void setNama(String nama){
        this.nama = nama;
    }

    public String getMerk(){
        return merk;
    }

    public void setMerk(String merk){
        this.merk = merk;
    }

    public int getHarga(){
        return harga;
    }

    public void setHarga(int harga){
        this.harga = harga;
    }

    @Override
    public String toString(){
        return " "+nama+"\n"+" "+merk+"\n"+" "+harga;
    }

    public String getUrll(){
        return urll;
    }

    public Barang (String id, String nm, String mrk, int hrg, String url){
        key = id;
        nama = nm;
        merk = mrk;
        harga = hrg;
        urll = url;
    }
}
