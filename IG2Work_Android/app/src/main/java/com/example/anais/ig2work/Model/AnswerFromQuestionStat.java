package com.example.anais.ig2work.Model;

/**
 * La classe AnswerFromQuestionStat représente une réponse à une question au sein d'une séance
 * Une réponse contient un intitulé et un pourcentage
 */

public class AnswerFromQuestionStat {
    private String answer;
    private int pourcentage;

    public AnswerFromQuestionStat(String answer, int pourcentage) {
        this.answer = answer;
        this.pourcentage = pourcentage;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getPourcentage() {
        return pourcentage;
    }
}
