package com.example.anais.ig2work.Model;

import java.io.Serializable;

/**
 * La classe Note représente une note personnelle au sein d'une séance
 * Une note contient un identifiant et une description
 */

public class Note implements Serializable {
    private int id;
    private String description;
    private boolean isPrivate;

    public Note(int id, String description, boolean isPrivate) {
        this.id = id;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
