package com.example.anais.ig2work.Model;

/**
 * Created by Utilisateur on 01/05/2017.
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

    public void setPourcentage(int pourcentage) {
        this.pourcentage = pourcentage;
    }
}
