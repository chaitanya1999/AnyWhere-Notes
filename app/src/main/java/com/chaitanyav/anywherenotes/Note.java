package com.chaitanyav.anywherenotes;

public class Note {
    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public Note(String name, int id){
        this.name=name;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return name;
    }
}
