package com.example.stella.recyclerViewsAdapters;

/**
 * profiles es una clase que se utiliza para la inserccion de datos en la tabla "profiles"
 */

public class profiles {

    int id;
    String name;

    public profiles(){}

    public profiles(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
