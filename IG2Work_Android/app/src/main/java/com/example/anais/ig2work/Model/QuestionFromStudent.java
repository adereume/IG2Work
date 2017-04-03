package com.example.anais.ig2work.Model;

/**
 * Created by Utilisateur on 30/03/2017.
 */

public class QuestionFromStudent {
    private int id;
    private int idTask;
    private String question;
    private String anwser;

    public QuestionFromStudent(int id, int idTask, String question, String anwser) {
        this.id = id;
        this.idTask = idTask;
        this.question = question;
        this.anwser = anwser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnwser() {
        return anwser;
    }

    public void setAnwser(String anwser) {
        this.anwser = anwser;
    }
}
