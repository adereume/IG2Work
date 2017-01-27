package com.example.anais.ig2work.Model;

import java.util.Date;

/**
 * Created by clementruffin on 26/01/2017.
 */

public class Seance {
    private int id;
    private String module;
    private String teacher;
    private String promo;
    private Date dayTime;
    private String room;
    private String target;

    public Seance(int id, String module, String teacher, String promo, Date dayTime, String room) {
        this.id = id;
        this.module = module;
        this.teacher = teacher;
        this.promo = promo;
        this.dayTime = dayTime;
        this.room = room;
    }

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
