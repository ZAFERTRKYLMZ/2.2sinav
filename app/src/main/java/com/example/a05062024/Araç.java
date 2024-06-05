package com.example.a05062024;

public class Araç {
    public int ID;
    public String Marka;
    public String Model;
    public int UretimYili;

    @Override
    public String toString() {
        return   Marka +
                 Model
                 + UretimYili
                ;
    }

}
