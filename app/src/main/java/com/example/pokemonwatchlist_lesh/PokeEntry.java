package com.example.pokemonwatchlist_lesh;

public class PokeEntry {
    private String name;
    private Integer id;

    public PokeEntry(String name, Integer id){
        this.name = name;
        this.id = id;
    }


    public String getName(){
        return name;
    }

    public Integer getId(){
        return id;
    }

    public String toString(){
        String s = name + " " + id;
        return s;
    }

}
