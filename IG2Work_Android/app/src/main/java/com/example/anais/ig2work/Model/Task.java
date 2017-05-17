package com.example.anais.ig2work.Model;

/**
 * La classe Task représente une tâche ou une question au sein d'une séance.
 * Une tâche contient un identifiant, un titre, une description, un type (tâche ou question).
 * Elle peut être visibile ou invisible (pour les étudiants) et réalisée ou non (par les étudiants)
 */

public class Task {
    private int id;
    private String titre;
    private String description;
    private String type;
    private boolean isVisible;
    private boolean isRealized;

    public Task(int id, String titre, String description, String type, boolean isVisible, boolean isRealized) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.isVisible = isVisible;
        this.isRealized = isRealized;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isRealized() {
        return isRealized;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isVisible=" + isVisible +
                ", isRealized=" + isRealized +
                '}';
    }
}
