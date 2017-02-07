package com.example.anais.ig2work.Model;

/**
 * Created by clementruffin on 07/02/2017.
 */

public class Note {
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
