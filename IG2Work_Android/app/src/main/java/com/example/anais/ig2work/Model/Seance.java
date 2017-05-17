package com.example.anais.ig2work.Model;

import java.util.Date;

/**
 * La classe Seance représente une séance.
 * Une séance contient un identifiant, un module, un enseignant, une promotion, une date de début,
 * une salle et une cible (enseignant ou étudiant)
 */

public class Seance {
    private int id;
    private String module;
    private String teacher;
    private String promo;
    private Date dayTime;
    private String room;
    private String target;

    public Seance(int id, String module, String teacher, String promo, Date dayTime, String room, String target) {
        this.id = id;
        this.module = module;
        this.teacher = teacher;
        this.promo = promo;
        this.dayTime = dayTime;
        this.room = room;
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getPromo() {
        return promo;
    }

    public Date getDayTime() {
        return dayTime;
    }

    public String getRoom() {
        return room;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "id=" + id +
                ", module='" + module + '\'' +
                ", teacher='" + teacher + '\'' +
                ", promo='" + promo + '\'' +
                ", dayTime=" + dayTime +
                ", room='" + room + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
