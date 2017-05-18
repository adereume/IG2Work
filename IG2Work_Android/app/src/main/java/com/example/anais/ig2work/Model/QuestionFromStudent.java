package com.example.anais.ig2work.Model;

/**
 * La classe QuestionFromStudent représente une question d'un étudiant au sein d'une tâche
 * Une question contient un identifiant, un identifiant de tâche, un intitulé et une réponse (donnée
 * par l'enseignant).
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnwser() {
        return anwser;
    }
}
