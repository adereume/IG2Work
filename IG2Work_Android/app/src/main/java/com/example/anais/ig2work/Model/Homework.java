package com.example.anais.ig2work.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by clementruffin on 29/01/2017.
 */

public class Homework implements Serializable {
    private int id;
    private String module;
    private String titre;
    private String description;
    private Date dueDate;
    private boolean realized;
    private boolean isVisible;

    public Homework(int id, String module, String titre, String description, Date dueDate, boolean realized, boolean isVisible) {
        this.id = id;
        this.module = module;
        this.titre = titre;
        this.description = description;
        this.dueDate = dueDate;
        this.realized = realized;
        this.isVisible = isVisible;
    }

    public int getId() {
        return id;
    }

    public String getModule() {
        return module;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public boolean isRealized() {
        return realized;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "id=" + id +
                ", module='" + module + '\'' +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", realized=" + realized +
                '}';
    }
}
